package com.zhanghe.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.zhanghe.protocol.RpcRequest;
import com.zhanghe.protocol.RpcResponse;

public class ServerMethodExcutor implements Runnable {
	
	/**
	 * 请求
	 */
	private RpcRequest request;
	/**
	 * 结果
	 */
	private RpcResponse response;
	/**
	 * 服务端注册的service集合
	 */
	private Map<String, Object> handlerMap;
	
	private ChannelHandlerContext ctx;
	
	private Channel channel;
	
	
	public ServerMethodExcutor( RpcRequest request ,Map<String, Object> handlerMap ,Channel channel){
		super();
		this.request = request;
		this.handlerMap = handlerMap;
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
	    channel.writeAndFlush(response);
	}
	
	/**
	 * 通过反射调用handlerMap中对应service的方法
	 * @param request
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @return Object
	 */
	public Object excuteByReflect(RpcRequest request) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		String className = request.getClassName();
		Object serviceBean = handlerMap.get(className);
		String methodName = request.getMethodName();
		Object[] params = request.getParametersVal();
		return serviceBean.getClass().getMethod(methodName, request.getTypeParameters()).invoke(serviceBean, params);
	}

}
