package com.zhanghe.rpc;

import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.protocol.v1.response.RpcResponse;
import com.zhanghe.service.TestService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RpcRequestProxy<T> implements InvocationHandler {

    private static Logger logger = LoggerFactory.getLogger(RpcRequestProxy.class);

    private Channel channel;

    private AtomicBoolean serverConnected = new AtomicBoolean(false);

    private AtomicBoolean servicesInited = new AtomicBoolean(false);

    private Lock channelLock = new ReentrantLock();

    public RpcRequestProxy() {
    }

    public RpcRequestProxy(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try{
            channelLock.lock();
            if (!serverConnected.get()) {
                throw new IllegalStateException("rpc server disconneted!");
            }
        }finally {
            channelLock.unlock();
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
        if (result.isSuccess()) {
            return result.getResult();
            } else {
            throw result.getException();
        }
    }



    public void connect(Channel channel){
        try{
            channelLock.lock();
            setChannel(channel);
        }finally {
            channelLock.unlock();
        }
    }

    public void initServices(){
        try{
            channelLock.lock();
            serverConnected.getAndSet(true);
        }finally {
            channelLock.unlock();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public AtomicBoolean getServerConnected() {
        return serverConnected;
    }

    public void setServerConnected(AtomicBoolean serverConnected) {
        this.serverConnected = serverConnected;
    }

    public AtomicBoolean getServicesInited() {
        return servicesInited;
    }

    public void setServicesInited(AtomicBoolean servicesInited) {
        this.servicesInited = servicesInited;
    }
}
