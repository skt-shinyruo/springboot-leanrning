package com.learning.springboot.springcorebeans.appendix;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;

class SpringCoreBeansGenericTypeMatchingPitfallsLabTest {

    @Test
    void genericTypeMatching_canFailWhenCandidateLosesGenericInformation_likeJdkProxySingleton() {
        ResolvableType handlerOfString = ResolvableType.forClassWithGenerics(Handler.class, String.class);

        DefaultListableBeanFactory beanFactoryWithClassMetadata = new DefaultListableBeanFactory();
        beanFactoryWithClassMetadata.registerBeanDefinition("stringHandler", new RootBeanDefinition(StringHandler.class));
        assertThat(beanFactoryWithClassMetadata.getBeanNamesForType(handlerOfString)).contains("stringHandler");

        DefaultListableBeanFactory beanFactoryWithProxyInstance = new DefaultListableBeanFactory();
        Handler<String> target = new StringHandler();
        Object proxy = Proxy.newProxyInstance(
                Handler.class.getClassLoader(),
                new Class<?>[]{Handler.class},
                (ignored, method, args) -> method.invoke(target, args)
        );
        beanFactoryWithProxyInstance.registerSingleton("handlerProxy", proxy);

        assertThat(beanFactoryWithProxyInstance.getBeanNamesForType(Handler.class)).contains("handlerProxy");
        assertThat(beanFactoryWithProxyInstance.getBeanNamesForType(handlerOfString)).isEmpty();
    }

    interface Handler<T> {
        T handle(T input);
    }

    static class StringHandler implements Handler<String> {
        @Override
        public String handle(String input) {
            return input;
        }
    }
}

