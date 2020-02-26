package com.zhanghe.rpc.core.server;

import com.zhanghe.config.RpcConfig;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractRpcServer implements Server {

  private static final Logger logger = LoggerFactory.getLogger(AbstractRpcServer.class);

  private AtomicBoolean started = new AtomicBoolean(false);

  private String ip;

  private int port;

  private RpcServerConnector rpcServerConnector;

  List<Object> services;

  public AbstractRpcServer() {
    this.rpcServerConnector = new RpcServerConnector();
  }

  public AbstractRpcServer(int port) {
    this.rpcServerConnector = new RpcServerConnector();
    this.ip = RpcConfig.DEFAULT_IP;
    this.port = port;
  }

  public AbstractRpcServer(String ip, int port) {
    this.rpcServerConnector = new RpcServerConnector();
    this.ip = ip;
    this.port = port;
  }

  @Override
  public void init() {
    logger.info("Server ready to init");
    if(started.compareAndSet(false,true)){
      logger.info("Ready start Server on port {}.",port);
      try{
        doInit();
        doStart();
      }catch (Exception e){
        started.set(false);
        logger.error("ERROR:Server started failed.reason:{}",e.getMessage());
        throw new RuntimeException(e);
      }
      logger.info("Server started on port {}.",port);
    }else{
      String error = "ERROR:Server already started!";
      logger.error(error);
      throw new RuntimeException(error);
    }
    logger.info("Server init success");
  }

  private void doInit(){
    rpcServerConnector.setIp(ip);
    rpcServerConnector.setPort(port);
    rpcServerConnector.init();
    if(services != null){
      bind(services);
    }
  }

  public boolean doStart() throws InterruptedException{
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
      logger.info("Server {}:{} stop success.",ip,port);
    }catch (Exception e){
      logger.error("ERROR:Server stop failed.reason:{}",e.getMessage());
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void bind(Object service){
    logger.info("bind service:"+service.getClass().getName());
    rpcServerConnector.getServerChannelInitializer().getBindRpcServiceHandler().getServiceMap().put(service.getClass().getInterfaces()[0].getName(), service);
  }

  @Override
  public void bind(List<Object> services){
    services.forEach(service -> {
      logger.info("bind service:" + service.getClass().getInterfaces()[0].getName());
      rpcServerConnector.getServerChannelInitializer().getBindRpcServiceHandler().getServiceMap().put(service.getClass().getInterfaces()[0].getName(), service);
    });
  }

  public void resetWorkGroup(){

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

  public List<Object> getServices() {
    return services;
  }

  public void setServices(List<Object> services) {
    this.services = services;
  }
}
