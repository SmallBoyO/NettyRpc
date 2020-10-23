package com.zhanghe.test.testClient.loadbalance;

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

public class RoundLoadBalanceClientTest {
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
    server1.stop();
    server2.stop();
    rpcClient.destroy();
  }

  @Test
  public void testLoadBalance() throws ClassNotFoundException{
    connect();
    call();
  }

  public void call(){
    Random random = new Random(System.currentTimeMillis());
    int total = random.nextInt(100000) * 2;
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
    Assert.assertTrue(server1Num == server2Num);
  }

  public void connect() throws ClassNotFoundException{
    RpcServerInfo rpcServerInfo1 = new RpcServerInfo();
    rpcServerInfo1.setIp("127.0.0.1");
    rpcServerInfo1.setWeight(10);
    rpcServerInfo1.setPort(7777);
    RpcServerInfo rpcServerInfo2 = new RpcServerInfo();
    rpcServerInfo2.setIp("127.0.0.1");
    rpcServerInfo2.setWeight(10);
    rpcServerInfo2.setPort(7778);
    rpcClient = new RpcLoadBalanceAdaptor();
    rpcClient.setLoadBalance("round");
    rpcClient.setServers(
        new ArrayList<RpcServerInfo>(){{
          add(rpcServerInfo1);
          add(rpcServerInfo2);
        }}
    );
    rpcClient.init();
    demoService = (DemoService) rpcClient.proxy(DemoService.class.getName());
    Assert.assertNotNull(demoService);
  }
}
