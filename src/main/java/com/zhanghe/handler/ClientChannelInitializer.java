package com.zhanghe.handler;

import com.zhanghe.decoder.ByteToResponseDecoder;
import com.zhanghe.encoder.RequestToByteEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
	@Override
	protected void initChannel( SocketChannel socketChannel ) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		pipeline.addLast(new LineBasedFrameDecoder(1024));
		pipeline.addLast(new RequestToByteEncoder());
		pipeline.addLast(new ByteToResponseDecoder());
        pipeline.addLast(new ClientHandler());
	}
}
