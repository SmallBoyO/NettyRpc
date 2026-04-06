package com.zhanghe.rpc.core.client.loadbalance;

import com.zhanghe.rpc.core.client.RpcServerInfo;

public class LoadBalanceService {

  public String name;

  public RpcServerInfo service;

  public int weight;

  public LoadBalanceService(String name, RpcServerInfo service) {
    this.name = name;
    this.service = service;
  }
  public LoadBalanceService(String name, RpcServerInfo service,int weight) {
    this.name = name;
    this.service = service;
    this.weight = weight;
  }

  public static LoadBalanceService of(String name,RpcServerInfo service){
    return new LoadBalanceService(name, service);
  }

  public static LoadBalanceService of(String name,RpcServerInfo service,int weight){
    return new LoadBalanceService(name, service, weight);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RpcServerInfo getService() {
    return service;
  }

  public void setService(RpcServerInfo service) {
    this.service = service;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }
}
