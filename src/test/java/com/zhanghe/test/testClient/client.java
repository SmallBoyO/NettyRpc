package com.zhanghe.test.testClient;

import com.zhanghe.rpc.RpcClient;
import com.zhanghe.service.TestService;

public class client {

    public static void main(String[] args) throws InterruptedException,ClassNotFoundException{
        RpcClient rpcClien= new RpcClient("127.0.0.1",7777);
        rpcClien.init();

        TestService service = (TestService)rpcClien.proxy(TestService.class.getName());
        service.hello();
    }

}
