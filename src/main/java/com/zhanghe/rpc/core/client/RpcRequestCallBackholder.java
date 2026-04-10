package com.zhanghe.rpc.core.client;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RpcRequestCallBackholder {

    private ConcurrentHashMap<String,RpcRequestCallBack> callBackMap = new ConcurrentHashMap<>();

    public void put(String requestId, RpcRequestCallBack callBack) {
        callBackMap.put(requestId, callBack);
    }

    public RpcRequestCallBack remove(String requestId) {
        return callBackMap.remove(requestId);
    }

    public int size() {
        return callBackMap.size();
    }

    public Set<String> keySet() {
        return callBackMap.keySet();
    }

}
