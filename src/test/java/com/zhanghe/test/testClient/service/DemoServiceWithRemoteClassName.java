package com.zhanghe.test.testClient.service;

import com.zhanghe.spring.annotation.RpcClient;

@RpcClient(value = "demoService",remoteClassName = "com.zhanghe.test.testClient.service.DemoService")
public interface DemoServiceWithRemoteClassName {

  String call(String str);

}
