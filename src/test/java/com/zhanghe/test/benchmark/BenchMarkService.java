package com.zhanghe.test.benchmark;

public interface BenchMarkService {

  /**
   * 调用
   * @param benchMarkRequestDTO
   * @return
   */
  BenchMarkResponseDTO call(BenchMarkRequestDTO benchMarkRequestDTO);

}
