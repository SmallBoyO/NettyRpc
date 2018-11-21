package com.zhanghe.channel.hanlder.client;

import com.zhanghe.protocol.request.HeartBeatRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 客户端心跳
 */
public class HeartBeatTimerHanlder extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatTimerHanlder.class);

    public static final HeartBeatTimerHanlder INSTANCE = new HeartBeatTimerHanlder();

    private static int HEARTBEAT_INTERVAL = 5;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        heartBeat(ctx);
        super.channelActive(ctx);
    }

    private void heartBeat(ChannelHandlerContext ctx){
        logger.debug("send heartBeatPacket.");
        ctx.executor().schedule(()->{
            if(ctx.channel().isActive()) {
                ctx.channel().writeAndFlush(HeartBeatRequest.INSTANCE);
                heartBeat(ctx);
            }
        },HEARTBEAT_INTERVAL,TimeUnit.SECONDS);
    }
}
