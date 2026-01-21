package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

// 演示：自定义 XML namespace 如何通过 spring.handlers/schemas 解析为 BeanDefinition 并注册进容器。

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.learning.springboot.springcorebeans.part05_aot_and_real_world.xmlns.DemoMessage;
import com.learning.springboot.springcorebeans.part05_aot_and_real_world.xmlns.DemoNamespaceHandler;

class SpringCoreBeansXmlNamespaceExtensionLabTest {

    @Test
    void customNamespaceHandler_isDiscoveredViaSpringHandlers_andCanRegisterBeanDefinitions() {
        DefaultNamespaceHandlerResolver resolver = new DefaultNamespaceHandlerResolver();
        NamespaceHandler handler = resolver.resolve(DemoNamespaceHandler.NAMESPACE_URI);

        assertThat(handler).isInstanceOf(DemoNamespaceHandler.class);
    }

    @Test
    void customXmlElement_isParsedIntoBeanDefinition_andThenIntoBeanInstance() {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "part05_aot_and_real_world/xml/demo-namespace.xml"
        )) {
            DemoMessage message = context.getBean("demoMessage", DemoMessage.class);
            assertThat(message.value()).isEqualTo("hello-from-namespace");

            BeanDefinition definition = context.getBeanFactory().getBeanDefinition("demoMessage");
            assertThat(definition.getBeanClassName()).isEqualTo(DemoMessage.class.getName());
        }
    }
}

