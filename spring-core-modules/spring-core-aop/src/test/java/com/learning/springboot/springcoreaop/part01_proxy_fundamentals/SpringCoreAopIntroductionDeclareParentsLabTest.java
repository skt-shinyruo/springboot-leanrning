package com.learning.springboot.springcoreaop.part01_proxy_fundamentals;

// 这个 Lab 用最小事实证明 Introduction（Mixin）不是“拦截方法”，而是“给 proxy 加接口能力”。
// 关键对照：
// - proxy instanceof 新接口 == true
// - target class 不变（ultimateTargetClass 仍是原始类型）
// - target 实例本身不实现新接口（只是 proxy 额外实现）

import static org.assertj.core.api.Assertions.assertThat;

import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareParents;
import org.junit.jupiter.api.Test;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

class SpringCoreAopIntroductionDeclareParentsLabTest {

    @Test
    void declareParents_adds_interface_to_proxy_but_not_to_target() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(IntroductionConfig.class)) {
            PlainBusinessService service = context.getBean(PlainBusinessService.class);

            assertThat(AopUtils.isAopProxy(service)).isTrue();
            assertThat(service).isInstanceOf(Auditable.class);

            assertThat(AopProxyUtils.ultimateTargetClass(service)).isEqualTo(PlainBusinessService.class);

            Object target = ((Advised) service).getTargetSource().getTarget();
            assertThat(target).isInstanceOf(PlainBusinessService.class);
            assertThat(target).isNotInstanceOf(Auditable.class);

            Auditable auditable = (Auditable) service;
            auditable.setAuditTag("v1");
            assertThat(auditable.getAuditTag()).isEqualTo("v1");

            assertThat(((Advised) service).getAdvisors())
                    .anySatisfy(advisor -> {
                        assertThat(advisor).isInstanceOf(IntroductionAdvisor.class);
                        Advice advice = advisor.getAdvice();
                        assertThat(advice).as("introduction advice should exist").isNotNull();
                    });
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class IntroductionConfig {

        @Bean
        PlainBusinessService plainBusinessService() {
            return new PlainBusinessService();
        }

        @Bean
        IntroductionAspect introductionAspect() {
            return new IntroductionAspect();
        }
    }

    static class PlainBusinessService {
        String hello(String name) {
            return "hello:" + name;
        }
    }

    interface Auditable {
        void setAuditTag(String tag);

        String getAuditTag();
    }

    static class AuditableMixin implements Auditable {
        private volatile String auditTag;

        @Override
        public void setAuditTag(String tag) {
            this.auditTag = tag;
        }

        @Override
        public String getAuditTag() {
            return auditTag;
        }
    }

    @Aspect
    static class IntroductionAspect {

        @DeclareParents(
                value = "com.learning.springboot.springcoreaop.part01_proxy_fundamentals.SpringCoreAopIntroductionDeclareParentsLabTest.PlainBusinessService+",
                defaultImpl = AuditableMixin.class)
        public static Auditable auditable;
    }
}

