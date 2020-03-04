package com.zhanghe.test.spring;


import com.zhanghe.resource.annotation.RpcService;

@RpcService(value = "rpcservice",name = "")
public class DemoServiceImpl implements DemoService {

  @Override
  public String call(String requestParam) {
    return "requestParam:" + requestParam;
  }
}
