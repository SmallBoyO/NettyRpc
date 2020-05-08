package com.zhanghe.test.spring.configuration;

import com.zhanghe.spring.annotation.EnableLoadBalanceRpcClient;
import com.zhanghe.spring.annotation.RpcServerInfo;
import org.springframework.context.annotation.Configuration;

@EnableLoadBalanceRpcClient(
    rpcServers = {
        @RpcServerInfo(ip = "127.0.0.1",port = 6666,weight = 10),
        @RpcServerInfo(ip = "127.0.0.1",port = 6667)
    },
    loadBalance="weight_random",
    scanPacakges = "com.zhanghe.test")
@Configuration
public class EnableLoadBalanceRpcClientConfiguration {

}
