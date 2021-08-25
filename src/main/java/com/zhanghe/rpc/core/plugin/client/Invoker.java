package com.zhanghe.rpc.core.plugin.client;

import java.lang.reflect.Method;

public interface Invoker {

  void invoke(Method method, Object[] args) throws Throwable;

}
