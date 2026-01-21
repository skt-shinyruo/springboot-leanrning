package com.learning.springboot.springcorebeans.part03_container_internals;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SpringCoreBeansContainerInternalsExerciseTest {

    @Test
    @Disabled("练习：实现一个自定义 Scope（不要用 SimpleThreadScope），并用 scoped proxy + 测试证明它在 singleton 注入场景下可工作")
    void exercise_customScopeAndScopedProxy() {
        assertThat(true)
                .as("""
                        练习：实现一个自定义 Scope（不要用 SimpleThreadScope），并用 scoped proxy + 测试证明它在 singleton 注入场景下可工作。

                        目标：
                        - 自己实现 `org.springframework.beans.factory.config.Scope`（例如 conversation/tenant scope）。
                        - 用 ThreadLocal 保存当前 conversationId。
                        - 同一个 conversationId 下拿到同一个实例，不同 conversationId 拿到不同实例。
                        - 把 scoped bean 注入 singleton 时必须通过 ObjectProvider 或 scoped proxy 才能按 conversation 解析。

                        推荐步骤：
                        1) 先阅读：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md`
                        2) 写一个最小 scope 实现：get/remove/registerDestructionCallback/getConversationId
                        3) 用 `beanFactory.registerScope("conversation", new YourScope())` 注册
                        4) 写一个 lab 风格的小测试：两个不同 conversationId 断言两个不同实例

                        常见坑：
                        - 忘了实现 destruction callbacks，会让资源清理变得不可控
                        - 直接把 scoped bean 注入 singleton 会“冻结”实例（见 28 章）
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：扩展 SmartLifecycle：增加 3 个不同 phase 的组件，并验证 start/stop 全序列（含同 phase 的处理策略）")
    void exercise_smartLifecycleDeepDive() {
        assertThat(true)
                .as("""
                        练习：扩展 SmartLifecycle：增加 3 个不同 phase 的组件，并验证 start/stop 全序列。

                        推荐步骤：
                        1) 先读：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md`
                        2) 在 `SpringCoreBeansSmartLifecycleLabTest` 的基础上增加更多组件：phase=-1/0/1
                        3) 写断言验证：start 升序、stop 反序
                        4) 同 phase 的顺序如果不稳定：
                           - 不要写脆弱断言
                           - 或者通过 dependsOn/显式依赖让顺序确定化
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：FactoryBean 进阶：实现一个 getObjectType 返回 null 的场景，并总结它会影响哪些 type-based 机制")
    void exercise_factoryBeanTypePitfalls() {
        assertThat(true)
                .as("""
                        练习：FactoryBean 进阶：实现一个 getObjectType 返回 null 的场景，并总结它会影响哪些 type-based 机制。

                        推荐步骤：
                        1) 先读：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/29-factorybean-edge-cases.md`
                        2) 试着把一个真实业务场景抽象成 FactoryBean（例如动态创建 client）
                        3) 让 `getObjectType()` 在某些情况下返回 null
                        4) 观察并总结：
                           - getBeanNamesForType
                           - 条件装配（@ConditionalOnMissingBean 等）
                           - 以及你自己的 type-based 扫描逻辑
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：实现一个最小的“proxying BPP”，并用断言复现 self-invocation 绕过 + 按实现类获取失败")
    void exercise_minimalProxyingBeanPostProcessor() {
        assertThat(true)
                .as("""
                        练习：实现一个最小的“proxying BPP（BeanPostProcessor）”，并用断言复现 2 个常见现象：

                        目标（必须可验证）：
                        1) 容器最终暴露的 bean 不是原始对象，而是一个 JDK proxy（例如 `$Proxy...`）
                        2) self-invocation（同类内部 `this.inner()`）仍然绕过代理，只拦截到外层入口方法
                        3) 当 bean 被包成 JDK proxy 后，按实现类 `getBean(Impl.class)` 会失败（按接口 `getBean(Interface.class)` 可用）

                        推荐步骤：
                        1) 先跑并读懂 Labs：
                           - `SpringCoreBeansProxyingPhaseLabTest`
                           - 对照 AOP：`docs/aop/spring-core-aop/part-01-proxy-fundamentals/03-self-invocation.md`
                           - 对照 Tx：`docs/tx/spring-core-tx/part-01-transaction-basics/02-transactional-proxy.md`
                        2) 自己实现一个 `BeanPostProcessor#postProcessAfterInitialization(...)`：
                           - 仅对一个接口类型（例如 `WorkService`）进行 proxying
                           - 在 InvocationHandler 里记录被拦截的方法名（不要只靠日志）
                        3) 写断言把现象固化：outer 被拦截、inner 因自调用不被拦截、按实现类获取失败

                        提示：
                        - 你不需要引入 Spring AOP；最小 JDK proxy 就足够讲清“容器替换实例”的机制
                        - 如果你想把“按实现类获取失败”变成更稳定的断言，优先用 `assertThatThrownBy(context::getBean)`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：扩展注入阶段实验：对照 required/optional 的注入语义（field vs constructor）")
    void exercise_injectionOptionalRequiredSemantics() {
        assertThat(true)
                .as("""
                        练习：扩展注入阶段实验，做出 required vs optional 注入语义的对照（field vs constructor）。

                        目标：
                        - 你能用断言稳定回答：依赖缺失时，field/constructor 各自会怎么失败？
                        - 你能解释清楚：哪些写法会导致“容器启动失败”，哪些写法会得到“可选的空值/Provider”。

                        推荐步骤：
                        1) 先跑并读懂 Labs：
                           - `SpringCoreBeansInjectionPhaseLabTest`
                           - `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md`
                        2) 设计 3 种“可选依赖”写法并对照验证：
                           - `@Autowired(required=false)`（field 或 setter）
                           - `ObjectProvider<T>`（constructor 注入 provider，再 `getIfAvailable()`）
                           - `@Nullable` / `Optional<T>`（二选一即可）
                        3) 设计 1 种“必填依赖缺失”写法并验证它会在 refresh 时失败：
                           - constructor injection + 缺失 bean

                        输出要求：
                        - 每个场景都必须有断言（不要靠 println/log）
                        - 每个测试只讲一个点（一个语义/一个坑/一个结论）
                        """)
                .isFalse();
    }
}
