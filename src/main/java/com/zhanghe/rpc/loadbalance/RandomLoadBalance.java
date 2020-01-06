package com.zhanghe.rpc.loadbalance;

import java.util.ArrayList;
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

}
