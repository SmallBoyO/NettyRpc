package com.zhanghe.rpc;

import java.util.concurrent.ConcurrentHashMap;

public class RpcRequestCallBackholder {

    public static ConcurrentHashMap<String,RpcRequestCallBack> callBackMap = new ConcurrentHashMap<>();

}
