package com.zhanghe.rpc.core.client.loadbalance;

import com.zhanghe.rpc.core.client.RpcServerInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RandomLoadBalance<T> implements LoadBalance {

  ThreadLocalRandom random = ThreadLocalRandom.current();

  private List<T> services;

  private ReentrantReadWriteLock lock;

  public RandomLoadBalance() {
    this.services = new ArrayList<>();
    this.lock = new ReentrantReadWriteLock();
  }

  @Override
  public Object next() {
    lock.readLock().lock();
    try {
      return services.get(random.nextInt(services.size()));
    }finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void addService(LoadBalanceService loadBalanceService) {
    lock.writeLock().lock();
    try {
      services.add((T) loadBalanceService.getService());
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
    }finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public Integer serverSize() {
    return services.size();
  }
}
