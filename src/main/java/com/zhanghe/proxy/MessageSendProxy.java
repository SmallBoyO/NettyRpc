package com.zhanghe.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import com.zhanghe.RpcClientLoader;
import com.zhanghe.handler.ClientHandler;
import com.zhanghe.protocol.RpcRequest;

public class MessageSendProxy<T> implements InvocationHandler {
	@Override
	public Object invoke( Object proxy ,Method method ,Object[] args ) throws Throwable {
		System.out.println("invoke");
		RpcRequest req = new RpcRequest();
		req.setId(UUID.randomUUID().toString());
		req.setClassName(method.getDeclaringClass().getName());
		req.setMethodName(method.getName());
		req.setParametersVal(args);
		req.setTypeParameters(method.getParameterTypes());
		
		ClientHandler clienthandler = RpcClientLoader.getInstance().getClientHandler();
		System.out.println(clienthandler);
		MessageCallBack callback = clienthandler.sendRequest(req);
		return callback.start();
	}

}
