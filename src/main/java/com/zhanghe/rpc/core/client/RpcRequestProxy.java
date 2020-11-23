package com.zhanghe.rpc.core.client;

import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.protocol.v1.response.RpcResponse;
import com.zhanghe.rpc.core.plugin.client.BaseInvoker;
import com.zhanghe.rpc.core.plugin.client.RpcClientFilter;
import com.zhanghe.rpc.core.plugin.client.RpcClientFilterChain;
import io.netty.channel.Channel;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcRequestProxy<T> implements InvocationHandler {

    private static Logger logger = LoggerFactory.getLogger(RpcRequestProxy.class);


    private Lock channelLock = new ReentrantLock();

    private Client client;

    public List<RpcClientFilter> filters;

    public RpcRequestProxy() {
    }

    public RpcRequestProxy(List<RpcClientFilter> filters) {
        this.filters = filters;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcClientFilterChain rpcClientFilterChain = new RpcClientFilterChain();
        this.filters.forEach(rpcClientFilter -> {
            rpcClientFilterChain.addFilter(rpcClientFilter);
        });
        BaseInvoker baseInvoker = new BaseInvoker(this);
        rpcClientFilterChain.doFilter(proxy,method,args, baseInvoker);
        RpcResponse result = baseInvoker.getRpcResponse();
        if (result.isSuccess()) {
                return result.getResult();
        } else {
            throw result.getException();
        }
    }

    public RpcResponse call(Object proxy, Method method, Object[] args){
        Channel channel = client.currentServer().getRpcClientConnector().getActiveChannel();
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
        return result;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<RpcClientFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<RpcClientFilter> filters) {
        this.filters = filters;
    }
}
