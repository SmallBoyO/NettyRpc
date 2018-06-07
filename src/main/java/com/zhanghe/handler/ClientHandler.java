package com.zhanghe.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.zhanghe.protocol.RpcRequest;
import com.zhanghe.protocol.RpcResponse;
import com.zhanghe.proxy.MessageCallBack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.channel.ChannelHandler.Sharable;
@Sharable
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	private ConcurrentHashMap<String, MessageCallBack> mapCallBack = new ConcurrentHashMap<String, MessageCallBack>();

    private volatile Channel channel;
    
    private SocketAddress remoteAddr;
    

	public Channel getChannel() {
		return channel;
	}

	public void setChannel( Channel channel ) {
		this.channel = channel;
	}

	/**
	 * 服务器的连接被建立后调用
	 */
	@Override
	public void channelActive( ChannelHandlerContext ctx ) throws Exception {
		System.out.println("channelActive");
//		RpcRequest re = new RpcRequest();
//		re.setId("1");
//		re.setClassName("com.zhanghe");
//		re.setMethodName("test");
//		re.setParametersVal(new Object[]{"1","2"});
//		re.setTypeParameters(new Class[]{String.class,String.class});
//		
//		Kryo kryo = new Kryo();
//	    Output output = new Output(new ByteArrayOutputStream());
//	    kryo.writeObject(output, re);
//	    output.toBytes();
//	    System.out.println("发送rpc请求:"+re);
//		ctx.writeAndFlush(Unpooled.copiedBuffer(output.toBytes()));
//		output.close();
		super.channelActive(ctx);
        this.channel = ctx.channel();
        this.remoteAddr = this.channel.remoteAddress();
	}

	/**
	 * 捕捉到异常时调用
	 */
	@Override
	public void exceptionCaught( ChannelHandlerContext ctx ,Throwable cause ) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}


	@Override
	protected void channelRead0( ChannelHandlerContext ctx ,ByteBuf msg ) throws Exception {
		ByteBuf bin = (ByteBuf) msg;
		byte[] binbytes = new byte[bin.readableBytes()];
		bin.readBytes(binbytes);
		
		ByteArrayInputStream bain = new ByteArrayInputStream(binbytes);
		Kryo kryo = new Kryo();
		Input input = new Input(bain);
		RpcResponse response = kryo.readObject(input, RpcResponse.class);
	    input.close();
        String messageId = response.getId();
        MessageCallBack callBack = mapCallBack.get(messageId);
        if (callBack != null) {
            mapCallBack.remove(messageId);
            callBack.over(response);
        }
        System.out.println("接收到回复:"+response);
	}
	
	public MessageCallBack sendRequest(RpcRequest request) {
        MessageCallBack callBack = new MessageCallBack(request);
        mapCallBack.put(request.getId(), callBack);
        
        Kryo kryo = new Kryo();
	    Output output = new Output(new ByteArrayOutputStream());
	    kryo.writeObject(output, request);
	    output.toBytes();
	    System.out.println("发送rpc请求:"+request);
	    channel.writeAndFlush(Unpooled.copiedBuffer(output.toBytes()));
		output.close();
        return callBack;
    }
}
