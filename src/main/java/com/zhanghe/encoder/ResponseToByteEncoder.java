package com.zhanghe.encoder;

import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.zhanghe.protocol.RpcResponse;

import io.netty.buffer.ByteBuf;
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
		 Kryo kryo = new Kryo();
		 Output output = new Output(new ByteArrayOutputStream());
		 kryo.writeObject(output, response);
		 output.toBytes();
		 out.writeBytes(output.toBytes());
		 output.close();
	}

}
