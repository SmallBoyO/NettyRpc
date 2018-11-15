package com.zhanghe.protocol.codec;

import com.zhanghe.channel.hanlder.common.AbstractCommandDecoder;
import com.zhanghe.channel.hanlder.common.AbstractCommandEncoder;
import io.netty.channel.ChannelHandler;


public class RpcCodeC implements Codec {

    @Override
    public ChannelHandler getEncoder() {
        return AbstractCommandEncoder.INSTANCE;
    }

    @Override
    public ChannelHandler getDecoder() {
        return new AbstractCommandDecoder();
    }
}
