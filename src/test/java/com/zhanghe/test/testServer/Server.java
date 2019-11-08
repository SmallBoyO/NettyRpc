package com.zhanghe.test.testServer;


import com.zhanghe.benchmark.BenchMarkRequestDTO;
import com.zhanghe.benchmark.BenchMarkResponseDTO;
import com.zhanghe.benchmark.BenchMarkServiceImpl;
import com.zhanghe.protocol.v1.Command;
import com.zhanghe.rpc.RpcServer;

public class Server {

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer(7777);
        rpcServer.bind(new BenchMarkServiceImpl());
        Byte request = 5;
        Byte response = 6;
        Command.rigester(request,BenchMarkRequestDTO.class);
        Command.rigester(response,BenchMarkResponseDTO.class);
        rpcServer.start();
    }

}
