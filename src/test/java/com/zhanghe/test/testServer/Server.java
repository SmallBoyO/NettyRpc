package com.zhanghe.test.testServer;


import com.zhanghe.rpc.RpcServer;

public class Server {

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer(7777);
        rpcServer.start();
    }

}
