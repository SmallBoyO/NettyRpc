package com.zhanghe.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.zhanghe.ThreadPool.RpcThreadPool;
import com.zhanghe.protocol.RpcRequest;
import com.zhanghe.protocol.RpcResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.CharsetUtil;

@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter{
	
	private Map<String, Object> handlerMap;
	
	private Executor excutor;
	
	public ServerHandler( Map<String, Object> handlerMap ){
		super();
		this.excutor = RpcThreadPool.getExecutor(10, 0);
		this.handlerMap = handlerMap;
	}
	/**
	 * 每个信息入站都会调用
	 */
	@Override
	public void channelRead( ChannelHandlerContext ctx ,Object msg ) throws Exception {
		System.out.println("接收到rpc请求:"+this);
		RpcRequest req = (RpcRequest) msg;
//		ByteBuf bin = (ByteBuf) msg;
//		byte[] binbytes = new byte[bin.readableBytes()];
//		bin.readBytes(binbytes);
//		
//		ByteArrayInputStream bain = new ByteArrayInputStream(binbytes);
//		Kryo kryo = new Kryo();
//		Input input = new Input(bain);
//	    RpcRequest req = kryo.readObject(input, RpcRequest.class);
//	    input.close();
	    System.out.println("接收到rpc请求:"+req);
	    
	    excutor.execute(new ServerMethodExcutor(req, handlerMap,ctx.channel()));
//		RpcResponse res = new RpcResponse();
//		res.setId(req.getId());
//		res.setResult("SUCCESS");
//		
//	    Output output = new Output(new ByteArrayOutputStream());
//	    kryo.writeObject(output, res);
//	    output.toBytes();
//	    System.out.println("发送rpc结果:"+output);
//		ctx.writeAndFlush(Unpooled.copiedBuffer(output.toBytes()));
//		output.close();
		
	}
//	/**
//	 * 通知处理器最后的 channelread() 是当前批处理中的最后一条消息时调用
//	 */
//	@Override
//	public void channelReadComplete( ChannelHandlerContext ctx ) throws Exception {
//		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//	}
	/**
	 * 读操作时捕获到异常时调用
	 */
	@Override
	public void exceptionCaught( ChannelHandlerContext ctx ,Throwable cause ) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
