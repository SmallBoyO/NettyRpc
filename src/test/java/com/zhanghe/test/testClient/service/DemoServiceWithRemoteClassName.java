package com.zhanghe.test.testClient.service;

import com.zhanghe.spring.annotation.RpcClient;

@RpcClient(remoteClassName = "com.zhanghe.test.testClient.service.DemoService")
public interface DemoServiceWithRemoteClassName {

  String call(String str);

}
