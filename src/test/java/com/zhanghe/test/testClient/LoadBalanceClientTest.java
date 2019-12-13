package com.zhanghe.test.testClient;

import com.zhanghe.rpc.RpcLoadBalanceAdaptor;
import com.zhanghe.rpc.RpcServer;
import com.zhanghe.rpc.RpcServerInfo;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LoadBalanceClientTest {

  private RpcServer server1;

  private RpcServer server2;

  private RpcLoadBalanceAdaptor rpcClient;

  private DemoService demoService;

  @Before
  public void init() {
    server1 = new RpcServer(7777);
    server1.bind(new DemoServiceImpl());
    server1.start();
    server1.bind(new DemoServiceLoadBalanceImpl("127.0.0.1:7777"));
    server2 = new RpcServer(7778);
    server2.bind(new DemoServiceImpl());
    server2.start();
    server2.bind(new DemoServiceLoadBalanceImpl("127.0.0.1:8888"));
  }
  @After
  public void destroy(){
    server1.stop();
    server2.stop();
    rpcClient.destroy();
  }

  @Test
  public void testLoadBalance(){
    connect();
    call();
  }

  public void call(){
    demoService.call("call");
  }

  public void connect(){
    RpcServerInfo rpcServerInfo1 = new RpcServerInfo();
    rpcServerInfo1.setIp("127.0.0.1");
    rpcServerInfo1.setWeight(10);
    rpcServerInfo1.setPort(7777);
    RpcServerInfo rpcServerInfo2 = new RpcServerInfo();
    rpcServerInfo2.setIp("127.0.0.1");
    rpcServerInfo2.setWeight(10);
    rpcServerInfo2.setPort(7778);
    rpcClient = new RpcLoadBalanceAdaptor();
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
