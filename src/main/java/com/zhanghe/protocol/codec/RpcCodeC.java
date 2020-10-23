package com.zhanghe.protocol.codec;

import com.zhanghe.channel.hanlder.common.BaseCommandDecoder;
import com.zhanghe.channel.hanlder.common.BaseCommandEncoder;
import io.netty.channel.ChannelHandler;


public class RpcCodeC implements Codec {

    @Override
    public ChannelHandler getEncoder() {
        return BaseCommandEncoder.INSTANCE;
    }

    @Override
    public ChannelHandler getDecoder() {
        return new BaseCommandDecoder();
    }
}
