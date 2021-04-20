package com.zhanghe.test.testClient;

import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.test.testClient.service.AsyncServiceImpl;

public class Test {

  public static void main(String[] args) {
    BaseRpcServer rpcServer = new BaseRpcServer(7777);
    rpcServer.init();
    rpcServer.bind(new AsyncServiceImpl());
  }

}
