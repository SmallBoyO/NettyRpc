package com.zhanghe.rpc;

import com.zhanghe.protocol.request.RpcRequest;
import com.zhanghe.service.TestService;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class RpcRequestProxy<T> implements InvocationHandler {

    private static Logger logger = LoggerFactory.getLogger(RpcRequestProxy.class);

    private Channel channel;

    public RpcRequestProxy(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(UUID.randomUUID().toString());
        rpcRequest.setClassName(TestService.class.getName());
        rpcRequest.setMethodName("hello");
        rpcRequest.setTypeParameters(new Class[]{});
        rpcRequest.setParametersVal(new Object[]{});
        RpcRequestCallBack callBack = new RpcRequestCallBack();
        RpcRequestCallBackholder.callBackMap.put(rpcRequest.getRequestId(),callBack);
        channel.writeAndFlush(rpcRequest);
        logger.debug("----------------------------------");
        Object result = callBack.start();
        logger.debug("RpcRequest result:{},"+result);
        return null;
    }
}
