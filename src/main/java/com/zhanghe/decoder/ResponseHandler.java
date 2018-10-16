package com.zhanghe.decoder;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.zhanghe.protocol.RpcRequest;
import com.zhanghe.protocol.RpcResponse;
import com.zhanghe.util.KryoUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageCodec;

@Sharable
public class ResponseHandler extends MessageToMessageCodec<ByteBuf, RpcResponse> {

	public static final ResponseHandler INSTANCE = new ResponseHandler();
	
	@Override
	protected void encode( ChannelHandlerContext ctx ,RpcResponse response ,List<Object> out ) throws Exception {
		ByteBuf byteBuf = ctx.channel().alloc().ioBuffer();
		KryoUtil.ObjectToByteAndWriteToByteBuff(response,byteBuf);
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
	    RpcResponse res = kryo.readObject(input, RpcResponse.class);
	    input.close();
	    out.add(res);
	}

}
