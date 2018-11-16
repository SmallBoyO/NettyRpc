package com.zhanghe.protocol.v1;


import com.zhanghe.protocol.request.HeartBeatRequest;
import com.zhanghe.protocol.request.RpcRequest;
import com.zhanghe.protocol.response.HeartBeatResponse;

import java.util.concurrent.ConcurrentHashMap;

public class Command {

    private static ConcurrentHashMap<Byte,Class> commamdClassMap = new ConcurrentHashMap<>();

    static{
        rigester(CommandType.HEART_BEAT_REQUEST,HeartBeatRequest.class);
        rigester(CommandType.HEART_BEAT_RESPONSE,HeartBeatResponse.class);
        rigester(CommandType.RPC_REQUEST,RpcRequest.class);
    }
    public static void rigester(Byte command,Class type){
        if(commamdClassMap.containsKey(command)){
            throw new RuntimeException("Command has been rigestered!");
        }
        commamdClassMap.put(command,type);
    }

    public static Class getCommandClass(Byte command){
       return commamdClassMap.get(command);
    }

}
