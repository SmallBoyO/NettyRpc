package com.zhanghe.test.testClient;

import com.zhanghe.rpc.RpcClientConnector;
import com.zhanghe.rpc.RpcServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClientReconnectTest {

  private RpcServer rpcServer;

  private RpcClientConnector rpcClientConnector;

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
    rpcClientConnector.stop();
  }
  @Test
  public void testConnectAndCall() throws ClassNotFoundException,InterruptedException{
    connect();
    stopServer();
    callDisconnect();
    startServer();
    Thread.sleep(10000);
    call();
  }

  public void connect() throws ClassNotFoundException{
    rpcClientConnector = new RpcClientConnector("127.0.0.1",7777);
    rpcClientConnector.start();
//    demoService = (DemoService) rpcClientConnector.proxy(DemoService.class.getName());
    Assert.assertNotNull(demoService);
  }

  public void call(){
    String str = "Random str:"+Math.random();
    String rpcRes = demoService.call(str);
    Assert.assertEquals("requestParam:" + str,rpcRes);
  }

  public void stopServer(){
    try {
      rpcServer.stop();
    }catch (Exception e){
      Assert.fail("Should not reach here");
    }
  }

  public void callDisconnect(){
    try {
      String str = "Random str:"+Math.random();
      String rpcRes = demoService.call(str);
      Assert.fail("Should not reach here");
    }catch (Exception e){

    }
  }

  public void startServer(){
    try {
      rpcServer = new RpcServer(7777);
      rpcServer.bind(new DemoServiceImpl());
      rpcServer.start();
    }catch (Exception e){
      Assert.fail("Should not reach here");
    }
  }
}
