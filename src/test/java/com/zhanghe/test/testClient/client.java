package com.zhanghe.test.testClient;

import com.zhanghe.benchmark.BenchMarkRequestDTO;
import com.zhanghe.benchmark.BenchMarkService;
import com.zhanghe.rpc.RpcClient;
import com.zhanghe.service.TestService;

public class client {

    public static void main(String[] args) throws InterruptedException,ClassNotFoundException{
        RpcClient rpcClient= new RpcClient("127.0.0.1",7777);
        rpcClient.start();

//        TestService service = (TestService)rpcClien.proxy(TestService.class.getName());
//        System.out.println(service.hello());
        BenchMarkService service = (BenchMarkService)rpcClient.proxy(BenchMarkService.class.getName());
        BenchMarkRequestDTO benchMarkRequestDTO = new BenchMarkRequestDTO();
        benchMarkRequestDTO.setRequestInfo("test ");
        System.out.println(service.call(benchMarkRequestDTO));
    }

}
