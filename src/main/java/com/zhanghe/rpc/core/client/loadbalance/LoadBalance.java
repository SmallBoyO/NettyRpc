package com.zhanghe.rpc.core.client.loadbalance;

public interface LoadBalance<T> {

  /**
   * 下一个获取到的服务
   * @return
   */
  T next();

  /**
   * 添加新的服务
   * @param loadBalanceService
   */
  void addService(LoadBalanceService loadBalanceService);

}
