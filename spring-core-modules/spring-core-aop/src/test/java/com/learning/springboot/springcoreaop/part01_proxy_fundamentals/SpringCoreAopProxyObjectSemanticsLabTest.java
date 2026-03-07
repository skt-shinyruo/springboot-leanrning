package com.learning.springboot.springcoreaop.part01_proxy_fundamentals;

// 这个 Lab 用可断言事实说明：proxy 是“替身对象”，它在类型/身份上通常不等价于 target。
// 关注点：
// - getClass()：看到的是 proxy class（JDK/CGLIB 都不是 target class 本体）
// - instanceof：JDK proxy 只实现接口；CGLIB proxy 是 target 的子类
// - Map key/缓存：proxy 与 target 不是同一个对象，别把“对象引用”当成“业务 identity”

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

class SpringCoreAopProxyObjectSemanticsLabTest {

    @Test
    void proxy_class_and_type_checks_differ_between_jdk_and_cglib() throws Exception {
        ProxySemanticsServiceImpl target = new ProxySemanticsServiceImpl();

        ProxyFactory jdkFactory = new ProxyFactory(target);
        jdkFactory.setInterfaces(ProxySemanticsService.class);
        ProxySemanticsService jdkProxy = (ProxySemanticsService) jdkFactory.getProxy();

        ProxyFactory cglibFactory = new ProxyFactory(target);
        cglibFactory.setProxyTargetClass(true);
        ProxySemanticsService cglibProxy = (ProxySemanticsService) cglibFactory.getProxy();

        assertThat(AopUtils.isAopProxy(jdkProxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(jdkProxy)).isTrue();
        assertThat(jdkProxy).isNotInstanceOf(ProxySemanticsServiceImpl.class);

        assertThat(AopUtils.isAopProxy(cglibProxy)).isTrue();
        assertThat(AopUtils.isCglibProxy(cglibProxy)).isTrue();
        assertThat(cglibProxy).isInstanceOf(ProxySemanticsServiceImpl.class);

        assertThat(jdkProxy.getClass()).isNotEqualTo(ProxySemanticsServiceImpl.class);
        assertThat(cglibProxy.getClass()).isNotEqualTo(ProxySemanticsServiceImpl.class);

        assertThat(AopProxyUtils.ultimateTargetClass(jdkProxy)).isEqualTo(ProxySemanticsServiceImpl.class);
        assertThat(AopProxyUtils.ultimateTargetClass(cglibProxy)).isEqualTo(ProxySemanticsServiceImpl.class);

        Object unwrappedTarget = ((Advised) jdkProxy).getTargetSource().getTarget();
        assertThat(unwrappedTarget).isSameAs(target);

        Map<Object, String> cache = new HashMap<>();
        cache.put(jdkProxy, "cached-by-proxy");
        assertThat(cache.get(jdkProxy)).isEqualTo("cached-by-proxy");
        assertThat(cache.get(target)).as("proxy != target，因此用 target 作为 key 查不到").isNull();
    }

    interface ProxySemanticsService {
        String business(String input);
    }

    static class ProxySemanticsServiceImpl implements ProxySemanticsService {
        @Override
        public String business(String input) {
            return "biz:" + input;
        }
    }
}

