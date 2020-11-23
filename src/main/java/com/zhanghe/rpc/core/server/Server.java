package com.zhanghe.rpc.core.server;

import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.rpc.core.plugin.server.RpcServerFilter;
import java.util.List;

public interface Server {

  void init();

  void destroy();

  void setSerializer(Serializer serializer);

  void bind(Object service);

  void bind(List<Object> services);

  void addFilter(RpcServerFilter filter);
}
