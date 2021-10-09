package com.zhanghe.rpc.core.plugin.client;

import java.lang.reflect.Method;

public interface RpcClientFilter {

  /**
   * 继续过滤
   * @param method
   * @param args
   * @param invoker
   * @param chain
   * @throws Throwable
   */
  void doFilter(Method method, Object[] args,Invoker invoker,RpcClientFilterChain chain) throws Throwable;

}
