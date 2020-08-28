package com.zhanghe.test.testClient.serializer;

import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.rpc.core.client.AbstractRpcClient;
import com.zhanghe.rpc.core.server.AbstractRpcServer;
import com.zhanghe.test.testClient.service.DemoService;
import com.zhanghe.test.testClient.service.DemoServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CustomSerializerTest {

  private AbstractRpcServer rpcServer;

  private AbstractRpcClient rpcClient;

  private DemoService demoService;

  private Serializer serializer;

  @Before
  public void init() {
    serializer = new TestSerializer();
    rpcServer = new AbstractRpcServer(7777);
    rpcServer.setSerializer(serializer);
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
    rpcClient.setSerializer(serializer);
    rpcClient.init();
    demoService = (DemoService) rpcClient.proxy(
        DemoService.class.getName());
    Assert.assertNotNull(demoService);
  }

  public void call(){
    String str = "Random str:"+Math.random();
    String rpcRes = demoService.call(str);
    Assert.assertEquals("requestParam:" + str,rpcRes);
  }

}