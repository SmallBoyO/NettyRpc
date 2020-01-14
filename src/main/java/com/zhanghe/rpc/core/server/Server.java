package com.zhanghe.rpc.core.server;

import java.util.List;

public interface Server {

  void init();

  void destroy();

  void bind(Object service);

  void bind(List<Object> services);
}
