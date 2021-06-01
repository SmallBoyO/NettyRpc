package com.zhanghe.test.testServer;

import com.zhanghe.rpc.core.client.BaseRpcClient;
import com.zhanghe.test.testServer.service.GracefulShutdownService;
import org.junit.Assert;

public class GracefulShutdownByExitClientTest {

  public static void main(String[] args) throws Exception {
    BaseRpcClient rpcClient = new BaseRpcClient("127.0.0.1",7777);
    rpcClient.init();
    GracefulShutdownService gracefulShutdownService = (GracefulShutdownService) rpcClient.proxy(GracefulShutdownService.class.getName());
    Assert.assertNotNull(gracefulShutdownService);
    String str = "test";
    for(int i = 0;i<20;i++) {
      new Thread(()->{
        gracefulShutdownService.costSomeTimes2(20000L, str);
      }).start();
    }
  }

}
