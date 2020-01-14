package com.zhanghe.protocol.codec;

import io.netty.channel.ChannelHandler;


public interface Codec {

    /**
     * 获取encoder
     * @return
     */
    ChannelHandler getEncoder();

    /**
     * 获取decoder
     * @return
     */
    ChannelHandler getDecoder();

}
