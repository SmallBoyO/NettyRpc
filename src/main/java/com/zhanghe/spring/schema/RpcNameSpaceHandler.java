package com.zhanghe.spring.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class RpcNameSpaceHandler extends NamespaceHandlerSupport {

  @Override
  public void init() {
    registerBeanDefinitionParser("server", new RpcServerNameSpaceBeanDefinitionParser());
    registerBeanDefinitionParser("client", new RpcClientNameSpaceBeanDefinitionParser());
    registerBeanDefinitionParser("loadBalanceClient", new RpcLoadBalanceClientServiceNameSpaceBeanDefinitionParser());
    registerBeanDefinitionParser("clientService", new RpcClientServiceNameSpaceBeanDefinitionParser());
  }
}
