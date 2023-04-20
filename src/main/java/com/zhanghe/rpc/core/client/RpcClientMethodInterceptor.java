package com.zhanghe.rpc.core.client;

import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.protocol.v1.response.RpcResponse;
import com.zhanghe.rpc.core.exception.RpcException;
import com.zhanghe.rpc.core.plugin.client.AsyncInvoker;
import com.zhanghe.rpc.core.plugin.client.BaseInvoker;
import com.zhanghe.rpc.core.plugin.client.RpcClientFilter;
import com.zhanghe.rpc.core.plugin.client.RpcClientFilterChain;
import com.zhanghe.spring.annotation.RpcMethod;
import io.netty.channel.Channel;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class RpcClientMethodInterceptor implements MethodInterceptor {

  private static Logger logger = LoggerFactory.getLogger(RpcClientMethodInterceptor.class);

  private Client client;

  public List<RpcClientFilter> filters;

  public String remoteClassName;

  public RpcClientMethodInterceptor(String remoteClassName, List<RpcClientFilter> filters,Client client) {
    this.remoteClassName = remoteClassName;
    this.filters = filters;
    this.client = client;
  }

  @Override
  public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
      throws Throwable {
    if(!client.isStarted()){
      String error = "client not start or client has stoped";
      throw new RuntimeException(error);
    }
    if(method.getAnnotation(AsyncMethod.class) == null){
      //是同步调用
      RpcClientFilterChain rpcClientFilterChain = new RpcClientFilterChain();
      this.filters.forEach(rpcClientFilter -> {
        rpcClientFilterChain.addFilter(rpcClientFilter);
      });
      BaseInvoker baseInvoker = new BaseInvoker(this);
      rpcClientFilterChain.doFilter(method,objects, baseInvoker);
      RpcResponse result = baseInvoker.getRpcResponse();
      if (result.isSuccess()) {
        return result.getResult();
      } else {
          throw new RpcException(result.getExceptionMessage());
      }
    }else{
      RpcClientFilterChain rpcClientFilterChain = new RpcClientFilterChain();
      this.filters.forEach(rpcClientFilter -> {
        rpcClientFilterChain.addFilter(rpcClientFilter);
      });
      AsyncInvoker asyncInvoker = new AsyncInvoker(this);
      rpcClientFilterChain.doFilter(method,objects, asyncInvoker);
      return null;
    }
  }
  public RpcResponse call( Method method, Object[] args) throws TimeoutException {
    Channel channel = client.currentServer().getRpcClientConnector().getActiveChannel();
    if (!channel.isActive()) {
      throw new IllegalStateException("rpc server disconnected!");
    }
    RpcRequest rpcRequest = new RpcRequest();
    String requestId = UUID.randomUUID().toString();
    rpcRequest.setRequestId(requestId);
    rpcRequest.setClassName(remoteClassName);
    rpcRequest.setMethodName(getRemoteMethodName(method));
    rpcRequest.setTypeParameters(method.getParameterTypes());
    rpcRequest.setParametersVal(args);
    RpcRequestCallBack callBack = new RpcRequestCallBack(requestId);

    RpcRequestCallBackholder.callBackMap.put(rpcRequest.getRequestId(), callBack);
    channel.writeAndFlush(rpcRequest);
    RpcResponse result = callBack.start();
    if(result == null && !channel.isActive()){
      throw new IllegalStateException("rpc server disconnected!");
    }
    return result;
  }

  public Future asyncCall(Method method, Object[] args){
    Channel channel = client.currentServer().getRpcClientConnector().getActiveChannel();
    if (!channel.isActive()) {
      throw new IllegalStateException("rpc server disconnected!");
    }
    RpcRequest rpcRequest = new RpcRequest();
    String requestId = UUID.randomUUID().toString();
    rpcRequest.setRequestId(requestId);
    rpcRequest.setClassName(remoteClassName);
    rpcRequest.setMethodName(getRemoteMethodName(method));
    rpcRequest.setTypeParameters(method.getParameterTypes());
    rpcRequest.setParametersVal(args);
    RpcRequestCallBack callBack = new RpcRequestCallBack(requestId);

    RpcRequestCallBackholder.callBackMap.put(rpcRequest.getRequestId(), callBack);
    channel.writeAndFlush(rpcRequest);
    if(!channel.isActive()){
      throw new IllegalStateException("rpc server disconnected!");
    }
    return new Future() {
      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
      }

      @Override
      public boolean isCancelled() {
        return false;
      }

      @Override
      public boolean isDone() {
        return callBack.isDone();
      }

      @Override
      public Object get() throws InterruptedException, ExecutionException {
        try {
          RpcResponse result = callBack.get(null,null);
          return result.getResult();
        }catch (Exception e){
          throw new ExecutionException(e);
        }
      }

      @Override
      public Object get(long timeout, TimeUnit unit)
          throws InterruptedException, ExecutionException, TimeoutException {
        RpcResponse result = callBack.get(timeout,unit);
        return result.getResult();
      }
    };
  }


  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public List<RpcClientFilter> getFilters() {
    return filters;
  }

  public void setFilters(List<RpcClientFilter> filters) {
    this.filters = filters;
  }

  /**
   * 获取需要调用的服务端方法名
   * @param method
   * @return
   */
  private String getRemoteMethodName(Method method){
    RpcMethod  rpcMethod = method.getAnnotation(RpcMethod.class);
    if(rpcMethod != null && !"".equals(rpcMethod.value())){
      return rpcMethod.value();
    }
    return method.getName();
  }
}
