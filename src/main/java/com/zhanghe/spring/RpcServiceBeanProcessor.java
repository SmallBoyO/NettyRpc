package com.zhanghe.spring;

import com.zhanghe.resource.annotation.RpcService;
import com.zhanghe.rpc.core.server.AbstractRpcServer;
import java.util.Iterator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class RpcServiceBeanProcessor implements BeanFactoryPostProcessor,BeanPostProcessor,ApplicationContextAware{

  private String scanPackage;

  public RpcServiceBeanProcessor(String scanPackage) {
    this.scanPackage = scanPackage;
  }

  private ApplicationContext applicationContext;

  private BeanDefinitionRegistry beanRegistry;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  /**
   * 扫描 RpcService注解
   * @param configurableListableBeanFactory
   * @throws BeansException
   */
  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    if (configurableListableBeanFactory instanceof BeanDefinitionRegistry) {
      BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) configurableListableBeanFactory;

      ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry, true);
      AnnotationTypeFilter filter = new AnnotationTypeFilter(RpcService.class);
      scanner.addIncludeFilter(filter);

      if(scanPackage.contains(",")){
        scanner.scan(scanPackage.split(","));
      }else{
        scanner.scan(scanPackage);
      }
    }
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (!this.isMatchPackage(bean)){
      return bean;
    }
    AbstractRpcServer abstractRpcServer = applicationContext.getBean(AbstractRpcServer.class);
    abstractRpcServer.bind(bean);
    return bean;
  }

  private boolean isMatchPackage(Object bean) {
    String[] annotationPackages = new String[]{"com.zhanghe.test"};
    if (annotationPackages == null || annotationPackages.length == 0) {
      return true;
    }
    String beanClassName = bean.getClass().getName();
    for (String pkg : annotationPackages) {
      if (beanClassName.startsWith(pkg)) {
        return true;
      }
    }
    return false;
  }

}
