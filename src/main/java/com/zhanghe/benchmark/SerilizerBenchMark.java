package com.zhanghe.benchmark;

import com.esotericsoftware.kryo.Kryo;
import com.zhanghe.protocol.serializer.impl.KyroSerializer;
import com.zhanghe.protocol.serializer.impl.ProtostuffSerializer;
import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.rpc.RpcClient;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.UUID;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Thread)
public class SerilizerBenchMark {

  RpcRequest rpcRequest = new RpcRequest();

  @Setup
  public void init(){
    rpcRequest = new RpcRequest();
    BenchMarkRequestDTO benchMarkRequestDTO = new BenchMarkRequestDTO();
    benchMarkRequestDTO.setRequestInfo("test ");
    rpcRequest.setClassName("com.zhanghe.benchmark.service.BenchmarkService");
    rpcRequest.setRequestId(UUID.randomUUID().toString());
    rpcRequest.setTypeParameters(new Class[]{BenchMarkRequestDTO.class});
    rpcRequest.setMethodName("call");
    rpcRequest.setParametersVal(new Object[]{benchMarkRequestDTO});
  }


  @Benchmark
  public void KryoSerializerBenchMark(Blackhole bh){
    byte[] bytes = KyroSerializer.INSTANCE.serialize(rpcRequest);
    bh.consume(KyroSerializer.INSTANCE.deserialize(RpcRequest.class,bytes));
  }
  @Benchmark
  public void ProtostuffSerializerBenchMark(Blackhole bh){
    byte[] bytes = ProtostuffSerializer.INSTANCE.serialize(rpcRequest);
    bh.consume(ProtostuffSerializer.INSTANCE.deserialize(RpcRequest.class,bytes));
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(SerilizerBenchMark.class.getSimpleName())
        .forks(1)
        .build();
    new Runner(opt).run();
  }
}
