package com.zhanghe.channel.hanlder.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RpcIdleStateHandler extends IdleStateHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcIdleStateHandler.class);

    private static final int READER_IDLE_TIME = 15;

    public RpcIdleStateHandler() {
        super(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        logger.debug("断开空闲连接.");
        ctx.channel().close();
    }

}
