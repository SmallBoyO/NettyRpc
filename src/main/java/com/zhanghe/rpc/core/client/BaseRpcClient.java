package com.zhanghe.rpc.core.client;

import com.zhanghe.config.RpcClientConfig;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.rpc.core.plugin.client.RpcClientFilter;
import com.zhanghe.spring.annotation.RpcClient;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;

public class BaseRpcClient implements Client {

  private static Logger logger = LoggerFactory.getLogger(BaseRpcClient.class);

  private RpcClientConnector rpcClientConnector;

  private RpcClientConfig rpcClientConfig;

  private RpcServerInfo rpcServerInfo;

  private Serializer serializer;

  private List<RpcClientFilter> filters;

  private volatile AtomicBoolean started = new AtomicBoolean(false);

  public BaseRpcClient() {
    this.filters = new ArrayList<>();
    this.rpcClientConfig = new RpcClientConfig();
  }

  public BaseRpcClient(String ip, int port) {
    this.filters = new ArrayList<>();
    this.rpcClientConfig = new RpcClientConfig(ip,port);
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
    //获取注解信息
    Class serviceClass = Class.forName(service);
    RpcClient rpcClientAnnotation = (RpcClient)serviceClass.getAnnotation(RpcClient.class);
    if( rpcClientAnnotation == null || rpcClientAnnotation.remoteClassName() == null){
      //使用默认的
    }else{

    }
    //todo 可以根据注解里面的remoteService 设置代理成不同的类
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

  @Override
  public boolean isStarted() {
    return started.get();
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
        RpcRequestCallBackholder.callBackMap.keySet().forEach(rpcRequestUuid -> {
          logger.debug("等待任务[{}]的返回结果",rpcRequestUuid);
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

}
