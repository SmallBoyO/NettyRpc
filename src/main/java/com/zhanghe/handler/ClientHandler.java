package com.zhanghe.handler;

import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.zhanghe.protocol.RpcRequest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	/**
	 * 服务器的连接被建立后调用
	 */
	@Override
	public void channelActive( ChannelHandlerContext ctx ) throws Exception {
		RpcRequest re = new RpcRequest();
		re.setId("1");
		re.setClassName("com.zhanghe");
		re.setMethodName("test");
		re.setParametersVal(new Object[]{"1","2"});
		re.setTypeParameters(new Class[]{String.class,String.class});
		
		Kryo kryo = new Kryo();
	    Output output = new Output(new ByteArrayOutputStream());
	    kryo.writeObject(output, re);
	    output.toBytes();
	    System.out.println("发送rpc请求:"+re);
		ctx.writeAndFlush(Unpooled.copiedBuffer(output.toBytes()));
		output.close();
	}

	/**
	 * 捕捉到异常时调用
	 */
	@Override
	public void exceptionCaught( ChannelHandlerContext ctx ,Throwable cause ) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}


	@Override
	protected void channelRead0( ChannelHandlerContext ctx ,ByteBuf msg ) throws Exception {
		System.out.println("客户端接收:"+msg.toString(CharsetUtil.UTF_8));
		
	}
}
