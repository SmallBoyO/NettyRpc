package com.zhanghe.test.testClient;

import com.zhanghe.rpc.RpcClient;
import com.zhanghe.rpc.RpcServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClientTest {

  private RpcServer rpcServer;

  private RpcClient rpcClient;

  private DemoService demoService;

  @Before
  public void init() {
    rpcServer = new RpcServer(7777);
    rpcServer.bind(new DemoServiceImpl());
    rpcServer.start();
  }

  @After
  public void destroy(){
    rpcServer.stop();
    rpcClient.stop();
  }
  @Test
  public void testConnectAndCall() throws ClassNotFoundException{
    connect();
    call();
  }

  public void connect() throws ClassNotFoundException{
    rpcClient = new RpcClient("127.0.0.1",7777);
    rpcClient.start();
    demoService = (DemoService)rpcClient.proxy(DemoService.class.getName());
    Assert.assertNotNull(demoService);
  }

  public void call(){
    String str = "Random str:"+Math.random();
    String rpcRes = demoService.call(str);
    Assert.assertEquals("requestParam:" + str,rpcRes);
  }

}
