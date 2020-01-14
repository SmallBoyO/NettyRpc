package com.zhanghe.attribute;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.Map;

public interface Attributes {

    AttributeKey<Map<String ,Object>> SERVERS = AttributeKey.newInstance("SERVERS");

}
