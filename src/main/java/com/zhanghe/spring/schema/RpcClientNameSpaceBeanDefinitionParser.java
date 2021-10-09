package com.zhanghe.spring.schema;

import com.zhanghe.config.RpcClientConfig;
import com.zhanghe.rpc.core.client.BaseRpcClient;
import com.zhanghe.spring.RpcClientBeanProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class RpcClientNameSpaceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

  private final static String IP_ATTRIBUTE_NAME = "ip";
  private final static String PORT_ATTRIBUTE_NAME = "port";
  private final static String SCAN_PACKAGE_ATTRIBUTE_NAME = "scanPackage";

  private final static String INIT_METHOD_NAME = "init";
  private final static String DESTROY_METHOD_NAME = "destroy";

  private final static String CLIENT_CONFIG_PROPERTY_NAME = "rpcClientConfig";

  @Override
  protected Class getBeanClass(Element ele){
    return BaseRpcClient.class;
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
    RpcClientConfig rpcClientConfig = new RpcClientConfig(ip,port);
    String scanPackage = element.getAttribute(SCAN_PACKAGE_ATTRIBUTE_NAME);
    builder.addPropertyValue(CLIENT_CONFIG_PROPERTY_NAME,rpcClientConfig);
    builder.setInitMethodName(INIT_METHOD_NAME);
    builder.setDestroyMethodName(DESTROY_METHOD_NAME);

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
