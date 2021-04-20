package com.zhanghe.test.testClient.service;

import com.zhanghe.rpc.core.client.AsyncMethod;
import java.util.concurrent.Future;

public interface AsyncService {

  @AsyncMethod
  String waitFiveSeconds(String str);

}
