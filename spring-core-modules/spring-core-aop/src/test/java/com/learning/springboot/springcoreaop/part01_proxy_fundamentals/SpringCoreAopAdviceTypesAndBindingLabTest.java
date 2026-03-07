package com.learning.springboot.springcoreaop.part01_proxy_fundamentals;

// 这个 Lab 用“最小可对照实验”固化 Advice 全家桶的语义差异：
// - @Before/@After/@AfterReturning/@AfterThrowing 在 success/error 两条路径上的顺序
// - returning/throwing/args/@annotation/JoinPoint 的绑定结果
// - 异常是否传播（默认传播，除非 @Around 主动吞掉/改写）

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

class SpringCoreAopAdviceTypesAndBindingLabTest {

    @Test
    void success_path_order_and_bindings_are_expected() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AdviceTypesConfig.class)) {
            AdviceEventLog log = context.getBean(AdviceEventLog.class);
            AdviceTypesDemoService service = context.getBean(AdviceTypesDemoService.class);

            log.clear();
            assertThat(service.ok("Bob")).isEqualTo("ok:Bob");

            assertThat(log.entries()).containsExactly(
                    "before:op=ok,name=Bob,method=ok",
                    "target-ok:Bob",
                    "afterReturning:op=ok,name=Bob,result=ok:Bob",
                    "after(finally):op=ok,name=Bob");
        }
    }

    @Test
    void error_path_order_and_bindings_are_expected_and_exception_propagates() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AdviceTypesConfig.class)) {
            AdviceEventLog log = context.getBean(AdviceEventLog.class);
            AdviceTypesDemoService service = context.getBean(AdviceTypesDemoService.class);

            log.clear();
            assertThatThrownBy(() -> service.fail("Alice"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("boom:Alice");

            assertThat(log.entries()).containsExactly(
                    "before:op=fail,name=Alice,method=fail",
                    "target-fail:Alice",
                    "afterThrowing:op=fail,name=Alice,ex=IllegalStateException:boom:Alice",
                    "after(finally):op=fail,name=Alice");
        }
    }

    @Configuration
    @EnableAspectJAutoProxy
    static class AdviceTypesConfig {

        @Bean
        AdviceEventLog adviceEventLog() {
            return new AdviceEventLog();
        }

        @Bean
        AdviceTypesDemoService adviceTypesDemoService(AdviceEventLog adviceEventLog) {
            return new AdviceTypesDemoService(adviceEventLog);
        }

        @Bean
        AdviceBindingAspect adviceBindingAspect(AdviceEventLog adviceEventLog) {
            return new AdviceBindingAspect(adviceEventLog);
        }
    }

    static class AdviceTypesDemoService {
        private final AdviceEventLog log;

        AdviceTypesDemoService(AdviceEventLog log) {
            this.log = log;
        }

        @BizOp("ok")
        public String ok(String name) {
            log.add("target-ok:" + name);
            return "ok:" + name;
        }

        @BizOp("fail")
        public String fail(String name) {
            log.add("target-fail:" + name);
            throw new IllegalStateException("boom:" + name);
        }
    }

    @Aspect
    static class AdviceBindingAspect {
        private final AdviceEventLog log;

        AdviceBindingAspect(AdviceEventLog log) {
            this.log = log;
        }

        @Before(value = "@annotation(bizOp) && args(name)", argNames = "joinPoint,bizOp,name")
        public void before(JoinPoint joinPoint, BizOp bizOp, String name) {
            log.add("before:op=" + bizOp.value() + ",name=" + name + ",method=" + joinPoint.getSignature().getName());
        }

        @AfterReturning(pointcut = "@annotation(bizOp) && args(name)", returning = "result", argNames = "bizOp,name,result")
        public void afterReturning(BizOp bizOp, String name, Object result) {
            log.add("afterReturning:op=" + bizOp.value() + ",name=" + name + ",result=" + result);
        }

        @AfterThrowing(pointcut = "@annotation(bizOp) && args(name)", throwing = "ex", argNames = "bizOp,name,ex")
        public void afterThrowing(BizOp bizOp, String name, Throwable ex) {
            log.add("afterThrowing:op=" + bizOp.value() + ",name=" + name + ",ex=" + ex.getClass().getSimpleName() + ":" + ex.getMessage());
        }

        @After(value = "@annotation(bizOp) && args(name)", argNames = "bizOp,name")
        public void afterFinally(BizOp bizOp, String name) {
            log.add("after(finally):op=" + bizOp.value() + ",name=" + name);
        }
    }

    static class AdviceEventLog {
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

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface BizOp {
        String value();
    }
}

