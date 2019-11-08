package com.zhanghe.benchmark;

import com.zhanghe.rpc.RpcClient;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Thread)
public class JMHBenchMark {

  private BenchMarkService service;

  private BenchMarkService localService;

  @Setup
  public void init() throws ClassNotFoundException{
    RpcClient rpcClient = new RpcClient("127.0.0.1",7777);
    rpcClient.start();
    service = (BenchMarkService)rpcClient.proxy(BenchMarkService.class.getName());
    localService = new BenchMarkServiceImpl();
  }

  @Benchmark
  public void benchMark_with_proxy(Blackhole bh){
    BenchMarkRequestDTO benchMarkRequestDTO = new BenchMarkRequestDTO();
    benchMarkRequestDTO.setRequestInfo("test ");
    bh.consume(service.call(benchMarkRequestDTO));
  }

  @Benchmark
  public void benchMark(Blackhole bh){
    BenchMarkRequestDTO benchMarkRequestDTO = new BenchMarkRequestDTO();
    benchMarkRequestDTO.setRequestInfo("test ");
    bh.consume(localService.call(benchMarkRequestDTO));
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(JMHBenchMark.class.getSimpleName())
        .forks(1)
        .build();
    new Runner(opt).run();
  }
}
