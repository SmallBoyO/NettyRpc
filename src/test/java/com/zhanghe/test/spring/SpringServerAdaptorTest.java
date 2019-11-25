package com.zhanghe.test.spring;

import com.zhanghe.rpc.RpcServerSpringAdaptor;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringServerAdaptorTest {

  @Test
  public void testSpringServerAdaptor(){
    ApplicationContext context = new ClassPathXmlApplicationContext(
        "spring-rpc-server-spring-adaptor.xml");
    RpcServerSpringAdaptor adaptor = (RpcServerSpringAdaptor)context.getBean("adaptor");
    Assert.assertNotNull(adaptor);
    ((ClassPathXmlApplicationContext) context).close();
  }
}
