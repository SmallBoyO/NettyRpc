package com.zhanghe.rpc.core.client;

import com.zhanghe.protocol.serializer.Serializer;
import io.netty.channel.Channel;
import java.util.Set;

public interface Client {

  void init();

  void destroy();

  void setSerializer(Serializer serializer);

  void setServices(String address, Set<String> services);

  void setChannel(String address, Channel channel);

  Object proxy(String service) throws ClassNotFoundException;

  RpcServerInfo currentServer();
}
