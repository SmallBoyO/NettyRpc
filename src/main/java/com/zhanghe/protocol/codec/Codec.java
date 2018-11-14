package com.zhanghe.protocol.codec;

import io.netty.channel.ChannelHandler;

public interface Codec {

    ChannelHandler getEncoder();

    ChannelHandler getDecoder();

}
