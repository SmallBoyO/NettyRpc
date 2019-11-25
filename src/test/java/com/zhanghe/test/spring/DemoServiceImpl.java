package com.zhanghe.test.spring;


public class DemoServiceImpl implements DemoService {

  @Override
  public String call(String requestParam) {
    return "requestParam:" + requestParam;
  }
}
