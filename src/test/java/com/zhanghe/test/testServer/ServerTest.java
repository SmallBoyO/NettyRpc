package com.zhanghe.test.testServer;

import com.zhanghe.rpc.core.server.BaseRpcServer;
import org.junit.Assert;
import org.junit.Test;

public class ServerTest {

  @Test
  public void doTest(){
    testService();
  }

  private void testService(){
    BaseRpcServer rpcServer = new BaseRpcServer("0.0.0.0",1111);
    try{
      rpcServer.init();
    }catch (Exception e){
      e.printStackTrace();
      Assert.fail("Should not reach here");
    }

    BaseRpcServer rpcServer2 = new BaseRpcServer("0.0.0.0",1111);
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
