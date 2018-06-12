package com.zhanghe.handler;

import java.util.Map;
import java.util.concurrent.Executor;
import com.zhanghe.ThreadPool.RpcThreadPool;
import com.zhanghe.protocol.RpcRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter{
	
	private Map<String, Object> handlerMap;
	
	private Executor excutor;
	
	private final static int parallel = Runtime.getRuntime().availableProcessors() * 2;
	
	public ServerHandler( Map<String, Object> handlerMap ){
		super();
		this.excutor = RpcThreadPool.getExecutor(parallel, 0);
		this.handlerMap = handlerMap;
	}
	/**
	 * 每个信息入站都会调用
	 */
	@Override
	public void channelRead( ChannelHandlerContext ctx ,Object msg ) throws Exception {
		RpcRequest req = (RpcRequest) msg;
	    excutor.execute(new ServerMethodExcutor(req, handlerMap,ctx.channel()));
		
	}
	/**
	 * 读操作时捕获到异常时调用
	 */
	@Override
	public void exceptionCaught( ChannelHandlerContext ctx ,Throwable cause ) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
