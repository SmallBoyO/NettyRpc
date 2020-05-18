package com.zhanghe.spring;

import com.zhanghe.spring.annotation.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class RpcClientBeanProcessor implements BeanFactoryPostProcessor,BeanPostProcessor,ApplicationContextAware {

  private static final Logger logger = LoggerFactory.getLogger(RpcClientBeanProcessor.class);

  private static String SCANPACKAGE_SPLITER = ",";

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
      logger.info("scan package:[{}]",scanPackage);
      if(scanPackage.contains(SCANPACKAGE_SPLITER)){
        scanner.scan(scanPackage.split(SCANPACKAGE_SPLITER));
      }else{
        scanner.scan(scanPackage);
      }
    }
  }

}
