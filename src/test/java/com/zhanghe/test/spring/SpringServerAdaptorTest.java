package com.zhanghe.test.spring;

import com.zhanghe.rpc.core.client.BaseRpcClient;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.spring.service.DemoService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringServerAdaptorTest {

  DemoService demoService;

  BaseRpcClient baseRpcClient;

  ClassPathXmlApplicationContext context;

  @Before
  public void init() throws Exception{
    context = new ClassPathXmlApplicationContext("spring-rpc-server-spring-adaptor.xml");
    BaseRpcServer adaptor = (BaseRpcServer)context.getBean("adaptor");
    Assert.assertNotNull(adaptor);
    baseRpcClient = new BaseRpcClient("127.0.0.1",6667);
    baseRpcClient.init();
    demoService = (DemoService) baseRpcClient.proxy(DemoService.class.getName());
  }

  @After
  public void destroy(){
    baseRpcClient.destroy();
    context.close();
  }

  @Test
  public void testSpringServerAdaptor(){
    String str = "Random str:"+Math.random();
    String rpcRes = demoService.call(str);
    Assert.assertEquals("requestParam:" + str,rpcRes);
  }
}
