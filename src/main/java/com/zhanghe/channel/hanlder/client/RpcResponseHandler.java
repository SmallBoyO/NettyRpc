package com.zhanghe.channel.hanlder.client;

import com.zhanghe.protocol.response.RpcResponse;
import com.zhanghe.rpc.RpcRequestCallBack;
import com.zhanghe.rpc.RpcRequestCallBackholder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private Logger logger = LoggerFactory.getLogger(RpcResponseHandler.class);

    public static RpcResponseHandler INSTANCE = new RpcResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        logger.debug("recive rpc response:{}",rpcResponse);
        RpcRequestCallBack callBack = RpcRequestCallBackholder.callBackMap.remove(rpcResponse.RequestId);
        if(callBack!=null) {
            callBack.callBack(rpcResponse);
        }
    }

}
