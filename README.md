# netty-rpc

![](https://www.travis-ci.org/SmallBoyO/NettyRpc.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/SmallBoyO/NettyRpc/badge.svg?branch=master)](https://coveralls.io/github/SmallBoyO/NettyRpc?branch=master)

#### 项目介绍
基于netty实现的rpc服务

#### 安装教程
1. clone代码到本地
2. 执行mvn insall 到本地仓库

#### 使用说明

##### java代码配置

1.1 服务端
```
    BaseRpcServer rpcServer = new BaseRpcServer(7777);
    rpcServer.init();
    rpcServer.bind(new DemoServiceImpl());
```

1.2 客户端
```
    BaseRpcClient rpcClient = new BaseRpcClient("127.0.0.1",7777);
    rpcClient.init();
    demoService = (DemoService) rpcClient.proxy(DemoService.class.getName());
```

##### spring xml配置

1.1 服务端
```
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:rpc="http://www.zhanghe.com/schema/rpc"
xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
  http://www.zhanghe.com/schema/rpc
  http://www.zhanghe.com/schema/rpc.xsd">

<bean name="demoService" class="com.zhanghe.test.spring.service.DemoServiceImpl"></bean>

<rpc:server  id="adaptor" port="6667" ip="127.0.0.1">
  <rpc:service value="demoService"></rpc:service>
</rpc:server>

</beans>
```

1.2 客户端
```
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:rpc="http://www.zhanghe.com/schema/rpc"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
  http://www.zhanghe.com/schema/rpc
  http://www.zhanghe.com/schema/rpc.xsd">

  <rpc:client id="client" port="7777" ip="127.0.0.1"></rpc:client>

  <rpc:clientService id="demoService" class="com.zhanghe.test.spring.service.DemoService"></rpc:clientService>

</beans>
```

1.3 服务端注解扫描

在rpc:server中添加scanPackage配置,在需要注册的service上添加注解@RpcService

```
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:rpc="http://www.zhanghe.com/schema/rpc"
  xmlns:context="http://www.springframework.org/schema/context"
xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
  http://www.zhanghe.com/schema/rpc
  http://www.zhanghe.com/schema/rpc.xsd
  http://www.springframework.org/schema/context
  http://www.springframework.org/schema/context/spring-context.xsd">

  <context:annotation-config></context:annotation-config>

  <rpc:server  id="adaptor" port="6668" ip="127.0.0.1" scanPackage="com.zhanghe.test.spring.service">
  </rpc:server>

</beans>

@RpcService(value = "rpcservice")
public class DemoServiceImpl implements DemoService {

  @Override
  public String call(String requestParam) {
    return "requestParam:" + requestParam;
  }
}

```

1.4 客户端注解扫描

在rpc:client中添加scanPackage配置,在需要注册的service上添加注解@DemoService
```
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:rpc="http://www.zhanghe.com/schema/rpc"
  xmlns:context="http://www.springframework.org/schema/context"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
  http://www.zhanghe.com/schema/rpc
  http://www.zhanghe.com/schema/rpc.xsd
  http://www.springframework.org/schema/context
  http://www.springframework.org/schema/context/spring-context.xsd">

  <context:annotation-config></context:annotation-config>

  <rpc:client id="client" port="6666" ip="127.0.0.1" scanPackage="com.zhanghe.test.spring.service"></rpc:client>

</beans>

@RpcClient(value = "rpcclient")
public interface DemoService{

  String call(String requestParam);

}
```
1.5 服务端使用Enable模块注解进行配置

给配置类加上@EnableRpcServer注解
```
@EnableRpcServer(ip = "127.0.0.1",port = 6666,scanPacakges = "com.zhanghe.test")
@Configuration
public class EnableRpcServerConfiguration {

}
``````
1.6 客户端使用Enable模块注解进行配置
给配置类加上@EnableRpcClient注解
```
@EnableRpcClient(ip = "127.0.0.1",port = 6666,scanPacakges = "com.zhanghe.test")
@Configuration
public class EnableRpcClientConfiguration {

}
```

##### 客户端负载均衡配置
1.1 xpring xml 配置
```
  <rpc:loadBalanceClient id="client" scanPackage="com.zhanghe.test.spring.service" loadBalance="weight_random">
    <rpc:loadBalanceServer port="7777" ip="127.0.0.1" weight="1"></rpc:loadBalanceServer>
    <rpc:loadBalanceServer port="7778" ip="127.0.0.1" weight="1"></rpc:loadBalanceServer>
  </rpc:loadBalanceClient>
```
```
1.2 Enable模块注解配置
给配置类添加@EnableLoadBalanceRpcClient注解
```
```
@EnableLoadBalanceRpcClient(
    rpcServers = {
        @RpcServerInfo(ip = "127.0.0.1",port = 6666,weight = 10),
        @RpcServerInfo(ip = "127.0.0.1",port = 6667)
    },
    loadBalance="weight_random",
    scanPacakges = "com.zhanghe.test")
@Configuration
public class EnableLoadBalanceRpcClientConfiguration {

}
```
负载均衡类型

| 算法 | 备注  |
|  ----  | ----  |
| random  | 随机 |
| weight_random  | 带权重的随机 |
| round  | 轮询 |


##### 客户端异步调用配置

1 在需要异步调用的方法加上@AsyncMethod注解
```
public interface AsyncService {

  @AsyncMethod
  String waitTwoSeconds(String str);

}
```
2 调用service
调用原方法之后,使用RpcContext.getInstance().getFuture()获取到future
```
 asyncService.waitTwoSeconds(str);
 Future<String> future = RpcContext.getInstance().getFuture();
 String result = future.get(10, TimeUnit.SECONDS);
```
