package com.zhanghe.test.spring;

import com.zhanghe.config.RpcServerConfig;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringServerBusinessThreadPoolConfigTest {

  @Test
  public void doTest(){
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-rpc-server-threadpool-config.xml");
    BaseRpcServer rpcServer = (BaseRpcServer)context.getBean("server");
    Assert.assertNotNull(rpcServer);
  }

}
