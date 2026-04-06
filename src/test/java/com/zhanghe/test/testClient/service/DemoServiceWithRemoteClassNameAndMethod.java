package com.zhanghe.test.testClient.service;

import com.zhanghe.spring.annotation.RpcClient;
import com.zhanghe.spring.annotation.RpcMethod;

@RpcClient(remoteClassName = "com.zhanghe.test.testClient.service.DemoService")
public interface DemoServiceWithRemoteClassNameAndMethod {

  @RpcMethod("call")
  String localCall(String str);

}
