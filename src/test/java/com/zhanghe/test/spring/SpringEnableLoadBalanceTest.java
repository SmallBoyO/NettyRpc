package com.zhanghe.test.spring;

import com.zhanghe.rpc.core.client.Client;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.spring.configuration.EnableLoadBalanceRpcClientConfiguration;
import com.zhanghe.test.spring.service.DemoService;
import com.zhanghe.test.spring.service.DemoServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringEnableLoadBalanceTest {
  private BaseRpcServer rpcServer1;

  private BaseRpcServer rpcServer2;

  @Before
  public void initRpcServer(){
    rpcServer1 = new BaseRpcServer("0.0.0.0",6666);
    rpcServer1.init();
    rpcServer1.bind(new DemoServiceImpl());
    rpcServer2 = new BaseRpcServer("0.0.0.0",6667);
    rpcServer2.init();
    rpcServer2.bind(new DemoServiceImpl());
  }

  @Test
  public void testSpringClientAdaptor(){
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(EnableLoadBalanceRpcClientConfiguration.class);
    Client client = (Client)annotationConfigApplicationContext.getBean("client");
    DemoService demoService = (DemoService)annotationConfigApplicationContext.getBean("demoService");
    demoService.call("call");
    annotationConfigApplicationContext.close();
  }

  @After
  public void destroyRpcServer(){
    rpcServer1.destroy();
    rpcServer2.destroy();
  }
}
