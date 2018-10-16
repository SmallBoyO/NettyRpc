package com.zhanghe.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import com.zhanghe.handler.ClientHandler;
import com.zhanghe.protocol.RpcRequest;

public class MessageSendProxy<T> implements InvocationHandler {
	
	private Bootstrap b ;
	private ChannelFuture f;
	
	public MessageSendProxy( Bootstrap b ) throws InterruptedException{
		super();
		this.b = b;
		f = b.connect().sync();
	}
	
	public void close() throws InterruptedException{
		f.channel().closeFuture().sync();
	}

	@Override
	public Object invoke( Object proxy ,Method method ,Object[] args ) throws Throwable {
		f.channel().pipeline().get(ClientHandler.class).setChannel(f.channel());
		RpcRequest req = new RpcRequest();
		req.setId(UUID.randomUUID().toString());
		req.setClassName(method.getDeclaringClass().getName());
		req.setMethodName(method.getName());
		req.setParametersVal(args);
		req.setTypeParameters(method.getParameterTypes());
		
		MessageCallBack callback = f.channel().pipeline().get(ClientHandler.class).sendRequest(req);
		Object res = callback.start();
		return res;
	}

}
