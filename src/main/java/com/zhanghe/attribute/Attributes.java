package com.zhanghe.attribute;

import com.zhanghe.protocol.serializer.Serializer;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.Map;

public interface Attributes {

    AttributeKey<Map<String ,Object>> SERVERS = AttributeKey.newInstance("SERVERS");

    AttributeKey<Serializer> SERIALIZER_ATTRIBUTE_KEY = AttributeKey.newInstance("SERIALIZER_ATTRIBUTE_KEY");

}
