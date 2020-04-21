package com.zhanghe.spring.schema;

import com.zhanghe.rpc.core.client.RpcClientSpringAdaptor;
import com.zhanghe.rpc.core.client.RpcLoadBalanceAdaptor;
import com.zhanghe.spring.RpcClientBeanProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

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
    builder.setInitMethodName("init");
    builder.setDestroyMethodName("destroy");
    //注册多个rpcserver
    ManagedList servers = new ManagedList();

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
