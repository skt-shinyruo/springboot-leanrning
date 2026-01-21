package com.learning.springboot.springcorebeans.part03_container_internals;

/*
 * 本实验补齐 prototype 销毁语义：
 * - 容器会负责 prototype 的创建，但默认不托管其销毁（context close 不会触发 destroy callbacks）
 * - 如果你确实需要触发销毁回调，需要显式调用 BeanFactory#destroyBean(...)
 */

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.annotation.PreDestroy;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansPrototypeDestroySemanticsLabTest {

    @Test
    void prototypeBean_isNotDestroyedOnContextClose_byDefault() {
        AtomicInteger constructed = new AtomicInteger();
        AtomicInteger destroyed = new AtomicInteger();

        PrototypeResource prototype;
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(
                    "prototypeResource",
                    PrototypeResource.class,
                    () -> new PrototypeResource(constructed, destroyed),
                    bd -> bd.setScope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
            );
            context.refresh();

            prototype = context.getBean(PrototypeResource.class);
            assertThat(constructed.get()).isEqualTo(1);
            assertThat(prototype.destroyedCount()).isEqualTo(0);
        }

        System.out.println("OBSERVE: context close does not trigger prototype destroy callbacks by default");
        assertThat(destroyed.get()).isEqualTo(0);
    }

    @Test
    void prototypeBean_canBeDestroyedManually_viaDestroyBean() {
        AtomicInteger constructed = new AtomicInteger();
        AtomicInteger destroyed = new AtomicInteger();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(
                    "prototypeResource",
                    PrototypeResource.class,
                    () -> new PrototypeResource(constructed, destroyed),
                    bd -> bd.setScope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
            );
            context.refresh();

            PrototypeResource prototype = context.getBean(PrototypeResource.class);
            ((ConfigurableBeanFactory) context.getBeanFactory()).destroyBean("prototypeResource", prototype);

            assertThat(prototype.destroyedCount()).isEqualTo(1);
            assertThat(destroyed.get()).isEqualTo(1);
        }

        System.out.println("OBSERVE: destroyBean(beanName, instance) triggers prototype destroy callbacks explicitly");
        assertThat(constructed.get()).isEqualTo(1);
        assertThat(destroyed.get()).isEqualTo(1);
    }

    static class PrototypeResource {
        private final AtomicInteger destroyed;

        PrototypeResource(AtomicInteger constructed, AtomicInteger destroyed) {
            constructed.incrementAndGet();
            this.destroyed = destroyed;
        }

        @PreDestroy
        void preDestroy() {
            destroyed.incrementAndGet();
        }

        int destroyedCount() {
            return destroyed.get();
        }
    }
}

