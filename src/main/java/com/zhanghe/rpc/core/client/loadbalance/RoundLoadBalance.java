package com.zhanghe.rpc.core.client.loadbalance;

import com.zhanghe.rpc.core.client.RpcServerInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoundLoadBalance<T> implements LoadBalance {

  private static Logger logger = LoggerFactory.getLogger(RoundLoadBalance.class);

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
    logger.info("loadballance add service:{}",loadBalanceService.getName());
    lock.writeLock().lock();
    try {
      position.set(0);
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
      position.set(0);
    }finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public Integer serverSize() {
    return services.size();
  }
}
