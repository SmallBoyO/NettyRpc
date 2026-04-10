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
import java.util.Arrays;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class LoadBalanceProxyServiceDiscoveryTimeoutTest {

  private BaseRpcServer server1;

  private BaseRpcServer server2;

  private RpcLoadBalanceAdaptor rpcClient;

  @After
  public void destroy() {
    if (rpcClient != null && rpcClient.isStarted()) {
      rpcClient.destroy();
    }
    if (server1 != null) {
      server1.stop();
    }
    if (server2 != null) {
      server2.stop();
    }
  }

  @Test(timeout = 8000)
  public void proxyShouldSucceedWhenAnyConfiguredNodeCanRouteTargetService()
      throws ClassNotFoundException {
    server1 = new BaseRpcServer(7787);
    server1.init();
    server1.bind(new DemoServiceLoadBalanceImpl("server1"));

    rpcClient = newClient(2000L, serverInfo(7787), serverInfo(7788));
    rpcClient.init();

    DemoService demoService = (DemoService) rpcClient.proxy(DemoService.class.getName());
    Assert.assertNotNull(demoService);
    Assert.assertTrue(demoService.call("call").startsWith("server1"));
  }

  @Test(timeout = 5000)
  public void proxyShouldTimeoutWhenTargetServiceIsNeverDiscovered() {
    rpcClient = newClient(500L, serverInfo(7789), serverInfo(7790));
    rpcClient.init();

    try {
      rpcClient.proxy(DemoService.class.getName());
      Assert.fail("proxy should time out when no backend exposes target service");
    } catch (ClassNotFoundException e) {
      Assert.fail("DemoService class should exist");
    } catch (IllegalStateException e) {
      Assert.assertTrue(e.getMessage().contains(DemoService.class.getName()));
      Assert.assertTrue(e.getMessage().contains("500"));
    }
  }

  @Test(timeout = 8000)
  public void proxyShouldWaitForTargetServiceInsteadOfAllServers()
      throws ClassNotFoundException {
    server1 = new BaseRpcServer(7791);
    server1.init();
    server1.bind(new AnotherServiceImpl());

    rpcClient = newClient(2000L, serverInfo(7792), serverInfo(7791));
    rpcClient.init();

    AnotherService anotherService = (AnotherService) rpcClient.proxy(AnotherService.class.getName());
    Assert.assertNotNull(anotherService);
    Assert.assertEquals("param", anotherService.call("param"));
  }

  private RpcLoadBalanceAdaptor newClient(long serviceDiscoveryTimeoutMillis,
      RpcServerInfo... rpcServerInfos) {
    RpcLoadBalanceAdaptor client = new RpcLoadBalanceAdaptor();
    client.setLoadBalance("round");
    client.setServiceDiscoveryTimeoutMillis(serviceDiscoveryTimeoutMillis);
    client.setServers(new ArrayList<>(Arrays.asList(rpcServerInfos)));
    return client;
  }

  private RpcServerInfo serverInfo(int port) {
    RpcServerInfo rpcServerInfo = new RpcServerInfo();
    rpcServerInfo.setRpcClientConfig(new RpcClientConfig("127.0.0.1", port));
    rpcServerInfo.setWeight(10);
    return rpcServerInfo;
  }
}
