package com.zhanghe.test.testClient.filter;

import com.zhanghe.rpc.core.plugin.client.Invoker;
import com.zhanghe.rpc.core.plugin.client.RpcClientFilter;
import com.zhanghe.rpc.core.plugin.client.RpcClientFilterChain;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoFilter implements RpcClientFilter{


  private String filterName;

  public DemoFilter(String filterName) {
    this.filterName = filterName;
  }

  private static Logger logger = LoggerFactory.getLogger(DemoFilter.class);

  @Override
  public void doFilter( Method method, Object[] args, Invoker invoker,
      RpcClientFilterChain chain) throws Throwable {
    logger.info("before {}",filterName);
    chain.doFilter(method,args,invoker);
    logger.info("after {}",filterName);
  }
}
