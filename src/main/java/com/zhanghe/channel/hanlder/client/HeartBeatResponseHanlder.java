package com.zhanghe.channel.hanlder.client;

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
public class HeartBeatResponseHanlder extends SimpleChannelInboundHandler<HeartBeatResponse> {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatResponseHanlder.class);

    public static final HeartBeatResponseHanlder INSTANCE = new HeartBeatResponseHanlder();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HeartBeatResponse heartBeatResponse) throws Exception {
        logger.debug("hertbeatresponse recived.");
    }
}
