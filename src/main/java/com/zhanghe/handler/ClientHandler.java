package com.zhanghe.handler;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import com.zhanghe.protocol.RpcRequest;
import com.zhanghe.protocol.RpcResponse;
import com.zhanghe.proxy.MessageCallBack;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
@Sharable
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
	
	public static final ClientHandler INSTANCE = new ClientHandler();
	
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
	protected void channelRead0( ChannelHandlerContext ctx ,RpcResponse response ) throws Exception {
        String messageId = response.getId();
        MessageCallBack callBack = mapCallBack.get(messageId);
        if (callBack != null) {
            mapCallBack.remove(messageId);
            callBack.over(response);
        }
	}
	
	public MessageCallBack sendRequest(RpcRequest request) {
        MessageCallBack callBack = new MessageCallBack(request);
        mapCallBack.put(request.getId(), callBack);
        channel.writeAndFlush(request);
        return callBack;
    }
}
