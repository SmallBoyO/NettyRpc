package com.zhanghe.encoder;

import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.zhanghe.protocol.RpcRequest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
/**  
 * RequestToByteEncoder
 * RpcRequest编码器
 * @author Clevo  
 * @date 2018/6/12 23:02 
 */  
public class RequestToByteEncoder extends MessageToByteEncoder<RpcRequest>{

	@Override
	protected void encode( ChannelHandlerContext ctx ,RpcRequest request ,ByteBuf out ) throws Exception {
		 Kryo kryo = new Kryo();
		 Output output = new Output(new ByteArrayOutputStream());
		 kryo.writeObject(output, request);
		 output.toBytes();
		 out.writeBytes(output.toBytes());
		 output.close();
	}

}
