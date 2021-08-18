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
    String ip = element.getAttribute("ip");
    int port=Integer.valueOf(element.getAttribute("port"));
    String scanPackage = element.getAttribute("scanPackage");
    RpcClientConfig rpcClientConfig = new RpcClientConfig(ip,port);
    builder.addPropertyValue("rpcClientConfig",rpcClientConfig);
    builder.setInitMethodName("init");
    builder.setDestroyMethodName("destroy");

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
