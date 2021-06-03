package com.zhanghe.test.testServer;

import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.testServer.service.GracefulShutdownServiceImpl;

public class GracefulShutdownByExitServerTest {

  public static void main(String[] args) {
    BaseRpcServer rpcServer = new BaseRpcServer(7777);
    rpcServer.init();
    rpcServer.bind(new GracefulShutdownServiceImpl());
  }

}
