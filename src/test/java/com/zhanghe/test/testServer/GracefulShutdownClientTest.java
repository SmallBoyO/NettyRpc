package com.zhanghe.test.testServer;

import com.zhanghe.rpc.core.client.BaseRpcClient;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.spring.annotation.RpcClient;
import com.zhanghe.spring.annotation.RpcService;
import com.zhanghe.test.testServer.service.GracefulShutdownService;
import com.zhanghe.test.testServer.service.GracefulShutdownServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class GracefulShutdownClientTest {

  private BaseRpcServer rpcServer;

  private BaseRpcClient rpcClient;

  private GracefulShutdownService gracefulShutdownService;

  StartServerThread startServerThread;

  @Before
  public void init(){
    rpcServer = new BaseRpcServer(7777);
    rpcServer.bind(new GracefulShutdownServiceImpl());
    startServerThread = new StartServerThread(rpcServer);
    new Thread(startServerThread).start();
  }

  @After
  public void destroy(){
    startServerThread.destroyRpcServer();
  }

  @Test
  public void test() throws Exception{
    connect();
    concurrentCall();
  }

  public void concurrentCall(){
    CountDownLatch countDownLatch = new CountDownLatch(1);
    String str = "test";
    for(int i = 0;i<6;i++) {
      new Thread(()->{
        try{
          countDownLatch.await();
        }catch (Exception e){
          e.printStackTrace();
        }
        System.out.println(gracefulShutdownService.costSomeTimes2(20*1000L,str));
      }).start();
    }
    try {
      Thread.sleep(3000);
    }catch (Exception e){

    }
    countDownLatch.countDown();
    //等待一段时间保证任务都已发送并开始执行
    try {
      Thread.sleep(1000);
    }catch (Exception e){

    }
    rpcClient.destroy();
  }

  public void connect() throws ClassNotFoundException{
    rpcClient = new BaseRpcClient("127.0.0.1",7777);
    rpcClient.init();
    gracefulShutdownService = (GracefulShutdownService) rpcClient.proxy(GracefulShutdownService.class.getName());
    Assert.assertNotNull(gracefulShutdownService);
  }

  class StartServerThread implements Runnable{

    BaseRpcServer baseRpcServer;

    public StartServerThread(BaseRpcServer baseRpcServer) {
      this.baseRpcServer = baseRpcServer;
    }

    @Override
    public void run() {
      baseRpcServer.init();
    }

    public void destroyRpcServer(){
      baseRpcServer.destroy();
    }
  }
}
