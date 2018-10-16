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
import com.zhanghe.service.TestServiceImpl;

public class RpcServer {
	
	private int port;
	
	private Map<String, Object> handlerMap;

	public RpcServer( int port ,Map<String, Object> handlerMap ){
		super();
		this.port = port;
		System.out.println(handlerMap);
		this.handlerMap = handlerMap;
	}
	
	public void start() throws InterruptedException{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
        	ServerBootstrap b = new ServerBootstrap();
        	b.group(bossGroup, workerGroup)
        		.channel(NioServerSocketChannel.class)
        		.localAddress(port)
        		.option(ChannelOption.SO_KEEPALIVE,true)
        		.childHandler(new ServerChannelInnitializer(handlerMap));
        	
        	ChannelFuture f = b.bind().sync();
        	System.out.println("started and listening for connections on " + port);
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
		new RpcServer(6666,handlerMap).start();
		
	}

	@Override
	public String toString() {
		return "RpcServer [port=" + port + ", handlerMap=" + handlerMap + "]";
	}
}
