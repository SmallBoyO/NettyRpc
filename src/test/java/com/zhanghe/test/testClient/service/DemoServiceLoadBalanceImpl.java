package com.zhanghe.test.testClient.service;

import com.zhanghe.test.testClient.service.DemoService;

public class DemoServiceLoadBalanceImpl implements DemoService {

  private String server;

  public DemoServiceLoadBalanceImpl(String server) {
    this.server = server;
  }

  @Override
  public String call(String requestParam) {
    return server + " requestParam:" + requestParam;
  }
}
