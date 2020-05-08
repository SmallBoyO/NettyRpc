package com.zhanghe.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghe
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RpcLoadBalanceClientConfigurationSelector.class)
public @interface EnableLoadBalanceRpcClient {

  RpcServerInfo[] rpcServers();

  String loadBalance();

  String scanPacakges();

}
