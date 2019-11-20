package com.zhanghe.test.testServer;


import com.zhanghe.benchmark.BenchMarkRequestDTO;
import com.zhanghe.benchmark.BenchMarkResponseDTO;
import com.zhanghe.benchmark.BenchMarkServiceImpl;
import com.zhanghe.protocol.v1.Command;
import com.zhanghe.rpc.RpcServer;
import com.zhanghe.service.DateServiceImpl;

public class Server {

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer(7777);
        rpcServer.bind(new BenchMarkServiceImpl());
        rpcServer.bind(new DateServiceImpl());
        Byte request = 7;
        Byte response = 8;
        Command.rigester(request,BenchMarkRequestDTO.class);
        Command.rigester(response,BenchMarkResponseDTO.class);
        rpcServer.start();
    }

}
