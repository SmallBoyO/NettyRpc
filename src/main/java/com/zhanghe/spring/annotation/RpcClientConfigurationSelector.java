package com.zhanghe.spring.annotation;

import com.zhanghe.protocol.serializer.SerializerAlgorithm;
import com.zhanghe.protocol.serializer.SerializerManager;
import com.zhanghe.rpc.core.client.BaseRpcClient;
import com.zhanghe.spring.RpcClientBeanProcessor;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

public class RpcClientConfigurationSelector implements ImportBeanDefinitionRegistrar {

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry) {
    AnnotationAttributes attributes =
        AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableRpcClient.class.getName(), false));

    String ip = attributes.getString("ip");
    int port = attributes.getNumber("port");
    String scanPacakges = attributes.getString("scanPacakges");
    String serializer = "";

    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(BaseRpcClient.class);
    builder.addPropertyValue("ip",ip);
    builder.addPropertyValue("port",port);
    if(!StringUtils.isEmpty(serializer)){
      switch ( serializer ){
        case "KRYO":
          builder.addPropertyValue("serializer",SerializerManager.getSerializer(SerializerAlgorithm.KYRO));
          break;
        case "JSON":
          builder.addPropertyValue("serializer",SerializerManager.getSerializer(SerializerAlgorithm.JSON));
          break;
        case "PROTOSTUFF":
          builder.addPropertyValue("serializer",SerializerManager.getSerializer(SerializerAlgorithm.PROTOSTUFF));
          break;
        default:
          builder.addPropertyValue("serializer",new RuntimeBeanReference(serializer));
          break;
      }
    }
    builder.setInitMethodName("init");
    builder.setDestroyMethodName("destroy");

    registry.registerBeanDefinition("client",builder.getBeanDefinition());

    if(!StringUtils.isEmpty(scanPacakges)){
      //注册RpcServiceBeanProcessor用于扫描RpcService注解
      RootBeanDefinition beanDefinition = new RootBeanDefinition();
      beanDefinition.setBeanClass(RpcClientBeanProcessor.class);
      beanDefinition.setLazyInit(false);
      beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0,scanPacakges);
      String id = RpcClientBeanProcessor.class.getName();
      registry.registerBeanDefinition(id, beanDefinition);
    }
  }
}
