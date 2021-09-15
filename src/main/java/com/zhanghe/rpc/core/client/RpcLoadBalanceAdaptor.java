package com.zhanghe.rpc.core.client;

import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.protocol.v1.response.RpcResponse;
import com.zhanghe.rpc.core.client.loadbalance.LoadBalance;
import com.zhanghe.rpc.core.client.loadbalance.LoadBalanceService;
import com.zhanghe.rpc.core.client.loadbalance.RandomLoadBalance;
import com.zhanghe.rpc.core.client.loadbalance.RoundLoadBalance;
import com.zhanghe.rpc.core.client.loadbalance.WeightRandomLoadBalance;
import com.zhanghe.rpc.core.plugin.client.RpcClientFilter;
import com.zhanghe.spring.annotation.RpcClient;
import io.netty.channel.Channel;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;

public class RpcLoadBalanceAdaptor implements Client{

  private static Logger logger = LoggerFactory.getLogger(RpcLoadBalanceAdaptor.class);

  public List<RpcServerInfo> servers;

  public Map<String,RpcClientConnector> serversMap;

  public Map<String,RpcServerInfo> serversInfoMap;

  List<RpcClientConnector> activeServer;

  private LoadBalance<RpcServerInfo> loadBalancer;

  private String loadBalance = "";

  private Serializer serializer;

  private List<RpcClientFilter> filters;

  private volatile AtomicBoolean started = new AtomicBoolean(false);

  public RpcLoadBalanceAdaptor() {
    serversMap = new HashMap<>();
    serversInfoMap = new HashMap<>();
    activeServer = new ArrayList<>();
    this.filters = new ArrayList<>();
  }

  @Override
  public void init(){
    logger.info("Rpc load balance client ready to init");
    if(started.compareAndSet(false,true)) {
      initLoadBalancer();
      servers.forEach(rpcServerInfo -> {
        logger.info("client {}:{} ready to init", rpcServerInfo.getRpcClientConfig().getIp(), rpcServerInfo.getRpcClientConfig().getPort());
        RpcClientConnector rpcClientConnector = new RpcClientConnector(rpcServerInfo.getRpcClientConfig().getIp(),
            rpcServerInfo.getRpcClientConfig().getPort());
        rpcClientConnector.setSerializer(serializer);
        rpcServerInfo.setRpcClientConnector(rpcClientConnector);
        serversMap
            .put("/" + rpcServerInfo.getRpcClientConfig().getIp() + ":" + rpcServerInfo.getRpcClientConfig().getPort(), rpcClientConnector);
        serversInfoMap
            .put("/" + rpcServerInfo.getRpcClientConfig().getIp() + ":" + rpcServerInfo.getRpcClientConfig().getPort(), rpcServerInfo);
        rpcClientConnector.setClient(this);
        rpcClientConnector.start();
        loadBalancer.addService(LoadBalanceService
            .of("/" + rpcServerInfo.getRpcClientConfig().getIp() + ":" + rpcServerInfo.getRpcClientConfig().getPort(), rpcServerInfo,
                rpcServerInfo.weight));
        logger.info("client {}:{} init finish", rpcServerInfo.getRpcClientConfig().getIp(), rpcServerInfo.getRpcClientConfig().getPort());
      });
      logger.info("Rpc load balance client init finish");
    }else {
      String error = "ERROR:Client already started!";
      logger.error(error);
      throw new RuntimeException(error);
    }
  }

  private void initLoadBalancer(){
    switch (loadBalance){
      case "random":
        loadBalancer = new RandomLoadBalance<>();
        logger.info("Rpc load balance client use RandomLoadBalance");
        break;
      case "weight_random":
        loadBalancer = new WeightRandomLoadBalance<>();
        logger.info("Rpc load balance client use WeightRandomLoadBalance");
        break;
      case "round":
        loadBalancer = new RoundLoadBalance<>();
        logger.info("Rpc load balance client use RoundLoadBalance");
        break;
      default:
        loadBalancer = new RandomLoadBalance<>();
        logger.info("Rpc load balance client use default RandomLoadBalance");
        break;
    }
  }
  @Override
  public void destroy(){
    if(started.getAndSet(false)) {
      logger.info("Rpc load balance client ready to destroy");
      servers.forEach(rpcServerInfo -> {
        logger
            .info("client {}:{} ready to destroy", rpcServerInfo.getRpcClientConfig().getIp(), rpcServerInfo.getRpcClientConfig().getPort());
        serversMap.get("/" + rpcServerInfo.getRpcClientConfig().getIp() + ":" + rpcServerInfo.getRpcClientConfig().getPort()).stop();
        logger.info("client {}:{} destroy finish", rpcServerInfo.getRpcClientConfig().getIp(), rpcServerInfo.getRpcClientConfig().getPort());
      });
      logger.info("Rpc load balance client destroy finish");
    }else{
      String error = "ERROR:Client not started!";
      logger.error(error);
      throw new IllegalStateException(error);
    }
  }

  @Override
  public void setServices(String address, Set<String> services) {
    logger.info("server[{}] get services:{}",address,services);
    serversInfoMap.get(address).setServices(services);
    activeServer.add(serversMap.get(address));
  }

  @Override
  public void setChannel(String address, Channel channel) {
    logger.info("server[{}] get channel",address);
  }

  @Override
  public Object proxy(String service) throws ClassNotFoundException{
    //等待每一个服务端获取到service列表
    serversInfoMap.forEach((s, rpcServerInfo) -> {
      rpcServerInfo.waitServerUseful();
    });
    Class<?> serviceClass = Class.forName(service);
    RpcClient rpcClientAnnotation = (RpcClient)serviceClass.getAnnotation(RpcClient.class);
    //使用 CGLIB
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(serviceClass);
    String remoteServiceName = null;
    if(rpcClientAnnotation!=null && !"".equals(rpcClientAnnotation.remoteClassName())){
      remoteServiceName = rpcClientAnnotation.remoteClassName();
    }else{
      remoteServiceName = service;
    }
    enhancer.setCallback(new RpcClientMethodInterceptor(remoteServiceName,filters,this));
    return enhancer.create();
  }

  public List<RpcServerInfo> getServers() {
    return servers;
  }

  public void setServers(List<RpcServerInfo> servers) {
    this.servers = servers;
  }

  @Override
  public void setSerializer(Serializer serializer) {
    this.serializer = serializer;
  }

  @Override
  public RpcServerInfo currentServer() {
    return this.loadBalancer.next();
  }

  @Override
  public void addFilter(RpcClientFilter rpcClientFilter) {
    filters.add(rpcClientFilter);
  }

  @Override
  public boolean isStarted() {
    return started.get();
  }

  public String getLoadBalance() {
    return loadBalance;
  }

  public void setLoadBalance(String loadBalance) {
    this.loadBalance = loadBalance;
  }
}
