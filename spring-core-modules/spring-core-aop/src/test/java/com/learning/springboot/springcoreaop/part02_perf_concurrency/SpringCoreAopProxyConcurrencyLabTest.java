package com.learning.springboot.springcoreaop.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoreaop.SpringCoreAopApplication;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;

/**
 * 并发/性能实验：同一个 AOP proxy 可被并发调用，advice 的 ThreadLocal 状态不会跨线程串线。
 */
@SpringBootTest(classes = { SpringCoreAopApplication.class, SpringCoreAopProxyConcurrencyLabTest.ConcurrencyConfig.class })
class SpringCoreAopProxyConcurrencyLabTest {

    @Autowired
    private ConcurrencyTracedService service;

    @Test
    void proxyInvocation_isThreadIsolated_underConcurrentCalls() throws Exception {
        int tasks = 24;
        ExecutorService pool = Executors.newFixedThreadPool(8, namedThreadFactory("springcoreaop-perf-"));
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(tasks);
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < tasks; i++) {
                String cid = "cid-" + i;
                pool.submit(() -> {
                    try {
                        startGate.await(1, TimeUnit.SECONDS);
                        assertThat(service.echoCorrelationId(cid)).isEqualTo(cid);
                    } catch (Throwable ex) {
                        errors.add(ex);
                    } finally {
                        doneGate.countDown();
                    }
                });
            }

            startGate.countDown();
            assertThat(doneGate.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(errors).isEmpty();
        } finally {
            pool.shutdownNow();
        }
    }

    private static ThreadFactory namedThreadFactory(String prefix) {
        AtomicInteger seq = new AtomicInteger();
        return r -> {
            Thread t = new Thread(r);
            t.setName(prefix + seq.incrementAndGet());
            t.setDaemon(true);
            return t;
        };
    }

    @TestConfiguration
    @EnableAspectJAutoProxy
    @Import(ConcurrencyTracedService.class)
    static class ConcurrencyConfig {
        @Bean
        CorrelationCarrier correlationCarrier() {
            return new CorrelationCarrier();
        }

        @Bean
        CorrelationIdAspect correlationIdAspect(CorrelationCarrier carrier) {
            return new CorrelationIdAspect(carrier);
        }
    }

    static class CorrelationCarrier {
        private final ThreadLocal<String> correlationId = new ThreadLocal<>();

        void set(String id) {
            correlationId.set(id);
        }

        String get() {
            return correlationId.get();
        }

        void clear() {
            correlationId.remove();
        }
    }

    @Aspect
    static class CorrelationIdAspect {
        private final CorrelationCarrier carrier;

        CorrelationIdAspect(CorrelationCarrier carrier) {
            this.carrier = carrier;
        }

        @Around("@annotation(com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced)")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
            Object[] args = joinPoint.getArgs();
            String cid = (args != null && args.length > 0 && args[0] != null) ? args[0].toString() : "n/a";
            carrier.set(cid);
            try {
                return joinPoint.proceed();
            } finally {
                carrier.clear();
            }
        }
    }

    @Service
    static class ConcurrencyTracedService {
        private final CorrelationCarrier carrier;

        ConcurrencyTracedService(CorrelationCarrier carrier) {
            this.carrier = carrier;
        }

        @Traced
        String echoCorrelationId(String cid) {
            return carrier.get();
        }
    }
}

