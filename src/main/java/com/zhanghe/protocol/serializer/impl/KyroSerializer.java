package com.zhanghe.protocol.serializer.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.serializer.SerializerAlgorithm;

import java.util.Collection;

public class KyroSerializer implements Serializer {

    public static final KyroSerializer INSTANCE = new KyroSerializer();

    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.KYRO;
    }

    @Override
    public byte[] serialize(Object object) {
        Kryo kryo = new Kryo();
        kryo.addDefaultSerializer(Collection.class, new JavaSerializer());
        Output output =new Output(1024,Integer.MAX_VALUE);
        kryo.writeClassAndObject(output, object);
        return output.toBytes();
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        Input input = new Input(bytes);
        Kryo kryo = new Kryo();
        kryo.addDefaultSerializer(Collection.class, new JavaSerializer());
        return kryo.readObject(input,clazz);
    }

    @Override
    public Object deserialize( byte[] bytes) {
        Input input = new Input(bytes);
        Kryo kryo = new Kryo();
        kryo.addDefaultSerializer(Collection.class, new JavaSerializer());
        return kryo.readClassAndObject(input);
    }
}
