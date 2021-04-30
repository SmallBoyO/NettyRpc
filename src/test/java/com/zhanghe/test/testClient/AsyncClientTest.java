package com.zhanghe.test.testClient;

import com.zhanghe.rpc.core.client.BaseRpcClient;
import com.zhanghe.rpc.core.client.RpcContext;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.testClient.service.AsyncService;
import com.zhanghe.test.testClient.service.AsyncServiceImpl;
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

public class AsyncClientTest {

  private BaseRpcServer rpcServer;

  private BaseRpcClient rpcClient;

  private AsyncService asyncService;

  @Before
  public void init() {
    rpcServer = new BaseRpcServer(7777);
    rpcServer.init();
    rpcServer.bind(new AsyncServiceImpl());
  }

  @After
  public void destroy(){
    rpcServer.stop();
    rpcClient.destroy();
  }

  @Test
  public void testConnectAndCall() throws ClassNotFoundException,InterruptedException,ExecutionException,TimeoutException{
    connect();
    concurrentCall();
  }

  public void connect() throws ClassNotFoundException{
    rpcClient = new BaseRpcClient("127.0.0.1",7777);
    rpcClient.init();
    asyncService = (AsyncService) rpcClient.proxy(AsyncService.class.getName());
    Assert.assertNotNull(asyncService);
  }

  public void concurrentCall(){
    CountDownLatch countDownLatch = new CountDownLatch(6);
    AtomicInteger atomicInteger = new AtomicInteger(0);
    for(int i = 0;i<6;i++){
      new Thread(new ConcurrentThread(asyncService,countDownLatch,atomicInteger)).start();
    }
    try {
      countDownLatch.await();
    }catch (Exception e){

    }
    Assert.assertEquals(atomicInteger.get(),6);
  }

  class ConcurrentThread implements Runnable{

    AsyncService asyncService;

    CountDownLatch countDownLatch;

    AtomicInteger atomicInteger;

    public ConcurrentThread(AsyncService asyncService,CountDownLatch countDownLatch,AtomicInteger atomicInteger ) {
      this.asyncService = asyncService;
      this.countDownLatch = countDownLatch;
      this.atomicInteger = atomicInteger;
    }

    @Override
    public void run() {
      String str = "Random str:"+Math.random();
      asyncService.waitTwoSeconds(str);
      Future future = RpcContext.getInstance().getFuture();
      try {
        String result = (String) future.get(10, TimeUnit.SECONDS);
        Assert.assertEquals(str, result);
        atomicInteger.incrementAndGet();
        countDownLatch.countDown();
      }catch (ExecutionException e){
        countDownLatch.countDown();
      }catch (TimeoutException e){
        countDownLatch.countDown();

      }catch (InterruptedException e){
        countDownLatch.countDown();
      }
    }
  }
}
