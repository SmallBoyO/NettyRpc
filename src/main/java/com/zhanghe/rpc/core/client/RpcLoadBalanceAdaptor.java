package com.zhanghe.rpc.core.client;

import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.rpc.core.client.loadbalance.LoadBalanceService;
import com.zhanghe.rpc.core.client.route.LoadBalanceRouter;
import com.zhanghe.rpc.core.client.route.Router;
import com.zhanghe.rpc.core.plugin.client.RpcClientFilter;
import com.zhanghe.spring.annotation.RpcClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;

public class RpcLoadBalanceAdaptor implements Client{

  private static Logger logger = LoggerFactory.getLogger(RpcLoadBalanceAdaptor.class);

  public List<RpcServerInfo> servers;

  public Map<String,RpcServerInfo> serversInfoMap;

  List<RpcClientConnector> activeServer;

  private Router router;

  private String loadBalance = "";

  private long serviceDiscoveryTimeoutMillis = 30000L;

  private Serializer serializer;

  private List<RpcClientFilter> filters;

  private volatile AtomicBoolean started = new AtomicBoolean(false);

  public RpcLoadBalanceAdaptor() {
    serversInfoMap = new HashMap<>();
    activeServer = new ArrayList<>();
    this.filters = new ArrayList<>();
  }

  @Override
  public void init(){
    synchronized (this){
      logger.info("Rpc load balance client ready to init");
      if(started.compareAndSet(false,true)) {
        initRouter();
        servers.forEach(rpcServerInfo -> {
          connectToServer(rpcServerInfo);
        });
        logger.info("Rpc load balance client init finish");
      }else {
        String error = "ERROR:Client already started!";
        logger.error(error);
        throw new RuntimeException(error);
      }
    }
  }

  private void connectToServer(RpcServerInfo rpcServerInfo){
    logger.info("client ready to connect server {}:{} ", rpcServerInfo.getRpcClientConfig().getIp(), rpcServerInfo.getRpcClientConfig().getPort());
    RpcClientConnector rpcClientConnector = new RpcClientConnector(rpcServerInfo.getRpcClientConfig().getIp(),
        rpcServerInfo.getRpcClientConfig().getPort());
    rpcClientConnector.setSerializer(serializer);
    rpcServerInfo.setRpcClientConnector(rpcClientConnector);
    rpcClientConnector.setClient(this);
    rpcClientConnector.start();
    serversInfoMap
        .put("/" + rpcServerInfo.getRpcClientConfig().getIp() + ":" + rpcServerInfo.getRpcClientConfig().getPort(), rpcServerInfo);
    logger.info("client connected server {}:{} ", rpcServerInfo.getRpcClientConfig().getIp(), rpcServerInfo.getRpcClientConfig().getPort());
  }

  public void addServer(RpcServerInfo rpcServerInfo){
    synchronized (this) {
      logger.info(" add server :[{}] ", rpcServerInfo);
      servers.add(rpcServerInfo);
      connectToServer(rpcServerInfo);
    }
  }


  public void removeServer(String ip,Integer port){
    synchronized (this) {
      logger.info(" remove server :[{},{}] ", ip, port);
      router.removeRoute(ip,port);
      RpcServerInfo rpcServerInfo = null;
      Iterator it = servers.iterator();
      while (it.hasNext()) {
        rpcServerInfo = (RpcServerInfo) it.next();
        if (rpcServerInfo.getRpcClientConfig().getIp().equals(ip)
            && rpcServerInfo.getRpcClientConfig().getPort() == port) {
          it.remove();
          break;
        }
      }
      serversInfoMap.remove("/" + ip + ":" + port).getRpcClientConnector().stop();
    }
  }

  private void initRouter(){
    router = new LoadBalanceRouter(loadBalance);
  }


  @Override
  public void destroy(){
    synchronized (this) {
      if (started.getAndSet(false)) {
        logger.info("Rpc load balance client ready to destroy");
        servers.forEach(rpcServerInfo -> {
          rpcServerInfo.getRpcClientConnector().stop();
          logger.info("client {}:{} ready to destroy", rpcServerInfo.getRpcClientConfig().getIp(),
                  rpcServerInfo.getRpcClientConfig().getPort());
          logger.info("client {}:{} destroy finish", rpcServerInfo.getRpcClientConfig().getIp(),
              rpcServerInfo.getRpcClientConfig().getPort());
        });
        logger.info("Rpc load balance client destroy finish");
      } else {
        String error = "ERROR:Client not started!";
        logger.error(error);
        throw new IllegalStateException(error);
      }
    }
  }

  @Override
  public void setServices(String address, Set<String> services) {
    logger.info("server[{}] get services:{}",address,services);
    activeServer.add(serversInfoMap.get(address).getRpcClientConnector());
    RpcServerInfo rpcServerInfo = serversInfoMap.get(address);
    services.forEach(service -> {
      router.refreshRoute(service,LoadBalanceService.of(address, rpcServerInfo,rpcServerInfo.weight));
    });
    //setServices 会导致rpcserverinfo可用,若此时路由还未刷新会导致短时间内该服务端虽然可用,但是路由不到该服务端
    rpcServerInfo.setServices(services);
  }

  @Override
  public Object proxy(String service) throws ClassNotFoundException{
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
    waitForServiceDiscoverable(remoteServiceName);
    enhancer.setCallback(new RpcClientMethodInterceptor(remoteServiceName,filters,this));
    return enhancer.create();
  }

  private void waitForServiceDiscoverable(String serviceName) {
    if (router.hasService(serviceName)) {
      return;
    }
    if (serviceDiscoveryTimeoutMillis <= 0) {
      throw new IllegalStateException(
          "rpc service [" + serviceName + "] not discovered, serviceDiscoveryTimeoutMillis="
              + serviceDiscoveryTimeoutMillis);
    }
    long deadline = System.currentTimeMillis() + serviceDiscoveryTimeoutMillis;
    while (System.currentTimeMillis() < deadline) {
      if (router.hasService(serviceName)) {
        return;
      }
      try {
        Thread.sleep(100L);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(
            "interrupted while waiting rpc service [" + serviceName + "] discovery", e);
      }
    }
    if (router.hasService(serviceName)) {
      return;
    }
    throw new IllegalStateException(
        "rpc service [" + serviceName + "] discovery timeout after "
            + serviceDiscoveryTimeoutMillis + "ms");
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
  public RpcServerInfo currentServer(String serviceName) {
    return router.getService(serviceName);
  }

  @Override
  public void addFilter(RpcClientFilter rpcClientFilter) {
    filters.add(rpcClientFilter);
  }

  @Override
  public boolean isStarted() {
    return started.get();
  }

  @Override
  public void connectorConnected(String address) {

  }

  @Override
  public void connectorDisConnected(String address) {
    RpcServerInfo rpcServerInfo = serversInfoMap.get(address);
    router.removeRoute(rpcServerInfo.getRpcClientConfig().getIp(),rpcServerInfo.getRpcClientConfig().getPort());
  }

  public void setLoadBalance(String loadBalance) {
    this.loadBalance = loadBalance;
  }

  public void setServiceDiscoveryTimeoutMillis(long serviceDiscoveryTimeoutMillis) {
    this.serviceDiscoveryTimeoutMillis = serviceDiscoveryTimeoutMillis;
  }

}
