package com.zhanghe.test.spring;

import com.zhanghe.rpc.core.client.AbstractRpcClient;
import com.zhanghe.rpc.core.server.AbstractRpcServer;
import com.zhanghe.test.spring.service.DemoService;
import com.zhanghe.test.testClient.service.DemoServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringServerAdaptorTest {

  DemoService demoService;

  AbstractRpcClient abstractRpcClient;

  ClassPathXmlApplicationContext context;

  @Before
  public void init() throws Exception{
    context = new ClassPathXmlApplicationContext("spring-rpc-server-spring-adaptor.xml");
    AbstractRpcServer adaptor = (AbstractRpcServer)context.getBean("adaptor");
    Assert.assertNotNull(adaptor);
    abstractRpcClient = new AbstractRpcClient("127.0.0.1",6667);
    abstractRpcClient.init();
    demoService = (DemoService)abstractRpcClient.proxy(DemoService.class.getName());
  }

  @After
  public void destroy(){
    abstractRpcClient.destroy();
    context.close();
  }

  @Test
  public void testSpringServerAdaptor(){
    String str = "Random str:"+Math.random();
    String rpcRes = demoService.call(str);
    Assert.assertEquals("requestParam:" + str,rpcRes);
  }
}
