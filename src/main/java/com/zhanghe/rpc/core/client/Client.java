package com.zhanghe.rpc.core.client;

import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.rpc.core.plugin.client.RpcClientFilter;
import io.netty.channel.Channel;
import java.util.Set;

public interface Client {

  /**
   * 初始化客户端
   */
  void init();

  /**
   * 销毁客户端
   */
  void destroy();

  /**
   * 设置序列化方式
   * @param serializer
   */
  void setSerializer(Serializer serializer);

  /**
   * 添加filter
   * @param rpcClientFilter
   */
  void addFilter(RpcClientFilter rpcClientFilter);

  /**
   * 设置服务端接口列表(即服务端支持调用的接口)
   * @param address
   * @param services
   */
  void setServices(String address, Set<String> services);

  /**
   * 设置channel
   * @param address
   * @param channel
   */
  void setChannel(String address, Channel channel);

  /**
   * 通过代理生成 service 的rpc代理类
   * @param service
   * @return
   * @throws ClassNotFoundException
   */
  Object proxy(String service) throws ClassNotFoundException;

  /**
   * 获取当前服务端信息
   * @return
   */
  RpcServerInfo currentServer();

  /**
   * client服务是否启动
   * @return
   */
  boolean isStarted();
}
