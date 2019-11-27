package com.zhanghe.rpc;

import io.netty.channel.Channel;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcLoadBalanceAdaptor implements RpcClientHolder{

  private static Logger logger = LoggerFactory.getLogger(RpcLoadBalanceAdaptor.class);

  public List<RpcServerInfo> servers;

  public Map<RpcServerInfo,RpcClientConnector> serversMap;

  private LoadBalanceProxy loadBalanceProxy;

  public RpcLoadBalanceAdaptor() {
    serversMap = new HashMap<>();
    this.loadBalanceProxy = new LoadBalanceProxy();
  }

  public void init(){
    logger.info("Rpc loadbalance client ready to init");
    servers.forEach(rpcServerInfo -> {
      logger.info("client {}:{} ready to init",rpcServerInfo.getIp(),rpcServerInfo.getPort());
        RpcClientConnector rpcClientConnector = new RpcClientConnector(rpcServerInfo.getIp(),rpcServerInfo.getPort());
        serversMap.put(rpcServerInfo, rpcClientConnector);
        rpcClientConnector.start();
      logger.info("client {}:{} init finish",rpcServerInfo.getIp(),rpcServerInfo.getPort());
    });
    logger.info("Rpc loadbalance client init finish");
  }

  public void destroy(){
    logger.info("Rpc loadbalance client ready to destroy");
    servers.forEach(rpcServerInfo -> {
      logger.info("client {}:{} ready to destroy",rpcServerInfo.getIp(),rpcServerInfo.getPort());
      serversMap.get(rpcServerInfo).stop();
      logger.info("client {}:{} destroy finish",rpcServerInfo.getIp(),rpcServerInfo.getPort());
    });
    logger.info("Rpc loadbalance client destroy finish");
  }

  @Override
  public void setServices(String address, Set<String> services) {
    System.out.println(address+"get Services");
  }

  @Override
  public void setChannel(String address, Channel channel) {
    System.out.println(address+"get channel");
  }

  public Object proxy(String service){
    try {
      Class<?> clazz = Class.forName(service);
      return Proxy.newProxyInstance(
          clazz.getClassLoader(),
          new Class<?>[]{ clazz },
          loadBalanceProxy
      );
    }catch (ClassNotFoundException e){
      e.printStackTrace();
      return null;
    }
  }

  public List<RpcServerInfo> getServers() {
    return servers;
  }

  public void setServers(List<RpcServerInfo> servers) {
    this.servers = servers;
  }

  class LoadBalanceProxy <T> implements InvocationHandler {

    ThreadLocalRandom random = ThreadLocalRandom.current();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      System.out.println("invoke");
//      RpcServerInfo rpcServerInfo = servers.get(random.nextInt()%servers.size());

      return null;
    }
  }

}
