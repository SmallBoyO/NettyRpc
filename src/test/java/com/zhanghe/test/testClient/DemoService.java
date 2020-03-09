package com.zhanghe.test.testClient;

import com.zhanghe.resource.annotation.RpcClient;

//@RpcClient(value = "rpcclient")
public interface DemoService{

  String call(String requestParam);

}
