package com.zhanghe.test.testServer;


import com.zhanghe.rpc.RpcServer;
import com.zhanghe.service.DateServiceImpl;

public class Server {

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer(7778);
        rpcServer.bind(new DateServiceImpl());
        Byte request = 7;
        Byte response = 8;
        rpcServer.start();
    }

}
