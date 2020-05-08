package com.zhanghe.test.spring;

import com.zhanghe.rpc.core.client.AbstractRpcClient;
import com.zhanghe.rpc.core.client.Client;
import com.zhanghe.rpc.core.server.AbstractRpcServer;
import com.zhanghe.test.spring.configuration.EnableLoadBalanceRpcClientConfiguration;
import com.zhanghe.test.spring.service.DemoService;
import com.zhanghe.test.spring.service.DemoServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringEnableLoadBalanceTest {
  private AbstractRpcServer rpcServer1;

  private AbstractRpcServer rpcServer2;

  @Before
  public void initRpcServer(){
    rpcServer1 = new AbstractRpcServer("0.0.0.0",6666);
    rpcServer1.init();
    rpcServer1.bind(new DemoServiceImpl());
    rpcServer2 = new AbstractRpcServer("0.0.0.0",6667);
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
