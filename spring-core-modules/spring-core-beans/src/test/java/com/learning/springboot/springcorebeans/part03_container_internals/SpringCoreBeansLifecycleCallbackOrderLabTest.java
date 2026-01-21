package com.learning.springboot.springcorebeans.part03_container_internals;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

class SpringCoreBeansLifecycleCallbackOrderLabTest {

    @Test
    void singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization() {
        LifecycleEvents lifecycleEvents = new LifecycleEvents(new ArrayList<>());

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(LifecycleEvents.class, () -> lifecycleEvents);
            context.registerBean(RecordingBeanPostProcessor.class, () -> new RecordingBeanPostProcessor(lifecycleEvents));
            context.registerBean(
                    "recordingBean",
                    RecordingBean.class,
                    () -> new RecordingBean(lifecycleEvents),
                    bd -> {
                        bd.setInitMethodName("initMethod");
                        bd.setDestroyMethodName("destroyMethod");
                    }
            );

            context.refresh();
        }

        System.out.println("OBSERVE: lifecycle order is constructor -> aware -> BPP(before) -> init callbacks -> BPP(after) -> destroy callbacks");
        assertThat(lifecycleEvents.events()).containsExactly(
                "constructor",
                "aware:beanName=recordingBean",
                "aware:beanFactory",
                "aware:applicationContext",
                "bpp:beforeInit",
                "postConstruct",
                "afterPropertiesSet",
                "initMethod",
                "bpp:afterInit",
                "preDestroy",
                "destroy",
                "destroyMethod"
        );
    }

    @Test
    void prototypeBeans_areNotDestroyedByContainerByDefault() {
        List<String> events = new ArrayList<>();
        PrototypeWithDestroy prototype;

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean(
                "prototypeBean",
                PrototypeWithDestroy.class,
                () -> new PrototypeWithDestroy(events),
                bd -> bd.setScope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        );
        context.refresh();

        prototype = context.getBean(PrototypeWithDestroy.class);
        context.close();

        System.out.println("OBSERVE: prototype beans are created by container, but destruction callbacks are not managed by default");
        assertThat(events).contains("constructed");
        assertThat(prototype.destroyCallbackInvoked()).isFalse();
    }

    static class LifecycleEvents {
        private final List<String> events;

        LifecycleEvents(List<String> events) {
            this.events = events;
        }

        void add(String event) {
            events.add(event);
        }

        List<String> events() {
            return events;
        }
    }

    static class RecordingBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {

        private final LifecycleEvents events;

        RecordingBeanPostProcessor(LifecycleEvents events) {
            this.events = events;
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof RecordingBean) {
                events.add("bpp:beforeInit");
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof RecordingBean) {
                events.add("bpp:afterInit");
            }
            return bean;
        }
    }

    static class RecordingBean implements BeanNameAware, BeanFactoryAware, ApplicationContextAware, InitializingBean, DisposableBean {

        private final LifecycleEvents events;

        RecordingBean(LifecycleEvents events) {
            this.events = events;
            this.events.add("constructor");
        }

        @Override
        public void setBeanName(String name) {
            events.add("aware:beanName=" + name);
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) {
            events.add("aware:beanFactory");
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) {
            events.add("aware:applicationContext");
        }

        @PostConstruct
        void postConstruct() {
            events.add("postConstruct");
        }

        @Override
        public void afterPropertiesSet() {
            events.add("afterPropertiesSet");
        }

        void initMethod() {
            events.add("initMethod");
        }

        @PreDestroy
        void preDestroy() {
            events.add("preDestroy");
        }

        @Override
        public void destroy() {
            events.add("destroy");
        }

        void destroyMethod() {
            events.add("destroyMethod");
        }
    }

    static class PrototypeWithDestroy {
        private final List<String> events;
        private boolean destroyCallbackInvoked;

        PrototypeWithDestroy(List<String> events) {
            this.events = events;
            this.events.add("constructed");
        }

        @PreDestroy
        void preDestroy() {
            destroyCallbackInvoked = true;
            events.add("preDestroy");
        }

        boolean destroyCallbackInvoked() {
            return destroyCallbackInvoked;
        }
    }
}
