package com.zhanghe.rpc.core.plugin.client;

import java.lang.reflect.Method;

public interface Invoker {

  /**
   * 执行method
   * @param method
   * @param args
   * @throws Throwable
   */
  void invoke(Method method, Object[] args) throws Throwable;

}
