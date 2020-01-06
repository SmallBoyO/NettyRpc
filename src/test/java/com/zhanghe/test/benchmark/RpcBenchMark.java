package com.zhanghe.test.benchmark;

import com.zhanghe.rpc.RpcClient;
import com.zhanghe.rpc.RpcClientConnector;
import com.zhanghe.rpc.RpcServer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author zhanghe
 * @date 2019-11-21
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Thread)
public class RpcBenchMark {

  private BenchMarkService service;

  private BenchMarkService localService;

  @Setup
  public void init() throws ClassNotFoundException{
    RpcServer rpcServer = new RpcServer(7777);
    rpcServer.bind(new BenchMarkServiceImpl());
    rpcServer.start();
    RpcClient rpcClient = new RpcClient("127.0.0.1",7777);
    rpcClient.init();
    service = (BenchMarkService) rpcClient.proxy(BenchMarkService.class.getName());
    localService = new BenchMarkServiceImpl();
  }

  @Benchmark
  public void benchMarkWithProxy(Blackhole bh){
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
        .include(RpcBenchMark.class.getSimpleName())
        .forks(1)
        .build();
    new Runner(opt).run();
  }
}
