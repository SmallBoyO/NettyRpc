package com.zhanghe.rpc.core.client.loadbalance;

import com.zhanghe.rpc.core.client.RpcServerInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SmoothWeightRoundLoadBalance <T> implements LoadBalance {

  private List<InsideLoadBalanceService> services;

  private ReentrantReadWriteLock lock;

  private AtomicInteger times;

  public SmoothWeightRoundLoadBalance() {
    this.lock = new ReentrantReadWriteLock();
    this.times = new AtomicInteger(0);
    this.services = new ArrayList<>();
  }

  @Override
  public Object next() {
    lock.readLock().lock();
    try {
      int allWeight = services.stream().mapToInt(value -> value.getWeight()).sum();
      InsideLoadBalanceService maxWeightService = null;
      for(InsideLoadBalanceService loadBalanceService:services){
        if( maxWeightService == null || maxWeightService.getConcurrentWeight() < loadBalanceService.getConcurrentWeight()){
          maxWeightService = loadBalanceService;
        }
      }
      maxWeightService.setConcurrentWeight(maxWeightService.getConcurrentWeight() - allWeight);
      for(InsideLoadBalanceService loadBalanceService:services){
        loadBalanceService.setConcurrentWeight(loadBalanceService.getWeight() + loadBalanceService.getConcurrentWeight());
      }
      return maxWeightService;
    }finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void addService(LoadBalanceService loadBalanceService) {
    lock.writeLock().lock();
    try {
      services.forEach((service)->{
        service.setConcurrentWeight(service.getWeight());
      });
      services.add(new InsideLoadBalanceService(loadBalanceService.getName(),loadBalanceService.getService(),loadBalanceService.getWeight()));
    }finally {
      lock.writeLock().unlock();
    }
  }

  class InsideLoadBalanceService extends LoadBalanceService{

    private int concurrentWeight;

    public InsideLoadBalanceService(String name, Object service,int weight) {
      super(name, service,weight);
      this.concurrentWeight = weight;
    }

    public int getConcurrentWeight() {
      return concurrentWeight;
    }

    public void setConcurrentWeight(int concurrentWeight) {
      this.concurrentWeight = concurrentWeight;
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
      services.forEach((service)->{
        service.setConcurrentWeight(service.getWeight());
      });
    }finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public Integer serverSize() {
    return services.size();
  }
}
