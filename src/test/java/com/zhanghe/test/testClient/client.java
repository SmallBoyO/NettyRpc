package com.zhanghe.test.testClient;

import com.zhanghe.rpc.RpcClient;
import com.zhanghe.rpc.RpcLoadBalanceAdaptor;
import com.zhanghe.rpc.RpcServer;
import com.zhanghe.rpc.RpcServerInfo;
import com.zhanghe.service.DateService;
import com.zhanghe.service.DateServiceImpl;
import com.zhanghe.service.TestService;
import java.util.ArrayList;
import java.util.List;

public class client {

    public static void main(String[] args) throws InterruptedException,ClassNotFoundException{
//        RpcClientConnector rpcClient= new RpcClientConnector("127.0.0.1",7777);
//        rpcClient.start();

//        TestService service = (TestService)rpcClien.proxy(TestService.class.getName());
//        System.out.println(service.hello());
//        DateService dateService = (DateService)rpcClient.proxy(DateService.class.getName());
//        TestService testService = (TestService)rpcClient.proxy(TestService.class.getName());
//        System.out.println(dateService.now());
//        RpcServer rpcServer1 = new RpcServer("127.0.0.10",7777);
//        RpcServer rpcServer2 = new RpcServer("127.0.0.10",7778);
//        DateServiceImpl dateService = new DateServiceImpl();
//        rpcServer1.bind(dateService);
//        rpcServer2.bind(dateService);
//        rpcServer1.start();
//        rpcServer2.start();
        RpcLoadBalanceAdaptor rpcLoadBalanceAdaptor = new RpcLoadBalanceAdaptor();
        RpcServerInfo rpcServerInfo = new RpcServerInfo();
        rpcServerInfo.setIp("127.0.0.1");
        rpcServerInfo.setPort(7777);
        rpcServerInfo.setWeight(12);
        RpcServerInfo rpcServerInfo2 = new RpcServerInfo();
        rpcServerInfo2.setIp("127.0.0.1");
        rpcServerInfo2.setPort(7778);
        rpcServerInfo2.setWeight(12);
        List<RpcServerInfo> rpcServerInfos = new ArrayList<>();
        rpcServerInfos.add(rpcServerInfo);
        rpcServerInfos.add(rpcServerInfo2);
        rpcLoadBalanceAdaptor.setServers(rpcServerInfos);
        rpcLoadBalanceAdaptor.init();

        DateService dateServiceprox = (DateService) rpcLoadBalanceAdaptor.proxy(DateService.class.getName());
        System.out.println("-----------------------");
        for(int i =0;i<1000;i++) {
            System.out.println(dateServiceprox.now());
        }
        Thread.sleep(100000);
        rpcLoadBalanceAdaptor.destroy();
//        RpcClient rpcClient = new RpcClient("127.0.0.1",7777);
//        rpcClient.init();
    }

}
