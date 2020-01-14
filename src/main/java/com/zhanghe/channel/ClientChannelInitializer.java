package com.zhanghe.channel;

import com.zhanghe.channel.hanlder.client.*;
import com.zhanghe.channel.hanlder.common.AbstractCommandDecoder;
import com.zhanghe.channel.hanlder.common.AbstractCommandEncoder;
import com.zhanghe.channel.hanlder.common.Spliter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class ClientChannelInitializer  extends ChannelInitializer {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast(new Spliter(Integer.MAX_VALUE,7,4));
        channel.pipeline().addLast(new RpcIdleStateHandler());
        channel.pipeline().addLast(AbstractCommandEncoder.INSTANCE);
        channel.pipeline().addLast(new AbstractCommandDecoder());
        channel.pipeline().addLast(HeartBeatTimerHanlder.INSTANCE);
        channel.pipeline().addLast(new GetRegisterServiceResponseHandler());
        channel.pipeline().addLast(RpcResponseHandler.INSTANCE);
        channel.pipeline().addLast(HeartBeatResponseHanlder.INSTANCE);
    }
}
