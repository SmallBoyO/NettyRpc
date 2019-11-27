package com.zhanghe.rpc;

import io.netty.channel.Channel;
import java.util.Set;

public interface RpcClientHolder {

  void setServices(String address,Set<String> services);

  void setChannel(String address,Channel channel);
}
