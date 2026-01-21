package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansSmartInitializingSingletonLabTest {

    @Test
    void afterSingletonsInstantiated_runsAfterNonLazySingletons_andBeforeLazyBeans() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();

            context.registerBean("eager", EagerBean.class, () -> new EagerBean(events));
            context.registerBean("lazy", LazyBean.class, () -> new LazyBean(events), bd -> bd.setLazyInit(true));
            context.registerBean("callback", AfterSingletonsRecorder.class, () -> new AfterSingletonsRecorder(beanFactory, events));

            context.refresh();

            System.out.println("OBSERVE: SmartInitializingSingleton runs after non-lazy singletons are instantiated");
            assertThat(beanFactory.containsSingleton("lazy")).isFalse();

            context.getBean("lazy", LazyBean.class);
            assertThat(beanFactory.containsSingleton("lazy")).isTrue();
        }

        assertThat(events).containsExactly(
                "eager:constructed",
                "afterSingletons:lazyInCache=false",
                "lazy:constructed"
        );
    }

    static class EagerBean {
        EagerBean(List<String> events) {
            events.add("eager:constructed");
        }
    }

    static class LazyBean {
        LazyBean(List<String> events) {
            events.add("lazy:constructed");
        }
    }

    static class AfterSingletonsRecorder implements SmartInitializingSingleton {
        private final DefaultListableBeanFactory beanFactory;
        private final List<String> events;

        AfterSingletonsRecorder(DefaultListableBeanFactory beanFactory, List<String> events) {
            this.beanFactory = beanFactory;
            this.events = events;
        }

        @Override
        public void afterSingletonsInstantiated() {
            boolean lazyInCache = beanFactory.containsSingleton("lazy");
            events.add("afterSingletons:lazyInCache=" + lazyInCache);
        }
    }
}
