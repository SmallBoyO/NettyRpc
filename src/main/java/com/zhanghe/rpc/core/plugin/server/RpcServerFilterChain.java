package com.zhanghe.rpc.core.plugin.server;

import com.zhanghe.protocol.v1.request.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;

public class RpcServerFilterChain {

  public List<RpcServerFilter> filters;

  public int position;

  public RpcServerFilterChain(List<RpcServerFilter> filters) {
    this.filters = filters;
    this.position = 0;
  }
  public void addFilter(RpcServerFilter rpcServerFilter){
    this.filters.add(rpcServerFilter);
  }

  public void doFilter(ChannelHandlerContext channelHandlerContext,RpcRequest request,Invoker invoker) throws Exception{
    if(position <= (filters.size()-1)){
      filters.get(position++).doFilter( channelHandlerContext,request,invoker,this);
    }else{
      invoker.invoke(channelHandlerContext,request);
    }
  }
}
