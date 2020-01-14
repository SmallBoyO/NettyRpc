package com.zhanghe.rpc;

import com.zhanghe.config.RpcConfig;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcServerSpringAdaptor {

  private static Logger logger = LoggerFactory.getLogger(RpcServerSpringAdaptor.class);

  String ip = RpcConfig.DEFAULT_IP;

  int port  = RpcConfig.DEFAULT_PORT;

  List<Object> services;

  public void setService(Object service) {
    services.add(service);
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

  public List<Object> getServices() {
    return services;
  }

  public void setServices(List<Object> services) {
    this.services = services;
  }

  private RpcServer rpcServer;

  public void init(){
    logger.info("Rpc Server ready to init");
    System.out.println(this);
    rpcServer = new RpcServer(ip,port);
    rpcServer.bind(services);
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
