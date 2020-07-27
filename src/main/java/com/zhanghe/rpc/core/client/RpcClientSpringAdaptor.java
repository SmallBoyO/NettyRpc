package com.zhanghe.rpc.core.client;

import com.zhanghe.config.RpcConfig;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.rpc.core.plugin.client.RpcClientFilter;
import io.netty.channel.Channel;
import java.lang.reflect.Proxy;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClientSpringAdaptor implements Client{

  private static Logger logger = LoggerFactory.getLogger(RpcClientSpringAdaptor.class);

  private String ip;

  private int port = RpcConfig.DEFAULT_PORT;

  private RpcServerInfo rpcServerInfo;

  private RpcClientConnector rpcClientConnector;

  private RpcRequestProxy proxy;

  private Serializer serializer;

  public RpcClientSpringAdaptor() {
    this.proxy = new RpcRequestProxy<>();
  }

  @Override
  public void init(){
    logger.info("Rpc client ready to init");
    if(rpcServerInfo == null){
      rpcServerInfo = new RpcServerInfo();
      rpcServerInfo.setIp(ip);
      rpcServerInfo.setPort(port);
    }
    proxy.setClient(this);
    rpcClientConnector = new RpcClientConnector(ip,port);
    rpcClientConnector.setSerializer(serializer);
    rpcClientConnector.setClient(this);
    rpcServerInfo.setRpcClientConnector(rpcClientConnector);
    rpcClientConnector.start();
    logger.info("Rpc client init finish");
  }

  @Override
  public void destroy(){
    logger.info("Rpc client ready to destroy");
    rpcClientConnector.stop();
    logger.info("Rpc client destroy finish");
  }

  @Override
  public Object proxy(String service) throws ClassNotFoundException{
    rpcServerInfo.waitServerUseful();
    if (!rpcServerInfo.getServices().contains(service)) {
      throw new RuntimeException("服务端未提供此service");
    }
    Class<?> clazz = Class.forName(service);
    return Proxy.newProxyInstance(
        clazz.getClassLoader(),
        new Class<?>[]{clazz},
        proxy
    );
  }

  @Override
  public void setServices(String address, Set<String> services) {
    rpcServerInfo.setServices(services);
    rpcServerInfo.signalServerUseful();
  }

  @Override
  public void setChannel(String address, Channel channel) {
//    proxy.setChannel(channel);
  }

  @Override
  public void setSerializer(Serializer serializer) {
    this.serializer = serializer;
  }

  @Override
  public RpcServerInfo currentServer() {
    return this.rpcServerInfo;
  }

  @Override
  public void addFilter(RpcClientFilter rpcClientFilter) {

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

  public RpcServerInfo getRpcServerInfo() {
    return rpcServerInfo;
  }

  public void setRpcServerInfo(RpcServerInfo rpcServerInfo) {
    this.rpcServerInfo = rpcServerInfo;
  }

  public RpcClientConnector getRpcClientConnector() {
    return rpcClientConnector;
  }

  public void setRpcClientConnector(RpcClientConnector rpcClientConnector) {
    this.rpcClientConnector = rpcClientConnector;
  }

  public RpcRequestProxy getProxy() {
    return proxy;
  }

  public void setProxy(RpcRequestProxy proxy) {
    this.proxy = proxy;
  }

}
