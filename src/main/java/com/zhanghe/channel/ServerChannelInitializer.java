package com.zhanghe.channel;

import com.zhanghe.attribute.Attributes;
import com.zhanghe.channel.hanlder.common.Spliter;
import com.zhanghe.channel.hanlder.server.BindRpcFilterHandler;
import com.zhanghe.channel.hanlder.server.BindRpcServiceHandler;
import com.zhanghe.channel.hanlder.server.GetRegisterServiceRequestHandler;
import com.zhanghe.channel.hanlder.server.HeartBeatRequestHanlder;
import com.zhanghe.channel.hanlder.server.RpcIdleStateHandler;
import com.zhanghe.channel.hanlder.common.BaseCommandDecoder;
import com.zhanghe.channel.hanlder.common.BaseCommandEncoder;
import com.zhanghe.channel.hanlder.server.RpcRequestHandler;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.serializer.SerializerManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import java.util.concurrent.ThreadPoolExecutor;
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

    private BindRpcFilterHandler bindRpcFilterHandler;

    private ThreadPoolExecutor businessLogicExcutor;

    @Override
    protected void initChannel(Channel channel) {
        if(serializer != null){
            channel.attr(Attributes.SERIALIZER_ATTRIBUTE_KEY).set(serializer);
            logger.info("use serializer:[{}].",serializer);
        }else {
            channel.attr(Attributes.SERIALIZER_ATTRIBUTE_KEY).set(SerializerManager.getDefault());
            logger.info("use serializer:[{}].",SerializerManager.getDefault());
        }
        channel.attr(Attributes.SERVER_BUSINESS_EXECUTOR).set(businessLogicExcutor);
        channel.pipeline().addLast(new Spliter(Integer.MAX_VALUE,7,4));
        channel.pipeline().addLast(new RpcIdleStateHandler());
        channel.pipeline().addLast(bindRpcServiceHandler);
        if(bindRpcFilterHandler!=null){
            channel.pipeline().addLast(bindRpcFilterHandler);
        }
        channel.pipeline().addLast(BaseCommandEncoder.INSTANCE);
        channel.pipeline().addLast(new BaseCommandDecoder());
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

    public void setBindRpcFilterHandler(
        BindRpcFilterHandler bindRpcFilterHandler) {
        this.bindRpcFilterHandler = bindRpcFilterHandler;
    }

    public void setBusinessLogicExcutor(ThreadPoolExecutor businessLogicExcutor) {
        this.businessLogicExcutor = businessLogicExcutor;
    }
}
