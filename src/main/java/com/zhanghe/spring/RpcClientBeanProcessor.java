package com.zhanghe.spring;

import com.zhanghe.resource.annotation.RpcClient;
import com.zhanghe.resource.annotation.RpcService;
import java.util.Iterator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class RpcClientBeanProcessor implements BeanFactoryPostProcessor,BeanPostProcessor,ApplicationContextAware {

  private String scanPackage;

  private ApplicationContext applicationContext;

  public RpcClientBeanProcessor(String scanPackage) {
    this.scanPackage = scanPackage;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    if (configurableListableBeanFactory instanceof BeanDefinitionRegistry) {

      BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) configurableListableBeanFactory;

      ClientScanner scanner = new ClientScanner(beanDefinitionRegistry);
      AnnotationTypeFilter filter = new AnnotationTypeFilter(RpcClient.class,false,true);
      scanner.addIncludeFilter(filter);
      if(scanPackage.contains(",")){
        scanner.scan(scanPackage.split(","));
      }else{
        scanner.scan(scanPackage);
      }
    }
  }

}
