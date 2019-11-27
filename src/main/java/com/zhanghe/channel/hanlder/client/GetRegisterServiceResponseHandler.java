package com.zhanghe.channel.hanlder.client;

import com.zhanghe.protocol.v1.response.GetRegisterServiceResponse;
import com.zhanghe.rpc.RpcClientConnector;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetRegisterServiceResponseHandler extends SimpleChannelInboundHandler<GetRegisterServiceResponse> {

  private static final Logger logger = LoggerFactory.getLogger(GetRegisterServiceResponseHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, GetRegisterServiceResponse msg) throws Exception {
    logger.debug("服务器提供的接口:{}",msg.getServices());
    ((RpcClientConnector)ctx.channel().attr(AttributeKey.valueOf("rpcClient")).get()).setRegisterServices(msg.getServices());
  }
}
