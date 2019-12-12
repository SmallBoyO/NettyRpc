package com.zhanghe.rpc.loadbalance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLoadBalance<T> implements LoadBalance {

  ThreadLocalRandom random = ThreadLocalRandom.current();

  private List<T> services;

  public RandomLoadBalance() {
    this.services = new ArrayList<>();
  }

  @Override
  public Object next() {
    return services.get(random.nextInt(services.size()));
  }

  @Override
  public void addService(LoadBalanceService loadBalanceService) {
    services.add((T)loadBalanceService.getService());
  }

}
