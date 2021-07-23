package com.zhanghe.config;

public class RpcClientConfig {

  public static final int DEFAILT_MAGIC_NUM = 0xfade;

  public static final int DEFAULT_PORT = 7777;

  public static final String DEFAULT_IP = "0.0.0.0";

  public static final int DEFAULT_BUSINESS_CORE_THREAD_NUM= 4;

  public static final int DEFAULT_BUSINESS_QUEUE_LENGTH = 1000;

  public String ip;

  public int port;

  public RpcClientConfig() {
  }

  public RpcClientConfig(int port) {
    this.port = port;
  }

  public RpcClientConfig(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  public String getIp() {
    if(!"".equals(ip) && ip != null) {
      return ip;
    }else{
      return DEFAULT_IP;
    }
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getPort() {
    if(port != 0) {
      return port;
    }else{
      return DEFAULT_PORT;
    }
  }

  public void setPort(int port) {
    this.port = port;
  }

}
