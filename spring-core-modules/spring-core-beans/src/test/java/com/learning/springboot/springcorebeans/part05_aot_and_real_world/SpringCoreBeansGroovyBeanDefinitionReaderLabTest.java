package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

// 演示：GroovyBeanDefinitionReader 如何把 groovy DSL 输入落到 BeanDefinitionRegistry。

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.learning.springboot.springcorebeans.part05_aot_and_real_world.reader.ReaderDemoMessage;

class SpringCoreBeansGroovyBeanDefinitionReaderLabTest {

    @Test
    void groovyBeanDefinitionReader_registersBeanDefinitions_fromGroovyScript() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            GroovyBeanDefinitionReader reader = new GroovyBeanDefinitionReader(context);
            reader.loadBeanDefinitions(new ClassPathResource("part05_aot_and_real_world/reader/beans.groovy"));

            context.refresh();

            ReaderDemoMessage message = context.getBean("demoMessage", ReaderDemoMessage.class);
            assertThat(message.getValue()).isEqualTo("hello-from-groovy");
        }
    }
}

