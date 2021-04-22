package com.zhanghe.test.testClient.service;


import java.util.Random;

public class AsyncServiceImpl implements AsyncService {

  private static Random random = new Random();

  @Override
  public String waitFiveSeconds(String str) {
    try {
      Thread.sleep(2 * 1000L + random.nextInt(2000));
    }catch (InterruptedException e){

    }
    return str;
  }

}
