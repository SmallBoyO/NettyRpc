package com.zhanghe.spring.schema;

import com.zhanghe.rpc.core.client.RpcLoadBalanceAdaptor;
import com.zhanghe.rpc.core.client.RpcServerInfo;
import com.zhanghe.spring.RpcClientBeanProcessor;
import java.util.ArrayList;
import java.util.List;
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

public class RpcLoadBalanceClientServiceNameSpaceBeanDefinitionParser extends
    AbstractSingleBeanDefinitionParser {

  @Override
  protected Class getBeanClass(Element ele){
    return RpcLoadBalanceAdaptor.class;
  }

  @Override
  protected void doParse(Element element, BeanDefinitionBuilder builder) {
    super.doParse(element, builder);
  }

  @Override
  protected void doParse(Element element, ParserContext parserContext,
      BeanDefinitionBuilder builder) {
    String scanPackage = element.getAttribute("scanPackage");
    String loadBalance = element.getAttribute("loadBalance");
    builder.addPropertyValue("loadBalance",loadBalance);
    builder.setInitMethodName("init");
    builder.setDestroyMethodName("destroy");
    //注册多个rpcserver
    List<RpcServerInfo> servers = new ArrayList<>();
//    ManagedList servers = new ManagedList();

    NodeList elementSons = element.getChildNodes();
    if (elementSons != null && elementSons.getLength() > 0) {
      for (int i = 0; i < elementSons.getLength(); i++) {
        Node node = elementSons.item(i);
        if (node instanceof Element) {
          if ("loadBalanceServer".equals(node.getNodeName()) || "loadBalanceServer".equals(node.getLocalName())) {
            String ip = ((Element) node).getAttribute("ip");
            String port = ((Element) node).getAttribute("port");
            String weight = ((Element) node).getAttribute("weight");
            RpcServerInfo rpcServerInfo = new RpcServerInfo(ip,Integer.valueOf(port));
            rpcServerInfo.setWeight(Integer.valueOf(weight));
            servers.add(rpcServerInfo);
          }
        }
      }
    }
    builder.addPropertyValue("servers",servers);
    if(!StringUtils.isEmpty(scanPackage)){
      //注册RpcServiceBeanProcessor用于扫描RpcClient注解
      RootBeanDefinition beanDefinition = new RootBeanDefinition();
      beanDefinition.setBeanClass(RpcClientBeanProcessor.class);
      beanDefinition.setLazyInit(false);
      beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0,scanPackage);
      String id = RpcClientBeanProcessor.class.getName();
      parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
    }
  }
}
