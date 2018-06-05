package com.zhanghe.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.zhanghe.handler.ServerHandler;

public class RpcServer {
	
	private int port;

	public RpcServer( int port ){
		super();
		this.port = port;
	}
	
	public void start() throws InterruptedException{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
        	ServerBootstrap b = new ServerBootstrap();
        	b.group(bossGroup, workerGroup)
        		.channel(NioServerSocketChannel.class)
        		.localAddress(port)
        		.childHandler(new ServerHandler());
        	
        	ChannelFuture f = b.bind().sync();
        	System.out.println("started and listening for connections on " + port);
        	
        	f.channel().closeFuture().sync();
		}finally{
        	workerGroup.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
        }
	}
	
	public static void main( String[] args ) throws InterruptedException {
		new RpcServer(6666).start();
	}
}
