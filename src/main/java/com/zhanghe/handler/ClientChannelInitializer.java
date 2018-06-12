package com.zhanghe.handler;

import com.zhanghe.decoder.ByteToResponseDecoder;
import com.zhanghe.encoder.RequestToByteEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

	//ObjectDecoder 底层默认继承半包解码器LengthFieldBasedFrameDecoder处理粘包问题的时候，
    //消息头开始即为长度字段，占据4个字节。这里出于保持兼容的考虑
    final public static int MESSAGE_LENGTH = 4;

	@Override
	protected void initChannel( SocketChannel socketChannel ) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		pipeline.addLast(new RequestToByteEncoder());
		pipeline.addLast(new ByteToResponseDecoder());
        pipeline.addLast(new ClientHandler());
	}
}
