package com.zhanghe.test.benchmark;

import com.zhanghe.protocol.serializer.impl.KyroSerializer;
import com.zhanghe.protocol.serializer.impl.ProtostuffSerializer;
import com.zhanghe.protocol.v1.request.RpcRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
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
  public void kryoSerializerBenchMark(Blackhole bh){
    byte[] bytes = KyroSerializer.INSTANCE.serialize(rpcRequest);
    bh.consume(KyroSerializer.INSTANCE.deserialize(RpcRequest.class,bytes));
  }
  @Benchmark
  public void protostuffSerializerBenchMark(Blackhole bh){
    byte[] bytes = ProtostuffSerializer.INSTANCE.serialize(rpcRequest);
    bh.consume(ProtostuffSerializer.INSTANCE.deserialize(RpcRequest.class,bytes));
  }
  @Benchmark
  public void jvmSerializerBenchMark(Blackhole bh){
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream os = new ObjectOutputStream(bos);
      os.writeObject(rpcRequest);
      os.flush();
      byte[] bytes = bos.toByteArray();
      os.close();
      bos.close();
      ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(bis);
      Object obj = ois.readObject();
      ois.close();
      bis.close();
      bh.consume((RpcRequest)obj);
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(SerilizerBenchMark.class.getSimpleName())
        .forks(2)
        .build();
    new Runner(opt).run();
  }
}
