package com.learning.springboot.springcorebeans.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ApplicationContext;

import com.learning.springboot.springcorebeans.part01_ioc_container.DirectPrototypeConsumer;
import com.learning.springboot.springcorebeans.part01_ioc_container.FormattingService;
import com.learning.springboot.springcorebeans.part01_ioc_container.ProviderPrototypeConsumer;

@SpringBootTest
class SpringCoreBeansExerciseTest {

    @Autowired
    private FormattingService formattingService;

    @Autowired
    private DirectPrototypeConsumer directPrototypeConsumer;

    @Autowired
    private ProviderPrototypeConsumer providerPrototypeConsumer;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @Disabled("练习：去掉 @Disabled，运行测试，然后确认并断言“缺失 Bean 查找”的具体异常类型")
    void exercise_identifyExceptionTypeForMissingBeanLookup() {
        assertThatThrownBy(() -> applicationContext.getBean("doesNotExist"))
                .as("""
                        练习：确认并断言“缺失 Bean 查找”的具体异常类型。

                        下一步：
                        1) 先只移除 `@Disabled`，直接运行看看抛什么异常。
                        2) 再把异常类型固定为断言（本题就是做这个）。

                        建议阅读：
                        - `spring-core-modules/spring-core-beans/docs/boot-debugging-and-observability.md`
                        """)
                .isInstanceOf(NoSuchBeanDefinitionException.class);
    }

    @Test
    @Disabled("练习：让 FormattingService 使用 lower formatter（并更新这里的预期输出）")
    void exercise_switchQualifierToLowerCaseFormatter() {
        assertThat(formattingService.format("Hello"))
                .as("""
                        练习：让 FormattingService 使用 lower formatter。

                        下一步：
                        1) 找到 `FormattingService` 的注入点（通常有 `@Qualifier`）。
                        2) 切换到 lower 的实现（或用你选择的命名）。
                        3) 让本断言通过。

                        建议阅读：
                        - `spring-core-modules/spring-core-beans/docs/ioc-dependency-injection-resolution.md`
                        """)
                .isEqualTo("hello");
    }

    @Test
    @Disabled("练习：改造 DirectPrototypeConsumer，让每次调用都返回全新的 prototype id（提示：ObjectProvider 或 scoped proxy）")
    void exercise_makeDirectPrototypeConsumerUseFreshPrototypeEachCall() {
        UUID first = directPrototypeConsumer.currentId();
        UUID second = directPrototypeConsumer.currentId();

        assertThat(first)
                .as("""
                        练习：改造 DirectPrototypeConsumer，让每次调用都返回全新的 prototype id。

                        下一步：
                        1) 先跑一次看看现象：为什么它“看起来像单例”？
                        2) 用 `ObjectProvider`（或 scoped proxy / @Lookup）改造实现。
                        3) 让本断言变绿。

                        建议阅读：
                        - `spring-core-modules/spring-core-beans/docs/ioc-scope-and-prototype.md`
                        """)
                .isNotEqualTo(second);
    }

    @Test
    @Disabled("练习：移除 FormattingService 上的 @Qualifier，用 @Primary 解决歧义（你来决定哪个 formatter 赢）")
    void exercise_resolveMultipleBeansViaPrimaryInsteadOfQualifier() {
        assertThat(formattingService.format("Hello"))
                .as("""
                        练习：移除 @Qualifier，用 @Primary 解决歧义。

                        下一步：
                        1) 移除注入点上的 `@Qualifier`。
                        2) 选择一个实现标记为 `@Primary`。
                        3) 让下面两个断言同时通过。

                        常见坑：
                        - 只是“碰巧注入了某个实现”不算完成；要让选择逻辑在代码里是确定的
                        """)
                .isEqualTo("HELLO");

        assertThat(formattingService.formatterImplementation())
                .as("""
                        断言：最终被选中的实现类应该是 Upper（或你选择的 primary 实现）。
                        """)
                .containsIgnoringCase("Upper");
    }

    @Test
    @Disabled("练习：复现并修复“单注入歧义”（NoUniqueBeanDefinitionException），用 @Primary 或 @Qualifier 让注入结果确定化")
    void exercise_fixSingleInjectionAmbiguity_withPrimaryOrQualifier() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(InjectionAmbiguityExerciseConfiguration.class);
            context.refresh();

            AmbiguityConsumer consumer = context.getBean(AmbiguityConsumer.class);

            assertThat(consumer.workerId())
                    .as("""
                            练习：修复单依赖注入歧义（multiple candidates）。

                            当前状态：
                            - 这个 context 里有两个 `Worker` 候选
                            - `AmbiguityConsumer` 只注入一个 `Worker`，因此 refresh 会 fail-fast

                            你的目标：
                            - 用 `@Primary`（默认胜者）或 `@Qualifier`（显式选择）修复歧义
                            - 让 context 能 refresh，并让这个断言变绿

                            建议入口（先看可运行版本，再回来做练习）：
                            - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionAmbiguityLabTest.java`

                            建议阅读：
                            - `spring-core-modules/spring-core-beans/docs/ioc-dependency-injection-resolution.md`
                            - `spring-core-modules/spring-core-beans/docs/wiring-autowire-candidate-selection-primary-priority-order.md`
                            """)
                    .isEqualTo("primary");
        }
    }

    @Test
    @Disabled("练习：让 providerPrototypeConsumer 连续两次返回相同 id（提示：把 PrototypeIdGenerator 改为 singleton），并更新预期")
    void exercise_changePrototypeScopeAndUpdateExpectations() {
        UUID provider1 = providerPrototypeConsumer.newId();
        UUID provider2 = providerPrototypeConsumer.newId();

        assertThat(provider1)
                .as("""
                        练习：让 providerPrototypeConsumer 连续两次返回相同 id。

                        下一步：
                        1) 找到 `PrototypeIdGenerator` 的 scope 定义。
                        2) 把它改成 singleton（或等价方式），观察行为变化。
                        3) 让本断言变绿。

                        建议阅读：
                        - `spring-core-modules/spring-core-beans/docs/ioc-scope-and-prototype.md`
                        """)
                .isEqualTo(provider2);
    }

    interface Worker {
        String id();
    }

    record AmbiguityConsumer(Worker worker) {
        String workerId() {
            return worker.id();
        }
    }

    @Configuration
    private static final class InjectionAmbiguityExerciseConfiguration {

        @Bean
        Worker primaryWorker() {
            return () -> "primary";
        }

        @Bean
        Worker secondaryWorker() {
            return () -> "secondary";
        }

        @Bean
        AmbiguityConsumer ambiguityConsumer(Worker worker) {
            return new AmbiguityConsumer(worker);
        }
    }
}
