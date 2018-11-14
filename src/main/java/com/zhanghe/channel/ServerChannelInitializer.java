package com.zhanghe.channel;

import com.zhanghe.protocol.codec.AbstractCommandDecoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class ServerChannelInitializer extends ChannelInitializer {

    public static ServerChannelInitializer INSTANCE = new ServerChannelInitializer();

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast(AbstractCommandDecoder.INSTANCE);
        channel.pipeline().addLast(AbstractCommandDecoder.INSTANCE);
    }

}
