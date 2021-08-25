package com.zhanghe.rpc.core.plugin.client;

import com.zhanghe.protocol.v1.response.RpcResponse;
import com.zhanghe.rpc.core.client.RpcClientMethodInterceptor;
import com.zhanghe.rpc.core.client.RpcRequestProxy;
import java.lang.reflect.Method;

public class BaseInvoker implements Invoker {

  private RpcClientMethodInterceptor rpcClientMethodInterceptor;

  private RpcResponse rpcResponse;

  public BaseInvoker(RpcClientMethodInterceptor rpcClientMethodInterceptor) {
    this.rpcClientMethodInterceptor = rpcClientMethodInterceptor;
  }

  @Override
  public void invoke(Method method, Object[] args) throws Throwable {
    rpcResponse =  rpcClientMethodInterceptor.call(method,args);
  }

  public RpcResponse getRpcResponse() {
    return rpcResponse;
  }

  public void setRpcResponse(RpcResponse rpcResponse) {
    this.rpcResponse = rpcResponse;
  }
}
