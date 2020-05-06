package com.zhanghe.test.spring.configuration;

import com.zhanghe.spring.annotation.EnableRpcServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@EnableRpcServer(ip = "127.0.0.1",port = 6666,scanPacakges = "com.zhanghe.test")
@Configuration
public class EnableRpcServerConfiguration {

}
