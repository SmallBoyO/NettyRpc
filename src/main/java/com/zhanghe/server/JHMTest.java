package com.zhanghe.server;

import com.zhanghe.service.TestService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * JHMTest
 *
 * @author Clevo
 * @date 2018/6/28
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JHMTest {

    private RpcClient client ;

    private TestService service ;
    @Setup
    public void init() {
        client = new RpcClient("127.0.0.1",6666);
        try {
            service = (TestService)client.proxy("com.zhanghe.service.TestService");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    @Benchmark
    public void hello(){
        service.hello();
    }

    public static void main(String[] args) throws RunnerException{
        Options opt = new OptionsBuilder().include(JHMTest.class.getSimpleName()).forks(1).build();
        new Runner(opt).run();
    }
}
