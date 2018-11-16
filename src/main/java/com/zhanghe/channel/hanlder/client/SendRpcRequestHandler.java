package com.zhanghe.channel.hanlder.client;

import com.zhanghe.protocol.request.HeartBeatRequest;
import com.zhanghe.protocol.request.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
/**
 * 测试用 定时发送rpc请求
 * @author zhanghe
 */
public class SendRpcRequestHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SendRpcRequestHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        heartBeat(ctx);
        super.channelActive(ctx);
    }

    private void heartBeat(ChannelHandlerContext ctx){
        logger.debug("send RpcRequest.");
        ctx.executor().schedule(()->{
            ctx.channel().writeAndFlush(new RpcRequest());
            heartBeat(ctx);
        },1,TimeUnit.SECONDS);
    }
}
