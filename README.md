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
  <bean name="client" class="com.zhanghe.rpc.RpcClientSpringAdaptor" init-method="init" destroy-method="destroy">
    <property name="ip" value="127.0.0.1"></property>
    <property name="port" value="7777"></property>
  </bean>
```
1.2 服务端配置
```
  <bean name="demoService" class="com.zhanghe.test.spring.DemoServiceImpl"></bean>

  <bean name="adaptor" class="com.zhanghe.rpc.RpcServerSpringAdaptor" init-method="init" destroy-method="destroy">
    <property name="ip" value="127.0.0.1"></property>
    <property name="port" value="777"></property>
    <property name="services">
      <list>
        <ref bean="demoService"></ref>
      </list>
    </property>
  </bean>

```