package com.zhanghe.test.testServer;

import com.zhanghe.rpc.core.client.BaseRpcClient;
import com.zhanghe.rpc.core.client.RpcContext;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.testServer.service.GracefulShutdownService;
import com.zhanghe.test.testServer.service.GracefulShutdownServiceImpl;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GracefulShutdownServerTest {

  private static final Logger logger = LoggerFactory.getLogger(GracefulShutdownServerTest.class);

  private BaseRpcServer rpcServer;

  private BaseRpcClient rpcClient;

  private GracefulShutdownService gracefulShutdownService;

  @Before
  public void init() {
    rpcServer = new BaseRpcServer(7777);
    rpcServer.init();
    rpcServer.bind(new GracefulShutdownServiceImpl());
  }

  @After
  public void destroy(){
    rpcClient.destroy();
  }

  @Test
  public void test() throws Exception{
    connect();
    concurrentCall();
  }

  public void connect() throws ClassNotFoundException{
    rpcClient = new BaseRpcClient("127.0.0.1",7777);
    rpcClient.init();
    gracefulShutdownService = (GracefulShutdownService) rpcClient.proxy(GracefulShutdownService.class.getName());
    Assert.assertNotNull(gracefulShutdownService);
  }

  public void concurrentCall(){
    gracefulShutdownService.initMethod();
    CountDownLatch countDownLatch = new CountDownLatch(6);
    CountDownLatch threadStartCountDown = new CountDownLatch(1);
    AtomicInteger atomicInteger = new AtomicInteger(0);
    for(int i = 0;i<6;i++){
      new Thread(new GracefulShutdownServerTest.ConcurrentThread(gracefulShutdownService,countDownLatch,threadStartCountDown,atomicInteger)).start();
    }
    try {
      Thread.sleep(3000);
    }catch (Exception e){

    }
    threadStartCountDown.countDown();
    //等待一段时间保证任务都已发送并开始执行
    try {
      Thread.sleep(500);
    }catch (Exception e){

    }
    //发送任务之后停止rpcserver
    rpcServer.stop();
    try {
      countDownLatch.await();
    }catch (Exception e){

    }
    Assert.assertEquals(atomicInteger.get(),6);
  }

  class ConcurrentThread implements Runnable{

    private GracefulShutdownService gracefulShutdownService;

    CountDownLatch countDownLatch;

    CountDownLatch threadStartCountDown;

    AtomicInteger atomicInteger;

    public ConcurrentThread(GracefulShutdownService gracefulShutdownService,CountDownLatch countDownLatch,CountDownLatch threadStartCountDown,AtomicInteger atomicInteger ) {
      this.gracefulShutdownService = gracefulShutdownService;
      this.countDownLatch = countDownLatch;
      this.atomicInteger = atomicInteger;
      this.threadStartCountDown = threadStartCountDown;
    }

    @Override
    public void run() {
      String str = "Random str:"+Math.random();
      try {
        threadStartCountDown.await();
      }catch (Exception e){

      }
      Random random = new Random(System.currentTimeMillis());
      Long waitTime = Long.valueOf(random.nextInt(1000));
      System.out.println("waitTime:" + waitTime);
      gracefulShutdownService.costSomeTimes(str,waitTime);
      Future future = RpcContext.getInstance().getFuture();
      try {
        String result = (String) future.get(waitTime + 3000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(str, result);
        atomicInteger.incrementAndGet();
        countDownLatch.countDown();
      }catch (ExecutionException e){
        e.printStackTrace();
        atomicInteger.incrementAndGet();
        countDownLatch.countDown();
      }catch (TimeoutException e){
        e.printStackTrace();
        atomicInteger.incrementAndGet();
        countDownLatch.countDown();
      }catch (InterruptedException e){
        e.printStackTrace();
        atomicInteger.incrementAndGet();
        countDownLatch.countDown();
      }
    }
  }

}
