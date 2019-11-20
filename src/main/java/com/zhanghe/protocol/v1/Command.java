package com.zhanghe.protocol.v1;


import com.zhanghe.protocol.v1.request.GetRegisterServiceRequest;
import com.zhanghe.protocol.v1.request.HeartBeatRequest;
import com.zhanghe.protocol.v1.request.RpcRequest;
import com.zhanghe.protocol.v1.response.GetRegisterServiceResponse;
import com.zhanghe.protocol.v1.response.HeartBeatResponse;
import com.zhanghe.protocol.v1.response.RpcResponse;

import java.util.concurrent.ConcurrentHashMap;

public class Command {

    private static ConcurrentHashMap<Byte,Class> commamdClassMap = new ConcurrentHashMap<>();

    static{
        rigester(CommandType.HEART_BEAT_REQUEST,HeartBeatRequest.class);
        rigester(CommandType.HEART_BEAT_RESPONSE,HeartBeatResponse.class);
        rigester(CommandType.RPC_REQUEST,RpcRequest.class);
        rigester(CommandType.RPC_RESPONSE,RpcResponse.class);
        rigester(CommandType.GET_REGISTER_SERVICE_REQUEST,GetRegisterServiceRequest.class);
        rigester(CommandType.GET_REGISTER_SERVICE_RESPONSE,GetRegisterServiceResponse.class);
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
