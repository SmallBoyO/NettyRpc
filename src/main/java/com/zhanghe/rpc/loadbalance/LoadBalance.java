package com.zhanghe.rpc.loadbalance;

public interface LoadBalance<T> {

  /**
   * 下一个获取到的服务
   * @return
   */
  T next();

  void addService(LoadBalanceService loadBalanceService);
}
