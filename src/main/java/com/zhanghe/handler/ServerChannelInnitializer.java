package com.zhanghe.handler;

import java.util.Map;
import com.zhanghe.decoder.ByteToRequestDecoder;
import com.zhanghe.decoder.RequestHandler;
import com.zhanghe.decoder.ResponseHandler;
import com.zhanghe.encoder.ResponseToByteEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;

public class ServerChannelInnitializer extends ChannelInitializer<SocketChannel>{
	
	private Map<String, Object> handlerMap;
	
	public ServerChannelInnitializer( Map<String, Object> handlerMap ){
		this.handlerMap = handlerMap;
	}
	
	@Override
	protected void initChannel( SocketChannel socketChannel ) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
		pipeline.addLast(RequestHandler.INSTANCE);
		pipeline.addLast(ResponseHandler.INSTANCE);
        pipeline.addLast(new ServerHandler(handlerMap));
	}
	
}
