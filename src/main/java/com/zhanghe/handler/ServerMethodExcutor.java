package com.zhanghe.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.zhanghe.protocol.RpcRequest;
import com.zhanghe.protocol.RpcResponse;

public class ServerMethodExcutor implements Runnable {

	private RpcRequest request;
	private RpcResponse response;
	private Map<String, Object> handlerMap;
	private ChannelHandlerContext ctx;
	private Channel channel;
	
	
	public ServerMethodExcutor( RpcRequest request ,Map<String, Object> handlerMap ,ChannelHandlerContext ctx ,Channel channel){
		super();
		this.request = request;
		this.handlerMap = handlerMap;
		this.ctx = ctx;
		this.channel = channel;
	}



	@Override
	public void run() {
		response = new RpcResponse();
		response.setId(request.getId());
		try{
			response.setResult(excuteByReflect(request));
		}catch (Exception e){
			response.setException(e);
			e.printStackTrace();
		}
		Output output = new Output(new ByteArrayOutputStream());
		Kryo kryo = new Kryo();
	    kryo.writeObject(output, response);
	    System.out.println("发送rpc结果:"+output);
	    channel.writeAndFlush(Unpooled.copiedBuffer(output.toBytes()));
	}
	
	public Object excuteByReflect(RpcRequest request) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		String className = request.getClassName();
		Object serviceBean = handlerMap.get(className);
		String methodName = request.getMethodName();
		Object[] params = request.getParametersVal();
		return serviceBean.getClass().getMethod(methodName, request.getTypeParameters()).invoke(serviceBean, params);
	}

}