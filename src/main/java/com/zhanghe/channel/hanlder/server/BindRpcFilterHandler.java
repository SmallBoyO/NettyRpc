package com.zhanghe.channel.hanlder.server;

import com.zhanghe.attribute.Attributes;
import com.zhanghe.rpc.core.plugin.server.RpcServerFilter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.List;

@Sharable
public class BindRpcFilterHandler extends ChannelInboundHandlerAdapter {

  private List<RpcServerFilter> serverFilterList;

  public BindRpcFilterHandler(
      List<RpcServerFilter> serverFilterList) {
    this.serverFilterList = serverFilterList;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    if(serverFilterList!=null) {
      ctx.channel().attr(Attributes.SERVER_FILTER_LIST).set(serverFilterList);
    }
    //绑定完成之后 移除自己
    ctx.pipeline().remove(this);
    super.channelActive(ctx);
  }

}
