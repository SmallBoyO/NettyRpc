package com.zhanghe.rpc.core.client;

import com.zhanghe.config.RpcClientConfig;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RpcServerInfo {

  public RpcClientConfig rpcClientConfig;

  public int weight;

  private AtomicBoolean useful;

  private Lock statusLock;

  private Condition statusCondition;

  private Set<String> services;

  private RpcClientConnector rpcClientConnector;

  public RpcServerInfo() {
    this.rpcClientConfig = new RpcClientConfig();
    useful = new AtomicBoolean(false);
    statusLock = new ReentrantLock();
    statusCondition = statusLock.newCondition();
  }

  public RpcServerInfo(String ip, int port) {
    this.rpcClientConfig = new RpcClientConfig(ip,port);
    useful = new AtomicBoolean(false);
    statusLock = new ReentrantLock();
    statusCondition = statusLock.newCondition();
  }

  public void waitServerUseful(){
    statusLock.lock();
    try {
      if(!useful.get()){
        //服务目前不可用,等待可用
        statusCondition.await();
      }
    }catch (Exception e){
        throw new IllegalStateException(e);
    }finally {
      statusLock.unlock();
    }
  }

  public void signalServerUseful(){
    statusLock.lock();
    try {
      statusCondition.signalAll();
    }catch (Exception e){
      throw new IllegalStateException(e);
    }finally {
      statusLock.unlock();
    }
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public String getAddressInfo(){
    return rpcClientConfig.getIp() + ":" + rpcClientConfig.getPort();
  }

  public Set<String> getServices() {
    return services;
  }

  public void setServices(Set<String> services) {
    this.services = services;
    this.useful.set(true);
    signalServerUseful();
  }

  public RpcClientConnector getRpcClientConnector() {
    return rpcClientConnector;
  }

  public void setRpcClientConnector(RpcClientConnector rpcClientConnector) {
    this.rpcClientConnector = rpcClientConnector;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RpcServerInfo that = (RpcServerInfo) o;
    return rpcClientConfig.getPort() == that.rpcClientConfig.getPort() &&
        weight == that.weight &&
        Objects.equals(rpcClientConfig.getIp(), that.rpcClientConfig.getIp());
  }

  public RpcClientConfig getRpcClientConfig() {
    return rpcClientConfig;
  }

  public void setRpcClientConfig(RpcClientConfig rpcClientConfig) {
    this.rpcClientConfig = rpcClientConfig;
  }
}
