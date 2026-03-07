package com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts;

// 这个 Lab 用最小 XML `<aop:config>` 配置证明：即使没有 @EnableAspectJAutoProxy / @Aspect，AOP 也能在 XML 入口生效。
// 目标：跑成事实 -> bean 被代理 -> advisor 生效 -> 拦截器链执行顺序可断言。

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

class SpringCoreAopXmlAopConfigLabTest {

    @Test
    void xml_aop_config_creates_proxy_and_applies_interceptor() {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("springcoreaop-xml-aop-config.xml")) {
            XmlInvocationLog log = context.getBean("xmlInvocationLog", XmlInvocationLog.class);
            XmlGreetingService service = context.getBean("xmlGreetingService", XmlGreetingService.class);

            assertThat(AopUtils.isAopProxy(service)).isTrue();

            log.clear();
            assertThat(service.greet("Bob")).isEqualTo("hi:Bob");

            assertThat(log.entries()).containsExactly(
                    "xml-before:greet",
                    "xml-target:Bob",
                    "xml-after:greet");
        }
    }

    public interface XmlGreetingService {
        String greet(String name);
    }

    public static class XmlInvocationLog {
        private final List<String> entries = new CopyOnWriteArrayList<>();

        public void add(String entry) {
            entries.add(entry);
        }

        public List<String> entries() {
            return List.copyOf(entries);
        }

        public void clear() {
            entries.clear();
        }
    }

    public static class XmlGreetingServiceImpl implements XmlGreetingService {
        private final XmlInvocationLog log;

        public XmlGreetingServiceImpl(XmlInvocationLog log) {
            this.log = log;
        }

        @Override
        public String greet(String name) {
            log.add("xml-target:" + name);
            return "hi:" + name;
        }
    }

    public static class XmlAroundInterceptor implements MethodInterceptor {
        private final XmlInvocationLog log;

        public XmlAroundInterceptor(XmlInvocationLog log) {
            this.log = log;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.add("xml-before:" + invocation.getMethod().getName());
            try {
                return invocation.proceed();
            } finally {
                log.add("xml-after:" + invocation.getMethod().getName());
            }
        }
    }
}

