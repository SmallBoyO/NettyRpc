package com.zhanghe.handler;

import com.zhanghe.decoder.ByteToResponseDecoder;
import com.zhanghe.decoder.RequestHandler;
import com.zhanghe.decoder.ResponseHandler;
import com.zhanghe.encoder.RequestToByteEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
	@Override
	protected void initChannel( SocketChannel socketChannel ) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));

		pipeline.addLast(ResponseHandler.INSTANCE);
		pipeline.addLast(RequestHandler.INSTANCE);
        pipeline.addLast(new ClientHandler());
	}
}
