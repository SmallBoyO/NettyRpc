package com.zhanghe.rpc.core.plugin.client;

import java.lang.reflect.Method;

public class AbstractRpcClientFilter implements RpcClientFilter {

  public String s;

  public AbstractRpcClientFilter(String s) {
    this.s = s;
  }

  @Override
  public void doFilter(Object proxy, Method method, Object[] args,Invoker invoker, RpcClientFilterChain chain)
      throws Throwable {
    System.out.println(s + ",start");
    chain.doFilter(proxy,method,args,invoker);
    System.out.println(s + ",end");
  }
}
