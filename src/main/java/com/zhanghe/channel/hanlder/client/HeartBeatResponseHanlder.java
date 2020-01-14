package com.zhanghe.channel.hanlder.client;

import com.zhanghe.protocol.v1.response.HeartBeatResponse;
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        super.exceptionCaught(ctx, cause);
    }
}
