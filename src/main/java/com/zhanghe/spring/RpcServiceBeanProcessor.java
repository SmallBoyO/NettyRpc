package com.zhanghe.spring;

import com.zhanghe.rpc.core.server.BaseRpcServer;
import com.zhanghe.spring.annotation.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class RpcServiceBeanProcessor implements BeanFactoryPostProcessor,BeanPostProcessor,ApplicationContextAware{

  private static final Logger logger = LoggerFactory.getLogger(RpcServiceBeanProcessor.class);

  private static String SCANPACKAGE_SPLITER = ",";

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

      logger.info("scan package:[{}]",scanPackage);
      if(scanPackage.contains(SCANPACKAGE_SPLITER)){
        scanner.scan(scanPackage.split(SCANPACKAGE_SPLITER));
      }else{
        scanner.scan(scanPackage);
      }
    }
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    if (!this.isMatchPackage(bean)){
      return bean;
    }
    RpcService rpcService = AnnotationUtils.findAnnotation(bean.getClass(),RpcService.class);
    if(rpcService != null){
      BaseRpcServer baseRpcServer = applicationContext.getBean(BaseRpcServer.class);
      baseRpcServer.bind(bean);
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  private boolean isMatchPackage(Object bean) {
    String[] annotationPackages;
    if(scanPackage.contains(SCANPACKAGE_SPLITER)){
      annotationPackages = scanPackage.split(SCANPACKAGE_SPLITER);
    }else{
      annotationPackages = new String[]{scanPackage};
    }
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
