package com.zhanghe.handler;

import java.util.Map;
import java.util.concurrent.Executor;

import com.zhanghe.ThreadPool.RpcThreadPool;
import com.zhanghe.decoder.ByteToRequestDecoder;
import com.zhanghe.encoder.ResponseToByteEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ServerChannelInnitializer extends ChannelInitializer<SocketChannel>{
	
	private Map<String, Object> handlerMap;
	
	public ServerChannelInnitializer( Map<String, Object> handlerMap ){
		this.handlerMap = handlerMap;
	}
	
	@Override
	protected void initChannel( SocketChannel socketChannel ) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		pipeline.addLast(new ResponseToByteEncoder());
		pipeline.addLast(new ByteToRequestDecoder());
        pipeline.addLast(new ServerHandler(handlerMap));
	}
	
}
