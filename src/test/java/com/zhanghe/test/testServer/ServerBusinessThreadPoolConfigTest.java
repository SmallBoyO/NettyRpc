package com.zhanghe.test.testServer;

import com.zhanghe.config.RpcServerConfig;
import com.zhanghe.rpc.core.server.BaseRpcServer;
import org.junit.Assert;
import org.junit.Test;

public class ServerBusinessThreadPoolConfigTest {

  @Test
  public void doTest(){
    RpcServerConfig rpcServerConfig = new RpcServerConfig("0.0.0.0",1111);
    rpcServerConfig.setBusinessLogicCoreThreadNum(6);
    rpcServerConfig.setBusinessLogicQueueLength(600);
    BaseRpcServer rpcServer = new BaseRpcServer(rpcServerConfig);
    rpcServer.init();
  }

}
