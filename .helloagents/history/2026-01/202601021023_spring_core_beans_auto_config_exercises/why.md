# Change Proposal: spring-core-beans Auto-configuration/Conditions Exercises 深挖（框架岗向）

## Requirement Background

当前 `spring-core-beans` 的 `docs/10` 已覆盖自动装配的基本概念、条件装配常见注解与最小 Lab，但对初学者而言仍存在两个学习瓶颈：

1) 只会“背条件注解”，不会把问题变成“可断言最小复现”，更不会形成断点闭环  
2) 面试追问经常落在“时机/顺序/报告定位”上（matchIfMissing、@ConditionalOnBean、after/before），但学习入口不够“练习驱动”

因此需要把 Boot 自动装配相关内容进一步落地为 Exercises（默认禁用），让学习者可以按提示完成并用测试绿灯证明掌握。

## Change Content

1. 在 `SpringCoreBeansAutoConfigurationExerciseTest` 中新增更深入的 Exercises：
   - `matchIfMissing` 三态（missing/false/true）可断言练习
   - `@ConditionalOnBean` 顺序/时机差异 + `@AutoConfiguration(after/before)` 确定化练习
   - “条件报告可查询”小工具练习：从 `ConditionEvaluationReport` 提取负向原因（不对长日志做断言）
2. 将新增面试点继续落到 `docs/10` 的“条件”正文小节（题目 + 追问 + 入口），避免出现独立面试题汇总文档

## Impact Scope

- **Modules:** `spring-core-beans`
- **Files:** `spring-core-beans/src/test/java/...`、`docs/beans/spring-core-beans/10-spring-boot-auto-configuration.md`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: Auto-configuration 练习闭环（Exercises 驱动）
**Module:** spring-core-beans
提供可执行练习，学习者移除 `@Disabled` 后按提示完成，最终 `mvn test` 全绿。

#### Scenario: matchIfMissing 三态能解释 + 能断言
- property missing / false / true 三态行为可断言
- 能用 ConditionEvaluationReport 给出“为什么 match/why skip”

#### Scenario: 顺序/时机差异能解释 + 能修复
- 能复现“运行时 bean 存在，但 @ConditionalOnBean 仍不匹配”的场景
- 能用 `@AutoConfiguration(after/before)` 把行为确定化，并给出断点闭环

#### Scenario: 条件报告变成可查询数据结构
- 能写一个小 helper：针对某个 auto-config source 输出 1-3 条负向原因
- 测试断言关键事实（bean 存在/不存在 + fullMatch），而不是对整段日志做断言

## Risk Assessment

- **Risk:** Exercises 过难导致初学者无从下手  
  **Mitigation:** 每个 Exercise 都提供“题目 + 追问 + 复现入口 + 推荐断点”，并引用对应 Lab 作为参考实现
- **Risk:** 依赖 Boot 内部实现细节导致未来版本不稳定  
  **Mitigation:** 断言以“是否注册/是否 match”为主，不对排序实现细节做断言

