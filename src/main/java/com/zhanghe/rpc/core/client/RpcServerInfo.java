package com.zhanghe.rpc.core.client;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RpcServerInfo {

  public String ip;

  public int port;

  public int weight;

  private AtomicBoolean useful;

  private Lock statusLock;

  private Condition statusCondition;

  private Set<String> services;

  private RpcClientConnector rpcClientConnector;

  public RpcServerInfo() {
    useful = new AtomicBoolean(false);
    statusLock = new ReentrantLock();
    statusCondition = statusLock.newCondition();
  }

  public RpcServerInfo(String ip, int port) {
    this.ip = ip;
    this.port = port;
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

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public String getAddressInfo(){
    return ip + ":" + port;
  }

  public Set<String> getServices() {
    return services;
  }

  public void setServices(Set<String> services) {
    this.services = services;
    this.useful.set(true);
    signalServerUseful();
  }

  public AtomicBoolean getUseful() {
    return useful;
  }

  public void setUseful(AtomicBoolean useful) {
    this.useful = useful;
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
    return port == that.port &&
        weight == that.weight &&
        Objects.equals(ip, that.ip);
  }
}
