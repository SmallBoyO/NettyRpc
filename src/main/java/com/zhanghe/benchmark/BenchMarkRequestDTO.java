package com.zhanghe.benchmark;

import java.io.Serializable;

public class BenchMarkRequestDTO implements Serializable {

  public String requestInfo;

  public String getRequestInfo() {
    return requestInfo;
  }

  public void setRequestInfo(String requestInfo) {
    this.requestInfo = requestInfo;
  }

  @Override
  public String toString() {
    return "BenchMarkRequestDTO{" +
        "requestInfo='" + requestInfo + '\'' +
        '}';
  }
}
