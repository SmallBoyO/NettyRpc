package com.zhanghe.spring;

import java.util.Set;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

public class ClientScanner extends ClassPathBeanDefinitionScanner {

  private final static String FACTORY_METHOD_NAME = "proxy";
  private final static String FACTORY_BEAN_NAME = "client";


  public ClientScanner(BeanDefinitionRegistry registry) {
    super(registry);
  }

  @Override
  protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
    Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
    return beanDefinitionHolders;
  }

  @Override
  protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
    return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
  }

  @Override
  protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
    beanDefinition.setFactoryBeanName(FACTORY_BEAN_NAME);
    beanDefinition.setFactoryMethodName(FACTORY_METHOD_NAME);
    beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0,beanDefinition.getBeanClassName());
  }
}
