package com.learning.springboot.springcorebeans.part02_boot_autoconfig;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.learning.springboot.springcorebeans.testsupport.BeanDefinitionOriginDumper;

class SpringCoreBeansBeanDefinitionOriginLabTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(OriginDemoAutoConfiguration.class))
            .withUserConfiguration(UserConfiguration.class)
            .withBean("manualBean", ManualBean.class, ManualBean::new);

    @Test
    void beanDefinitionMetadata_canAnswerWhoRegisteredThisBean_andWhereItCameFrom() {
        runner.run(context -> {
            Map<String, Object> candidates = context.getBeansOfType(Object.class);
            assertThat(candidates).isNotEmpty();

            System.out.println("OBSERVE: BeanDefinition tells you bean origin (factory method vs direct class, resource, source metadata)");
            System.out.println(BeanDefinitionOriginDumper.dump(context.getBeanFactory(), "demoService"));
            System.out.println(BeanDefinitionOriginDumper.dump(context.getBeanFactory(), "userService"));
            System.out.println(BeanDefinitionOriginDumper.dump(context.getBeanFactory(), "manualBean"));

            AbstractBeanDefinition demoServiceDefinition = (AbstractBeanDefinition) context.getBeanFactory().getBeanDefinition("demoService");
            assertThat(demoServiceDefinition.getFactoryMethodName()).isEqualTo("demoService");
            assertThat(demoServiceDefinition.getFactoryBeanName()).isNotNull();
            String demoFactoryBeanName = demoServiceDefinition.getFactoryBeanName();
            AbstractBeanDefinition demoFactoryDefinition = (AbstractBeanDefinition) context.getBeanFactory().getBeanDefinition(demoFactoryBeanName);
            assertThat(demoFactoryDefinition.getBeanClassName()).contains(OriginDemoAutoConfiguration.class.getName());

            AbstractBeanDefinition userServiceDefinition = (AbstractBeanDefinition) context.getBeanFactory().getBeanDefinition("userService");
            assertThat(userServiceDefinition.getFactoryMethodName()).isEqualTo("userService");
            assertThat(userServiceDefinition.getFactoryBeanName()).isNotNull();
            String userFactoryBeanName = userServiceDefinition.getFactoryBeanName();
            AbstractBeanDefinition userFactoryDefinition = (AbstractBeanDefinition) context.getBeanFactory().getBeanDefinition(userFactoryBeanName);
            assertThat(userFactoryDefinition.getBeanClassName()).contains(UserConfiguration.class.getName());

            AbstractBeanDefinition manualBeanDefinition = (AbstractBeanDefinition) context.getBeanFactory().getBeanDefinition("manualBean");
            assertThat(manualBeanDefinition.getFactoryMethodName()).isNull();
            assertThat(manualBeanDefinition.getBeanClassName()).contains(ManualBean.class.getName());
        });
    }

    interface DemoService {
        String origin();
    }

    static class AutoConfiguredDemoService implements DemoService {
        @Override
        public String origin() {
            return "auto-config";
        }
    }

    static class UserProvidedDemoService implements DemoService {
        @Override
        public String origin() {
            return "user-config";
        }
    }

    static class ManualBean {
    }

    @AutoConfiguration
    static class OriginDemoAutoConfiguration {
        @Bean
        DemoService demoService() {
            return new AutoConfiguredDemoService();
        }
    }

    @Configuration
    static class UserConfiguration {
        @Bean
        DemoService userService() {
            return new UserProvidedDemoService();
        }
    }
}
