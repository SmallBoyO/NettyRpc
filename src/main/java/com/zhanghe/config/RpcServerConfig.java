package com.zhanghe.config;

public class RpcServerConfig {

  int port;

  String ip;

  public RpcServerConfig() {
    new RpcServerConfig(RpcConfig.DEFAULT_PORT,RpcConfig.DEFAULT_IP);
  }

  public RpcServerConfig(int setPort) {
    new RpcServerConfig(setPort,RpcConfig.DEFAULT_IP);
  }
  public RpcServerConfig(String setIp) {
    new RpcServerConfig(RpcConfig.DEFAULT_PORT,setIp);
  }

  public RpcServerConfig(int setPort,String setIp) {
    port = setPort;
    ip = RpcConfig.DEFAULT_IP;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }
}
