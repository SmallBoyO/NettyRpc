package com.zhanghe.rpc.core.client.loadbalance;

import com.zhanghe.rpc.core.client.RpcServerInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WeightRandomLoadBalance<T> implements LoadBalance {

  ThreadLocalRandom random = ThreadLocalRandom.current();

  private List<LoadBalanceService> services;

  private ReentrantReadWriteLock lock;

  public WeightRandomLoadBalance() {
    this.services = new ArrayList<>();
    this.lock = new ReentrantReadWriteLock();
  }

  @Override
  public Object next() {
    lock.readLock().lock();
    try {
      int allWeight = services.stream().mapToInt(value -> value.getWeight()).sum();
      int randomNum = random.nextInt(allWeight);
      for (LoadBalanceService service : services) {
        if( randomNum < service.getWeight() ){
          return service.getService();
        }else{
          randomNum = randomNum - service.getWeight();
        }
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
    }finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public Integer serverSize() {
    return services.size();
  }

}
