package com.zhanghe.channel.hanlder.server;

import com.zhanghe.protocol.v1.request.HeartBeatRequest;
import com.zhanghe.protocol.v1.response.HeartBeatResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  处理接收到的心跳请求
 * @author zhanghe
 */
@ChannelHandler.Sharable
public class HeartBeatRequestHanlder extends SimpleChannelInboundHandler<HeartBeatRequest> {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatRequestHanlder.class);

    public static final HeartBeatRequestHanlder INSTANCE = new HeartBeatRequestHanlder();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HeartBeatRequest heartBeatRequest) throws Exception {
        logger.debug("recive hertbeat packet.");
        channelHandlerContext.channel().writeAndFlush(HeartBeatResponse.INSTANCE);
    }
}
