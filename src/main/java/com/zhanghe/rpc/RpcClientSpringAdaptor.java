package com.zhanghe.rpc;

import com.zhanghe.config.RpcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClientSpringAdaptor {

  private static Logger logger = LoggerFactory.getLogger(RpcClientSpringAdaptor.class);

  private String ip;

  private int port = RpcConfig.DEFAULT_PORT;

  private RpcClient rpcClient;

  public void init(){
    logger.info("Rpc client ready to init");
    rpcClient = new RpcClient(ip,port);
    rpcClient.start();
    logger.info("Rpc client init finish");
  }

  public void destroy(){
    logger.info("Rpc client ready to destroy");
    rpcClient.stop();
    logger.info("Rpc client destroy finish");
  }

  public Object proxy(String service){
    return rpcClient.proxy(service);
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
}
