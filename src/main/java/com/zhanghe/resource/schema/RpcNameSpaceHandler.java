package com.zhanghe.resource.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class RpcNameSpaceHandler extends NamespaceHandlerSupport {

  @Override
  public void init() {
    registerBeanDefinitionParser("server", new RpcServerNameSpaceBeanDefinitionParser());
  }
}
