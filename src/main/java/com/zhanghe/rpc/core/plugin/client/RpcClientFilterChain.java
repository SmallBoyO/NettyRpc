package com.zhanghe.rpc.core.plugin.client;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RpcClientFilterChain {

  public List<RpcClientFilter> filters;

  public int position;

  public RpcClientFilterChain() {
    filters = new ArrayList<>();
    position = 0;
  }

  public void addFilter(RpcClientFilter rpcClientFilter){
    this.filters.add(rpcClientFilter);
  }

  public void doFilter(Method method, Object[] args,Invoker invoker) throws Throwable{
    if(position <= (filters.size()-1)){
        filters.get(position++).doFilter(method,args,invoker,this);
      }else{
        invoker.invoke(method,args);
    }
  }

}
