package com.zhanghe.benchmark;

import com.zhanghe.rpc.RpcServer;

public class BenchMarkServer {

  public static void main(String[] args) {
    RpcServer rpcServer = new RpcServer(7777);
    rpcServer.bind(new BenchMarkServiceImpl());
    rpcServer.start();
  }

}
