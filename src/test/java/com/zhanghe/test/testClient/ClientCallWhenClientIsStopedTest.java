package com.zhanghe.test.testClient;

import com.zhanghe.rpc.core.client.BaseRpcClient;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.testClient.service.DemoService;
import com.zhanghe.test.testClient.service.DemoServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ClientCallWhenClientIsStopedTest {

  private BaseRpcServer rpcServer;

  private BaseRpcClient rpcClient;

  private DemoService demoService;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void init() {
    rpcServer = new BaseRpcServer(7777);
    rpcServer.init();
    rpcServer.bind(new DemoServiceImpl());
  }

  @After
  public void destroy(){
    rpcServer.stop();
  }

  @Test
  public void testConnectAndCall() throws ClassNotFoundException,InterruptedException{
    connect();
    call();
  }

  public void connect() throws ClassNotFoundException,InterruptedException{
    rpcClient = new BaseRpcClient("127.0.0.1",7777);
    rpcClient.init();
    demoService = (DemoService) rpcClient.proxy(DemoService.class.getName());
    Assert.assertNotNull(demoService);
  }

  public void call(){
    thrown.expect(RuntimeException.class);
    thrown.expectMessage("client not start or client has stoped");
    rpcClient.destroy();
    String rpcResOnStoped = demoService.call("str");
  }
}
