package com.zhanghe.test.spring;

import com.zhanghe.rpc.core.client.AbstractRpcClient;
import com.zhanghe.rpc.core.client.RpcClientSpringAdaptor;
import com.zhanghe.rpc.core.server.AbstractRpcServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringClientAnnotationTest {

  private AbstractRpcServer rpcServer;

  @Before
  public void initRpcServer(){
    rpcServer = new AbstractRpcServer("0.0.0.0",6666);
    rpcServer.init();
    rpcServer.bind(new DemoServiceImpl());
  }
  @Test
  public void testSpringServerAdaptor() throws Exception{
    ApplicationContext contextClient = new ClassPathXmlApplicationContext(
        "spring-rpc-client-spring-adaptor-annotation.xml");
    RpcClientSpringAdaptor client = (RpcClientSpringAdaptor)contextClient.getBean("client");
    Assert.assertNotNull(client);
    DemoService demoService = (DemoService)contextClient.getBean("demoService");
    Assert.assertNotNull(demoService);
    String str = "Random str:"+Math.random();
    String rpcRes = demoService.call(str);
    Assert.assertEquals("requestParam:" + str,rpcRes);

  }
}
