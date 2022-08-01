package com.zhanghe.test.testServer.service;

import com.zhanghe.rpc.core.client.AsyncMethod;

public interface GracefulShutdownService {

  @AsyncMethod
  String costSomeTimes(String str,Long waitTime);

  String costSomeTimes2(Long time,String str);

  void initMethod();

}
