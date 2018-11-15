package com.zhanghe.protocol.serializer;

/**
 * 序列化接口
 *
 * @author zhanghe
 */
public interface Serializer {

    /**
     * 序列化算法
     * @return
     */
    byte getSerializerAlgorithm();

    /**
     * java 对象转换成二进制
     * @param object
     * @return
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialize(Class<T> clazz,byte[] bytes);

    /**
     * 二进制转换成 java 对象
     * @param bytes
     * @return
     */
    public Object deserialize( byte[] bytes);

}
