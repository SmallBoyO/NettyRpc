package com.zhanghe.rpc.core.plugin.server;


import com.zhanghe.protocol.v1.request.RpcRequest;
import io.netty.channel.ChannelHandlerContext;

public interface Invoker {

  void invoke(ChannelHandlerContext channelHandlerContext,RpcRequest rpcRequest) throws Exception;

}
