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
import java.util.concurrent.atomic.AtomicBoolean;

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

  private volatile AtomicBoolean started = new AtomicBoolean(false);

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
    logger.info("Server ready to init");
    if(started.compareAndSet(false,true)){
      logger.info("Ready start Client,connect to server : [{}:{}]",this.rpcClientConfig.getIp(),this.rpcClientConfig.getPort());
      try{
        doInit();
        doStart();
      }catch (Exception e){
        started.set(false);
        logger.error("ERROR:Client started failed.reason:{}",e.getMessage());
        throw new RuntimeException(e);
      }
      logger.info("Client started.");
    }else{
      String error = "ERROR:Client already started!";
      logger.error(error);
      throw new RuntimeException(error);
    }
  }

  private void doInit(){
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
  }

  private void doStart(){
    //注册关闭钩子
//    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//              try {
//                doStop();
//              }catch (Exception e){
//                e.printStackTrace();
//              }
//            })
//    );
    rpcClientConnector.start();
  }

  @Override
  public void destroy() {
    logger.info("client ready to destroy");
    stop();
    logger.info("client destroy success");
  }

  public void stop(){
    try{
      if(started.getAndSet(false)) {
        doStop();
      }else{
        String error = "ERROR:Client not started!";
        logger.error(error);
        throw new IllegalStateException(error);
      }
      logger.info("Client stop success.");
    }catch (Exception e){
      logger.error("ERROR:Client stop failed.reason:{}",e.getMessage());
      throw new IllegalStateException(e);
    }
  }

  private void doStop(){
    gracefulShutdown();
    rpcClientConnector.stop();
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

  public void gracefulShutdown(){
    //todo 停止继续发送rpc请求
    waitRunningRpcRequest();
  }

  /**
   * 等待执行中的rpc任务全部完成
   */
  private void waitRunningRpcRequest(){
    while(RpcRequestCallBackholder.callBackMap.size()>0){
      try {
        RpcRequestCallBackholder.callBackMap.keySet().forEach(rpcRequestUUID -> {
          logger.debug("等待任务[{}]的返回结果",rpcRequestUUID);
        });
        Thread.sleep(1000);
      }catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public Serializer getSerializer() {
    return serializer;
  }

    public RpcClientConfig getRpcClientConfig() {
        return rpcClientConfig;
    }

    public void setRpcClientConfig(RpcClientConfig rpcClientConfig) {
        this.rpcClientConfig = rpcClientConfig;
    }
}
