package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.serviceloader.ServiceListFactoryBean;
import org.springframework.beans.factory.serviceloader.ServiceLoaderFactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.learning.springboot.springcorebeans.part05_aot_and_real_world.serviceloader.DemoGreetingService;

class SpringCoreBeansServiceLoaderFactoryBeansLabTest {

    @Test
    void serviceListFactoryBean_loadsProviders_fromMetaInfServices() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBeanDefinition(
                    "greetingServices",
                    BeanDefinitionBuilder.genericBeanDefinition(ServiceListFactoryBean.class)
                            .addPropertyValue("serviceType", DemoGreetingService.class)
                            .getBeanDefinition());

            context.refresh();

            @SuppressWarnings("unchecked")
            List<DemoGreetingService> services = (List<DemoGreetingService>) context.getBean("greetingServices");

            System.out.println("OBSERVE: ServiceListFactoryBean returns a List<serviceType> from Java ServiceLoader");
            assertThat(services).extracting(DemoGreetingService::hello).containsExactlyInAnyOrder("hello", "hi");

            Object factory = context.getBean("&greetingServices");
            assertThat(factory).isInstanceOf(ServiceListFactoryBean.class);
        }
    }

    @Test
    void serviceLoaderFactoryBean_exposesRawServiceLoader() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBeanDefinition(
                    "greetingServiceLoader",
                    BeanDefinitionBuilder.genericBeanDefinition(ServiceLoaderFactoryBean.class)
                            .addPropertyValue("serviceType", DemoGreetingService.class)
                            .getBeanDefinition());

            context.refresh();

            @SuppressWarnings("unchecked")
            ServiceLoader<DemoGreetingService> loader =
                    (ServiceLoader<DemoGreetingService>) context.getBean("greetingServiceLoader");

            List<String> messages = new ArrayList<>();
            for (DemoGreetingService service : loader) {
                messages.add(service.hello());
            }

            System.out.println("OBSERVE: ServiceLoaderFactoryBean exposes ServiceLoader<serviceType> as the product");
            assertThat(messages).containsExactlyInAnyOrder("hello", "hi");
        }
    }
}

