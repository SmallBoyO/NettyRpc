package com.zhanghe.rpc.core.plugin.client;

import com.zhanghe.protocol.v1.response.RpcResponse;
import com.zhanghe.rpc.core.client.RpcContext;
import com.zhanghe.rpc.core.client.RpcRequestProxy;
import java.lang.reflect.Method;
import java.util.concurrent.Future;

public class AsyncInvoker implements Invoker {

  public AsyncInvoker(RpcRequestProxy rpcRequestProxy) {
    this.rpcRequestProxy = rpcRequestProxy;
  }

  private RpcRequestProxy rpcRequestProxy;

  private Future future;

  @Override
  public void invoke(Object proxy, Method method, Object[] args) throws Throwable {
    future = rpcRequestProxy.asyncCall(proxy,method,args);
    RpcContext.getInstance().setFuture(future);
  }

  public RpcResponse getRpcResponse() {
    return null;
  }

}
