package com.zhanghe.server;

import java.util.HashMap;
import java.util.Map;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.zhanghe.handler.ServerChannelInnitializer;
import com.zhanghe.handler.ServerHandler;
import com.zhanghe.service.TestService;
import com.zhanghe.service.TestServiceImpl;

public class RpcServer {
	
	private int port;

	public RpcServer( int port ){
		super();
		this.port = port;
	}
	
	public void start(Map<String, Object> handlerMap) throws InterruptedException{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
        	ServerBootstrap b = new ServerBootstrap();
        	b.group(bossGroup, workerGroup)
        		.channel(NioServerSocketChannel.class)
        		.localAddress(port)
        		//通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
        		.option(ChannelOption.SO_KEEPALIVE,true)
        		.childHandler(new ServerChannelInnitializer(handlerMap));
        	
        	ChannelFuture f = b.bind().sync();
        	System.out.println("started and listening for connections on " + port+","+f.channel().pipeline().get(ServerHandler.class));
			//RpcServerLoader.getInstance().setServerHandler(f.channel().pipeline().get(ServerHandler.class));
        	f.channel().closeFuture().sync();
		}finally{
        	workerGroup.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
        }
	}
	
	public static void main( String[] args ) throws InterruptedException {
		TestServiceImpl test = new TestServiceImpl();
		Map<String, Object> handlerMap = new HashMap<>();
		handlerMap.put(test.getClass().getInterfaces()[0].getName(), test);
		new RpcServer(6666).start(handlerMap);
	}
}
