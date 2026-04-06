package com.zhanghe.test.spring.service;

import com.zhanghe.spring.annotation.RpcClient;

@RpcClient
public interface DemoService{

  String call(String requestParam);

}
