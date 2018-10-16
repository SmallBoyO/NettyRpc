package com.zhanghe.decoder;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.zhanghe.handler.ServerHandler;
import com.zhanghe.protocol.RpcRequest;
import com.zhanghe.util.KryoUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageCodec;

@Sharable
public class RequestHandler extends MessageToMessageCodec<ByteBuf, RpcRequest> {

	public static final RequestHandler INSTANCE = new RequestHandler();
	
	@Override
	protected void encode( ChannelHandlerContext ctx ,RpcRequest request ,List<Object> out ) throws Exception {
		ByteBuf byteBuf = ctx.channel().alloc().ioBuffer();
		KryoUtil.ObjectToByteAndWriteToByteBuff(request,byteBuf);
		out.add(byteBuf);
	}

	@Override
	protected void decode( ChannelHandlerContext ctx ,ByteBuf in ,List<Object> out ) throws Exception {
		Integer length = in.readInt();
		byte[] binbytes = new byte[length];
		in.readBytes(binbytes);
		ByteArrayInputStream bain = new ByteArrayInputStream(binbytes);
		Kryo kryo = new Kryo();
		Input input = new Input(bain);
	    RpcRequest req = kryo.readObject(input, RpcRequest.class);
	    input.close();
	    out.add(req);
	}

}
