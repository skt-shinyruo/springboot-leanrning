package com.learning.springboot.springcoreaop.part03_proxy_stacking;

import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.InvocationLog;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.TracingAspect;

// 这个 Lab 用“真实的 Tx/Cache/Method Security 基础设施”验证：AOP 叠加不是概念，而是可观察的 advisors + 可验证的调用语义。

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.DefaultAdvisorChainFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class SpringCoreAopRealWorldStackingLabTest {

    @AfterEach
    void cleanupSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void unauthorized_call_is_denied_and_does_not_invoke_target_or_cache() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RealWorldStackingConfig.class)) {
            RealWorldStackingService service = context.getBean(RealWorldStackingService.class);
            TargetInvocationProbe probe = context.getBean(TargetInvocationProbe.class);
            Cache cache = context.getBean(CacheManager.class).getCache(RealWorldStackingConfig.CACHE_NAME);

            probe.reset();
            setUser();

            assertThatThrownBy(() -> service.process("a")).isInstanceOf(AccessDeniedException.class);

            assertThat(probe.invocationCount()).isEqualTo(0);
            assertThat(cache).isNotNull();
            assertThat(cache.get("a")).isNull();
        }
    }

    @Test
    void authorized_call_invokes_target_with_transaction_and_populates_cache() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RealWorldStackingConfig.class)) {
            RealWorldStackingService service = context.getBean(RealWorldStackingService.class);
            TargetInvocationProbe probe = context.getBean(TargetInvocationProbe.class);
            InvocationLog invocationLog = context.getBean(InvocationLog.class);
            Cache cache = context.getBean(CacheManager.class).getCache(RealWorldStackingConfig.CACHE_NAME);

            probe.reset();
            invocationLog.reset();
            setAdmin();

            String result = service.process("a");

            assertThat(probe.invocationCount()).isEqualTo(1);
            assertThat(probe.lastTransactionActive()).isTrue();

            assertThat(invocationLog.count()).isEqualTo(1);
            assertThat(cache).isNotNull();
            assertThat(cache.get("a").get()).isEqualTo(result);
        }
    }

    @Test
    void cache_hit_short_circuits_target_but_security_still_applies() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RealWorldStackingConfig.class)) {
            RealWorldStackingService service = context.getBean(RealWorldStackingService.class);
            TargetInvocationProbe probe = context.getBean(TargetInvocationProbe.class);

            probe.reset();
            setAdmin();

            String first = service.process("x");
            String second = service.process("x");

            assertThat(second).isEqualTo(first);
            assertThat(probe.invocationCount()).isEqualTo(1);

            setUser();

            assertThatThrownBy(() -> service.process("x")).isInstanceOf(AccessDeniedException.class);
            assertThat(probe.invocationCount()).isEqualTo(1);
        }
    }

    @Test
    void proxy_contains_infrastructure_advisors_and_chain_is_observable() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RealWorldStackingConfig.class)) {
            RealWorldStackingService service = context.getBean(RealWorldStackingService.class);

            assertThat(AopUtils.isAopProxy(service)).isTrue();
            assertThat(service).isInstanceOf(Advised.class);

            Advised advised = (Advised) service;
            assertThat(advised.getAdvisors())
                    .anySatisfy(advisor -> assertThat(advisor.getAdvice())
                            .isInstanceOf(org.springframework.transaction.interceptor.TransactionInterceptor.class))
                    .anySatisfy(advisor -> assertThat(advisor.getAdvice())
                            .isInstanceOf(org.springframework.cache.interceptor.CacheInterceptor.class))
                    .anySatisfy(advisor -> assertThat(advisor.getAdvice().getClass().getName())
                            .startsWith("org.springframework.security"));

            Method process = RealWorldStackingService.class.getMethod("process", String.class);
            List<String> chainElementTypes = AopChainInspector.chainElementTypes(service, process);

            assertThat(chainElementTypes).anyMatch(type -> type.contains("TransactionInterceptor"));
            assertThat(chainElementTypes).anyMatch(type -> type.contains("CacheInterceptor"));
            assertThat(chainElementTypes).anyMatch(type -> type.startsWith("org.springframework.security"));
        }
    }

    private static void setAdmin() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(
                        "admin",
                        "n/a",
                        AuthorityUtils.createAuthorityList("ROLE_ADMIN")));
    }

    private static void setUser() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(
                        "user",
                        "n/a",
                        AuthorityUtils.createAuthorityList("ROLE_USER")));
    }

    interface RealWorldStackingService {
        String process(String input);
    }

    static class RealWorldStackingServiceImpl implements RealWorldStackingService {
        private final TargetInvocationProbe probe;

        RealWorldStackingServiceImpl(TargetInvocationProbe probe) {
            this.probe = probe;
        }

        @Override
        @Traced
        @PreAuthorize("hasRole('ADMIN')")
        @Cacheable(cacheNames = RealWorldStackingConfig.CACHE_NAME, key = "#p0")
        @Transactional
        public String process(String input) {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            int invocationIndex = probe.record(txActive);
            return "ok:" + input + "#" + invocationIndex;
        }
    }

    static class TargetInvocationProbe {
        private final AtomicInteger invocationCount = new AtomicInteger();
        private final AtomicReference<Boolean> lastTransactionActive = new AtomicReference<>();

        int record(boolean txActive) {
            lastTransactionActive.set(txActive);
            return invocationCount.incrementAndGet();
        }

        int invocationCount() {
            return invocationCount.get();
        }

        boolean lastTransactionActive() {
            return Boolean.TRUE.equals(lastTransactionActive.get());
        }

        void reset() {
            invocationCount.set(0);
            lastTransactionActive.set(null);
        }
    }

    static final class AopChainInspector {
        private AopChainInspector() {
        }

        static List<String> chainElementTypes(Object proxy, Method method) {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(proxy);
            return DefaultAdvisorChainFactory.INSTANCE
                    .getInterceptorsAndDynamicInterceptionAdvice((Advised) proxy, method, targetClass)
                    .stream()
                    .map(element -> element.getClass().getName())
                    .toList();
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    @EnableTransactionManagement
    @EnableCaching
    @EnableMethodSecurity(prePostEnabled = true)
    static class RealWorldStackingConfig {
        static final String CACHE_NAME = "work";

        @Bean
        TargetInvocationProbe targetInvocationProbe() {
            return new TargetInvocationProbe();
        }

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(CACHE_NAME);
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new InMemoryTransactionManager();
        }

        @Bean
        InvocationLog invocationLog() {
            return new InvocationLog();
        }

        @Bean
        TracingAspect tracingAspect(InvocationLog invocationLog) {
            return new TracingAspect(invocationLog);
        }

        @Bean
        RealWorldStackingService realWorldStackingService(TargetInvocationProbe targetInvocationProbe) {
            return new RealWorldStackingServiceImpl(targetInvocationProbe);
        }
    }

    static class InMemoryTransactionManager extends AbstractPlatformTransactionManager {
        @Override
        protected Object doGetTransaction() {
            return new Object();
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
        }
    }
}
