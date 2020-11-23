package com.zhanghe.test.testServer.filter;

import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.rpc.core.plugin.server.Invoker;
import com.zhanghe.rpc.core.plugin.server.RpcServerFilter;
import com.zhanghe.rpc.core.plugin.server.RpcServerFilterChain;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoServerFilter implements RpcServerFilter {

  private static Logger logger = LoggerFactory.getLogger(DemoServerFilter.class);

  private String filterName;

  public DemoServerFilter(String filterName) {
    this.filterName = filterName;
  }

  @Override
  public void doFilter(ChannelHandlerContext channelHandlerContext,RpcRequest request,Invoker invoker, RpcServerFilterChain chain) throws Exception {
    logger.info("before {}",filterName);
    chain.doFilter(channelHandlerContext,request,invoker);
    logger.info("after {}",filterName);
  }
}
