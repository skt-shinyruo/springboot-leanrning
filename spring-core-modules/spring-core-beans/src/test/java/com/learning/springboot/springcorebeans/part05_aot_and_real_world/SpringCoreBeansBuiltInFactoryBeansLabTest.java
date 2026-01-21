package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

/*
 * 本实验聚焦 spring-beans 里“内置 FactoryBean”的代表性用法与排障价值：
 * 1) MethodInvokingFactoryBean：把“调用一个方法”包装成一个 bean（product），并可控制是否缓存（isSingleton）
 * 2) ServiceLocatorFactoryBean：生成一个 service locator 代理，每次方法调用都会回到 BeanFactory 做查找（特别适合 +prototype）
 * 3) &beanName：获取 FactoryBean 本体 vs 获取 product（排障时常用）
 *
 * 读者应该能在断点里解释：为什么 getBean("x") 拿到的是 product，而 getBean("&x") 才是 factory。
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansBuiltInFactoryBeansLabTest {

    @Test
    void builtInFactoryBeans_methodInvoking_and_serviceLocator_and_factoryDereference() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            registerMethodInvokingFactoryBeans(context);
            registerServiceLocatorFactoryBean(context);
            context.refresh();

            // 1) MethodInvokingFactoryBean：singleton product（容器会缓存 product）
            UUID s1 = context.getBean("uuidSingleton", UUID.class);
            UUID s2 = context.getBean("uuidSingleton", UUID.class);
            assertThat(s1).isSameAs(s2);

            // 2) MethodInvokingFactoryBean：non-singleton product（每次 getBean 都会重新 invoke）
            UUID p1 = context.getBean("uuidPrototype", UUID.class);
            UUID p2 = context.getBean("uuidPrototype", UUID.class);
            assertThat(p1).isNotSameAs(p2);

            // 3) &beanName：拿到 FactoryBean 本体（排障关键点）
            Object uuidPrototypeFactory = context.getBean("&uuidPrototype");
            assertThat(uuidPrototypeFactory).isInstanceOf(MethodInvokingFactoryBean.class);

            GreetingLocator locator = context.getBean("greetingLocator", GreetingLocator.class);
            Greeting cn1 = locator.get("cnPrototype");
            Greeting cn2 = locator.get("cnPrototype");
            assertThat(cn1.language()).isEqualTo("cn");
            assertThat(cn1).isNotSameAs(cn2); // prototype：每次调用都重新查找

            Greeting en1 = locator.get("enSingleton");
            Greeting en2 = locator.get("enSingleton");
            assertThat(en1.language()).isEqualTo("en");
            assertThat(en1).isSameAs(en2); // singleton：每次调用都命中同一实例

            Object locatorFactory = context.getBean("&greetingLocator");
            assertThat(locatorFactory).isInstanceOf(ServiceLocatorFactoryBean.class);

            System.out.println("OBSERVE: getBean(\"x\") returns product; getBean(\"&x\") returns FactoryBean itself");
            System.out.println("OBSERVE: ServiceLocator proxy delegates each method call back to BeanFactory (prototype returns new)");
        }
    }

    private static void registerMethodInvokingFactoryBeans(BeanDefinitionRegistry registry) {
        registry.registerBeanDefinition(
                "uuidSingleton",
                BeanDefinitionBuilder.genericBeanDefinition(MethodInvokingFactoryBean.class)
                        .addPropertyValue("targetClass", UUID.class)
                        .addPropertyValue("targetMethod", "randomUUID")
                        .addPropertyValue("singleton", true)
                        .getBeanDefinition());

        registry.registerBeanDefinition(
                "uuidPrototype",
                BeanDefinitionBuilder.genericBeanDefinition(MethodInvokingFactoryBean.class)
                        .addPropertyValue("targetClass", UUID.class)
                        .addPropertyValue("targetMethod", "randomUUID")
                        .addPropertyValue("singleton", false)
                        .getBeanDefinition());
    }

    private static void registerServiceLocatorFactoryBean(BeanDefinitionRegistry registry) {
        RootBeanDefinition cnPrototype = new RootBeanDefinition(CnGreeting.class);
        cnPrototype.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        registry.registerBeanDefinition("cnPrototype", cnPrototype);

        registry.registerBeanDefinition("enSingleton", new RootBeanDefinition(EnGreeting.class));

        registry.registerBeanDefinition(
                "greetingLocator",
                BeanDefinitionBuilder.genericBeanDefinition(ServiceLocatorFactoryBean.class)
                        .addPropertyValue("serviceLocatorInterface", GreetingLocator.class)
                        .getBeanDefinition());
    }

    interface Greeting {
        String language();
    }

    static class CnGreeting implements Greeting {
        @Override
        public String language() {
            return "cn";
        }
    }

    static class EnGreeting implements Greeting {
        @Override
        public String language() {
            return "en";
        }
    }

    interface GreetingLocator {
        Greeting get(String beanName);
    }
}
