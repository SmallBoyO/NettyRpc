# netty-rpc

![](https://www.travis-ci.org/SmallBoyO/NettyRpc.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/SmallBoyO/NettyRpc/badge.svg?branch=master)](https://coveralls.io/github/SmallBoyO/NettyRpc?branch=master)

#### 项目介绍
基于netty实现的rpc服务

#### 安装教程
1. clone代码到本地
2. 执行mvn insall 到本地仓库

#### 使用说明

##### spring xml配置

1.1 客户端配置
```
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:rpc="http://www.zhanghe.com/schema/rpc"
xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
  http://www.zhanghe.com/schema/rpc
  http://www.zhanghe.com/schema/rpc.xsd">

  <rpc:client id="client" port="6667" ip="127.0.0.1"></rpc:client>

  <rpc:clientService id="demoService" class="com.zhanghe.rpc.demo.service.DemoService"></rpc:clientService>

</beans>rpc:clientService>
```
1.2 服务端配置
```
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:rpc="http://www.zhanghe.com/schema/rpc"
xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
  http://www.zhanghe.com/schema/rpc
  http://www.zhanghe.com/schema/rpc.xsd">

<bean name="demoService" class="com.zhanghe.rpc.demo.service.impl.DemoServiceImpl"></bean>

<rpc:server  id="adaptor" port="6667" ip="127.0.0.1">
  <rpc:service value="demoService"></rpc:service>
</rpc:server>

</beans>
```
##### spring扫描注解

2.1 客户端配置
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

  <context:component-scan base-package="com.zhanghe.rpc.demo" ></context:component-scan>

  <rpc:client id="client" port="6667" ip="127.0.0.1" scanPackage="com.zhanghe.rpc.demo.service"></rpc:client>

</beans>
```
2.2 服务端配置
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


  <context:component-scan base-package="com.zhanghe.rpc.demo"></context:component-scan>

  <rpc:server  id="adaptor" port="6667" ip="127.0.0.1" scanPackage="com.zhanghe.rpc.demo.service.impl">
  </rpc:server>

</beans>
```
##### spring注解配置

3.1 客户端配置
```
  @Bean
  public BaseRpcClient getBaseRpcClient(){
    BaseRpcClient rpcClient = new BaseRpcClient("127.0.0.1",6666);
    rpcClient.init();
    return rpcClient;
  }

  @Bean
  public DemoService getDemoService() throws ClassNotFoundException{
    return (DemoService)getBaseRpcClient().proxy(DemoService.class.getName());
  }
```
3.2 服务端配置
```
 @Bean
  public BaseRpcServer getBaseRpcServer(){
    BaseRpcServer baseRpcServer = new BaseRpcServer();
    baseRpcServer.init();
    baseRpcServer.setServices(Arrays.asList(new DemoServiceImpl()));
    return baseRpcServer;
  }
```