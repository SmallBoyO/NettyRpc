package com.zhanghe.spring.schema;

import com.zhanghe.config.RpcServerConfig;
import com.zhanghe.protocol.serializer.SerializerAlgorithm;
import com.zhanghe.protocol.serializer.SerializerManager;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.spring.RpcServiceBeanProcessor;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RpcServerNameSpaceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

  @Override
  protected Class getBeanClass(Element ele){
    return BaseRpcServer.class;
  }

  @Override
  protected void doParse(Element element, BeanDefinitionBuilder builder) {
    super.doParse(element, builder);
  }

  @Override
  protected void doParse(Element element, ParserContext parserContext,
      BeanDefinitionBuilder builder) {
    String ip = element.getAttribute("ip");
    int port=Integer.valueOf(element.getAttribute("port"));
    String serializer = element.getAttribute("serializer");
    String scanPackage = element.getAttribute("scanPackage");
    RpcServerConfig rpcServerConfig = new RpcServerConfig(ip,port);
    builder.addPropertyValue("rpcServerConfig",rpcServerConfig);

    if(!StringUtils.isEmpty(serializer)){
      switch ( serializer ){
        case "KRYO":
          builder.addPropertyValue("serializer",SerializerManager.getSerializer(SerializerAlgorithm.KYRO));
          break;
        case "JSON":
          builder.addPropertyValue("serializer",SerializerManager.getSerializer(SerializerAlgorithm.JSON));
          break;
        case "PROTOSTUFF":
          builder.addPropertyValue("serializer",SerializerManager.getSerializer(SerializerAlgorithm.PROTOSTUFF));
          break;
        default:
          builder.addPropertyValue("serializer",new RuntimeBeanReference(serializer));
          break;
      }
    }

    builder.setInitMethodName("init");
    builder.setDestroyMethodName("destroy");
    ManagedList lists = new ManagedList();
    NodeList elementSons = element.getChildNodes();
    if (elementSons != null && elementSons.getLength() > 0) {
      for (int i = 0; i < elementSons.getLength(); i++) {
        Node node = elementSons.item(i);
        if (node instanceof Element) {
          if ("service".equals(node.getNodeName()) || "service".equals(node.getLocalName())) {
            String value = ((Element) node).getAttribute("value");
            lists.add(new RuntimeBeanReference(value));
          }
        }
      }
    }
    builder.addPropertyValue("services",lists);

    if(!StringUtils.isEmpty(scanPackage)){
      //注册RpcServiceBeanProcessor用于扫描RpcService注解
      RootBeanDefinition beanDefinition = new RootBeanDefinition();
      beanDefinition.setBeanClass(RpcServiceBeanProcessor.class);
      beanDefinition.setLazyInit(false);
      beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0,scanPackage);
      String id = RpcServiceBeanProcessor.class.getName();
      parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
    }
  }

}
