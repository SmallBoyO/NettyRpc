package com.zhanghe.rpc.loadbalance;

public class LoadBalanceService {

  public String name;

  public Object service;

  public int weight;

  public LoadBalanceService(String name, Object service) {
    this.name = name;
    this.service = service;
  }
  public LoadBalanceService(String name, Object service,int weight) {
    this.name = name;
    this.service = service;
    this.weight = weight;
  }

  public static LoadBalanceService of(String name,Object service){
    return new LoadBalanceService(name, service);
  }

  public static LoadBalanceService of(String name,Object service,int weight){
    return new LoadBalanceService(name, service, weight);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getService() {
    return service;
  }

  public void setService(Object service) {
    this.service = service;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }
}
