package com.zhanghe.test.testClient;

import com.zhanghe.rpc.core.client.AbstractRpcClient;
import com.zhanghe.rpc.core.server.AbstractRpcServer;
import com.zhanghe.test.testClient.filter.DemoFilter;
import com.zhanghe.test.testClient.service.DemoService;
import com.zhanghe.test.testClient.service.DemoServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClientFilterTest {

  private AbstractRpcServer rpcServer;

  private AbstractRpcClient rpcClient;

  private DemoService demoService;

  @Before
  public void init() {
    rpcServer = new AbstractRpcServer(7777);
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
    rpcClient = new AbstractRpcClient("127.0.0.1",7777);
    rpcClient.addFilter(new DemoFilter("filter1"));
    rpcClient.addFilter(new DemoFilter("filter2"));
    rpcClient.init();
    demoService = (DemoService) rpcClient.proxy(DemoService.class.getName());
    Assert.assertNotNull(demoService);
  }

  public void call(){
    String str = "Random str:"+Math.random();
    String rpcRes = demoService.call(str);
    Assert.assertEquals("requestParam:" + str,rpcRes);
  }

}
