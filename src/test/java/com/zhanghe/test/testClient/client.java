package com.zhanghe.test.testClient;

import com.zhanghe.rpc.RpcClient;

public class client {

    public static void main(String[] args) throws InterruptedException,ClassNotFoundException{
        RpcClient rpcClient= new RpcClient("127.0.0.1",7777);
        rpcClient.start();

//        TestService service = (TestService)rpcClien.proxy(TestService.class.getName());
//        System.out.println(service.hello());
//        DateService dateService = (DateService)rpcClient.proxy(DateService.class.getName());
//        TestService testService = (TestService)rpcClient.proxy(TestService.class.getName());
//        System.out.println(dateService.now());
    }

}
