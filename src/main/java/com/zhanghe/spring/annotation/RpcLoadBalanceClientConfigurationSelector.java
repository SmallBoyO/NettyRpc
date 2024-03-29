package com.zhanghe.spring.annotation;

import com.zhanghe.config.RpcClientConfig;
import com.zhanghe.rpc.core.client.RpcLoadBalanceAdaptor;
import com.zhanghe.rpc.core.client.RpcServerInfo;
import com.zhanghe.spring.RpcClientBeanProcessor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

public class RpcLoadBalanceClientConfigurationSelector implements ImportBeanDefinitionRegistrar {

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry) {
    AnnotationAttributes attributes =
        AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableLoadBalanceRpcClient.class.getName(), false));
    AnnotationAttributes[] attributesAnnotationArray = attributes.getAnnotationArray("rpcServers");
    String scanPacakges = attributes.getString("scanPacakges");
    String loadBalance = attributes.getString("loadBalance");

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcLoadBalanceAdaptor.class);
    List<RpcServerInfo> servers = new ArrayList<>();
    for(AnnotationAttributes annotationAttributes:attributesAnnotationArray){
      RpcServerInfo rpcServerInfo = new RpcServerInfo();
      RpcClientConfig rpcClientConfig = new RpcClientConfig(annotationAttributes.getString("ip"),annotationAttributes.getNumber("port"));
      rpcServerInfo.setRpcClientConfig(rpcClientConfig);
      rpcServerInfo.setWeight(annotationAttributes.getNumber("weight"));
      servers.add(rpcServerInfo);
    }
    builder.addPropertyValue("servers",servers);
    builder.addPropertyValue("loadBalance",loadBalance);
    builder.setInitMethodName("init");
    builder.setDestroyMethodName("destroy");

    registry.registerBeanDefinition("client",builder.getBeanDefinition());
    if(!StringUtils.isEmpty(scanPacakges)){
      //注册RpcServiceBeanProcessor用于扫描RpcClient注解
      RootBeanDefinition beanDefinition = new RootBeanDefinition();
      beanDefinition.setBeanClass(RpcClientBeanProcessor.class);
      beanDefinition.setLazyInit(false);
      beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0,scanPacakges);
      String id = RpcClientBeanProcessor.class.getName();
      registry.registerBeanDefinition(id, beanDefinition);
    }
  }
}
