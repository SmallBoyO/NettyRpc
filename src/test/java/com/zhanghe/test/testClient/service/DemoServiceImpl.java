package com.zhanghe.test.testClient.service;


public class DemoServiceImpl implements DemoService{

  @Override
  public String call(String requestParam) {
    return "requestParam:" + requestParam;
  }
}
