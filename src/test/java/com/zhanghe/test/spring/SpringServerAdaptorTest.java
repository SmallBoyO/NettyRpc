package com.zhanghe.test.spring;

import com.zhanghe.rpc.core.client.AbstractRpcClient;
import com.zhanghe.rpc.core.server.AbstractRpcServer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringServerAdaptorTest {

  @Test
  public void testSpringServerAdaptor() throws Exception{
    ApplicationContext context = new ClassPathXmlApplicationContext(
        "spring-rpc-server-spring-adaptor.xml");
    AbstractRpcServer adaptor = (AbstractRpcServer)context.getBean("adaptor");
    Assert.assertNotNull(adaptor);
    AbstractRpcClient abstractRpcClient = new AbstractRpcClient("127.0.0.1",6667);
    abstractRpcClient.init();
    DemoService demoService = (DemoService)abstractRpcClient.proxy(DemoService.class.getName());
    demoService.call("");
    abstractRpcClient.destroy();
    ((ClassPathXmlApplicationContext) context).close();
  }
}
