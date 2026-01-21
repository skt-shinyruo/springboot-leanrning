package com.learning.springboot.springcoreaop.part00_guide;

import com.learning.springboot.springcoreaop.part01_proxy_fundamentals.SelfInvocationExampleService;
import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.InvocationLog;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringCoreAopExerciseTest {

    @Autowired
    private SelfInvocationExampleService selfInvocationExampleService;

    @Autowired
    private InvocationLog invocationLog;

    @Test
    @Disabled("练习：启用 exposeProxy，并让 outer(...) 通过 AopContext.currentProxy() 调用 inner(...)，使 inner(...) 也被 traced")
    void exercise_makeSelfInvocationTriggerAdvice() {
        invocationLog.reset();

        selfInvocationExampleService.outer("Bob");

        assertThat(invocationLog.count())
                .as("""
                        练习：启用 exposeProxy，并让 outer(...) 通过 AopContext.currentProxy() 调用 inner(...)，使 inner(...) 也被 traced。

                        目标：
                        - 只调用一次 `outer("Bob")`，但切面能记录到 `outer` 与 `inner` 两次调用（count=2）。

                        下一步：
                        1) 先读“自调用陷阱”与 exposeProxy：
                           - `docs/aop/spring-core-aop/part-01-proxy-fundamentals/03-self-invocation.md`
                           - `docs/aop/spring-core-aop/part-01-proxy-fundamentals/05-expose-proxy.md`
                        2) 启用 exposeProxy（例如通过 `@EnableAspectJAutoProxy(exposeProxy = true)`）。
                        3) 修改 `SelfInvocationExampleService#outer`：用 `AopContext.currentProxy()` 调用 `inner`。

                        常见坑：
                        - 只有“入口走代理”时才会有 currentProxy；同类内部 this 调用永远绕过代理
                        """)
                .isEqualTo(2);
    }

    @Test
    @Disabled("练习：新增一个 @Order(0) 的切面，并证明它会在现有 TracingAspect 之前执行")
    void exercise_addOrderedAspect() {
        assertThat(true)
                .as("""
                        练习：新增一个 @Order(0) 的切面，并证明它会在现有 TracingAspect 之前执行。

                        下一步：
                        1) 新建一个 Aspect，在 advice 里写入一个可断言的观察点（例如往 log 里记录顺序）。
                        2) 用 `@Order(0)` 设置优先级，并写测试证明顺序。

                        建议阅读：
                        - `docs/aop/spring-core-aop/part-01-proxy-fundamentals/06-debugging.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：为 TracedBusinessService 增加接口，让 Spring 使用 JDK 代理，并断言代理类型变化")
    void exercise_switchToJdkProxy() {
        assertThat(true)
                .as("""
                        练习：为 TracedBusinessService 增加接口，让 Spring 使用 JDK 代理，并断言代理类型变化。

                        下一步：
                        1) 给业务类加一个接口并让它实现。
                        2) 观察注入类型与代理类型差异（JDK proxy 只能代理接口）。
                        3) 在测试里用 `AopUtils.isJdkDynamicProxy(...)` / `isCglibProxy(...)` 固化结论。

                        建议阅读：
                        - `docs/aop/spring-core-aop/part-01-proxy-fundamentals/02-jdk-vs-cglib.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：把 pointcut 从 @annotation 改为 execution(package pattern)，并更新测试")
    void exercise_changePointcutStyle() {
        assertThat(true)
                .as("""
                        练习：把 pointcut 从 @annotation 改为 execution(package pattern)，并更新测试。

                        下一步：
                        1) 修改 TracingAspect 的 pointcut（从 `@annotation(...)` 到 `execution(...)`）。
                        2) 选择一个合适的包路径范围，避免过宽/过窄。
                        3) 更新测试断言：哪些方法应该被拦截、哪些不应该。

                        常见坑：
                        - pointcut 写太宽会导致你误判“哪里都被拦截了”
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：复现一个代理限制（final class 或 final method），并在 README 里解释原因")
    void exercise_proxyLimitation() {
        assertThat(true)
                .as("""
                        练习：复现一个代理限制（final class 或 final method），并在 README 里解释原因。

                        下一步：
                        1) 选一个 final method（或把类改成 final）并尝试让它被拦截。
                        2) 用测试证明“拦截不到”的现象。
                        3) 写下原因：JDK 代理只能代理接口；CGLIB 基于继承，无法覆盖 final。

                        建议阅读：
                        - `docs/aop/spring-core-aop/part-01-proxy-fundamentals/04-final-and-proxy-limits.md`
                        """)
                .isFalse();
    }
}
