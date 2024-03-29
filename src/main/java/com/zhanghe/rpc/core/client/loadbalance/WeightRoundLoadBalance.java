package com.zhanghe.rpc.core.client.loadbalance;

import com.zhanghe.rpc.core.client.RpcServerInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WeightRoundLoadBalance<T> implements LoadBalance {

  private List<LoadBalanceService> services;

  private ReentrantReadWriteLock lock;

  private AtomicInteger times;

  public WeightRoundLoadBalance() {
    this.lock = new ReentrantReadWriteLock();
    this.times = new AtomicInteger(0);
    this.services = new ArrayList<>();
  }

  @Override
  public Object next() {
    lock.readLock().lock();
    try {
      int allWeight = services.stream().mapToInt(value -> value.getWeight()).sum();
      int num = times.getAndAdd(1) % allWeight;
      for(LoadBalanceService service:services){
        if( num < service.getWeight() ){
          return service;
        }
        num = num - service.getWeight();
      }
      return null;
    }finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void addService(LoadBalanceService loadBalanceService) {
    lock.writeLock().lock();
    try {
      times.set(0);
      services.add(loadBalanceService);
    }finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void removeService(String ip,Integer port) {
    lock.writeLock().lock();
    try {
      Iterator it = services.iterator();
      while(it.hasNext()){
        RpcServerInfo rpcServerInfo = (RpcServerInfo)it.next();
        if(rpcServerInfo.getRpcClientConfig().getIp().equals(ip) &&  rpcServerInfo.getRpcClientConfig().getPort() == port){
          it.remove();
          break;
        }
      }
      times.set(0);
    }finally {
      lock.writeLock().unlock();
    }
  }
  @Override
  public Integer serverSize() {
    return services.size();
  }
}
