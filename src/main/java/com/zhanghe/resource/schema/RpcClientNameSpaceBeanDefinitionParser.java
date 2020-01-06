package com.zhanghe.resource.schema;

import com.zhanghe.rpc.RpcClientSpringAdaptor;
import com.zhanghe.rpc.RpcServerSpringAdaptor;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
  }

}
