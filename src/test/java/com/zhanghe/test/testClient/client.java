package com.zhanghe.test.testClient;

import com.zhanghe.rpc.RpcClient;

public class client {

    public static void main(String[] args) throws InterruptedException{
        RpcClient rpcClien= new RpcClient("127.0.0.1",7777);
        rpcClien.init();

    }

}
