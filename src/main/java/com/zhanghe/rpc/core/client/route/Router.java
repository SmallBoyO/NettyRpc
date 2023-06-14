package com.zhanghe.rpc.core.client.route;

import com.zhanghe.rpc.core.client.RpcServerInfo;
import com.zhanghe.rpc.core.client.loadbalance.LoadBalance;
import com.zhanghe.rpc.core.client.loadbalance.LoadBalanceService;
import java.util.concurrent.ConcurrentHashMap;

public interface Router {


  void refreshRoute(String serviceName,RpcServerInfo rpcServerInfo);

  void refreshRoute(String serviceName,LoadBalanceService loadBalanceService);

  void removeRoute(String ip,Integer port);

  RpcServerInfo getService(String serviceName);

}
