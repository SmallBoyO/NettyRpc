package com.zhanghe.test.spring.service;


import com.zhanghe.spring.annotation.RpcService;

@RpcService(value = "rpcservice")
public class DemoServiceImpl implements DemoService {

  @Override
  public String call(String requestParam) {
    return "requestParam:" + requestParam;
  }
}
