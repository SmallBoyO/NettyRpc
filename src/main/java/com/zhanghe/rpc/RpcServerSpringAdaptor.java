package com.zhanghe.rpc;

import com.zhanghe.config.RpcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcServerSpringAdaptor {

  private static Logger logger = LoggerFactory.getLogger(RpcServerSpringAdaptor.class);

  String ip = RpcConfig.DEFAULT_IP;

  int port  = RpcConfig.DEFAULT_PORT;

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

  private RpcServer rpcServer;

  public void init(){
    logger.info("Rpc Server ready to init");
    System.out.println(this);
    rpcServer = new RpcServer(ip,port);
    rpcServer.start();
    logger.info("Rpc Server init finish");
  }

  public void destroy(){
    logger.info("Rpc Server ready to destroy");
    rpcServer.stop();
    logger.info("Rpc Server destroy finish");
  }

  @Override
  public String toString() {
    return "RpcServerSpringAdaptor{" +
        "ip='" + ip + '\'' +
        ", port=" + port +
        '}';
  }
}
