package com.zhanghe.channel.hanlder.server;

import com.zhanghe.attribute.Attributes;
import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.protocol.v1.response.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 处理接收到的rpc请求
 * @author zhanghe
 */
@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    public static final RpcRequestHandler INSTANCE = new RpcRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        logger.debug("recive rpcRequest:{}",rpcRequest);
        Future<RpcResponse> f = channelHandlerContext.executor().submit(()->{
            RpcResponse rpcResponse = new RpcResponse();
            try {
                rpcResponse.setRequestId(rpcRequest.getRequestId());
                String className = rpcRequest.getClassName();
                Map mserverMap = channelHandlerContext.channel().attr(Attributes.servers).get();
                Object serviceBean = mserverMap.get(className);
                Object result = serviceBean.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getTypeParameters()).invoke(serviceBean,rpcRequest.getParametersVal());
                rpcResponse.setResult(result);
                rpcResponse.setSuccess(true);
                return rpcResponse;
            }catch (Exception e){
                e.printStackTrace();
                rpcResponse.setException(e);
                rpcResponse.setSuccess(false);
                return rpcResponse;
            }
        });
        f.addListener((future)->{
            RpcResponse rpcResponse = (RpcResponse)future.get();
            channelHandlerContext.channel().writeAndFlush(rpcResponse);
        });
    }
}
