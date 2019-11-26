package com.zhanghe.rpc;

import java.util.Objects;

public class RpcServerInfo {

  public String ip;

  public int port;

  public int weight;

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
