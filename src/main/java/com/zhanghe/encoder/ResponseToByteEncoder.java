package com.zhanghe.encoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.zhanghe.protocol.RpcResponse;

import com.zhanghe.util.KryoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
/**
 * ResponseToByteEncoder
 * RpcResponse编码器
 * @author Clevo
 * @date 2018/6/12 23:40
 */
public class ResponseToByteEncoder extends MessageToByteEncoder<RpcResponse>{

	@Override
	protected void encode( ChannelHandlerContext ctx ,RpcResponse response ,ByteBuf out ) throws Exception {
		KryoUtil.ObjectToByteAndWriteToByteBuff(response,out);
	}
	
}
