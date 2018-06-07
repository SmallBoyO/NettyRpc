package com.zhanghe.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.UUID;

import com.zhanghe.RpcClientLoader;
import com.zhanghe.handler.ClientChannelInitializer;
import com.zhanghe.handler.ClientHandler;
import com.zhanghe.protocol.RpcRequest;
import com.zhanghe.proxy.MessageCallBack;
import com.zhanghe.proxy.MessageSendProxy;
import com.zhanghe.service.TestService;

public class RpcClient<T> {
	private String host;
	private int port;
	private InetSocketAddress socketaddress;
	public Bootstrap b ;
	public RpcClient( String host ,int port ){
		super();
		this.socketaddress = new InetSocketAddress(host, port);
		b = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		b.group(group)
		.channel(NioSocketChannel.class)
		.remoteAddress(socketaddress)
		.handler(new ClientHandler());
	}
	
	public void start() throws InterruptedException{
//			Bootstrap b = new Bootstrap();
//	        b.group(group)
//	                .channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true);
//	        b.handler(new ClientHandler());
//
//	        ChannelFuture channelFuture = b.connect(new InetSocketAddress(host, port));
//	        channelFuture.addListener(new ChannelFutureListener() {
//	            public void operationComplete(final ChannelFuture channelFuture) throws Exception {
//	                if (channelFuture.isSuccess()) {
//	                	System.out.println("channelFuture");
//	                	ClientHandler handler = channelFuture.channel().pipeline().get(ClientHandler.class);
//	                    RpcClientLoader.getInstance().setClientHandler(handler);
//	                    RpcRequest re = new RpcRequest();
//	            		re.setId("1");
//	            		re.setClassName("com.zhanghe");
//	            		re.setMethodName("test");
//	            		re.setParametersVal(new Object[]{"1","2"});
//	            		re.setTypeParameters(new Class[]{String.class,String.class});
//	            	    System.out.println("发送rpc请求:"+re);
//	                    MessageCallBack callback = RpcClientLoader.getInstance().getClientHandler().sendRequest(re);
//	                    callback.start();
//	                }
//	            }
//	        });
			ChannelFuture f = b.connect().sync();
			//System.out.println("connected");
//			RpcRequest re = new RpcRequest();
//			re.setId(UUID.randomUUID().toString());
//			re.setClassName("com.zhanghe");
//			re.setMethodName("test");
//			re.setParametersVal(new Object[]{"1","2"});
//			re.setTypeParameters(new Class[]{String.class,String.class});
			f.channel().pipeline().get(ClientHandler.class).setChannel(f.channel());
			RpcClientLoader.getInstance().setClientHandler(f.channel().pipeline().get(ClientHandler.class));
//			f.channel().pipeline().get(ClientHandler.class).sendRequest(re);
//			MessageSendProxy proxy = new MessageSendProxy<>(b);
//			TestService service = (TestService) Proxy.newProxyInstance(
//	                TestService.class.getClassLoader(),
//	                new Class<?>[]{ TestService.class},
//	                proxy
//	        );
//			service.hello();
//			service.hello();
//			f.channel().closeFuture().sync();
	}
	
	public static void main( String[] args ) throws InterruptedException {
		RpcClient rpc = new RpcClient("127.0.0.1", 6666);
		long start = System.currentTimeMillis();
		rpc.start();
		long end = System.currentTimeMillis();

        System.out.println("over:"+(end-start));
        
		
        start = System.currentTimeMillis();
		rpc.start();
        end = System.currentTimeMillis();
        System.out.println("over:"+(end-start));
        start = System.currentTimeMillis();
		rpc.start();
        end = System.currentTimeMillis();
        System.out.println("over:"+(end-start));
        start = System.currentTimeMillis();
		rpc.start();
        end = System.currentTimeMillis();
        System.out.println("over:"+(end-start));
	}
}
