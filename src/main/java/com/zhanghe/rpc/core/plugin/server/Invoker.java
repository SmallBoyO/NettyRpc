package com.zhanghe.rpc.core.plugin.server;


import com.zhanghe.protocol.v1.request.RpcRequest;
import io.netty.channel.ChannelHandlerContext;

public interface Invoker {

  /**
   * 执行method
   * @param channelHandlerContext
   * @param rpcRequest
   * @throws Exception
   */
  void invoke(ChannelHandlerContext channelHandlerContext,RpcRequest rpcRequest) throws Exception;

}
