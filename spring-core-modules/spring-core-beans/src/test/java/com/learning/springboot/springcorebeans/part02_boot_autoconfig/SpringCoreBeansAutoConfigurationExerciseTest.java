package com.learning.springboot.springcorebeans.part02_boot_autoconfig;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

class SpringCoreBeansAutoConfigurationExerciseTest {

    @Test
    @Disabled("练习：给 DemoGreeting 增加一个 @ConditionalOnProperty 约束，并用 ApplicationContextRunner 断言 matchIfMissing 行为")
    void exercise_addPropertyGateToGreeting() {
        assertThat(true)
                .as("""
                        练习：给 DemoGreeting 增加一个 @ConditionalOnProperty 约束，并用 ApplicationContextRunner 断言 matchIfMissing 行为。

                        下一步：
                        1) 在自动装配/配置类中为某个 Bean 增加 `@ConditionalOnProperty`。
                        2) 用 runner 分别覆盖：缺失该 property / property=false / property=true。
                        3) 断言 Bean 是否存在，并记录 matchIfMissing 的行为。

                        参考：
                        - `spring-core-modules/spring-core-beans/src/test/java/.../SpringCoreBeansAutoConfigurationLabTest.java`
                        - `spring-core-modules/spring-core-beans/src/test/java/.../SpringCoreBeansConditionEvaluationReportLabTest.java`（matchIfMissing 三态）
                        - `spring-core-modules/spring-core-beans/src/test/java/.../SpringCoreBeansAutoConfigurationOrderingLabTest.java`（顺序/时机类问题的最小复现写法）
                        - `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：故意制造同类型 Bean 的歧义（注册两个 Bean），再用 @Primary 或 @Qualifier 解决")
    void exercise_createAndResolveAmbiguity() {
        assertThat(true)
                .as("""
                        练习：故意制造同类型 Bean 的歧义（注册两个 Bean），再用 @Primary 或 @Qualifier 解决。

                        下一步：
                        1) 先制造歧义：在同一个场景里注册两个相同类型 Bean。
                        2) 观察启动失败的异常（NoUniqueBeanDefinitionException）。
                        3) 再用 @Primary/@Qualifier 让它变成“确定性选择”。

                        建议阅读：
                        - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个小的 debug summary（active properties + 谁赢了），避免对长日志做断言")
    void exercise_addDebugSummaryHelper() {
        assertThat(true)
                .as("""
                        练习：增加一个小的 debug summary（active properties + 谁赢了），避免对长日志做断言。

                        下一步：
                        1) 写一个 helper：输出关键 property + 最终注入的 Bean 名/类型。
                        2) 测试里断言关键事实（Bean 存在/类型），而不是断言整段日志。

                        建议阅读：
                        - `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：matchIfMissing 的 missing/false/true 三态 + ConditionEvaluationReport（可查询）")
    void exercise_matchIfMissingTriState_andConditionEvaluationReport() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DefaultEnabledFeatureAutoConfiguration.class));

        runner.run(context -> {
            assertThat(context).hasSingleBean(DefaultEnabledFeature.class);
            assertThat(context.getBean(DefaultEnabledFeature.class).origin()).isEqualTo("enabled-by-default");

            assertThat(negativeMessages(context, DefaultEnabledFeatureAutoConfiguration.class))
                    .as("fullMatch=true 时，负向原因应为空（你可以打印出来观察）")
                    .isEmpty();

            System.out.println("OBSERVE: matchIfMissing=true + missing property => feature enabled by default");
        });

        runner.withPropertyValues("exercise.default.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(DefaultEnabledFeature.class);

                    assertThat(negativeMessages(context, DefaultEnabledFeatureAutoConfiguration.class))
                            .as("fullMatch=false 时，ConditionEvaluationReport 至少应给出 1 条失败原因")
                            .isNotEmpty();

                    System.out.println("OBSERVE: exercise.default.enabled=false => feature disabled");
                });

        runner.withPropertyValues("exercise.default.enabled=true")
                .run(context -> assertThat(context).hasSingleBean(DefaultEnabledFeature.class));

        assertThat(true)
                .as("""
                        练习目标：把上面的三态行为讲清楚（不是背注解）。

                        常见追问：
                        1) 为什么 matchIfMissing=true 会被用在某些功能开关上？它有什么风险？
                        2) 当“缺失配置”时你怎么证明它是 match 还是 skip？（报告/断点）

                        推荐断点（从主线到细节）：
                        - `ConditionEvaluator#shouldSkip`
                        - `ConditionEvaluationReport#getConditionAndOutcomesBySource`

                        参考实现：
                        - `spring-core-modules/spring-core-beans/src/test/java/.../SpringCoreBeansConditionEvaluationReportLabTest.java`
                        - `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：复现 @ConditionalOnBean 的顺序/时机差异，并用 @AutoConfiguration(after/before) 让行为确定化")
    void exercise_makeConditionalOnBeanDeterministic_withAutoConfigurationAfter() {
        ApplicationContextRunner orderSensitive = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(MarkerAutoConfiguration.class, DependentAutoConfigurationWithoutOrdering.class));

        orderSensitive.run(context -> {
            assertThat(context).hasSingleBean(Marker.class);
            assertThat(context).doesNotHaveBean(DependentFeature.class);

            List<String> reasons = negativeMessages(context, DependentAutoConfigurationWithoutOrdering.class);
            assertThat(reasons)
                    .as("请用 negativeMessages(...) 把“why skip”变成可读信息（不要对长日志做断言）")
                    .isNotEmpty();

            System.out.println("OBSERVE: runtime has Marker, but DependentFeature is missing (condition evaluated earlier)");
        });

        ApplicationContextRunner deterministic = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DependentAutoConfigurationAfterMarker.class, MarkerAutoConfiguration.class));

        deterministic.run(context -> {
            assertThat(context).hasSingleBean(DependentFeature.class);
            assertThat(context.getBean(DependentFeature.class).origin()).isEqualTo("conditional-on-marker");
        });

        assertThat(true)
                .as("""
                        练习目标：你要能把“为什么运行时 bean 存在，但条件仍不生效”解释清楚，并能修复它。

                        常见追问：
                        1) 这个问题的本质是“顺序”还是“时机”？为什么说更偏“时机”？
                        2) after/before 元数据是如何插入到 auto-config 排序主线里的？
                        3) 你会在哪些方法下断点证明：条件评估发生在 Marker 注册之前？

                        推荐断点（从现象到闭环）：
                        - `ConditionEvaluator#shouldSkip`
                        - `OnBeanCondition#getMatchOutcome`

                        参考实现：
                        - `spring-core-modules/spring-core-beans/src/test/java/.../SpringCoreBeansAutoConfigurationOrderingLabTest.java`
                        - `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md`
                        """)
                .isFalse();
    }

    private static List<String> negativeMessages(AssertableApplicationContext context, Class<?> source) {
        ConditionEvaluationReport report = ConditionEvaluationReport.get(context.getBeanFactory());
        ConditionEvaluationReport.ConditionAndOutcomes outcomes = report.getConditionAndOutcomesBySource().get(source.getName());
        if (outcomes == null || outcomes.isFullMatch()) {
            return List.of();
        }

        return outcomes.stream()
                .filter(it -> !it.getOutcome().isMatch())
                .map(it -> it.getOutcome().getMessage())
                .toList();
    }

    record DefaultEnabledFeature(String origin) {
    }

    @AutoConfiguration
    @ConditionalOnProperty(prefix = "exercise.default", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class DefaultEnabledFeatureAutoConfiguration {

        @Bean
        DefaultEnabledFeature defaultEnabledFeature() {
            return new DefaultEnabledFeature("enabled-by-default");
        }
    }

    record Marker(String origin) {
    }

    record DependentFeature(String origin, Marker marker) {
    }

    @AutoConfiguration
    static class MarkerAutoConfiguration {
        @Bean
        Marker marker() {
            return new Marker("from-marker-auto-config");
        }
    }

    @AutoConfiguration
    static class DependentAutoConfigurationWithoutOrdering {

        @Bean
        @ConditionalOnBean(Marker.class)
        DependentFeature dependentFeature(Marker marker) {
            return new DependentFeature("conditional-on-marker", marker);
        }
    }

    @AutoConfiguration(after = MarkerAutoConfiguration.class)
    static class DependentAutoConfigurationAfterMarker {

        @Bean
        @ConditionalOnBean(Marker.class)
        DependentFeature dependentFeature(Marker marker) {
            return new DependentFeature("conditional-on-marker", marker);
        }
    }
}
