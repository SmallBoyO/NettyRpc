package com.zhanghe.test.testClient.service;

import java.util.concurrent.Future;

public class AsyncServiceImpl implements AsyncService {

  @Override
  public String waitFiveSeconds(String str) {
    try {
      System.out.println("str:"+str);
      System.out.println("thread:" + Thread.currentThread().getName());
      Thread.sleep(10 * 1000L);
    }catch (InterruptedException e){

    }
    return str;
  }

}
