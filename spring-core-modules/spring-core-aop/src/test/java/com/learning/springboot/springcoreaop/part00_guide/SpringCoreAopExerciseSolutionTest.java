package com.learning.springboot.springcoreaop.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoreaop.SpringCoreAopApplication;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.InvocationLog;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced;
import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.AopContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * 参考实现：对齐 SpringCoreAopExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
@SpringBootTest(classes = { SpringCoreAopApplication.class, SpringCoreAopExerciseSolutionTest.SolutionConfig.class })
class SpringCoreAopExerciseSolutionTest {

    @org.springframework.beans.factory.annotation.Autowired
    private ExposeProxySelfInvocationService exposeProxySelfInvocationService;

    @org.springframework.beans.factory.annotation.Autowired
    private InvocationLog invocationLog;

    @org.springframework.beans.factory.annotation.Autowired
    private AspectOrderLog aspectOrderLog;

    @Test
    void solution_exposeProxy_allowsSelfInvocationToTriggerAdvice() {
        invocationLog.reset();

        exposeProxySelfInvocationService.outer("Bob");

        assertThat(invocationLog.count()).isEqualTo(2);
        assertThat(invocationLog.lastMethod())
                .as("外层方法结束得更晚，因此 lastMethod 往往是 outer；关键证据是 count=2")
                .contains("outer");
    }

    @Test
    void solution_orderedAspect_runsBeforeTracingAspect() {
        invocationLog.reset();
        aspectOrderLog.reset();

        exposeProxySelfInvocationService.inner("Alice");

        assertThat(invocationLog.count()).isEqualTo(1);
        assertThat(aspectOrderLog.events()).containsExactly("ordered:before", "ordered:after");
    }

    static class AspectOrderLog {
        private final List<String> events = new ArrayList<>();

        void add(String e) {
            events.add(e);
        }

        List<String> events() {
            return List.copyOf(events);
        }

        void reset() {
            events.clear();
        }
    }

    @Service
    static class ExposeProxySelfInvocationService {

        @Traced
        public String outer(String name) {
            ExposeProxySelfInvocationService proxy = (ExposeProxySelfInvocationService) AopContext.currentProxy();
            return "outer->" + proxy.inner(name);
        }

        @Traced
        public String inner(String name) {
            return "inner->" + name;
        }
    }

    @Aspect
    @Order(0)
    static class OrderedMarkerAspect {

        private final AspectOrderLog log;

        OrderedMarkerAspect(AspectOrderLog log) {
            this.log = log;
        }

        @Around("@annotation(com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced)")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
            log.add("ordered:before");
            try {
                return joinPoint.proceed();
            } finally {
                log.add("ordered:after");
            }
        }
    }

    @TestConfiguration
    @EnableAspectJAutoProxy(exposeProxy = true)
    @Import(ExposeProxySelfInvocationService.class)
    static class SolutionConfig {

        @Bean
        AspectOrderLog aspectOrderLog() {
            return new AspectOrderLog();
        }

        @Bean
        OrderedMarkerAspect orderedMarkerAspect(AspectOrderLog log) {
            return new OrderedMarkerAspect(log);
        }
    }
}
