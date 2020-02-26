package com.zhanghe.test.spring;

import com.zhanghe.rpc.core.client.RpcClientSpringAdaptor;
import com.zhanghe.rpc.core.server.AbstractRpcServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringClientAdaptorTest {

  private AbstractRpcServer rpcServer;

  @Before
  public void initRpcServer(){
    rpcServer = new AbstractRpcServer("0.0.0.0",7777);
    rpcServer.init();
    rpcServer.bind(new DemoServiceImpl());
  }

  @Test
  public void testSpringClientAdaptor(){
    ApplicationContext context = new ClassPathXmlApplicationContext(
        "spring-rpc-client-spring-adaptor.xml");
    RpcClientSpringAdaptor adaptor = (RpcClientSpringAdaptor)context.getBean("client");
    Assert.assertNotNull(adaptor);
    DemoService demoService = (DemoService)context.getBean("demoService");
    Assert.assertNotNull(demoService);
    String str = "Random str:"+Math.random();
    String rpcRes = demoService.call(str);
    Assert.assertEquals("requestParam:" + str,rpcRes);
    ((ClassPathXmlApplicationContext) context).close();
  }

  @After
  public void destroyRpcServer(){
    rpcServer.stop();
  }
}
