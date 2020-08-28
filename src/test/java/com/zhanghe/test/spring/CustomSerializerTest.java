package com.zhanghe.test.spring;

import com.zhanghe.rpc.core.client.AbstractRpcClient;
import com.zhanghe.rpc.core.server.AbstractRpcServer;
import com.zhanghe.test.spring.serializer.TestSerializer;
import com.zhanghe.test.spring.service.DemoService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CustomSerializerTest {

  @Test
  public void testSpringServerAdaptor() throws Exception{
    ApplicationContext context = new ClassPathXmlApplicationContext(
        "spring-rpc-server-serializer-spring-adaptor.xml");
    AbstractRpcServer adaptor = (AbstractRpcServer)context.getBean("adaptor");
    Assert.assertNotNull(adaptor);
    AbstractRpcClient abstractRpcClient = new AbstractRpcClient("localhost",6666);
    abstractRpcClient.setSerializer(new TestSerializer());
    abstractRpcClient.init();
    DemoService demoService = (DemoService)abstractRpcClient.proxy(DemoService.class.getName());
    demoService.call("");
    abstractRpcClient.destroy();
    ((ClassPathXmlApplicationContext) context).close();
  }

}
