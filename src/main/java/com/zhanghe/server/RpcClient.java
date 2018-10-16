package com.zhanghe.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.UUID;

import com.zhanghe.handler.ClientChannelInitializer;
import com.zhanghe.protocol.RpcRequest;
import com.zhanghe.proxy.MessageSendProxy;
import com.zhanghe.service.TestService;

public class RpcClient {
	private String host;
	private int port;
	private InetSocketAddress socketaddress;
	public Bootstrap b ;
	MessageSendProxy proxy;
	EventLoopGroup group;
	public RpcClient( String host ,int port ) throws InterruptedException{
		super();
		this.host = host;
		this.port = port;
		init();
	}
	
	public void init() throws InterruptedException{
		this.socketaddress = new InetSocketAddress(host, port);
		b = new Bootstrap();
		group = new NioEventLoopGroup();
		b.group(group)
			.channel(NioSocketChannel.class)
			.remoteAddress(socketaddress)
			.handler(new ClientChannelInitializer());
		this.proxy = new MessageSendProxy<>(b);
	}
	
	public void close() throws InterruptedException{
		proxy.close();
		if(group!=null){
			group.shutdownGracefully().sync();
		}
	}
	
	public Object proxy(String serviceName) throws ClassNotFoundException{
		Class<?> clazz = Class.forName(serviceName);
		return Proxy.newProxyInstance(
				clazz.getClassLoader(),
                new Class<?>[]{ clazz },
                proxy
        );
	}
	
	public static void main( String[] args ) throws InterruptedException, ClassNotFoundException {
		RpcClient rpc = new RpcClient("127.0.0.1", 6666);
		TestService service = (TestService) rpc.proxy("com.zhanghe.service.TestService");
		System.out.println(service.hello());
	}
}
