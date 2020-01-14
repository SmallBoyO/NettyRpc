package com.zhanghe.channel.hanlder.server;

import com.zhanghe.attribute.Attributes;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ConcurrentHashMap;

public class BindRpcServiceHandler extends ChannelInboundHandlerAdapter {

    private ConcurrentHashMap<String,Object> serviceMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(serviceMap!=null) {
            ctx.channel().attr(Attributes.SERVERS).set(serviceMap);
        }
        //绑定完成之后 移除自己
        ctx.pipeline().remove(this);
        super.channelActive(ctx);
    }

    public ConcurrentHashMap<String, Object> getServiceMap() {
        return serviceMap;
    }

    public void setServiceMap(ConcurrentHashMap<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }
}
