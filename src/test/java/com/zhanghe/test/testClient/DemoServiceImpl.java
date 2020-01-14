package com.zhanghe.test.testClient;


public class DemoServiceImpl implements DemoService{

  @Override
  public String call(String requestParam) {
    return "requestParam:" + requestParam;
  }
}
