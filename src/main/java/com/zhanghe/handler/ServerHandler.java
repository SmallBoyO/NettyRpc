package com.zhanghe.handler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.zhanghe.protocol.RpcRequest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.CharsetUtil;

@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter{
	/**
	 * 每个信息入站都会调用
	 */
	@Override
	public void channelRead( ChannelHandlerContext ctx ,Object msg ) throws Exception {
		System.out.println("接收到rpc请求:"+this);
		ByteBuf bin = (ByteBuf) msg;
		byte[] binbytes = new byte[bin.readableBytes()];
		bin.readBytes(binbytes);
		
		ByteArrayInputStream bain = new ByteArrayInputStream(binbytes);
		Kryo kryo = new Kryo();
		Input input = new Input(bain);
	    RpcRequest req = kryo.readObject(input, RpcRequest.class);
	    input.close();
	    System.out.println("接收到rpc请求:"+req);
	    ctx.write(Unpooled.copiedBuffer("接收到rpc", CharsetUtil.UTF_8));
	    ctx.writeAndFlush(Unpooled.copiedBuffer("接收到rpc", CharsetUtil.UTF_8));
	}
	/**
	 * 通知处理器最后的 channelread() 是当前批处理中的最后一条消息时调用
	 */
	@Override
	public void channelReadComplete( ChannelHandlerContext ctx ) throws Exception {
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
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
