package com.learning.springboot.springcorebeans.part05_aot_and_real_world.xmlns;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class DemoNamespaceHandler extends NamespaceHandlerSupport {

    public static final String NAMESPACE_URI = "http://learning.springboot/schema/demo";

    @Override
    public void init() {
        registerBeanDefinitionParser("message", new DemoMessageBeanDefinitionParser());
    }
}

