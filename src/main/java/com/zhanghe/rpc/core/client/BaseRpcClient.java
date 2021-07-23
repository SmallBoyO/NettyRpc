package com.zhanghe.rpc.core.client;

import com.zhanghe.config.RpcClientConfig;
import com.zhanghe.config.RpcServerConfig;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.rpc.core.plugin.client.RpcClientFilter;
import io.netty.channel.Channel;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseRpcClient implements Client {

  private static Logger logger = LoggerFactory.getLogger(BaseRpcClient.class);

  private RpcClientConnector rpcClientConnector;

  private RpcClientConfig rpcClientConfig;

  private RpcServerInfo rpcServerInfo;

  private RpcRequestProxy proxy;

  private Serializer serializer;

  private List<RpcClientFilter> filters;

  public BaseRpcClient() {
    this.filters = new ArrayList<>();
    this.proxy = new RpcRequestProxy<>();
    proxy.setFilters(this.filters);
    this.rpcClientConfig = new RpcClientConfig();
  }

  public BaseRpcClient(String ip, int port) {
    this.filters = new ArrayList<>();
    this.rpcClientConfig = new RpcClientConfig(ip,port);
    this.proxy = new RpcRequestProxy<>();
    proxy.setFilters(this.filters);
  }

  @Override
  public void init() {
    logger.info("client ready to init");
    if(rpcServerInfo == null){
      rpcServerInfo = new RpcServerInfo();
      rpcServerInfo.setIp(rpcClientConfig.getIp());
      rpcServerInfo.setPort(rpcClientConfig.getPort());
    }
    proxy.setClient(this);
    rpcClientConnector = new RpcClientConnector(rpcClientConfig.getIp(),rpcClientConfig.getPort());
    rpcClientConnector.setSerializer(serializer);
    rpcClientConnector.setClient(this);
    rpcServerInfo.setRpcClientConnector(rpcClientConnector);
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
//    proxy.setChannel(channel);
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
    filters.add(rpcClientFilter);
  }

  public Serializer getSerializer() {
    return serializer;
  }

}
