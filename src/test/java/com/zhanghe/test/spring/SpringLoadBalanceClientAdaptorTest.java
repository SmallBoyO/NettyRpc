package com.zhanghe.test.spring;

import com.zhanghe.rpc.core.client.RpcLoadBalanceAdaptor;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.spring.service.DemoService;
import com.zhanghe.test.spring.service.DemoServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringLoadBalanceClientAdaptorTest {

  private BaseRpcServer rpcServer1;

  private BaseRpcServer rpcServer2;

  @Before
  public void initRpcServer(){
    rpcServer1 = new BaseRpcServer("0.0.0.0",7777);
    rpcServer1.init();
    rpcServer1.bind(new DemoServiceImpl());
    rpcServer2 = new BaseRpcServer("0.0.0.0",7778);
    rpcServer2.init();
    rpcServer2.bind(new DemoServiceImpl());
  }

  @Test
  public void testSpringClientAdaptor(){
    ApplicationContext context = new ClassPathXmlApplicationContext(
        "spring-rpc-loadbalance-client-spring-adaptor.xml");
    RpcLoadBalanceAdaptor adaptor = (RpcLoadBalanceAdaptor)context.getBean("client");
    Assert.assertNotNull(adaptor);
    DemoService demoService = (com.zhanghe.test.spring.service.DemoService) context.getBean("demoService");
    for(int i = 0;i<1000;i++){
      String result = demoService.call("demo");
    }
    ((ClassPathXmlApplicationContext) context).close();
  }

  @After
  public void destroyRpcServer(){
    rpcServer1.destroy();
    rpcServer2.destroy();
  }
}
