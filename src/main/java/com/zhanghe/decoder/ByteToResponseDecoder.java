package com.zhanghe.decoder;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.zhanghe.protocol.RpcRequest;
import com.zhanghe.protocol.RpcResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ByteToResponseDecoder extends ByteToMessageDecoder {
	
	@Override
	protected void decode( ChannelHandlerContext ctx ,ByteBuf in ,List<Object> out ) throws Exception {
		byte[] binbytes = new byte[in.readableBytes()];
		in.readBytes(binbytes);
		
		ByteArrayInputStream bain = new ByteArrayInputStream(binbytes);
		Kryo kryo = new Kryo();
		Input input = new Input(bain);
	    RpcResponse res = kryo.readObject(input, RpcResponse.class);
	    input.close();
	    out.add(res);
	}
}
