package com.zhanghe.handler;

import java.util.Map;
import com.zhanghe.decoder.ByteToRequestDecoder;
import com.zhanghe.encoder.ResponseToByteEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

public class ServerChannelInnitializer extends ChannelInitializer<SocketChannel>{
	
	private Map<String, Object> handlerMap;
	
	public ServerChannelInnitializer( Map<String, Object> handlerMap ){
		this.handlerMap = handlerMap;
	}
	
	@Override
	protected void initChannel( SocketChannel socketChannel ) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		//pipeline.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
		pipeline.addLast(new ResponseToByteEncoder());
		pipeline.addLast(new ByteToRequestDecoder());
        pipeline.addLast(new ServerHandler(handlerMap));
	}
	
}
