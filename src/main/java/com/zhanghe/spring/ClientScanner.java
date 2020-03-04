package com.zhanghe.spring;

import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

public class ClientScanner extends ClassPathBeanDefinitionScanner {

  public ClientScanner(BeanDefinitionRegistry registry) {
    super(registry);
  }

  @Override
  protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
    System.out.println("scan");
    Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
    if(beanDefinitionHolders != null){
      beanDefinitionHolders.forEach(beanDefinitionHolder -> {
        GenericBeanDefinition genericBeanDefinition = (GenericBeanDefinition)beanDefinitionHolder.getBeanDefinition();
        genericBeanDefinition.setFactoryBeanName("client");
        genericBeanDefinition.setFactoryMethodName("proxy");
        genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(genericBeanDefinition.getBeanClassName());
        System.out.println("----------------");
        System.out.println(genericBeanDefinition.getBeanClassName());
      });
    }
    return beanDefinitionHolders;
  }
}
