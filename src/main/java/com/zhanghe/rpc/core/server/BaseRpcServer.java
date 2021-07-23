package com.zhanghe.rpc.core.server;

import com.zhanghe.channel.hanlder.server.BindRpcFilterHandler;
import com.zhanghe.config.RpcServerConfig;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.rpc.core.plugin.server.RpcServerFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseRpcServer implements Server {

  private static final Logger logger = LoggerFactory.getLogger(BaseRpcServer.class);

  private AtomicBoolean started = new AtomicBoolean(false);

  private RpcServerConnector rpcServerConnector;

  private RpcServerConfig rpcServerConfig;

  List<Object> services;

  ConcurrentHashMap<String,Object> servicesMap;

  private Serializer serializer;

  private List<RpcServerFilter> filters;

  public BaseRpcServer() {
    this.rpcServerConnector = new RpcServerConnector();
    this.servicesMap = new ConcurrentHashMap<>();
    this.filters = new ArrayList<>();
    this.rpcServerConfig = new RpcServerConfig();
  }

  public BaseRpcServer(int port) {
    this.rpcServerConnector = new RpcServerConnector();
    this.servicesMap = new ConcurrentHashMap<>();
    this.rpcServerConfig = new RpcServerConfig(port);
    this.filters = new ArrayList<>();
  }

  public BaseRpcServer(String ip, int port) {
    this.rpcServerConfig = new RpcServerConfig(ip,port);
    this.filters = new ArrayList<>();
    this.rpcServerConnector = new RpcServerConnector();
    this.servicesMap = new ConcurrentHashMap<>();
  }

  public BaseRpcServer(RpcServerConfig rpcServerConfig){
    this.rpcServerConnector = new RpcServerConnector();
    this.servicesMap = new ConcurrentHashMap<>();
    this.rpcServerConfig = rpcServerConfig;
    this.filters = new ArrayList<>();
  }

  @Override
  public void init() {
    logger.info("Server ready to init");
    if(started.compareAndSet(false,true)){
      logger.info("Ready start Server on port {}.", rpcServerConfig.getPort());
      try{
        doInit();
        doStart();
      }catch (Exception e){
        started.set(false);
        logger.error("ERROR:Server started failed.reason:{}",e.getMessage());
        throw new RuntimeException(e);
      }
      logger.info("Server started on port {}.", rpcServerConfig.getPort());
    }else{
      String error = "ERROR:Server already started!";
      logger.error(error);
      throw new RuntimeException(error);
    }
    logger.info("Server init success");
  }

  private void doInit(){
    rpcServerConnector.setIp(rpcServerConfig.getIp());
    rpcServerConnector.setPort(rpcServerConfig.getPort());
    rpcServerConnector.setBusinessLogicCoreThreadNum(rpcServerConfig.getBusinessLogicCoreThreadNum());
    rpcServerConnector.setBusinessLogicQueueLength(rpcServerConfig.getBusinessLogicQueueLength());
    rpcServerConnector.setSerializer(this.serializer);
    rpcServerConnector.init();
    rpcServerConnector.getServerChannelInitializer().getBindRpcServiceHandler().setServiceMap(servicesMap);
    rpcServerConnector.getServerChannelInitializer().setBindRpcFilterHandler(new BindRpcFilterHandler(this.filters));
    if(services != null){
      bind(services);
    }
  }

  public boolean doStart() throws InterruptedException{
    //注册关闭钩子
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          try {
           doStop();
          }catch (Exception e){
            e.printStackTrace();
          }
        })
    );
    return rpcServerConnector.start();
  }

  @Override
  public void destroy() {
    logger.info("Server ready to destroy");
    stop();
    logger.info("Server destroy success");
  }

  public void doStop() throws InterruptedException{
    rpcServerConnector.stop();
  }

  public void stop(){
    try{
      if(started.getAndSet(false)) {
        doStop();
      }else{
        String error = "ERROR:Server not started!";
        logger.error(error);
        throw new IllegalStateException(error);
      }
      logger.info("Server {}:{} stop success.", rpcServerConfig.getIp(), rpcServerConfig.getPort());
    }catch (Exception e){
      logger.error("ERROR:Server stop failed.reason:{}",e.getMessage());
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void bind(Object service){
    logger.info("bind service:"+service.getClass().getName());
    this.servicesMap.put(service.getClass().getInterfaces()[0].getName(), service);
  }

  @Override
  public void bind(List<Object> services){
    services.forEach(service -> {
      logger.info("bind service:" + service.getClass().getInterfaces()[0].getName());
        this.servicesMap.put(service.getClass().getInterfaces()[0].getName(), service);
    });
  }

  public Serializer getSerializer() {
    return serializer;
  }

  @Override
  public void setSerializer(Serializer serializer) {
    this.serializer = serializer;
  }

  public List<Object> getServices() {
    return services;
  }

  public void setServices(List<Object> services) {
    this.services = services;
  }

  @Override
  public void addFilter(RpcServerFilter filter) {
    this.filters.add(filter);
  }
}
