package com.zhanghe.test.testClient;

import com.zhanghe.config.RpcClientConfig;
import com.zhanghe.rpc.core.client.BaseRpcClient;
import com.zhanghe.rpc.core.client.RpcLoadBalanceAdaptor;
import com.zhanghe.rpc.core.client.RpcServerInfo;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.testServer.service.GracefulShutdownService;
import com.zhanghe.test.testServer.service.GracefulShutdownServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class ClientCallbackIsolationTest {

  private static final long LONG_RUNNING_REQUEST_MILLIS = 5000L;

  private BaseRpcServer rpcServer;

  private BaseRpcClient baseClientA;

  private BaseRpcClient baseClientB;

  private RpcLoadBalanceAdaptor loadBalanceClientA;

  private RpcLoadBalanceAdaptor loadBalanceClientB;

  @After
  public void destroy() {
    destroyBaseClient(baseClientA);
    destroyBaseClient(baseClientB);
    destroyLoadBalanceClient(loadBalanceClientA);
    destroyLoadBalanceClient(loadBalanceClientB);
    stopServer(rpcServer);
  }

  @Test(timeout = 15000)
  public void baseClientDestroyShouldNotWaitOtherClientRequests() throws Exception {
    rpcServer = new BaseRpcServer(7801);
    rpcServer.init();
    rpcServer.bind(new GracefulShutdownServiceImpl());

    baseClientA = new BaseRpcClient("127.0.0.1", 7801);
    baseClientB = new BaseRpcClient("127.0.0.1", 7801);
    baseClientA.init();
    baseClientB.init();

    GracefulShutdownService serviceA = (GracefulShutdownService) baseClientA.proxy(
        GracefulShutdownService.class.getName());
    GracefulShutdownService serviceB = (GracefulShutdownService) baseClientB.proxy(
        GracefulShutdownService.class.getName());
    Assert.assertNotNull(serviceA);
    Assert.assertNotNull(serviceB);

    AtomicReference<Throwable> callFailure = new AtomicReference<>();
    AtomicBoolean requestFinished = new AtomicBoolean(false);
    Thread requestThread = new Thread(() -> {
      try {
        serviceB.costSomeTimes2(LONG_RUNNING_REQUEST_MILLIS, "clientB");
      } catch (Throwable throwable) {
        callFailure.set(throwable);
      } finally {
        requestFinished.set(true);
      }
    });
    requestThread.start();
    Thread.sleep(300L);

    baseClientA.destroy();
    baseClientA = null;
    Assert.assertFalse("destroy should not wait other client's requests", requestFinished.get());

    requestThread.join(LONG_RUNNING_REQUEST_MILLIS + 5000L);
    Assert.assertFalse(requestThread.isAlive());
    Assert.assertNull(callFailure.get());
  }

  @Test(timeout = 15000)
  public void baseClientDestroyShouldWaitOwnRequests() throws Exception {
    rpcServer = new BaseRpcServer(7802);
    rpcServer.init();
    rpcServer.bind(new GracefulShutdownServiceImpl());

    baseClientB = new BaseRpcClient("127.0.0.1", 7802);
    baseClientB.init();

    GracefulShutdownService serviceB = (GracefulShutdownService) baseClientB.proxy(
        GracefulShutdownService.class.getName());
    Assert.assertNotNull(serviceB);

    AtomicReference<Throwable> callFailure = new AtomicReference<>();
    AtomicBoolean requestFinished = new AtomicBoolean(false);
    Thread requestThread = new Thread(() -> {
      try {
        serviceB.costSomeTimes2(LONG_RUNNING_REQUEST_MILLIS, "clientB");
      } catch (Throwable throwable) {
        callFailure.set(throwable);
      } finally {
        requestFinished.set(true);
      }
    });
    requestThread.start();
    Thread.sleep(300L);

    baseClientB.destroy();
    baseClientB = null;
    Assert.assertTrue("destroy should wait own requests", requestFinished.get());

    requestThread.join(LONG_RUNNING_REQUEST_MILLIS + 5000L);
    Assert.assertFalse(requestThread.isAlive());
    Assert.assertNull(callFailure.get());
  }

  @Test(timeout = 15000)
  public void loadBalanceDestroyShouldIsolateInstancesAndWaitOwnRequests() throws Exception {
    rpcServer = new BaseRpcServer(7803);
    rpcServer.init();
    rpcServer.bind(new GracefulShutdownServiceImpl());

    loadBalanceClientA = newLoadBalanceClient(7803);
    loadBalanceClientB = newLoadBalanceClient(7803);
    loadBalanceClientA.init();
    loadBalanceClientB.init();

    GracefulShutdownService serviceA = (GracefulShutdownService) loadBalanceClientA.proxy(
        GracefulShutdownService.class.getName());
    GracefulShutdownService serviceB = (GracefulShutdownService) loadBalanceClientB.proxy(
        GracefulShutdownService.class.getName());
    Assert.assertNotNull(serviceA);
    Assert.assertNotNull(serviceB);

    AtomicReference<Throwable> callFailure = new AtomicReference<>();
    AtomicBoolean requestFinished = new AtomicBoolean(false);
    Thread requestThread = new Thread(() -> {
      try {
        serviceB.costSomeTimes2(LONG_RUNNING_REQUEST_MILLIS, "loadBalanceClientB");
      } catch (Throwable throwable) {
        callFailure.set(throwable);
      } finally {
        requestFinished.set(true);
      }
    });
    requestThread.start();
    Thread.sleep(300L);

    loadBalanceClientA.destroy();
    loadBalanceClientA = null;
    Assert.assertFalse("destroy should not wait other load-balance client's requests",
        requestFinished.get());

    loadBalanceClientB.destroy();
    loadBalanceClientB = null;
    Assert.assertTrue("destroy should wait own load-balance requests", requestFinished.get());

    requestThread.join(LONG_RUNNING_REQUEST_MILLIS + 5000L);
    Assert.assertFalse(requestThread.isAlive());
    Assert.assertNull(callFailure.get());
  }

  private RpcLoadBalanceAdaptor newLoadBalanceClient(int port) {
    RpcServerInfo rpcServerInfo = new RpcServerInfo();
    rpcServerInfo.setRpcClientConfig(new RpcClientConfig("127.0.0.1", port));
    rpcServerInfo.setWeight(1);

    RpcLoadBalanceAdaptor rpcLoadBalanceAdaptor = new RpcLoadBalanceAdaptor();
    rpcLoadBalanceAdaptor.setLoadBalance("round");
    rpcLoadBalanceAdaptor.setServers(new ArrayList<>(Arrays.asList(rpcServerInfo)));
    rpcLoadBalanceAdaptor.setServiceDiscoveryTimeoutMillis(3000L);
    return rpcLoadBalanceAdaptor;
  }

  private void destroyBaseClient(BaseRpcClient rpcClient) {
    if (rpcClient != null && rpcClient.isStarted()) {
      rpcClient.destroy();
    }
  }

  private void destroyLoadBalanceClient(RpcLoadBalanceAdaptor rpcClient) {
    if (rpcClient != null && rpcClient.isStarted()) {
      rpcClient.destroy();
    }
  }

  private void stopServer(BaseRpcServer baseRpcServer) {
    if (baseRpcServer != null) {
      try {
        baseRpcServer.stop();
      } catch (Exception ignoredException) {
      }
    }
  }
}
