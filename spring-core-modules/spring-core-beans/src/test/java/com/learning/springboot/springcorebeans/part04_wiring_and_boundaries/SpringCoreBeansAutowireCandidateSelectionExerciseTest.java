package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SpringCoreBeansAutowireCandidateSelectionExerciseTest {

    @Test
    @Disabled("练习：修复单依赖注入歧义（NoUniqueBeanDefinitionException），并对比 @Qualifier vs @Primary 的语义差异")
    void exercise_fixSingleInjectionAmbiguity_andCompareQualifierVsPrimary() {
        assertThat(true)
                .as("""
                        练习：修复单依赖注入歧义（NoUniqueBeanDefinitionException），并对比 @Qualifier vs @Primary 的语义差异。

                        目标：
                        - 你能用断言证明：同类型多个候选时，容器默认应 fail-fast（避免静默注错）。
                        - 你能分别用两种方式修复：@Qualifier（显式选择）与 @Primary（默认胜者）。
                        - 你能解释：为什么 @Qualifier 是“更强信号”，可以绕过 @Primary。

                        推荐路径（先证据后背诵）：
                        1) 先跑 Labs 固定现象：
                           - `SpringCoreBeansInjectionAmbiguityLabTest`
                           - `SpringCoreBeansAutowireCandidateSelectionLabTest`
                        2) 阅读 doc（把规则写成决策树）：
                           - `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md`
                        3) 下断点验证你看到的分支（不要只看日志）：
                           - `DefaultListableBeanFactory#doResolveDependency`
                           - `DefaultListableBeanFactory#determineAutowireCandidate`

                        验证方式（你应该能给出一句话答案）：
                        - @Primary 提供默认胜者；@Qualifier 显式选择目标。
                        - qualifier 命中时，可以选择非 primary（见 Lab：qualifierOverridesPrimary）。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：复现 by-name fallback（依赖名匹配 beanName）并写下它的工程风险与替代方案")
    void exercise_understandByNameFallback_andAvoidRelyingOnIt() {
        assertThat(true)
                .as("""
                        练习：复现 by-name fallback（依赖名匹配 beanName）并写下它的工程风险与替代方案。

                        目标：
                        - 你能解释：为什么“没写 @Qualifier/@Primary，却没有报歧义”有时会发生。
                        - 你能指出：by-name fallback 的不稳定性来自哪里（字段名/参数名/重构改名）。
                        - 你能给出替代方案：显式 @Qualifier 或明确的接口拆分。

                        推荐路径：
                        1) 先跑 Lab 固定现象：
                           - `SpringCoreBeansAutowireCandidateSelectionLabTest#byNameFallback_canResolveSingleInjectionAmbiguity_forAutowiredFieldInjection`
                        2) 再跑对照：
                           - `SpringCoreBeansAutowireCandidateSelectionLabTest#primaryOverridesByNameFallback_forSingleInjection`
                           - `SpringCoreBeansAutowireCandidateSelectionLabTest#qualifierOverridesPrimary_forSingleInjection`
                        3) 回到 doc（33 章）把 by-name fallback 放进你的候选收敛决策树里。

                        思考题（写 2 句话即可）：
                        - 为什么 field injection 更容易触发 by-name fallback？constructor injection 依赖什么（-parameters）？
                        - 为什么它是“隐式规则”，在真实工程里更推荐显式 @Qualifier？
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：理解 ObjectProvider#getIfUnique 的语义，并用它写一个“可选依赖/多候选安全”的获取方式")
    void exercise_objectProviderGetIfUnique_semantics() {
        assertThat(true)
                .as("""
                        练习：理解 ObjectProvider#getIfUnique 的语义，并用它写一个“可选依赖/多候选安全”的获取方式。

                        目标：
                        - 你能解释：getIfAvailable vs getIfUnique 的语义差异。
                        - 你能说明：为什么 ObjectProvider 不是“让容器更聪明”，而是“让你把语义写清楚”。

                        推荐路径：
                        1) 先跑 Lab 固定行为：
                           - `SpringCoreBeansAutowireCandidateSelectionLabTest#objectProvider_getIfUnique_returnsNull_whenMultipleCandidatesExist`
                        2) 阅读 doc：
                           - `docs/beans/spring-core-beans/appendix/90-common-pitfalls.md`（ObjectProvider 条目）

                        额外加分（可选）：
                        - 设计一个场景：0 候选 / 1 候选 / 多候选，分别写出你希望的行为（返回 null/返回对象/显式失败）。
                        """)
                .isFalse();
    }
}

