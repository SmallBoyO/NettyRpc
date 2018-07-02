# netty-rpc

#### 项目介绍
基于netty实现的rpc服务

#### 安装教程
1. clone代码到本地
2. 执行mvn insall 到本地仓库

#### 使用说明

1. springxml配置

1.1 客户端配置
```
<!--  配置rpc服务的ip和端口-->
<bean id="rpcClient" class="com.zhanghe.server.RpcClient">
    <constructor-arg name="host" value="127.0.0.1"></constructor-arg>
    <constructor-arg name="port" value="6666"></constructor-arg>
</bean>
<!-- 通过proxy方法代理相应接口  -->	  	
<bean id="testService" class="com.zhanghe.service.TestService" factory-bean="rpcClient" factory-method="proxy">
    <constructor-arg name="serviceName" value="com.zhanghe.service.TestService"></constructor-arg>
</bean>
```
1.2 服务端配置
```
<!-- 配置需要绑定的服务 -->
<bean id="dateService" class="com.zhanghe.service.TestServiceImpl"></bean>
	  
<!-- 配置rpcServer监听端口,并且指定需要绑定的服务 -->	  
<bean id="rpcServer" class="com.zhanghe.server.RpcServer">
    <constructor-arg value="6666" name="port">
    </constructor-arg>
    <constructor-arg name="handlerMap">
        <map>
            <entry key="com.zhanghe.service.TestService" value-ref="dateService"></entry>
        </map>
    </constructor-arg>
</bean>

```