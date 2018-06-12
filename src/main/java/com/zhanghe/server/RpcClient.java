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
	
	public RpcClient( String host ,int port ){
		super();
		this.host = host;
		this.port = port;
		init();
	}
	
	public void init(){
		this.socketaddress = new InetSocketAddress(host, port);
		b = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		b.group(group)
			.channel(NioSocketChannel.class)
			.remoteAddress(socketaddress)
			.handler(new ClientChannelInitializer());
		this.proxy = new MessageSendProxy<>(b);
	}
	
	public Object proxy(String serviceName) throws ClassNotFoundException{
		Class clazz = Class.forName(serviceName);
		return Proxy.newProxyInstance(
				clazz.getClassLoader(),
                new Class<?>[]{ clazz},
                proxy
        );
	}
	
	public static void main( String[] args ) throws InterruptedException, ClassNotFoundException {
		RpcClient rpc = new RpcClient("127.0.0.1", 6666);
		long start = System.currentTimeMillis();
		//rpc.start();
		long end = System.currentTimeMillis();

        System.out.println("over:"+(end-start));
        
        
        
        RpcRequest re = new RpcRequest();
		re.setId(UUID.randomUUID().toString());
		re.setClassName("com.zhanghe.service");
		re.setMethodName("TestService");
		re.setParametersVal(new Object[]{"1","2"});
		re.setTypeParameters(new Class[]{String.class,String.class});
		TestService service = (TestService) rpc.proxy("com.zhanghe.service.TestService");
		service.hello();
//
//        start = System.currentTimeMillis();
//		rpc.start();
//        end = System.currentTimeMillis();
//        System.out.println("over:"+(end-start));
//        start = System.currentTimeMillis();
//		rpc.start();
//        end = System.currentTimeMillis();
//        System.out.println("over:"+(end-start));
//        start = System.currentTimeMillis();
//		rpc.start();
//        end = System.currentTimeMillis();
//        System.out.println("over:"+(end-start));
	}
}
