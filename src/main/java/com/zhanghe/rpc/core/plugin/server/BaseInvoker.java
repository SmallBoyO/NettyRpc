package com.zhanghe.rpc.core.plugin.server;

import com.zhanghe.attribute.Attributes;
import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.protocol.v1.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import java.util.Map;

public class BaseInvoker implements Invoker {

  private RpcResponse rpcResponse;

  @Override
  public void invoke(ChannelHandlerContext channelHandlerContext,RpcRequest rpcRequest) throws Exception {
      RpcResponse rpcResponse = new RpcResponse();
      try {
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        String className = rpcRequest.getClassName();
        Map mserverMap = channelHandlerContext.channel().attr(Attributes.SERVERS).get();
        Object serviceBean = mserverMap.get(className);
        Object result = serviceBean.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getTypeParameters()).invoke(serviceBean,rpcRequest.getParametersVal());
        rpcResponse.setResult(result);
        rpcResponse.setSuccess(true);
        this.rpcResponse = rpcResponse;
      }catch (Exception e){
        e.printStackTrace();
        rpcResponse.setException(e);
        rpcResponse.setSuccess(false);
        this.rpcResponse = rpcResponse;
      }
  }

  public RpcResponse getRpcResponse() {
    return rpcResponse;
  }

  public void setRpcResponse(RpcResponse rpcResponse) {
    this.rpcResponse = rpcResponse;
  }
}
