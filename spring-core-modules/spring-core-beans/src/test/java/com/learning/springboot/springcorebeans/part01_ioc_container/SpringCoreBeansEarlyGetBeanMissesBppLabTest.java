package com.learning.springboot.springcorebeans.part01_ioc_container;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansEarlyGetBeanMissesBppLabTest {

    @Test
    void earlyGetBeanInBeanFactoryPostProcessor_canCauseTargetToMissLaterBeanPostProcessors() {
        try (AnnotationConfigApplicationContext normal = new AnnotationConfigApplicationContext()) {
            normal.registerBean("targetWrappingBpp", TargetWrappingBpp.class);
            normal.registerBean("target", TargetImpl.class);
            normal.refresh();

            TargetApi target = normal.getBean(TargetApi.class);
            assertThat(Proxy.isProxyClass(target.getClass())).isTrue();
        }

        try (AnnotationConfigApplicationContext early = new AnnotationConfigApplicationContext()) {
            early.registerBean("targetWrappingBpp", TargetWrappingBpp.class);
            early.registerBean("target", TargetImpl.class);

            early.addBeanFactoryPostProcessor(beanFactory -> beanFactory.getBean("target"));

            early.refresh();

            TargetApi target = early.getBean(TargetApi.class);
            assertThat(Proxy.isProxyClass(target.getClass())).isFalse();
            assertThat(target).isInstanceOf(TargetImpl.class);
        }
    }

    interface TargetApi {
        String ping();
    }

    static class TargetImpl implements TargetApi {
        @Override
        public String ping() {
            return "pong";
        }
    }

    static class TargetWrappingBpp implements BeanPostProcessor {

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (!"target".equals(beanName)) {
                return bean;
            }
            if (!(bean instanceof TargetApi target)) {
                return bean;
            }

            if (Proxy.isProxyClass(bean.getClass())) {
                return bean;
            }

            return Proxy.newProxyInstance(
                    TargetApi.class.getClassLoader(),
                    new Class<?>[]{TargetApi.class},
                    (proxy, method, args) -> method.invoke(target, args)
            );
        }
    }
}

