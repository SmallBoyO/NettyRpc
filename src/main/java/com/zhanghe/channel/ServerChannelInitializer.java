package com.zhanghe.channel;

import com.zhanghe.attribute.Attributes;
import com.zhanghe.channel.hanlder.common.Spliter;
import com.zhanghe.channel.hanlder.server.BindRpcServiceHandler;
import com.zhanghe.channel.hanlder.server.GetRegisterServiceRequestHandler;
import com.zhanghe.channel.hanlder.server.HeartBeatRequestHanlder;
import com.zhanghe.channel.hanlder.server.RpcIdleStateHandler;
import com.zhanghe.channel.hanlder.common.AbstractCommandDecoder;
import com.zhanghe.channel.hanlder.common.AbstractCommandEncoder;
import com.zhanghe.channel.hanlder.server.RpcRequestHandler;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.serializer.SerializerManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerChannelInitializer extends ChannelInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ServerChannelInitializer.class);

    private Serializer serializer;

    public ServerChannelInitializer() {
        super();
    }

    public ServerChannelInitializer(Serializer serializer) {
        this.serializer = serializer;
    }

    private BindRpcServiceHandler bindRpcServiceHandler = new BindRpcServiceHandler();

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
        channel.pipeline().addLast(bindRpcServiceHandler);
        channel.pipeline().addLast(AbstractCommandEncoder.INSTANCE);
        channel.pipeline().addLast(new AbstractCommandDecoder());
        channel.pipeline().addLast(HeartBeatRequestHanlder.INSTANCE);
        channel.pipeline().addLast(new GetRegisterServiceRequestHandler());
        channel.pipeline().addLast(RpcRequestHandler.INSTANCE);
    }

    public BindRpcServiceHandler getBindRpcServiceHandler() {
        return bindRpcServiceHandler;
    }

    public void setBindRpcServiceHandler(
        BindRpcServiceHandler bindRpcServiceHandler) {
        this.bindRpcServiceHandler = bindRpcServiceHandler;
    }
}
