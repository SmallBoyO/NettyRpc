package com.zhanghe.channel;

import com.zhanghe.channel.hanlder.client.HeartBeatResponseHanlder;
import com.zhanghe.channel.hanlder.client.HeartBeatTimerHanlder;
import com.zhanghe.channel.hanlder.common.AbstractCommandDecoder;
import com.zhanghe.channel.hanlder.common.AbstractCommandEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class ClientChannelInitializer  extends ChannelInitializer {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast(AbstractCommandEncoder.INSTANCE);
        channel.pipeline().addLast(new AbstractCommandDecoder());
        channel.pipeline().addLast(HeartBeatTimerHanlder.INSTANCE);
        channel.pipeline().addLast(HeartBeatResponseHanlder.INSTANCE);
    }
}
