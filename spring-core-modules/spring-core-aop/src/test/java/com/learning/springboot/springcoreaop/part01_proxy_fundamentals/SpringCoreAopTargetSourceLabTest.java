package com.learning.springboot.springcoreaop.part01_proxy_fundamentals;

// 这个 Lab 用两种典型 TargetSource 证明：proxy 不一定“直接持有 target 引用”，而是通过 TargetSource 决定调用转发到谁。
// - HotSwappableTargetSource：同一个 proxy 不变，但 target 可运行期切换
// - LazyInitTargetSource：target 延迟到第一次方法调用才创建（配合 @Lazy）

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.HotSwappableTargetSource;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

class SpringCoreAopTargetSourceLabTest {

    @Test
    void hotSwappableTargetSource_allows_switching_target_behind_same_proxy() throws Exception {
        EchoService targetV1 = input -> "v1:" + input;
        EchoService targetV2 = input -> "v2:" + input;

        HotSwappableTargetSource targetSource = new HotSwappableTargetSource(targetV1);

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(EchoService.class);
        proxyFactory.setTargetSource(targetSource);
        EchoService proxy = (EchoService) proxyFactory.getProxy();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(((Advised) proxy).getTargetSource()).isSameAs(targetSource);

        assertThat(proxy.echo("a")).isEqualTo("v1:a");
        targetSource.swap(targetV2);
        assertThat(proxy.echo("b")).isEqualTo("v2:b");
    }

    @Test
    void lazyInitTargetSource_defers_target_creation_until_first_method_call() {
        LazyEchoServiceImpl.resetCounter();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(LazyInitConfig.class)) {
            assertThat(LazyEchoServiceImpl.createdCount()).as("target should not be eagerly instantiated").isEqualTo(0);

            EchoService proxy = context.getBean("lazyEchoService", EchoService.class);
            assertThat(AopUtils.isAopProxy(proxy)).isTrue();
            assertThat(((Advised) proxy).getTargetSource()).isInstanceOf(LazyInitTargetSource.class);

            assertThat(LazyEchoServiceImpl.createdCount()).as("obtaining proxy should not create target").isEqualTo(0);

            assertThat(proxy.echo("x")).isEqualTo("lazy:x");
            assertThat(LazyEchoServiceImpl.createdCount()).as("first invocation creates target").isEqualTo(1);

            assertThat(proxy.echo("y")).isEqualTo("lazy:y");
            assertThat(LazyEchoServiceImpl.createdCount()).as("target is reused (not recreated per call)").isEqualTo(1);
        }
    }

    interface EchoService {
        String echo(String input);
    }

    static class LazyEchoServiceImpl implements EchoService {
        private static final AtomicInteger CREATED = new AtomicInteger();

        LazyEchoServiceImpl() {
            CREATED.incrementAndGet();
        }

        static int createdCount() {
            return CREATED.get();
        }

        static void resetCounter() {
            CREATED.set(0);
        }

        @Override
        public String echo(String input) {
            return "lazy:" + input;
        }
    }

    @Configuration
    static class LazyInitConfig {

        @Bean
        @Lazy
        EchoService lazyEchoTarget() {
            return new LazyEchoServiceImpl();
        }

        @Bean
        LazyInitTargetSource lazyInitTargetSource() {
            LazyInitTargetSource targetSource = new LazyInitTargetSource();
            targetSource.setTargetBeanName("lazyEchoTarget");
            return targetSource;
        }

        @Bean(name = "lazyEchoService")
        ProxyFactoryBean lazyEchoService(LazyInitTargetSource lazyInitTargetSource) throws ClassNotFoundException {
            ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
            proxyFactoryBean.setTargetSource(lazyInitTargetSource);
            proxyFactoryBean.setProxyInterfaces(new Class<?>[] { EchoService.class });
            return proxyFactoryBean;
        }
    }
}
