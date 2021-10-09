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

  private final static String IP_ATTRIBUTE_NAME = "ip";
  private final static String PORT_ATTRIBUTE_NAME = "port";
  private final static String SERIALIZER_ATTRIBUTE_NAME = "serializer";
  private final static String SCAN_PACKAGE_ATTRIBUTE_NAME = "scanPackage";
  private final static String SERVICE_ATTRIBUTE_NAME = "service";
  private final static String SERVICE_ATTRIBUTE_VALUE_NAME = "value";
  private final static String BUSINESS_LOGIC_CORE_THREAD_NUM_ATTRIBUTE_NAME = "businessLogicCoreThreadNum";
  private final static String BUSINESS_LOGIC_QUEUE_LENGTH_ATTRIBUTE_NAME = "businessLogicQueueLength";

  private final static String INIT_METHOD_NAME = "init";
  private final static String DESTROY_METHOD_NAME = "destroy";

  private final static String SERVER_CONFIG_PROPERTY_NAME = "rpcServerConfig";
  private final static String SERIALIZER_PROPERTY_NAME = "serializer";
  private final static String SERVICES_PROPERTY_NAME = "services";

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
    String ip = element.getAttribute(IP_ATTRIBUTE_NAME);
    int port = Integer.valueOf(element.getAttribute(PORT_ATTRIBUTE_NAME));
    RpcServerConfig rpcServerConfig = new RpcServerConfig(ip,port);
    if(element.hasAttribute(BUSINESS_LOGIC_CORE_THREAD_NUM_ATTRIBUTE_NAME)){
      int businessLogicCoreThreadNum = Integer.valueOf(element.getAttribute(BUSINESS_LOGIC_CORE_THREAD_NUM_ATTRIBUTE_NAME));
      rpcServerConfig.setBusinessLogicCoreThreadNum(businessLogicCoreThreadNum);
    }
    if(element.hasAttribute(BUSINESS_LOGIC_QUEUE_LENGTH_ATTRIBUTE_NAME)){
      int businessLogicQueueLength = Integer.valueOf(element.getAttribute(BUSINESS_LOGIC_QUEUE_LENGTH_ATTRIBUTE_NAME));
      rpcServerConfig.setBusinessLogicQueueLength(businessLogicQueueLength);
    }
    String serializer = element.getAttribute(SERIALIZER_ATTRIBUTE_NAME);
    String scanPackage = element.getAttribute(SCAN_PACKAGE_ATTRIBUTE_NAME);
    builder.addPropertyValue(SERVER_CONFIG_PROPERTY_NAME,rpcServerConfig);

    if(!StringUtils.isEmpty(serializer)){
      switch ( serializer ){
        case "KRYO":
          builder.addPropertyValue(SERIALIZER_PROPERTY_NAME,SerializerManager.getSerializer(SerializerAlgorithm.KYRO));
          break;
        case "JSON":
          builder.addPropertyValue(SERIALIZER_PROPERTY_NAME,SerializerManager.getSerializer(SerializerAlgorithm.JSON));
          break;
        case "PROTOSTUFF":
          builder.addPropertyValue(SERIALIZER_PROPERTY_NAME,SerializerManager.getSerializer(SerializerAlgorithm.PROTOSTUFF));
          break;
        default:
          builder.addPropertyValue(SERIALIZER_PROPERTY_NAME,new RuntimeBeanReference(serializer));
          break;
      }
    }

    builder.setInitMethodName(INIT_METHOD_NAME);
    builder.setDestroyMethodName(DESTROY_METHOD_NAME);
    ManagedList lists = new ManagedList();
    NodeList elementSons = element.getChildNodes();
    if (elementSons != null && elementSons.getLength() > 0) {
      for (int i = 0; i < elementSons.getLength(); i++) {
        Node node = elementSons.item(i);
        if (node instanceof Element) {
          if (SERVICE_ATTRIBUTE_NAME.equals(node.getNodeName()) || SERVICE_ATTRIBUTE_NAME.equals(node.getLocalName())) {
            String value = ((Element) node).getAttribute(SERVICE_ATTRIBUTE_VALUE_NAME);
            lists.add(new RuntimeBeanReference(value));
          }
        }
      }
    }
    builder.addPropertyValue(SERVICES_PROPERTY_NAME,lists);

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
