package com.learning.springboot.springcorebeans.appendix;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import java.util.function.Supplier;

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

    @Test
    void genericTypeMatching_canWorkWhenCandidateKeepsGenericSignature_likeConcreteClassInstance() {
        ResolvableType handlerOfString = ResolvableType.forClassWithGenerics(Handler.class, String.class);

        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("stringHandlerInstance", new StringHandler());

        System.out.println("OBSERVE: a concrete class instance still carries generic signature in its class metadata");
        assertThat(beanFactory.getBeanNamesForType(handlerOfString)).contains("stringHandlerInstance");
    }

    @Test
    void genericTypeMatching_canBeRestoredByProvidingTargetTypeMetadata_evenIfRuntimeInstanceIsAProxy() {
        ResolvableType handlerOfString = ResolvableType.forClassWithGenerics(Handler.class, String.class);

        Handler<String> target = new StringHandler();
        Handler proxy = (Handler) Proxy.newProxyInstance(
                Handler.class.getClassLoader(),
                new Class<?>[]{Handler.class},
                (ignored, method, args) -> method.invoke(target, args)
        );

        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        Supplier<Handler> proxySupplier = () -> proxy;
        RootBeanDefinition beanDefinition = new RootBeanDefinition(Handler.class, proxySupplier);
        beanDefinition.setTargetType(handlerOfString);

        beanFactory.registerBeanDefinition("handlerProxyWithTargetType", beanDefinition);

        System.out.println("OBSERVE: generic matching relies on type metadata; you can explicitly provide target ResolvableType");
        assertThat(beanFactory.getBeanNamesForType(handlerOfString)).contains("handlerProxyWithTargetType");
        assertThat(beanFactory.getBean("handlerProxyWithTargetType")).isSameAs(proxy);
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
