package com.zhanghe.test.testClient.service;

//@RpcClient(value = "rpcclient")
public interface DemoService{

  String call(String requestParam);

}
