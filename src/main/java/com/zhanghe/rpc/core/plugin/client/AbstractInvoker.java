package com.zhanghe.rpc.core.plugin.client;

import com.zhanghe.protocol.v1.response.RpcResponse;
import com.zhanghe.rpc.core.client.RpcRequestProxy;
import java.lang.reflect.Method;

public class AbstractInvoker implements Invoker {

  private RpcRequestProxy rpcRequestProxy;

  private RpcResponse rpcResponse;

  public AbstractInvoker(RpcRequestProxy rpcRequestProxy) {
    this.rpcRequestProxy = rpcRequestProxy;
  }

  @Override
  public void invoke(Object proxy, Method method, Object[] args) throws Throwable {
    rpcResponse =  rpcRequestProxy.call(proxy,method,args);
  }

  public RpcResponse getRpcResponse() {
    return rpcResponse;
  }

  public void setRpcResponse(RpcResponse rpcResponse) {
    this.rpcResponse = rpcResponse;
  }
}
