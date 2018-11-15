package com.zhanghe.protocol.serializer;

public interface SerializerAlgorithm {
    /**
     * Kyro 序列化标识
     */
    byte KYRO = 1;
    /**
     * JSON 序列化标识
     */
    byte JSON = 2;
}
