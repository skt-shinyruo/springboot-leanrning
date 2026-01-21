package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansProxyingPhaseLabTest {

    @Test
    void beanPostProcessorCanReturnAProxyAsTheFinalExposedBean_andSelfInvocationStillBypassesTheProxy() {
        CallLog callLog = new CallLog();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean(CallLog.class, () -> callLog);
            context.registerBean(ProxyingPostProcessor.class, () -> new ProxyingPostProcessor(callLog));
            context.registerBean(SelfInvocationService.class);
            context.refresh();

            WorkService service = context.getBean(WorkService.class);

            System.out.println("OBSERVE: BeanPostProcessor can replace a bean instance with a JDK proxy");
            System.out.println("OBSERVE: Calls are intercepted only when the call goes through the proxy (self-invocation bypasses)");

            assertThat(Proxy.isProxyClass(service.getClass())).isTrue();

            callLog.clear();
            assertThat(service.outer("Bob")).isEqualTo("outer:inner:Bob");
            assertThat(callLog.entries()).containsExactly("outer");

            callLog.clear();
            assertThat(service.inner("Bob")).isEqualTo("inner:Bob");
            assertThat(callLog.entries()).containsExactly("inner");
        }
    }

    @Test
    void whenABeanIsWrappedAsJdkProxy_lookupByConcreteClassMayBecomeUnavailable() {
        CallLog callLog = new CallLog();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean(CallLog.class, () -> callLog);
            context.registerBean(ProxyingPostProcessor.class, () -> new ProxyingPostProcessor(callLog));
            context.registerBean(SelfInvocationService.class);
            context.refresh();

            System.out.println("OBSERVE: JDK proxy only implements interfaces; it is not assignable to the concrete class");
            assertThatThrownBy(() -> context.getBean(SelfInvocationService.class))
                    .isInstanceOf(BeansException.class);
        }
    }

    @Test
    void whenABeanIsWrappedAsCglibProxy_lookupByConcreteClassMayStillWork() {
        CallLog callLog = new CallLog();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean(CallLog.class, () -> callLog);
            context.registerBean(CglibProxyingPostProcessor.class, () -> new CglibProxyingPostProcessor(callLog));
            context.registerBean(SelfInvocationService.class);
            context.refresh();

            SelfInvocationService service = context.getBean(SelfInvocationService.class);
            WorkService byInterface = context.getBean(WorkService.class);

            System.out.println("OBSERVE: class-based (CGLIB) proxy is a subclass; it is still assignable to the concrete class");
            assertThat(byInterface).isSameAs(service);
            assertThat(Enhancer.isEnhanced(service.getClass())).isTrue();
            assertThat(service.getClass()).isNotEqualTo(SelfInvocationService.class);

            callLog.clear();
            assertThat(service.outer("Bob")).isEqualTo("outer:inner:Bob");
            assertThat(callLog.entries()).containsExactly("outer");
        }
    }

    interface WorkService {
        String outer(String name);

        String inner(String name);
    }

    static class SelfInvocationService implements WorkService {

        @Override
        public String outer(String name) {
            return "outer:" + this.inner(name);
        }

        @Override
        public String inner(String name) {
            return "inner:" + name;
        }
    }

    static class CallLog {
        private final List<String> entries = new CopyOnWriteArrayList<>();

        void add(String methodName) {
            entries.add(methodName);
        }

        List<String> entries() {
            return List.copyOf(entries);
        }

        void clear() {
            entries.clear();
        }
    }

    static class ProxyingPostProcessor implements BeanPostProcessor {
        private final CallLog callLog;

        ProxyingPostProcessor(CallLog callLog) {
            this.callLog = callLog;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (!(bean instanceof WorkService target)) {
                return bean;
            }

            if (Proxy.isProxyClass(bean.getClass())) {
                return bean;
            }

            return Proxy.newProxyInstance(
                    WorkService.class.getClassLoader(),
                    new Class<?>[]{WorkService.class},
                    (proxy, method, args) -> {
                        callLog.add(method.getName());
                        return method.invoke(target, args);
                    }
            );
        }
    }

    static class CglibProxyingPostProcessor implements BeanPostProcessor {
        private final CallLog callLog;

        CglibProxyingPostProcessor(CallLog callLog) {
            this.callLog = callLog;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (!(bean instanceof SelfInvocationService target)) {
                return bean;
            }

            if (Enhancer.isEnhanced(bean.getClass())) {
                return bean;
            }

            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(bean.getClass());
            enhancer.setInterfaces(new Class<?>[]{WorkService.class});
            enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                callLog.add(method.getName());
                return method.invoke(target, args);
            });
            return enhancer.create();
        }
    }
}
