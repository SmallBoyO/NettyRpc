# netty-rpc

#### 项目介绍
基于netty实现的rpc服务

#### 安装教程
1. clone代码到本地
2. 执行mvn insall 到本地仓库

#### 使用说明

1. spring xml配置

1.1 客户端配置
```
<rpc:client id="client" port="7777" ip="127.0.0.1"></rpc:client>
  
<rpc:clientService id="demoService" class="com.zhanghe.test.spring.DemoService"></rpc:clientService>
```
1.2 服务端配置
```
<bean name="demoService" class="com.zhanghe.test.spring.DemoServiceImpl"></bean>

<rpc:server  id="adaptor" port="6666" ip="127.0.0.1">
  <rpc:service value="demoService"></rpc:service>
</rpc:server>
```
2. spring注解配置

2.1 客户端配置
```
  @Bean
  public AbstractRpcClient getAbstractRpcClient(){
    AbstractRpcClient rpcClient = new AbstractRpcClient("127.0.0.1",666600);
    rpcClient.init();
    return rpcClient;
  }

  @Bean
  public DemoService getDemoService() throws ClassNotFoundException{
    return (DemoService)getAbstractRpcClient().proxy(DemoService.class.getName());
  }
```
2.2 服务端配置
```
 @Bean
  public AbstractRpcServer getAbstractRpcServer(){
    AbstractRpcServer abstractRpcServer = new AbstractRpcServer();
    abstractRpcServer.init();
    abstractRpcServer.setServices(Arrays.asList(new DemoServiceImpl()));
    return abstractRpcServer;
  }
```