package com.zhanghe.channel.hanlder.server;

import com.zhanghe.protocol.request.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理接收到的rpc请求
 * @author zhanghe
 */
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    public static final RpcRequestHandler INSTANCE = new RpcRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        logger.debug("recive rpcRequest:{}",rpcRequest);
        Future<Object> f = channelHandlerContext.executor().submit(()->{
            try {
                Thread.sleep(100);
            }catch (Exception e){

            }
            return 1;
        });
         f.addListener((future)->{
            System.out.println(future.get());
        });
    }
}
