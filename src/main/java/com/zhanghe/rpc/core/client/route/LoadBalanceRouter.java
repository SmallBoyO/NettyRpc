package com.zhanghe.rpc.core.client.route;

import com.zhanghe.rpc.core.client.RpcServerInfo;
import com.zhanghe.rpc.core.client.loadbalance.LoadBalance;
import com.zhanghe.rpc.core.client.loadbalance.LoadBalanceService;
import com.zhanghe.rpc.core.client.loadbalance.RandomLoadBalance;
import com.zhanghe.rpc.core.client.loadbalance.RoundLoadBalance;
import com.zhanghe.rpc.core.client.loadbalance.WeightRandomLoadBalance;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadBalanceRouter implements Router {

  public static final Logger logger = LoggerFactory.getLogger(LoadBalanceRouter.class);

  private ConcurrentHashMap<String,LoadBalance<RpcServerInfo>> serverInfoConcurrentHashMap;

  private String loadbalanceType;

  private ReadWriteLock readWriteLock;

  public LoadBalanceRouter(String loadbalanceType) {
    this.loadbalanceType = loadbalanceType;
    serverInfoConcurrentHashMap = new ConcurrentHashMap<>();
    readWriteLock = new ReentrantReadWriteLock();
  }

  @Override
  public void refreshRoute(String serviceName, RpcServerInfo rpcServerInfo) {
  }

  @Override
  public void refreshRoute(String serviceName, LoadBalanceService loadBalanceService) {
    Lock writeLock = readWriteLock.writeLock();
    try{
      writeLock.lock();
      logger.info("LoadBalanceRouter refreshRoute:{}",serviceName);
      LoadBalance loadBalancer =  serverInfoConcurrentHashMap.get(serviceName);
      if(loadBalancer == null){
        switch (loadbalanceType){
          case "random":
            loadBalancer = new RandomLoadBalance<>();
            logger.info("Rpc load balance client use RandomLoadBalance");
            break;
          case "weight_random":
            loadBalancer = new WeightRandomLoadBalance<>();
            logger.info("Rpc load balance client use WeightRandomLoadBalance");
            break;
          case "round":
            loadBalancer = new RoundLoadBalance<>();
            logger.info("Rpc load balance client use RoundLoadBalance");
            break;
          default:
            loadBalancer = new RandomLoadBalance<>();
            logger.info("Rpc load balance client use default RandomLoadBalance");
            break;
        }
        loadBalancer.addService(loadBalanceService);
        serverInfoConcurrentHashMap.put(serviceName,loadBalancer);
      }else{
        loadBalancer.addService(loadBalanceService);
      }
    }finally {
      writeLock.unlock();
    }
  }

  @Override
  public void removeRoute(String ip,Integer port) {
    serverInfoConcurrentHashMap.forEach((serviceName, rpcServerInfoLoadBalance) -> {
      rpcServerInfoLoadBalance.removeService(ip,port);
    });
  }

  @Override
  public RpcServerInfo getService(String serviceName) {
    Lock readLock = readWriteLock.readLock();
    try {
      readLock.lock();
      if(serverInfoConcurrentHashMap.containsKey(serviceName)){
        return serverInfoConcurrentHashMap.get(serviceName).next();
      }else {
        return null;
      }
    }finally {
      readLock.unlock();
    }
  }
}
