package com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts;

// 这个 Lab 用最小事实证明 “动态切点/运行期匹配” 的成本模型：
// - MethodMatcher#isRuntime() == true 时，会产生 per-invocation 的运行期匹配（matches(method, class, args)）
// - 即使最终 advice 没有执行，这个运行期匹配也会发生（属于额外开销）
//
// 对照目标：理解 docs 里经常出现的 `interceptorsAndDynamicMethodMatchers` 这一坨到底是什么、什么时候发生。

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;
import org.springframework.aop.support.AopUtils;

class SpringCoreAopRuntimePointcutCostLabTest {

    @Test
    void runtime_method_matcher_is_evaluated_on_each_invocation_and_can_depend_on_args() {
        CountingDynamicPointcut pointcut = new CountingDynamicPointcut();
        CountingInterceptor interceptor = new CountingInterceptor();

        RuntimeMatchService target = input -> "ok:" + input;

        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.setInterfaces(RuntimeMatchService.class);
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(pointcut, interceptor));
        RuntimeMatchService proxy = (RuntimeMatchService) proxyFactory.getProxy();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();

        assertThat(proxy.process("miss")).isEqualTo("ok:miss");
        assertThat(proxy.process("hit")).isEqualTo("ok:hit");
        assertThat(proxy.process("hit")).isEqualTo("ok:hit");

        assertThat(pointcut.runtimeMatchCount()).isEqualTo(3);
        assertThat(interceptor.interceptedCount()).isEqualTo(2);
        assertThat(pointcut.staticMatchCount()).isBetween(1, 3);
    }

    interface RuntimeMatchService {
        String process(String input);
    }

    static class CountingInterceptor implements MethodInterceptor {
        private final AtomicInteger intercepted = new AtomicInteger();

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            intercepted.incrementAndGet();
            return invocation.proceed();
        }

        int interceptedCount() {
            return intercepted.get();
        }
    }

    static class CountingDynamicPointcut extends DynamicMethodMatcherPointcut {
        private final AtomicInteger staticMatches = new AtomicInteger();
        private final AtomicInteger runtimeMatches = new AtomicInteger();

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            staticMatches.incrementAndGet();
            return "process".equals(method.getName());
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            runtimeMatches.incrementAndGet();
            return args != null && args.length > 0 && "hit".equals(args[0]);
        }

        int staticMatchCount() {
            return staticMatches.get();
        }

        int runtimeMatchCount() {
            return runtimeMatches.get();
        }
    }
}

