package com.zhanghe.rpc.core.client;

import com.zhanghe.config.RpcConfig;
import io.netty.channel.Channel;
import java.lang.reflect.Proxy;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractRpcClient implements Client {

  private static Logger logger = LoggerFactory.getLogger(AbstractRpcClient.class);

  private RpcClientConnector rpcClientConnector;

  private String ip;

  private int port = RpcConfig.DEFAULT_PORT;

  private RpcServerInfo rpcServerInfo;

  private RpcRequestProxy proxy;

  public AbstractRpcClient() {
    this.proxy = new RpcRequestProxy<>();
  }

  public AbstractRpcClient(String ip, int port) {
    this.ip = ip;
    this.port = port;
    this.proxy = new RpcRequestProxy<>();
  }

  @Override
  public void init() {
    logger.info("client ready to init");
    if(rpcServerInfo == null){
      rpcServerInfo = new RpcServerInfo();
      rpcServerInfo.setIp(ip);
      rpcServerInfo.setPort(port);
    }
    rpcClientConnector = new RpcClientConnector(ip,port);
    rpcClientConnector.setClient(this);
    rpcClientConnector.start();
    logger.info("client init success");
  }

  @Override
  public void destroy() {
    logger.info("client ready to destroy");
    rpcClientConnector.stop();
    logger.info("client destroy success");
  }

  @Override
  public void setServices(String address, Set<String> services) {
    rpcServerInfo.setServices(services);
    rpcServerInfo.signalServerUseful();
  }

  @Override
  public void setChannel(String address, Channel channel) {
    proxy.setChannel(channel);
  }

  @Override
  public Object proxy(String service) throws ClassNotFoundException {
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
}
