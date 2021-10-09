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

  private final static String IP_ATTRIBUTE_NAME = "ip";
  private final static String PORT_ATTRIBUTE_NAME = "port";
  private final static String WEIGHT_ATTRIBUTE_NAME = "weight";
  private final static String SCAN_PACKAGE_ATTRIBUTE_NAME = "scanPackage";
  private final static String LOAD_BALANCE_ATTRIBUTE_NAME = "loadBalance";
  private final static String LOAD_BALANCE_PROPERTY_NAME = "loadBalance";

  private final static String INIT_METHOD_NAME = "init";
  private final static String DESTROY_METHOD_NAME = "destroy";

  private final static String LOAD_BALANCE_SERVER_NODE_NAME = "loadBalanceServer";

  private final static String SERVERS_PROPERTY_NAME = "servers";

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
    String scanPackage = element.getAttribute(SCAN_PACKAGE_ATTRIBUTE_NAME);
    String loadBalance = element.getAttribute(LOAD_BALANCE_ATTRIBUTE_NAME);
    builder.addPropertyValue(LOAD_BALANCE_PROPERTY_NAME,loadBalance);
    builder.setInitMethodName(INIT_METHOD_NAME);
    builder.setDestroyMethodName(DESTROY_METHOD_NAME);
    //注册多个rpcserver
    List<RpcServerInfo> servers = new ArrayList<>();
//    ManagedList servers = new ManagedList();

    NodeList elementSons = element.getChildNodes();
    if (elementSons != null && elementSons.getLength() > 0) {
      for (int i = 0; i < elementSons.getLength(); i++) {
        Node node = elementSons.item(i);
        if (node instanceof Element) {
          if (LOAD_BALANCE_SERVER_NODE_NAME.equals(node.getNodeName()) || LOAD_BALANCE_SERVER_NODE_NAME.equals(node.getLocalName())) {
            String ip = ((Element) node).getAttribute(IP_ATTRIBUTE_NAME);
            String port = ((Element) node).getAttribute(PORT_ATTRIBUTE_NAME);
            String weight = ((Element) node).getAttribute(WEIGHT_ATTRIBUTE_NAME);
            RpcServerInfo rpcServerInfo = new RpcServerInfo(ip,Integer.valueOf(port));
            rpcServerInfo.setWeight(Integer.valueOf(weight));
            servers.add(rpcServerInfo);
          }
        }
      }
    }
    builder.addPropertyValue(SERVERS_PROPERTY_NAME,servers);
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
