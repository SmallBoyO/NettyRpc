package com.zhanghe.test.testClient.loadbalance;

import com.zhanghe.config.RpcClientConfig;
import com.zhanghe.rpc.core.client.RpcLoadBalanceAdaptor;
import com.zhanghe.rpc.core.client.RpcServerInfo;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.testClient.service.AnotherService;
import com.zhanghe.test.testClient.service.AnotherServiceImpl;
import com.zhanghe.test.testClient.service.DemoService;
import com.zhanghe.test.testClient.service.DemoServiceLoadBalanceImpl;
import java.util.ArrayList;
import java.util.Random;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServerWithDifferentServiceTest {
  private BaseRpcServer server1;

  private BaseRpcServer server2;

  private RpcLoadBalanceAdaptor rpcClient;

  private DemoService demoService;

  private AnotherService anotherService;

  private DemoServiceLoadBalanceImpl demoService1;

  private DemoServiceLoadBalanceImpl demoService2;

  private AnotherServiceImpl anotherServiceImpl;

  @Before
  public void init() {
    server1 = new BaseRpcServer(7777);
    demoService1 = new DemoServiceLoadBalanceImpl("server1");
    server1.init();
    server1.bind(demoService1);
    server2 = new BaseRpcServer(7778);
    server2.init();
    demoService2 = new DemoServiceLoadBalanceImpl("server2");
    server2.bind(demoService2);
    anotherServiceImpl = new AnotherServiceImpl();
    server2.bind(anotherServiceImpl);
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
    Random random = new Random();
    int total = random.nextInt(1000);
    for(int i = 0;i<total * 2;i ++){
      demoService.call("param");
    }

    for(int i = 0;i<total * 2;i ++){
      try {
        anotherService.call("param");
        anotherService.call("param");
      }catch (Exception e){
        e.printStackTrace();
      }
    }
  }

  public void connect() throws ClassNotFoundException{
    RpcServerInfo rpcServerInfo1 = new RpcServerInfo();
    rpcServerInfo1.setRpcClientConfig(new RpcClientConfig("127.0.0.1",7777));
    rpcServerInfo1.setWeight(10);
    RpcServerInfo rpcServerInfo2 = new RpcServerInfo();
    rpcServerInfo2.setRpcClientConfig(new RpcClientConfig("127.0.0.1",7778));
    rpcServerInfo2.setWeight(10);
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
    anotherService = (AnotherService) rpcClient.proxy(AnotherService.class.getName());
    Assert.assertNotNull(demoService);
    Assert.assertNotNull(anotherService);
  }

}
