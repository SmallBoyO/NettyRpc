package com.zhanghe.rpc.core.client.loadbalance;

import com.zhanghe.rpc.core.client.RpcServerInfo;

public interface LoadBalance {

  /**
   * 下一个获取到的服务
   * @return
   */
  RpcServerInfo next();

  /**
   * 添加新的服务
   * @param loadBalanceService
   */
  void addService(LoadBalanceService loadBalanceService);

  /**
   *  删除服务
   * @param ip
   * @param port
   */
  void removeService(String ip,Integer port);

  /**
   * 获取当前server总数量
   * @return
   */
  Integer serverSize();

}
