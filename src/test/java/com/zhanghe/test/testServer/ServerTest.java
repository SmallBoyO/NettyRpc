package com.zhanghe.test.testServer;

import com.zhanghe.rpc.RpcServer;
import org.junit.Assert;
import org.junit.Test;

public class ServerTest {

  @Test
  public void doTest(){
    testService();
  }

  private void testService(){
    RpcServer rpcServer = new RpcServer("0.0.0.0",1111);
    try{
      rpcServer.start();
    }catch (Exception e){
      Assert.fail("Should not reach here");
    }

    RpcServer rpcServer2 = new RpcServer("0.0.0.0",1111);
    try{
      rpcServer2.start();
      Assert.fail("Should not reach here");
    }catch (Exception e){
    }


    try {
      rpcServer.stop();
    } catch (IllegalStateException e) {
      Assert.fail("Should not reach here");
    }

    try {
      rpcServer2.stop();
      Assert.fail("Should not reach here");
    } catch (IllegalStateException e) {
    }
  }

}
