package com.zhanghe.decoder;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.zhanghe.protocol.RpcRequest;
import com.zhanghe.protocol.RpcResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
/**
 * ByteToRequestDecoder
 * RpcRequest解码器
 * @author Clevo
 * @date 2018/6/12 23:01
 */
public class ByteToRequestDecoder extends ByteToMessageDecoder {
	
	@Override
	protected void decode( ChannelHandlerContext ctx ,ByteBuf in ,List<Object> out ) throws Exception {
		doDecode(ctx, in, out);
	}
	
	public boolean doDecode(ChannelHandlerContext ctx ,ByteBuf in ,List<Object> out){
		Integer length = in.readInt();
		byte[] binbytes = new byte[length];
		in.readBytes(binbytes);
		ByteArrayInputStream bain = new ByteArrayInputStream(binbytes);
		Kryo kryo = new Kryo();
		Input input = new Input(bain);
	    RpcRequest req = kryo.readObject(input, RpcRequest.class);
	    input.close();
	    out.add(req);
	    return true;
	}
}
