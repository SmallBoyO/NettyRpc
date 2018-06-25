package com.zhanghe.encoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.zhanghe.protocol.RpcResponse;

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
		 Kryo kryo = new Kryo();
		 Output output =new Output(1024,Integer.MAX_VALUE);
		 kryo.writeObject(output, response);
		 int length = output.toBytes().length;
		 //System.out.println("发送Response包长度:"+length);
		 out.writeInt(length);
		 //out.writeBytes(Unpooled.copyInt(length));
		 out.writeBytes(output.toBytes());
		 
		 output.close();
	}
	
}
