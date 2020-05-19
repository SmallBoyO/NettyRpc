package com.zhanghe.rpc.core.client;

import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.protocol.v1.response.RpcResponse;
import com.zhanghe.rpc.core.client.loadbalance.LoadBalance;
import com.zhanghe.rpc.core.client.loadbalance.LoadBalanceService;
import com.zhanghe.rpc.core.client.loadbalance.RandomLoadBalance;
import com.zhanghe.rpc.core.client.loadbalance.RoundLoadBalance;
import com.zhanghe.rpc.core.client.loadbalance.WeightRandomLoadBalance;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcLoadBalanceAdaptor implements Client{

  private static Logger logger = LoggerFactory.getLogger(RpcLoadBalanceAdaptor.class);

  public List<RpcServerInfo> servers;

  public Map<String,RpcClientConnector> serversMap;

  public Map<String,RpcServerInfo> serversInfoMap;

  List<RpcClientConnector> activeServer;

  private RpcRequestProxy proxy;

  private LoadBalance<RpcServerInfo> loadBalancer;

  private String loadBalance = "";

  private Serializer serializer;

  public RpcLoadBalanceAdaptor() {
    this.proxy = new RpcRequestProxy<>();
    serversMap = new HashMap<>();
    serversInfoMap = new HashMap<>();
    activeServer = new ArrayList<>();
  }

  @Override
  public void init(){
    logger.info("Rpc load balance client ready to init");
    initLoadBalancer();
    proxy.setClient(this);
    servers.forEach(rpcServerInfo -> {
      logger.info("client {}:{} ready to init",rpcServerInfo.getIp(),rpcServerInfo.getPort());
        RpcClientConnector rpcClientConnector = new RpcClientConnector(rpcServerInfo.getIp(),rpcServerInfo.getPort());
        rpcClientConnector.setSerializer(serializer);
        rpcServerInfo.setRpcClientConnector(rpcClientConnector);
        serversMap.put("/"+rpcServerInfo.getIp() + ":" +rpcServerInfo.getPort(), rpcClientConnector);
        serversInfoMap.put("/"+rpcServerInfo.getIp() + ":" +rpcServerInfo.getPort(),rpcServerInfo);
        rpcClientConnector.setClient(this);
        rpcClientConnector.start();
        loadBalancer.addService(LoadBalanceService.of("/"+rpcServerInfo.getIp() + ":" +rpcServerInfo.getPort(),rpcServerInfo,rpcServerInfo.weight));
      logger.info("client {}:{} init finish",rpcServerInfo.getIp(),rpcServerInfo.getPort());
    });
    logger.info("Rpc load balance client init finish");
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
    logger.info("Rpc load balance client ready to destroy");
    servers.forEach(rpcServerInfo -> {
      logger.info("client {}:{} ready to destroy",rpcServerInfo.getIp(),rpcServerInfo.getPort());
      serversMap.get("/"+rpcServerInfo.getIp() + ":" +rpcServerInfo.getPort()).stop();
      logger.info("client {}:{} destroy finish",rpcServerInfo.getIp(),rpcServerInfo.getPort());
    });
    logger.info("Rpc load balance client destroy finish");
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
    Class<?> clazz = Class.forName(service);
    return Proxy.newProxyInstance(
        clazz.getClassLoader(),
        new Class<?>[]{ clazz },
        proxy
    );
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

  public String getLoadBalance() {
    return loadBalance;
  }

  public void setLoadBalance(String loadBalance) {
    this.loadBalance = loadBalance;
  }
}
