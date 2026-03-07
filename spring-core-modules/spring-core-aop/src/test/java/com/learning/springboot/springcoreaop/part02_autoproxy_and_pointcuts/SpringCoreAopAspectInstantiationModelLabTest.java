package com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts;

// 这个 Lab 用一正一反两套配置固化 “prototype gate”：
// - @Aspect("pertarget(...)") / perthis / pertypewithin 这类非 singleton per-clause
// - 在 Spring AOP（proxy-based）里要求 aspect bean 是 prototype，否则会被忽略（表现为：目标 bean 没有被代理）

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;

class SpringCoreAopAspectInstantiationModelLabTest {

    @Test
    void pertarget_aspect_is_ignored_when_aspect_bean_is_singleton() {
        PerTargetIdAspect.resetIds();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SingletonAspectConfig.class)) {
            PerTargetInvocationLog log = context.getBean(PerTargetInvocationLog.class);
            PerTargetDemoService s1 = context.getBean("perTargetService1", PerTargetDemoService.class);
            PerTargetDemoService s2 = context.getBean("perTargetService2", PerTargetDemoService.class);

            assertThat(AopUtils.isAopProxy(s1)).isFalse();
            assertThat(AopUtils.isAopProxy(s2)).isFalse();

            log.clear();
            s1.work("a");
            s2.work("b");

            assertThat(log.entries()).containsExactly("target:one:a", "target:two:b");
        }
    }

    @Test
    void pertarget_aspect_works_when_aspect_bean_is_prototype_and_is_instantiated_per_target() {
        PerTargetIdAspect.resetIds();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PrototypeAspectConfig.class)) {
            PerTargetInvocationLog log = context.getBean(PerTargetInvocationLog.class);
            PerTargetDemoService s1 = context.getBean("perTargetService1", PerTargetDemoService.class);
            PerTargetDemoService s2 = context.getBean("perTargetService2", PerTargetDemoService.class);

            assertThat(AopUtils.isAopProxy(s1)).isTrue();
            assertThat(AopUtils.isAopProxy(s2)).isTrue();

            log.clear();
            s1.work("a");
            s2.work("b");
            s1.work("c");

            assertThat(log.entries()).containsExactly(
                    "aspect:1",
                    "target:one:a",
                    "aspect:2",
                    "target:two:b",
                    "aspect:1",
                    "target:one:c");
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = false)
    static class SingletonAspectConfig {
        @Bean
        PerTargetInvocationLog perTargetInvocationLog() {
            return new PerTargetInvocationLog();
        }

        @Bean(name = "perTargetService1")
        PerTargetDemoService perTargetService1(PerTargetInvocationLog log) {
            return new PerTargetDemoServiceImpl("one", log);
        }

        @Bean(name = "perTargetService2")
        PerTargetDemoService perTargetService2(PerTargetInvocationLog log) {
            return new PerTargetDemoServiceImpl("two", log);
        }

        @Bean
        PerTargetIdAspect perTargetIdAspect(PerTargetInvocationLog log) {
            return new PerTargetIdAspect(log);
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = false)
    static class PrototypeAspectConfig {
        @Bean
        PerTargetInvocationLog perTargetInvocationLog() {
            return new PerTargetInvocationLog();
        }

        @Bean(name = "perTargetService1")
        PerTargetDemoService perTargetService1(PerTargetInvocationLog log) {
            return new PerTargetDemoServiceImpl("one", log);
        }

        @Bean(name = "perTargetService2")
        PerTargetDemoService perTargetService2(PerTargetInvocationLog log) {
            return new PerTargetDemoServiceImpl("two", log);
        }

        @Bean
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        PerTargetIdAspect perTargetIdAspect(PerTargetInvocationLog log) {
            return new PerTargetIdAspect(log);
        }
    }
}

interface PerTargetDemoService {
    void work(String input);
}

class PerTargetDemoServiceImpl implements PerTargetDemoService {
    private final String name;
    private final PerTargetInvocationLog log;

    PerTargetDemoServiceImpl(String name, PerTargetInvocationLog log) {
        this.name = name;
        this.log = log;
    }

    @Override
    public void work(String input) {
        log.add("target:" + name + ":" + input);
    }
}

class PerTargetInvocationLog {
    private final List<String> entries = new CopyOnWriteArrayList<>();

    void add(String entry) {
        entries.add(entry);
    }

    List<String> entries() {
        return List.copyOf(entries);
    }

    void clear() {
        entries.clear();
    }
}

@Aspect("pertarget(execution(* com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.PerTargetDemoService.work(..)))")
class PerTargetIdAspect {
    private static final AtomicInteger NEXT_ID = new AtomicInteger();

    private final int id = NEXT_ID.incrementAndGet();
    private final PerTargetInvocationLog log;

    PerTargetIdAspect(PerTargetInvocationLog log) {
        this.log = log;
    }

    static void resetIds() {
        NEXT_ID.set(0);
    }

    @Around("execution(* com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.PerTargetDemoService.work(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.add("aspect:" + id);
        return joinPoint.proceed();
    }
}

