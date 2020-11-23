package com.zhanghe.rpc.core.plugin.server;

import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.rpc.core.plugin.server.Invoker;
import io.netty.channel.ChannelHandlerContext;

public interface RpcServerFilter {

  void doFilter(ChannelHandlerContext channelHandlerContext,RpcRequest request,Invoker invoker,RpcServerFilterChain chain) throws Exception;

}
