package com.zhanghe.protocol.serializer;

import com.zhanghe.protocol.serializer.impl.JsonSerializer;
import com.zhanghe.protocol.serializer.impl.KryoSerializer;
import com.zhanghe.protocol.serializer.impl.ProtostuffSerializer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化管理器
 *
 * @author zhanghe
 */
public class SerializerManager {

    public static ConcurrentHashMap<Byte,Serializer> serializers = new ConcurrentHashMap<>(10);

    static{
        serializers.put(SerializerAlgorithm.JSON,JsonSerializer.INSTANCE);
        serializers.put(SerializerAlgorithm.KYRO,KryoSerializer.INSTANCE);
        serializers.put(SerializerAlgorithm.PROTOSTUFF,ProtostuffSerializer.INSTANCE);
    }

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

    public static void setDefault(Byte algorithm){
        if(!serializers.containsKey(algorithm)){
            throw new RuntimeException("Serializer has not registered!");
        }
        defaultSerializer = serializers.get(algorithm);
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
