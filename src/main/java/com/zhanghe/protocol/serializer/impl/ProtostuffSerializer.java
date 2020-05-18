package com.zhanghe.protocol.serializer.impl;

import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.serializer.SerializerAlgorithm;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ProtostuffSerializer
 *
 * @author Clevo
 * @date 2019/11/10
 */
public class ProtostuffSerializer implements Serializer {

    public static final ProtostuffSerializer INSTANCE = new ProtostuffSerializer();

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private static <T> Schema<T> getSchema(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(clazz);
            if (schema != null) {
                cachedSchema.put(clazz, schema);
            }
        }
        return schema;
    }

    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.PROTOSTUFF;
    }

    @Override
    public byte[] serialize(Object object) {
        Class clazz =  object.getClass();
        Schema schema = getSchema(clazz);
        byte[] data;
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            data = ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        Schema<T> schema = getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return null;
    }

}
