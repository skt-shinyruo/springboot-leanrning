package com.learning.springboot.springcorebeans.part05_aot_and_real_world.xmlns;

import java.util.Objects;

import org.w3c.dom.Element;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;

final class DemoMessageBeanDefinitionParser implements BeanDefinitionParser {

    @Override
    public org.springframework.beans.factory.config.BeanDefinition parse(Element element, ParserContext parserContext) {
        String id = element.getAttribute("id");
        String value = element.getAttribute("value");

        if (id == null || id.isBlank()) {
            id = "demoMessage";
        }

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DemoMessage.class);
        builder.addConstructorArgValue(Objects.toString(value, ""));

        org.springframework.beans.factory.config.BeanDefinition definition = builder.getBeanDefinition();
        parserContext.getRegistry().registerBeanDefinition(id, definition);
        return definition;
    }
}

