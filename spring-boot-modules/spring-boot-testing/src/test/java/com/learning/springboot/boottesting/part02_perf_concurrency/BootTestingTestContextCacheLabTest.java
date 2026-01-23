package com.learning.springboot.boottesting.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.boottesting.BootTestingApplication;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 并发/性能实验：TestContext 的上下文缓存（Context Cache）会复用“相同配置”的 ApplicationContext。
 *
 * <p>注意：这里不做耗时阈值断言，只用“实例化次数/上下文 id”做可回归证据链。
 */
@Suite
@SelectClasses({
        BootTestingTestContextCacheLabTest.ContextCacheProbeA.class,
        BootTestingTestContextCacheLabTest.ContextCacheProbeB.class
})
class BootTestingTestContextCacheLabTest {

    static final AtomicInteger initCount = new AtomicInteger();
    static final AtomicReference<String> firstContextId = new AtomicReference<>();

    @TestConfiguration
    static class ProbeConfig {
        @Bean
        InitCounter initCounter(ApplicationContext applicationContext) {
            return new InitCounter(applicationContext.getId());
        }
    }

    static class InitCounter {
        private final String contextId;

        InitCounter(String contextId) {
            this.contextId = contextId;
            initCount.incrementAndGet();
            firstContextId.compareAndSet(null, contextId);
        }

        int initCount() {
            return initCount.get();
        }

        String firstContextId() {
            return firstContextId.get();
        }

        String contextId() {
            return contextId;
        }
    }

    @SpringBootTest(classes = { BootTestingApplication.class, ProbeConfig.class })
    static class ContextCacheProbeA {

        @Autowired
        private InitCounter initCounter;

        @Test
        void firstContextBuildsInitCounterOnce() {
            assertThat(initCounter.initCount()).isEqualTo(1);
        }
    }

    @SpringBootTest(classes = { BootTestingApplication.class, ProbeConfig.class })
    static class ContextCacheProbeB {

        @Autowired
        private InitCounter initCounter;

        @Test
        void secondTestClass_reusesTheSameCachedContext() {
            assertThat(initCounter.initCount())
                    .as("若上下文被复用，initCounter 只会被创建 1 次；否则会增长为 2")
                    .isEqualTo(1);
            assertThat(initCounter.contextId()).isEqualTo(initCounter.firstContextId());
        }
    }
}

