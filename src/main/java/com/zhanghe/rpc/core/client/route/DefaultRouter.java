package com.zhanghe.rpc.core.client.route;

import com.zhanghe.rpc.core.client.RpcServerInfo;
import com.zhanghe.rpc.core.client.loadbalance.LoadBalanceService;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultRouter implements Router{

  private ConcurrentHashMap<String,RpcServerInfo> serverInfoConcurrentHashMap;

  public DefaultRouter() {
    this.serverInfoConcurrentHashMap = new ConcurrentHashMap<>();
  }

  @Override
  public void refreshRoute(String serviceName,RpcServerInfo rpcServerInfo) {
    serverInfoConcurrentHashMap.put(serviceName,rpcServerInfo);
  }

  @Override
  public RpcServerInfo getService(String serviceName) {
    return serverInfoConcurrentHashMap.get(serviceName);
  }

  @Override
  public void removeRoute(String ip, Integer port) {

  }

  @Override
  public void refreshRoute(String serviceName, LoadBalanceService loadBalanceService) {

  }
}
