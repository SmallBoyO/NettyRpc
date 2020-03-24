package com.zhanghe.test.testClient;

//@RpcClient(value = "rpcclient")
public interface DemoService{

  String call(String requestParam);

}
