package com.zhanghe.test.testClient;

import com.zhanghe.rpc.RpcLoadBalanceAdaptor;
import com.zhanghe.rpc.RpcServerInfo;
import com.zhanghe.service.DateService;
import java.util.ArrayList;
import java.util.List;

public class client {

    public static void main(String[] args) throws InterruptedException,ClassNotFoundException{
//        RpcClient rpcClient= new RpcClient("127.0.0.1",7777);
//        rpcClient.start();

//        TestService service = (TestService)rpcClien.proxy(TestService.class.getName());
//        System.out.println(service.hello());
//        DateService dateService = (DateService)rpcClient.proxy(DateService.class.getName());
//        TestService testService = (TestService)rpcClient.proxy(TestService.class.getName());
//        System.out.println(dateService.now());
        RpcLoadBalanceAdaptor rpcLoadBalanceAdaptor = new RpcLoadBalanceAdaptor();
        RpcServerInfo rpcServerInfo = new RpcServerInfo();
        rpcServerInfo.setIp("127.0.0.1");
        rpcServerInfo.setPort(7777);
        rpcServerInfo.setWeight(12);
        RpcServerInfo rpcServerInfo2 = new RpcServerInfo();
        rpcServerInfo2.setIp("127.0.0.1");
        rpcServerInfo2.setPort(7777);
        rpcServerInfo2.setWeight(12);
        List<RpcServerInfo> rpcServerInfos = new ArrayList<>();
        rpcServerInfos.add(rpcServerInfo);
        rpcServerInfos.add(rpcServerInfo2);
        rpcLoadBalanceAdaptor.setServers(rpcServerInfos);
        rpcLoadBalanceAdaptor.init();

        DateService dateService = (DateService) rpcLoadBalanceAdaptor.proxy(DateService.class.getName());
        System.out.println(dateService.now());
        rpcLoadBalanceAdaptor.destroy();
    }

}
