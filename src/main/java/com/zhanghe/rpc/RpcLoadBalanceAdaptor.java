package com.zhanghe.rpc;

import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.protocol.v1.response.RpcResponse;
import com.zhanghe.rpc.loadbalance.LoadBalance;
import com.zhanghe.rpc.loadbalance.LoadBalanceService;
import com.zhanghe.rpc.loadbalance.RandomLoadBalance;
import com.zhanghe.rpc.loadbalance.RoundLoadBalance;
import com.zhanghe.rpc.loadbalance.WeightRandomLoadBalance;
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

public class RpcLoadBalanceAdaptor implements RpcClientHolder{

  private static Logger logger = LoggerFactory.getLogger(RpcLoadBalanceAdaptor.class);

  public List<RpcServerInfo> servers;

  public Map<String,RpcClientConnector> serversMap;

  public Map<String,RpcServerInfo> serversInfoMap;

  List<RpcClientConnector> activeServer;

  private LoadBalanceProxy loadBalanceProxy;

  private LoadBalance<RpcServerInfo> loadBalancer;

  private String loadBalance = "";

  public RpcLoadBalanceAdaptor() {
    serversMap = new HashMap<>();
    this.loadBalanceProxy = new LoadBalanceProxy();
    serversInfoMap = new HashMap<>();
    activeServer = new ArrayList<>();
  }

  public void init(){
    logger.info("Rpc load balance client ready to init");
    initLoadBalancer();
    servers.forEach(rpcServerInfo -> {
      logger.info("client {}:{} ready to init",rpcServerInfo.getIp(),rpcServerInfo.getPort());
        RpcClientConnector rpcClientConnector = new RpcClientConnector(rpcServerInfo.getIp(),rpcServerInfo.getPort());
        rpcServerInfo.setRpcClientConnector(rpcClientConnector);
        serversMap.put("/"+rpcServerInfo.getIp() + ":" +rpcServerInfo.getPort(), rpcClientConnector);
        serversInfoMap.put("/"+rpcServerInfo.getIp() + ":" +rpcServerInfo.getPort(),rpcServerInfo);
        rpcClientConnector.setRpcClientHolder(this);
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

  public Object proxy(String service){
    //等待每一个服务端获取到service列表
    serversInfoMap.forEach((s, rpcServerInfo) -> {
      rpcServerInfo.waitServerUseful();
    });
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
      RpcServerInfo server = loadBalancer.next();
      logger.debug("choose server:[{}:{}]",server.getIp(),server.getPort());
      if(!server.getUseful().get()){
        throw new IllegalStateException("server ["+server.getIp()+":"+server.getPort()+"] disconnected!");
      }
      Channel channel = server.getRpcClientConnector().getActiveChannel();
      RpcRequest rpcRequest = new RpcRequest();
      String requestId = UUID.randomUUID().toString();
      rpcRequest.setRequestId(requestId);
      rpcRequest.setClassName(proxy.getClass().getInterfaces()[0].getName());
      rpcRequest.setMethodName(method.getName());
      rpcRequest.setTypeParameters(method.getParameterTypes());
      rpcRequest.setParametersVal(args);
      RpcRequestCallBack callBack = new RpcRequestCallBack(requestId);

      RpcRequestCallBackholder.callBackMap.put(rpcRequest.getRequestId(), callBack);
      channel.writeAndFlush(rpcRequest);
      RpcResponse result = callBack.start();
      if(result == null && !channel.isActive()){
        throw new IllegalStateException("rpc server disconnected!");
      }
      if (result.isSuccess()) {
        return result.getResult();
      } else {
        throw result.getException();
      }
    }

  }

  public String getLoadBalance() {
    return loadBalance;
  }

  public void setLoadBalance(String loadBalance) {
    this.loadBalance = loadBalance;
  }
}
