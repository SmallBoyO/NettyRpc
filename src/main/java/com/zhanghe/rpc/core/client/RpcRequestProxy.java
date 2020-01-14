package com.zhanghe.rpc.core.client;

import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.protocol.v1.response.RpcResponse;
import io.netty.channel.Channel;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcRequestProxy<T> implements InvocationHandler {

    private static Logger logger = LoggerFactory.getLogger(RpcRequestProxy.class);

    private Channel channel;

    private Lock channelLock = new ReentrantLock();

    public RpcRequestProxy() {
    }

    public RpcRequestProxy(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!channel.isActive()) {
            throw new IllegalStateException("rpc server disconnected!");
        }
        RpcRequest rpcRequest = new RpcRequest();
        String requestId = UUID.randomUUID().toString();
        rpcRequest.setRequestId(requestId);
        rpcRequest.setClassName(proxy.getClass().getInterfaces()[0].getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setTypeParameters(method.getParameterTypes());
        rpcRequest.setParametersVal(args);
        RpcRequestCallBack callBack = new RpcRequestCallBack(requestId);

        RpcRequestCallBackholder.callBackMap.put(rpcRequest.getRequestId(), callBack);
        channel.writeAndFlush(rpcRequest);
        RpcResponse result = callBack.start();
        if(result == null && !channel.isActive()){
            throw new IllegalStateException("rpc server disconnected!");
        }
        if (result.isSuccess()) {
                return result.getResult();
        } else {
            throw result.getException();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}
