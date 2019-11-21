package com.zhanghe.channel.hanlder.server;


import com.zhanghe.attribute.Attributes;
import com.zhanghe.protocol.v1.request.GetRegisterServiceRequest;
import com.zhanghe.protocol.v1.response.GetRegisterServiceResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetRegisterServiceRequestHandler extends SimpleChannelInboundHandler<GetRegisterServiceRequest> {

  private static final Logger logger = LoggerFactory.getLogger(GetRegisterServiceRequestHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, GetRegisterServiceRequest msg) throws Exception {
    logger.debug("客户端获取注册services");
    GetRegisterServiceResponse response  = new GetRegisterServiceResponse();
    Map servicesMap = ctx.channel().attr(Attributes.SERVERS).get();
    Set set = new HashSet<String>();
    for(Object key : servicesMap.keySet()){
      set.add(key.toString());
    }
    response.setServices(set);
    ctx.channel().writeAndFlush(response);
  }
}
