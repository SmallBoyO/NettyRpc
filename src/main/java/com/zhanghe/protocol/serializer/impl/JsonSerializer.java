package com.zhanghe.protocol.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.serializer.SerializerAlgorithm;
import io.netty.util.CharsetUtil;

public class JsonSerializer implements Serializer {

    public static final JsonSerializer INSTANCE = new JsonSerializer();

    static{
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JSON;
    }

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONString(object, SerializerFeature.WriteClassName).getBytes(CharsetUtil.UTF_8);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        String str = new String(bytes,CharsetUtil.UTF_8);
        return JSONObject.parseObject(str,clazz);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        String str = new String(bytes,CharsetUtil.UTF_8);
        return JSONObject.parseObject(str);
    }
}
