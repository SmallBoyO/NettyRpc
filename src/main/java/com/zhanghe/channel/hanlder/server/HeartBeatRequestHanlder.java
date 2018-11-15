package com.zhanghe.channel.hanlder.server;

import com.zhanghe.protocol.request.HeartBeatRequest;
import com.zhanghe.protocol.response.HeartBeatResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zhanghe
 */
@ChannelHandler.Sharable
public class HeartBeatRequestHanlder extends SimpleChannelInboundHandler<HeartBeatRequest> {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatRequestHanlder.class);

    public static final HeartBeatRequestHanlder INSTANCE = new HeartBeatRequestHanlder();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HeartBeatRequest heartBeatRequest) throws Exception {
        logger.debug("recive hertbeatpacket.");
        channelHandlerContext.channel().writeAndFlush(HeartBeatResponse.INSTANCE);
    }
}
