package com.zhanghe.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import com.zhanghe.RpcClientLoader;
import com.zhanghe.handler.ClientHandler;
import com.zhanghe.protocol.RpcRequest;

public class MessageSendProxy<T> implements InvocationHandler {
	
	private Bootstrap b ;
	
	
	public MessageSendProxy( Bootstrap b ){
		super();
		this.b = b;
	}


	@Override
	public Object invoke( Object proxy ,Method method ,Object[] args ) throws Throwable {
		ChannelFuture f = b.connect().sync();
		//f.channel().pipeline().get(ClientHandler.class).setChannel(f.channel());
		//RpcClientLoader.getInstance().setClientHandler(f.channel().pipeline().get(ClientHandler.class));
		System.out.println("invoke");
		RpcRequest req = new RpcRequest();
		req.setId(UUID.randomUUID().toString());
		req.setClassName(method.getDeclaringClass().getName());
		req.setMethodName(method.getName());
		req.setParametersVal(args);
		req.setTypeParameters(method.getParameterTypes());
		
		//ClientHandler clienthandler = RpcClientLoader.getInstance().getClientHandler();
		//System.out.println(clienthandler);
		MessageCallBack callback = f.channel().pipeline().get(ClientHandler.class).sendRequest(req);
		//f.channel().closeFuture().sync();
		return callback.start();
	}

}
