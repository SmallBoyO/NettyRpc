package com.zhanghe.attribute;

import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.rpc.core.plugin.server.RpcServerFilter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public interface Attributes {

    AttributeKey<Map<String ,Object>> SERVERS = AttributeKey.newInstance("SERVERS");

    AttributeKey<Serializer> SERIALIZER_ATTRIBUTE_KEY = AttributeKey.newInstance("SERIALIZER_ATTRIBUTE_KEY");

    AttributeKey<List<RpcServerFilter>> SERVER_FILTER_LIST = AttributeKey.newInstance("SERVER_FILTER_LIST");

    AttributeKey<ThreadPoolExecutor> SERVER_BUSINESS_EXCUTOR = AttributeKey.newInstance("SERVER_BUSINESS_EXCUTOR");

}
