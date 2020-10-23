package com.zhanghe.test.spring;

import com.zhanghe.rpc.core.client.BaseRpcClient;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.spring.service.DemoService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringAnnotationTest {
  @Test
  public void testSpringServerAdaptor() throws Exception{
    ApplicationContext context = new ClassPathXmlApplicationContext(
        "spring-rpc-server-spring-adaptor-annotation.xml");
    BaseRpcServer adaptor = (BaseRpcServer)context.getBean("adaptor");
    Assert.assertNotNull(adaptor);
    BaseRpcClient baseRpcClient = new BaseRpcClient("127.0.0.1",6668);
    baseRpcClient.init();
    DemoService demoService = (DemoService) baseRpcClient.proxy(DemoService.class.getName());
    demoService.call("");
    baseRpcClient.destroy();
    ((ClassPathXmlApplicationContext) context).close();

  }
}
