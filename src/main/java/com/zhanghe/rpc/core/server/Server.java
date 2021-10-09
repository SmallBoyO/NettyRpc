package com.zhanghe.rpc.core.server;

import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.rpc.core.plugin.server.RpcServerFilter;
import java.util.List;

public interface Server {

  /**
   * 初始化服务
   */
  void init();

  /**
   * 销毁服务
   */
  void destroy();

  /**
   * 设置序列化方式
   * @param serializer
   */
  void setSerializer(Serializer serializer);

  /**
   * 绑定一个service(即提供给客户端调用的rpc接口)
   * @param service
   */
  void bind(Object service);

  /**
   *  绑定多个service
   * @param services
   */
  void bind(List<Object> services);

  /**
   * 添加过滤器
   * @param filter
   */
  void addFilter(RpcServerFilter filter);
}
