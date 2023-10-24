package com.zhanghe.test.testClient.loadbalance;

import com.zhanghe.config.RpcClientConfig;
import com.zhanghe.rpc.core.client.RpcLoadBalanceAdaptor;
import com.zhanghe.rpc.core.client.RpcServerInfo;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.testClient.service.DemoService;
import com.zhanghe.test.testClient.service.DemoServiceLoadBalanceImpl;
import java.util.ArrayList;
import java.util.Random;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DynamicServerLoadBalanceTest {

  private BaseRpcServer server1;

  private BaseRpcServer server2;

  private RpcLoadBalanceAdaptor rpcClient;

  private DemoService demoService;

  private DemoServiceLoadBalanceImpl demoService1;

  private DemoServiceLoadBalanceImpl demoService2;

  @Before
  public void init() {
    server1 = new BaseRpcServer(7777);
    demoService1 = new DemoServiceLoadBalanceImpl("server1");
    server1.init();
    server1.bind(demoService1);
    server2 = new BaseRpcServer(7778);
    demoService2 = new DemoServiceLoadBalanceImpl("server2");
    server2.init();
    server2.bind(demoService2);
  }
  @After
  public void destroy(){
    rpcClient.destroy();
    server1.stop();
    server2.stop();
  }

  @Test
  public void testLoadBalance() throws ClassNotFoundException{
    connect();
    call();
  }

  public void call(){

      Random random = new Random(System.currentTimeMillis());
      int total = random.nextInt(10000) * 2;
      int server1Num = 0;
      int server2Num = 0;
      for(int i = 0; i < total ;i++){
        String result = demoService.call("call");
        if (result.startsWith("server1")){
          server1Num++;
        }
        if (result.startsWith("server2")){
          server2Num++;
        }
      }
      Assert.assertTrue(server2Num == 0);
      Assert.assertTrue(server1Num == total);
      server1Num = 0;
      server2Num = 0;
      // 添加另一个server
      System.out.println("add a new Server");
      RpcServerInfo rpcServerInfo2 = new RpcServerInfo();
      rpcServerInfo2.setRpcClientConfig(new RpcClientConfig("127.0.0.1",7778));
      rpcServerInfo2.setWeight(10);
      rpcClient.addServer(rpcServerInfo2);
      //等待第二个server连接成功
      rpcServerInfo2.waitServerUseful();
      for(int i = 0; i < total ;i++){
        String result = demoService.call("call");
        if (result.startsWith("server1")){
          server1Num++;
        }
        if (result.startsWith("server2")){
          server2Num++;
        }
      }
      Assert.assertTrue(server1Num == server2Num);
      //删除一个server
      rpcClient.removeServer("127.0.0.1",7778);
      server1Num = 0;
      server2Num = 0;
      for(int i = 0; i < total ;i++){
        String result = demoService.call("call");
        if (result.startsWith("server1")){
          server1Num++;
        }
        if (result.startsWith("server2")){
          server2Num++;
        }
      }
      Assert.assertTrue(server2Num == 0);
      Assert.assertTrue(server1Num == total);
  }

  public void connect() throws ClassNotFoundException{
    RpcServerInfo rpcServerInfo1 = new RpcServerInfo();
    rpcServerInfo1.setRpcClientConfig(new RpcClientConfig("127.0.0.1",7777));
    rpcServerInfo1.setWeight(10);
    rpcClient = new RpcLoadBalanceAdaptor();
    rpcClient.setLoadBalance("round");
    rpcClient.setServers(
        new ArrayList<RpcServerInfo>(){{
          add(rpcServerInfo1);
        }}
    );
    rpcClient.init();
    demoService = (DemoService) rpcClient.proxy(DemoService.class.getName());
    Assert.assertNotNull(demoService);
  }
}
