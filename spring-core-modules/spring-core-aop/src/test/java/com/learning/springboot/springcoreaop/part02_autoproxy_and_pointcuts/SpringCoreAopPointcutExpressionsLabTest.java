package com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts;

// 这个 Lab 用可断言结论证明：this/target 的命中结果会随 JDK/CGLIB 代理类型发生变化。

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

class SpringCoreAopPointcutExpressionsLabTest {

    @Test
    void this_vs_target_differs_between_JdkProxy_and_CglibProxy() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JdkProxyThisTargetConfig.class)) {
            MatchLog log = context.getBean(MatchLog.class);
            ThisTargetService service = context.getBean(ThisTargetService.class);

            assertThat(AopUtils.isAopProxy(service)).isTrue();
            assertThat(AopUtils.isJdkDynamicProxy(service)).isTrue();

            log.clear();
            service.work();

            assertThat(log.entries()).containsExactly("target", "method");
        }

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CglibProxyThisTargetConfig.class)) {
            MatchLog log = context.getBean(MatchLog.class);
            ThisTargetService service = context.getBean(ThisTargetService.class);

            assertThat(AopUtils.isAopProxy(service)).isTrue();
            assertThat(AopUtils.isCglibProxy(service)).isTrue();

            log.clear();
            service.work();

            assertThat(log.entries()).containsExactly("this", "target", "method");
        }
    }

    @org.aspectj.lang.annotation.Aspect
    @Order(1)
    static class ThisImplAspect {
        private final MatchLog log;

        ThisImplAspect(MatchLog log) {
            this.log = log;
        }

        @org.aspectj.lang.annotation.Around("this(com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.ThisTargetServiceImpl)")
        public Object around(org.aspectj.lang.ProceedingJoinPoint joinPoint) throws Throwable {
            log.add("this");
            return joinPoint.proceed();
        }
    }

    @org.aspectj.lang.annotation.Aspect
    @Order(2)
    static class TargetImplAspect {
        private final MatchLog log;

        TargetImplAspect(MatchLog log) {
            this.log = log;
        }

        @org.aspectj.lang.annotation.Around("target(com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.ThisTargetServiceImpl)")
        public Object around(org.aspectj.lang.ProceedingJoinPoint joinPoint) throws Throwable {
            log.add("target");
            return joinPoint.proceed();
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = false)
    static class JdkProxyThisTargetConfig {

        @Bean
        MatchLog matchLog() {
            return new MatchLog();
        }

        @Bean
        ThisTargetService thisTargetService(MatchLog matchLog) {
            return new ThisTargetServiceImpl(matchLog);
        }

        @Bean
        ThisImplAspect thisImplAspect(MatchLog matchLog) {
            return new ThisImplAspect(matchLog);
        }

        @Bean
        TargetImplAspect targetImplAspect(MatchLog matchLog) {
            return new TargetImplAspect(matchLog);
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class CglibProxyThisTargetConfig {

        @Bean
        MatchLog matchLog() {
            return new MatchLog();
        }

        @Bean
        ThisTargetService thisTargetService(MatchLog matchLog) {
            return new ThisTargetServiceImpl(matchLog);
        }

        @Bean
        ThisImplAspect thisImplAspect(MatchLog matchLog) {
            return new ThisImplAspect(matchLog);
        }

        @Bean
        TargetImplAspect targetImplAspect(MatchLog matchLog) {
            return new TargetImplAspect(matchLog);
        }
    }
}

interface ThisTargetService {
    void work();
}

class ThisTargetServiceImpl implements ThisTargetService {
    private final MatchLog log;

    ThisTargetServiceImpl(MatchLog log) {
        this.log = log;
    }

    @Override
    public void work() {
        log.add("method");
    }
}

class MatchLog {
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

