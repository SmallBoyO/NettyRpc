package com.zhanghe.benchmark;

public class BenchMarkResponseDTO {

  public String responseInfo;

  public String getResponseInfo() {
    return responseInfo;
  }

  public void setResponseInfo(String responseInfo) {
    this.responseInfo = responseInfo;
  }

  @Override
  public String toString() {
    return "BenchMarkResponseDTO{" +
        "responseInfo='" + responseInfo + '\'' +
        '}';
  }
}
