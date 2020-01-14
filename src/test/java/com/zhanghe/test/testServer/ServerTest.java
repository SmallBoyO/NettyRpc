package com.zhanghe.test.testServer;

import com.zhanghe.rpc.core.server.AbstractRpcServer;
import org.junit.Assert;
import org.junit.Test;

public class ServerTest {

  @Test
  public void doTest(){
    testService();
  }

  private void testService(){
    AbstractRpcServer rpcServer = new AbstractRpcServer("0.0.0.0",1111);
    try{
      rpcServer.init();
    }catch (Exception e){
      e.printStackTrace();
      Assert.fail("Should not reach here");
    }

    AbstractRpcServer rpcServer2 = new AbstractRpcServer("0.0.0.0",1111);
    try{
      rpcServer2.init();
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
