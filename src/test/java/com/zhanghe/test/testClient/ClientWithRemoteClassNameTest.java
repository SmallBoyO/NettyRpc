package com.zhanghe.test.testClient;

import com.zhanghe.rpc.core.client.BaseRpcClient;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.testClient.service.DemoService;
import com.zhanghe.test.testClient.service.DemoServiceImpl;
import com.zhanghe.test.testClient.service.DemoServiceWithRemoteClassName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClientWithRemoteClassNameTest {

  private BaseRpcServer rpcServer;

  private BaseRpcClient rpcClient;

  private DemoServiceWithRemoteClassName demoServiceWithRemoteClassName;

  @Before
  public void init() {
    rpcServer = new BaseRpcServer(7777);
    rpcServer.init();
    rpcServer.bind(new DemoServiceImpl());
  }

  @After
  public void destroy(){
    rpcServer.stop();
    rpcClient.destroy();
  }

  @Test
  public void testConnectAndCall() throws ClassNotFoundException,InterruptedException{
    connect();
    call();
  }

  public void connect() throws ClassNotFoundException,InterruptedException{
    rpcClient = new BaseRpcClient("127.0.0.1",7777);
    rpcClient.init();
    demoServiceWithRemoteClassName = (DemoServiceWithRemoteClassName) rpcClient.proxy(DemoServiceWithRemoteClassName.class.getName());
    Assert.assertNotNull(demoServiceWithRemoteClassName);
  }

  public void call(){
    String str = "Random str:"+Math.random();
    String rpcRes = demoServiceWithRemoteClassName.call(str);
    Assert.assertEquals("requestParam:" + str,rpcRes);
  }

}
