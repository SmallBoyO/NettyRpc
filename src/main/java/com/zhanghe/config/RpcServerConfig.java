package com.zhanghe.config;

public class RpcServerConfig {

  public static final int DEFAILT_MAGIC_NUM = 0xfade;

  public static final int DEFAULT_PORT = 7777;

  public static final String DEFAULT_IP = "0.0.0.0";

  public static final int DEFAULT_BUSINESS_CORE_THREAD_NUM = 4;

  public static final int DEFAULT_BUSINESS_QUEUE_LENGTH = 1000;

  public String ip;

  public int port;

  private int businessLogicCoreThreadNum;

  private int businessLogicQueueLength;

  public RpcServerConfig() {
  }

  public RpcServerConfig(int port) {
    this.port = port;
  }

  public RpcServerConfig(String ip, int port) {
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

  public int getBusinessLogicCoreThreadNum() {
    if(businessLogicCoreThreadNum != 0) {
      return businessLogicCoreThreadNum;
    }else{
      return DEFAULT_BUSINESS_CORE_THREAD_NUM;
    }
  }

  public void setBusinessLogicCoreThreadNum(int businessLogicCoreThreadNum) {
    this.businessLogicCoreThreadNum = businessLogicCoreThreadNum;
  }

  public int getBusinessLogicQueueLength() {
    if(businessLogicQueueLength != 0) {
      return businessLogicQueueLength;
    }else{
      return DEFAULT_BUSINESS_QUEUE_LENGTH;
    }
  }

  public void setBusinessLogicQueueLength(int businessLogicQueueLength) {
    this.businessLogicQueueLength = businessLogicQueueLength;
  }
}
