package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

// 演示：PropertiesBeanDefinitionReader 如何把 properties 输入落到 BeanDefinitionRegistry（再由 BeanFactory 产出实例）。

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

import com.learning.springboot.springcorebeans.part05_aot_and_real_world.reader.ReaderDemoMessage;

class SpringCoreBeansPropertiesBeanDefinitionReaderLabTest {

    @Test
    void propertiesBeanDefinitionReader_registersBeanDefinitions_fromPropertiesFile() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(beanFactory);

        int count = reader.loadBeanDefinitions(new ClassPathResource("part05_aot_and_real_world/reader/beans.properties"));
        assertThat(count).isGreaterThan(0);

        ReaderDemoMessage message = beanFactory.getBean("demoMessage", ReaderDemoMessage.class);
        assertThat(message.getValue()).isEqualTo("hello-from-properties");
    }
}

