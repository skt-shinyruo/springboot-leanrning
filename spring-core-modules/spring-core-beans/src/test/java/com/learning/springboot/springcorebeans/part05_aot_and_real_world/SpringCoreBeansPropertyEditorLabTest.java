package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

/*
 * 本实验演示 spring-beans 体系里“老但仍然重要”的 PropertyEditor：
 *
 * - 为什么 String 能注入到 int/enum/自定义类型？（不仅仅是 ConversionService）
 * - PropertyEditor 在哪些阶段介入？（applyPropertyValues → BeanWrapper/TypeConverterDelegate）
 * - 如何注册自定义 editor？（CustomEditorConfigurer / PropertyEditorRegistrar）
 *
 * 你会在真实排障中遇到：某些 legacy 配置/XML 仍依赖 PropertyEditor，或者某些类型转换行为“看起来不受 ConversionService 控制”。
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.beans.PropertyEditorSupport;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansPropertyEditorLabTest {

    @Test
    void withoutCustomPropertyEditor_stringToCustomType_shouldFail() {
        assertThatThrownBy(() -> {
            try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
                RootBeanDefinition client = new RootBeanDefinition(Client.class);
                client.getPropertyValues().add("endpoint", "localhost:8080");
                context.registerBeanDefinition("client", client);
                context.refresh();
            }
        }).isInstanceOf(BeansException.class);

        System.out.println("OBSERVE: Without PropertyEditor, String -> HostAndPort conversion fails during bean property population");
    }

    @Test
    void withCustomPropertyEditor_stringToCustomType_shouldSucceed() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean("customEditorConfigurer", CustomEditorConfigurer.class, () -> {
                CustomEditorConfigurer configurer = new CustomEditorConfigurer();
                configurer.setPropertyEditorRegistrars(new PropertyEditorRegistrar[] {new HostAndPortRegistrar()});
                return configurer;
            });
            RootBeanDefinition clientDefinition = new RootBeanDefinition(Client.class);
            clientDefinition.getPropertyValues().add("endpoint", "localhost:8080");
            context.registerBeanDefinition("client", clientDefinition);

            context.refresh();

            Client client = context.getBean(Client.class);
            assertThat(client.endpoint()).isEqualTo(new HostAndPortEndpoint("localhost", 8080));

            System.out.println("OBSERVE: CustomEditorConfigurer registers PropertyEditor, conversion happens in applyPropertyValues path");
        }
    }

    static class Client {
        private Endpoint endpoint;

        public void setEndpoint(Endpoint endpoint) {
            this.endpoint = endpoint;
        }

        Endpoint endpoint() {
            return endpoint;
        }
    }

    interface Endpoint {
        String host();

        int port();
    }

    record HostAndPortEndpoint(String host, int port) implements Endpoint {
    }

    static class HostAndPortEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) {
            String[] parts = text.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid host:port: " + text);
            }
            setValue(new HostAndPortEndpoint(parts[0], Integer.parseInt(parts[1])));
        }
    }

    static class HostAndPortRegistrar implements PropertyEditorRegistrar {
        @Override
        public void registerCustomEditors(PropertyEditorRegistry registry) {
            registry.registerCustomEditor(Endpoint.class, new HostAndPortEditor());
        }
    }
}
