package com.learning.springboot.springcorebeans.part02_boot_autoconfig;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ByteArrayResource;

class SpringCoreBeansExceptionNavigationLabTest {

    @Test
    void unsatisfiedDependency_failsFast() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(UnsatisfiedDependencyConfig.class);

        assertThatThrownBy(context::refresh)
                .isInstanceOf(UnsatisfiedDependencyException.class)
                .hasRootCauseInstanceOf(NoSuchBeanDefinitionException.class);

        context.close();

        System.out.println("OBSERVE: UnsatisfiedDependencyException is a common wrapper for dependency injection failures");
        System.out.println("OBSERVE: The root cause is often NoSuchBeanDefinitionException / NoUniqueBeanDefinitionException");
    }

    @Test
    void beanDefinitionStoreException_invalidXml() {
        GenericApplicationContext context = new GenericApplicationContext();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);

        String invalidXml = """
                <beans>
                  <bean id="bad" class="com.example.DoesNotMatter">
                </beans>
                """;

        assertThatThrownBy(() -> reader.loadBeanDefinitions(new ByteArrayResource(invalidXml.getBytes(StandardCharsets.UTF_8))))
                .isInstanceOf(BeanDefinitionStoreException.class);

        System.out.println("OBSERVE: BeanDefinitionStoreException indicates a definition reading/parsing problem (e.g., invalid XML)");
        System.out.println("OBSERVE: It typically fails during refresh 'front half' (definition loading), before bean instantiation");
    }

    @Configuration
    static class UnsatisfiedDependencyConfig {

        @Bean
        Consumer consumer(MissingDependency missingDependency) {
            return new Consumer(missingDependency);
        }
    }

    interface MissingDependency {
    }

    record Consumer(MissingDependency missingDependency) {
    }
}

