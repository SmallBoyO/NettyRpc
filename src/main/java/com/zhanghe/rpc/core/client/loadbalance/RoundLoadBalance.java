package com.zhanghe.rpc.core.client.loadbalance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RoundLoadBalance<T> implements LoadBalance {

  private List<T> services;

  private AtomicInteger position;

  private ReentrantReadWriteLock lock;

  public RoundLoadBalance() {
    this.services = new ArrayList<>();
    this.position = new AtomicInteger(0);
    this.lock = new ReentrantReadWriteLock();
  }

  @Override
  public Object next() {
    lock.readLock().lock();
    try {
      Integer[] positions = getPosition();
      while(!position.compareAndSet(positions[0],positions[1]+1)){
        positions = getPosition();
      }
      return services.get(positions[1]);
    }finally {
      lock.readLock().unlock();
    }
  }

  Integer[] getPosition(){
    Integer nowPosition = position.get();
    Integer readPosition = nowPosition;
    if( readPosition >= services.size() ){
      readPosition = 0;
    }
    return new Integer[]{nowPosition,readPosition};
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
}
