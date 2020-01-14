package com.zhanghe.test.testClient;

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
