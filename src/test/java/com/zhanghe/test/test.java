package com.zhanghe.test;

import com.zhanghe.server.RpcClient;
import com.zhanghe.service.TestService;

public class test {
	public static void main( String[] args ) throws InterruptedException, ClassNotFoundException {
//		InetSocketAddress socketaddress = new InetSocketAddress("127.0.0.1", 6666);
//		Bootstrap b = new Bootstrap();
//		NioEventLoopGroup group = new NioEventLoopGroup();
//		b.group(group)
//			.channel(NioSocketChannel.class)
//			.remoteAddress(socketaddress)
//			.handler(new ClientChannelInitializer());
//		MessageSendProxy proxy = new MessageSendProxy<>(b);
//		ChannelFuture f = b.connect().sync();
//		f.channel().pipeline().get(ClientHandler.class).setChannel(f.channel());
//		RpcRequest req = new RpcRequest();
//		req.setId(UUID.randomUUID().toString());
//		req.setClassName("com.zhanghe.service.TestService");
//		req.setMethodName("hello");
//		req.setParametersVal(args);
//		req.setTypeParameters(new Class[]{});
//		
//		MessageCallBack callback = f.channel().pipeline().get(ClientHandler.class).sendRequest(req);
//		Object res = callback.start();
//		System.out.println(res);
//		f.channel().closeFuture().sync();
//		group.shutdownGracefully().sync();
		
		RpcClient r = new RpcClient("127.0.0.1", 6666);
		TestService testService = (TestService) r.proxy(TestService.class.getName());
		System.out.println(testService.hello());
		r.close();
		
	}
}
