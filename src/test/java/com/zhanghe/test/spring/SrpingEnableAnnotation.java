package com.zhanghe.test.spring;

import com.zhanghe.rpc.core.server.AbstractRpcServer;
import com.zhanghe.test.spring.configuration.EnableRpcServerConfiguration;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SrpingEnableAnnotation {

  @Test
  public void test(){
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(EnableRpcServerConfiguration.class);
    AbstractRpcServer abstractRpcServer = (AbstractRpcServer)annotationConfigApplicationContext.getBean("server");
    annotationConfigApplicationContext.close();
  }

}
