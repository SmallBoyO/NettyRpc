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
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class RpcClientBeanProcessor implements BeanFactoryPostProcessor,BeanPostProcessor,ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void postProcessBeanFactory(
      ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    if (configurableListableBeanFactory instanceof BeanDefinitionRegistry) {

      BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) configurableListableBeanFactory;

      ClientScanner scanner = new ClientScanner(beanDefinitionRegistry);
      AnnotationTypeFilter filter = new AnnotationTypeFilter(RpcClient.class);
      scanner.addIncludeFilter(filter);

      scanner.scan("com.zhanghe.test");
    }
    Iterator it = configurableListableBeanFactory.getBeanNamesIterator();

    String[] names = configurableListableBeanFactory.getBeanDefinitionNames();
    // 获取了所有的bean名称列表
    for(int i=0; i<names.length; i++){
      String name = names[i];

      BeanDefinition bd = configurableListableBeanFactory.getBeanDefinition(name);
      System.out.println(name + " bean properties: " + bd.getPropertyValues().toString());
      // 本内容只是个demo，打印持有的bean的属性情况
    }
  }

}
