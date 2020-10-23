package com.zhanghe.test.spring;

import com.zhanghe.rpc.core.client.BaseRpcClient;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.spring.configuration.EnableRpcClientConfiguration;
import com.zhanghe.test.spring.configuration.EnableRpcServerConfiguration;
import com.zhanghe.test.spring.service.DemoService;
import com.zhanghe.test.spring.service.DemoServiceImpl;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SrpingEnableAnnotation {

  @Test
  public void serverTest(){
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(EnableRpcServerConfiguration.class);
    BaseRpcServer baseRpcServer = (BaseRpcServer)annotationConfigApplicationContext.getBean("server");
    annotationConfigApplicationContext.close();
  }

  @Test
  public void clientTest(){
    BaseRpcServer rpcServer = new BaseRpcServer(6666);
    rpcServer.bind(new DemoServiceImpl());
    rpcServer.init();
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(EnableRpcClientConfiguration.class);
    BaseRpcClient rpcClient = (BaseRpcClient)annotationConfigApplicationContext.getBean("client");
    DemoService demoService = (DemoService)annotationConfigApplicationContext.getBean("demoService");
    demoService.call("call");
    annotationConfigApplicationContext.close();
    rpcServer.destroy();
  }

  @Test
  public void clientTest2(){
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(EnableRpcServerConfiguration.class,EnableRpcClientConfiguration.class);
    BaseRpcClient rpcClient = (BaseRpcClient)annotationConfigApplicationContext.getBean("client");
    DemoService demoService = (DemoService)annotationConfigApplicationContext.getBean("demoService");
    demoService.call("call");
    annotationConfigApplicationContext.close();
  }
}
