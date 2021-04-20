package com.zhanghe.rpc.core.client;


import java.util.concurrent.Future;

public class RpcContext {

  private static RpcContext instance = new RpcContext();

  private RpcContext(){
    threadLocal = new ThreadLocal();
  }

  public static RpcContext getInstance() {
    return instance;
  }

  ThreadLocal<Future> threadLocal;

  public Future getFuture(){
    Future future = threadLocal.get();
    threadLocal.remove();
    return future;
  }

  public void setFuture(Future future){
    threadLocal.set(future);
  }

  public void remove(){
    threadLocal.remove();
  }

}
