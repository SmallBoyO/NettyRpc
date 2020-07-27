package com.zhanghe.rpc.core.plugin.client;

import java.lang.reflect.Method;

public interface RpcClientFilter {

  void doFilter(Object proxy, Method method, Object[] args,Invoker invoker,RpcClientFilterChain chain) throws Throwable;

}
