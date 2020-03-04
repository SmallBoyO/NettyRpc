package com.zhanghe.resource.schema;

import com.zhanghe.rpc.core.client.RpcClientSpringAdaptor;
import com.zhanghe.spring.RpcClientBeanProcessor;
import com.zhanghe.spring.RpcServiceBeanProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class RpcClientNameSpaceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

  @Override
  protected Class getBeanClass(Element ele){
    return RpcClientSpringAdaptor.class;
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
    builder.addPropertyValue("ip",ip);
    builder.addPropertyValue("port",port);
    builder.setInitMethodName("init");
    builder.setDestroyMethodName("destroy");

    //注册RpcServiceBeanProcessor用于扫描RpcService注解
    RootBeanDefinition beanDefinition = new RootBeanDefinition();
    beanDefinition.setBeanClass(RpcClientBeanProcessor.class);
    beanDefinition.setLazyInit(false);

    String id = RpcClientBeanProcessor.class.getName();
    parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
  }

}
