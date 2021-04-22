package com.zhanghe.channel.hanlder.server;

import com.zhanghe.attribute.Attributes;
import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.protocol.v1.response.RpcResponse;
import com.zhanghe.rpc.core.plugin.server.BaseInvoker;
import com.zhanghe.rpc.core.plugin.server.RpcServerFilterChain;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
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
        ThreadPoolExecutor businessExecutor =  channelHandlerContext.channel().attr(Attributes.SERVER_BUSINESS_EXECUTOR).get();
        businessExecutor.submit(() -> {
          RpcServerFilterChain rpcServerFilterChain = new RpcServerFilterChain(channelHandlerContext.channel().attr(Attributes.SERVER_FILTER_LIST).get());
            try {
              logger.debug("开始执行任务:{}",rpcRequest);
              BaseInvoker baseInvoker = new BaseInvoker();
              rpcServerFilterChain.doFilter(channelHandlerContext,rpcRequest,baseInvoker);
              channelHandlerContext.channel().writeAndFlush(baseInvoker.getRpcResponse());
              }catch (Exception e){
              e.printStackTrace();
              RpcResponse rpcResponse = new RpcResponse();
              rpcResponse.setException(e);
              rpcResponse.setSuccess(false);
              channelHandlerContext.channel().writeAndFlush(rpcResponse);
            }
      });
    }
}
