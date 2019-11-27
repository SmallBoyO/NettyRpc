package com.zhanghe.rpc;

import com.zhanghe.config.RpcConfig;
import io.netty.channel.Channel;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClientSpringAdaptor implements RpcClientHolder{

  private static Logger logger = LoggerFactory.getLogger(RpcClientSpringAdaptor.class);

  private String ip;

  private int port = RpcConfig.DEFAULT_PORT;

  private RpcClientConnector rpcClientConnector;

  public void init(){
    logger.info("Rpc client ready to init");
    rpcClientConnector = new RpcClientConnector(ip,port);
    rpcClientConnector.start();
    logger.info("Rpc client init finish");
  }

  public void destroy(){
    logger.info("Rpc client ready to destroy");
    rpcClientConnector.stop();
    logger.info("Rpc client destroy finish");
  }

  public Object proxy(String service){
    return null;
  }

  @Override
  public void setServices(String address, Set<String> services) {

  }

  @Override
  public void setChannel(String address, Channel channel) {

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
