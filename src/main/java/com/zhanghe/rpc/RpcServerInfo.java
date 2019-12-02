package com.zhanghe.rpc;

import java.util.List;
import java.util.Objects;
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

  List<String> services;

  public RpcServerInfo() {
    useful = new AtomicBoolean(false);
    statusLock = new ReentrantLock();
    statusCondition = statusLock.newCondition();
  }

  public void waitServerUserful(){
    statusLock.lock();
    try {


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

  public List<String> getServices() {
    return services;
  }

  public void setServices(List<String> services) {
    this.services = services;
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
