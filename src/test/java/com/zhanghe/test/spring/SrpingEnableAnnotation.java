package com.zhanghe.test.spring;

import com.zhanghe.rpc.core.client.AbstractRpcClient;
import com.zhanghe.rpc.core.server.AbstractRpcServer;
import com.zhanghe.test.spring.configuration.EnableRpcClientConfiguration;
import com.zhanghe.test.spring.configuration.EnableRpcServerConfiguration;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SrpingEnableAnnotation {

  @Test
  public void serverTest(){
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(EnableRpcServerConfiguration.class);
    AbstractRpcServer abstractRpcServer = (AbstractRpcServer)annotationConfigApplicationContext.getBean("server");
    annotationConfigApplicationContext.close();
  }

  @Test
  public void clientTest(){
    AbstractRpcServer rpcServer = new AbstractRpcServer(6666);
    rpcServer.bind(new DemoServiceImpl());
    rpcServer.init();
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(EnableRpcClientConfiguration.class);
    AbstractRpcClient rpcClient = (AbstractRpcClient)annotationConfigApplicationContext.getBean("client");
    annotationConfigApplicationContext.close();
    rpcServer.destroy();
  }

  @Test
  public void clientTest2(){
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(EnableRpcClientConfiguration.class,EnableRpcServerConfiguration.class);
    AbstractRpcClient rpcClient = (AbstractRpcClient)annotationConfigApplicationContext.getBean("client");
    annotationConfigApplicationContext.close();
  }
}
