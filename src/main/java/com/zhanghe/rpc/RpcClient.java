package com.zhanghe.rpc;

import com.zhanghe.config.RpcConfig;
import io.netty.channel.Channel;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClient implements RpcClientHolder{

  private static Logger logger = LoggerFactory.getLogger(RpcClientSpringAdaptor.class);

  private String ip;

  private int port = RpcConfig.DEFAULT_PORT;

  private RpcClientConnector rpcClientConnector;

  private RpcRequestProxy proxy;

  private Set<String> registeredServices;

  private Lock lock = new ReentrantLock();

  private Condition servicesInitCondition = lock.newCondition();

  public RpcClient() {
    this.proxy = new RpcRequestProxy<>();
  }

  public RpcClient(String ip, int port) {
    this.ip = ip;
    this.port = port;
    this.proxy = new RpcRequestProxy<>();
  }

  public void init(){
    logger.info("Rpc client ready to init");
    rpcClientConnector = new RpcClientConnector(ip,port);
    rpcClientConnector.setRpcClientHolder(this);
    rpcClientConnector.start();
    logger.info("Rpc client init finish");
  }

  public void destroy(){
    logger.info("Rpc client ready to destroy");
    rpcClientConnector.stop();
    logger.info("Rpc client destroy finish");
  }

  public Object proxy(String service) throws ClassNotFoundException,InterruptedException{
    lock.lock();
    try {
      System.out.println("proxy:"+rpcClientConnector.getGetServices().get());
      if( !rpcClientConnector.getGetServices().get()){
        servicesInitCondition.await();
      }
      if (!registeredServices.contains(service)) {
        throw new RuntimeException("服务端未提供此service");
      }
      Class<?> clazz = Class.forName(service);
      return Proxy.newProxyInstance(
          clazz.getClassLoader(),
          new Class<?>[]{clazz},
          proxy
      );
    }finally {
      lock.unlock();
    }
  }

  @Override
  public void setServices(String address, Set<String> services) {
    this.registeredServices = services;
    lock.lock();
    try {
      servicesInitCondition.signalAll();
    }finally {
      lock.unlock();
    }
  }

  @Override
  public void setChannel(String address, Channel channel) {
    proxy.setChannel(channel);
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

}
