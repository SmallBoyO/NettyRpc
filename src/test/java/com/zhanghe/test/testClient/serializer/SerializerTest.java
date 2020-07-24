package com.zhanghe.test.testClient.serializer;

import com.zhanghe.protocol.serializer.SerializerAlgorithm;
import com.zhanghe.protocol.serializer.SerializerManager;
import com.zhanghe.rpc.core.client.AbstractRpcClient;
import com.zhanghe.rpc.core.server.AbstractRpcServer;
import com.zhanghe.test.testClient.service.DemoServiceImpl;
import com.zhanghe.test.testClient.service.DemoService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试不同序列化器
 */
public class SerializerTest {

  private AbstractRpcServer kryoServer;

  private AbstractRpcServer jsonServer;

  private AbstractRpcServer protostuffServer;

  private AbstractRpcClient kryoClient;

  private AbstractRpcClient jsonClient;

  private AbstractRpcClient protostuffClient;

  private DemoService kryoDemoService;

  private DemoService jsonDemoService;

  private DemoService protostuffDemoService;

  @Test
  public void test(){
    String str = "Random str:"+Math.random();
    String rpcRes = kryoDemoService.call(str);
    Assert.assertEquals("requestParam:" + str,rpcRes);

    String jsonStr = "Random str:"+Math.random();
    String jsonRpcRes = jsonDemoService.call(jsonStr);
    Assert.assertEquals("requestParam:" + jsonStr,jsonRpcRes);

    String prostuffStr = "Random str:"+Math.random();
    String prostuffRpcRes = protostuffDemoService.call(prostuffStr);
    Assert.assertEquals("requestParam:" + prostuffStr,prostuffRpcRes);
  }

  @Before
  public void beforeTest() throws ClassNotFoundException{
    initKryoServerAndClient();
    initJsonServerAndClient();
    initProtostuffServerAndClient();
  }

  @After
  public void afterTest(){
    destroyKryoServerAndClient();
    destroyJsonServerAndClient();
    destroyProtostuffServerAndClient();
  }

  private void initKryoServerAndClient() throws ClassNotFoundException{
    kryoServer = new AbstractRpcServer("127.0.0.1",3659);
    kryoServer.setSerializer(SerializerManager.getSerializer(SerializerAlgorithm.KYRO));
    kryoServer.bind(new DemoServiceImpl());
    kryoServer.init();
    kryoClient = new AbstractRpcClient("127.0.0.1",3659);
    kryoClient.init();
    kryoDemoService = (DemoService)kryoClient.proxy(DemoService.class.getName());
  }

  private void destroyKryoServerAndClient(){
    kryoClient.destroy();
    kryoServer.stop();
  }

  private void initJsonServerAndClient() throws ClassNotFoundException{
    jsonServer = new AbstractRpcServer("127.0.0.1",3660);
    jsonServer.setSerializer(SerializerManager.getSerializer(SerializerAlgorithm.KYRO));
    jsonServer.bind(new DemoServiceImpl());
    jsonServer.init();
    jsonClient = new AbstractRpcClient("127.0.0.1",3660);
    jsonClient.init();
    jsonDemoService = (DemoService)jsonClient.proxy(DemoService.class.getName());
  }

  private void destroyJsonServerAndClient(){
    jsonClient.destroy();
    jsonServer.stop();
  }

  private void initProtostuffServerAndClient() throws ClassNotFoundException{
    protostuffServer = new AbstractRpcServer("127.0.0.1",3661);
    protostuffServer.setSerializer(SerializerManager.getSerializer(SerializerAlgorithm.KYRO));
    protostuffServer.bind(new DemoServiceImpl());
    protostuffServer.init();
    protostuffClient = new AbstractRpcClient("127.0.0.1",3661);
    protostuffClient.init();
    protostuffDemoService = (DemoService)protostuffClient.proxy(DemoService.class.getName());
  }

  private void destroyProtostuffServerAndClient(){
    protostuffClient.destroy();
    protostuffServer.stop();
  }
}
