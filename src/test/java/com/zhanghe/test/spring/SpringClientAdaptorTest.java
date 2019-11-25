package com.zhanghe.test.spring;

import com.zhanghe.rpc.RpcClientSpringAdaptor;
import com.zhanghe.rpc.RpcServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringClientAdaptorTest {

  private RpcServer rpcServer;

  @Before
  public void initRpcServer(){
    rpcServer = new RpcServer("0.0.0.0",7777);
    rpcServer.start();
  }

  @Test
  public void testSpringClientAdaptor(){
    ApplicationContext context = new ClassPathXmlApplicationContext(
        "spring-rpc-client-spring-adaptor.xml");
    RpcClientSpringAdaptor adaptor = (RpcClientSpringAdaptor)context.getBean("client");
    Assert.assertNotNull(adaptor);
    ((ClassPathXmlApplicationContext) context).close();
  }

  @After
  public void destroyRpcServer(){
    rpcServer.stop();
  }
}
