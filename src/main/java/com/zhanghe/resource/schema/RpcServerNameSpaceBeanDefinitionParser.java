package com.zhanghe.resource.schema;

import com.zhanghe.rpc.core.server.AbstractRpcServer;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RpcServerNameSpaceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

  @Override
  protected Class getBeanClass(Element ele){
    return AbstractRpcServer.class;
  }

  @Override
  protected void doParse(Element element, BeanDefinitionBuilder builder) {
    super.doParse(element, builder);
  }

  @Override
  protected void doParse(Element element, ParserContext parserContext,
      BeanDefinitionBuilder builder) {
    String ip = element.getAttribute("ip");
    int port=Integer.valueOf(element.getAttribute("port"));
    builder.addPropertyValue("ip",ip);
    builder.addPropertyValue("port",port);
    builder.setInitMethodName("init");
    builder.setDestroyMethodName("destroy");
    ManagedList lists = new ManagedList();
    NodeList elementSons = element.getChildNodes();
    if (elementSons != null && elementSons.getLength() > 0) {
      for (int i = 0; i < elementSons.getLength(); i++) {
        Node node = elementSons.item(i);
        if (node instanceof Element) {
          if ("service".equals(node.getNodeName()) || "service".equals(node.getLocalName())) {
            String value = ((Element) node).getAttribute("value");
            lists.add(new RuntimeBeanReference(value));
          }
        }
      }
    }
    builder.addPropertyValue("services",lists);
  }

}
