package com.zhanghe.channel;

import com.zhanghe.attribute.Attributes;
import com.zhanghe.channel.hanlder.client.*;
import com.zhanghe.channel.hanlder.common.AbstractCommandDecoder;
import com.zhanghe.channel.hanlder.common.AbstractCommandEncoder;
import com.zhanghe.channel.hanlder.common.Spliter;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.serializer.SerializerManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientChannelInitializer  extends ChannelInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ClientChannelInitializer.class);

    private Serializer serializer;

    public ClientChannelInitializer() {
        super();
    }

    public ClientChannelInitializer(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        if(serializer != null){
            channel.attr(Attributes.SERIALIZER_ATTRIBUTE_KEY).set(serializer);
            logger.info("use serializer:[{}].",serializer);
        }else {
            channel.attr(Attributes.SERIALIZER_ATTRIBUTE_KEY).set(SerializerManager.getDefault());
            logger.info("use serializer:[{}].",SerializerManager.getDefault());
        }
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
