package com.zhanghe.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

import com.zhanghe.handler.ClientHandler;

public class RpcClient {
	private String host;
	private int port;
	
	public RpcClient( String host ,int port ){
		super();
		this.host = host;
		this.port = port;
	}
	
	public void start() throws InterruptedException{
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioSocketChannel.class)
			.remoteAddress(new InetSocketAddress(host, port))
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel( SocketChannel ch ) throws Exception {
					ch.pipeline().addLast(new ClientHandler());
				}
			});
			
			ChannelFuture f = b.connect().sync();
			
			f.channel().closeFuture().sync();
		}finally{
			group.shutdownGracefully().sync();
		}
	}
	
	public static void main( String[] args ) throws InterruptedException {
        new RpcClient("127.0.0.1", 6666).start();
	}
}
