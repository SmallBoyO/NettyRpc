package com.zhanghe.protocol.serializer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化管理器
 *
 * @author zhanghe
 */
public class SerializerManager {

    public static ConcurrentHashMap<Byte,Serializer> serializers = new ConcurrentHashMap<>(10);

    private static Serializer defaultSerializer;

    public static void register(Byte algorithm,Serializer serializer){
        if( algorithm == null || serializer == null){
            throw new RuntimeException("Algorithm or serializer cannot be null");
        }
        Serializer exist = serializers.putIfAbsent(algorithm,serializer);
        if(exist != null){
            throw new RuntimeException("serializer od code " + algorithm + " has exists!");
        }
    }

    public static Serializer unRigster(Byte algorithm){
        return serializers.remove(algorithm);
    }

    public static void setDefault(Byte algorithm,Serializer serializer){
        if(defaultSerializer!=null){
            throw new RuntimeException("defaultSerializer has exists!");
        }
        register(algorithm,serializer);
        defaultSerializer = serializer;
    }

    public static Serializer getDefault(){
        if(defaultSerializer == null){
            throw new RuntimeException("defaultSerializer has not set!");
        }
        return defaultSerializer;
    }

    public static Serializer getSerializer(Byte algorithm){
        Serializer serializer = serializers.get(algorithm);
        return  serializer;
    }
}
