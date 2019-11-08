package com.zhanghe.benchmark;

public class BenchMarkServiceImpl implements BenchMarkService {

  @Override
  public BenchMarkResponseDTO call(BenchMarkRequestDTO benchMarkRequestDTO) {
    BenchMarkResponseDTO response = new BenchMarkResponseDTO();
    response.setResponseInfo(benchMarkRequestDTO.getRequestInfo());
    return response;
  }
}
