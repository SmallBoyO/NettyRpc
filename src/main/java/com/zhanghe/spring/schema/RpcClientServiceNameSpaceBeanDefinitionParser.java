package com.zhanghe.spring.schema;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class RpcClientServiceNameSpaceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

  private final static String CLASS_ATTRIBUTE_NAME = "class";
  private final static String FACTORY_METHOD_NAME = "proxy";
  private final static String FACTORY_BEAN_NAME = "client";

  @Override
  protected Class getBeanClass(Element ele){
    return Object.class;
  }

  @Override
  protected void doParse(Element element, BeanDefinitionBuilder builder) {
    super.doParse(element, builder);
  }

  @Override
  protected void doParse(Element element, ParserContext parserContext,
      BeanDefinitionBuilder builder) {
    String clazz = element.getAttribute(CLASS_ATTRIBUTE_NAME);
    builder.setFactoryMethodOnBean(FACTORY_METHOD_NAME,FACTORY_BEAN_NAME);
    builder.addConstructorArgValue(clazz);
  }

}
