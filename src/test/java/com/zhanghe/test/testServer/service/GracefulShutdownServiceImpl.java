package com.zhanghe.test.testServer.service;

public class GracefulShutdownServiceImpl implements GracefulShutdownService {

  @Override
  public String costSomeTimes(String str,Long waitTime) {
    try {
      Thread.sleep(waitTime);
    }catch (InterruptedException e){

    }
    return str;
  }

  @Override
  public String costSomeTimes2(Long time, String str) {
    try {
      Thread.sleep(time);
    }catch (InterruptedException e){

    }
    return str;
  }

  @Override
  public void initMethod() {

  }
}
