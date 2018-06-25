package com.zhanghe.encoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Output;
import com.zhanghe.protocol.RpcRequest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RequestToByteEncoder RpcRequest编码器
 * 
 * @author Clevo
 * @date 2018/6/12 23:02
 */
public class RequestToByteEncoder extends MessageToByteEncoder<RpcRequest> {

	@Override
	protected void encode( ChannelHandlerContext ctx ,RpcRequest request ,ByteBuf out ) throws Exception {
		Kryo kryo = new Kryo();
		Output output =new Output(1024,Integer.MAX_VALUE);
		kryo.writeObject(output, request);
		int length = output.toBytes().length;
		//System.out.println("发送包长度:" + length);
		out.writeInt(length);
		out.writeBytes(output.toBytes());
		output.close();
	}

}