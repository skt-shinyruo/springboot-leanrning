# Change Proposal: spring-core-beans 自动装配（Auto-configuration）顺序与条件“可复现”补齐

## Requirement Background

当前 `spring-core-beans` 已经覆盖了：
- `@ConditionalOnProperty/@ConditionalOnClass/@ConditionalOnMissingBean` 的最小可控演示
- `ConditionEvaluationReport` 的“可查询条件报告”用法

但仍缺两个对“原理/框架岗”面试与工程排障都很高频、也最容易被“背概念”带偏的点：

1) `@ConditionalOnProperty(matchIfMissing=...)` 的默认语义与坑点  
2) 自动配置之间的**顺序依赖**：`@ConditionalOnBean` 这类跨自动配置的条件，为什么会因为导入顺序而“看起来没生效”，以及如何用 `@AutoConfiguration(after/before=...)` 把它变成确定性行为

本次变更目标是把上述两点从“概念”升级为：
- 文档里能解释清楚（题目 + 追问）
- 测试里能稳定复现并断言（Lab）
- README 里有明确入口（快速跑起来 + 断点建议）

## Change Content

1. 新增/补齐 `matchIfMissing` 的可断言 Lab：缺失/false/true 三种配置下的注册差异
2. 新增自动配置顺序 Lab：对照“无顺序元数据 → 条件不生效”与“声明 after/before → 行为确定化”
3. 将新增面试点以“题目 + 追问 + 本仓库复现入口”的形式落到 `docs/10-spring-boot-auto-configuration.md` 对应正文小节，并补齐 README 的 Lab 索引入口

## Impact Scope

- **Modules:** `spring-core-beans`
- **Files:** `docs/beans/spring-core-beans/*`、`spring-core-beans/src/test/java/...`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: `@ConditionalOnProperty` 的 matchIfMissing 语义可复现
**Module:** spring-core-beans
把 `matchIfMissing` 的“缺省行为”与“显式关闭”跑成可断言最小复现。

#### Scenario: property 缺失时仍匹配（matchIfMissing=true）
- 当 property 缺失时：条件应匹配，bean 被注册
- 当 property=false 时：条件不匹配，bean 不注册
- 当 property=true 时：条件匹配，bean 被注册

### Requirement: 自动配置之间的顺序依赖可复现
**Module:** spring-core-beans
把 `@ConditionalOnBean` 这类跨自动配置依赖的“顺序敏感”问题落到可断言 Lab，并给出确定化修复方式。

#### Scenario: 顺序变化导致条件装配差异（无 after/before 元数据）
- 当 `DependentAutoConfiguration` 先导入：因 `Marker` 尚未注册，`@ConditionalOnBean(Marker)` 不匹配
- 当 `MarkerAutoConfiguration` 先导入：`Marker` 已存在，条件匹配，dependent bean 注册

#### Scenario: 声明 after/before 后，行为与导入列表顺序解耦
- 在 `DependentAutoConfiguration` 上声明 `@AutoConfiguration(after=MarkerAutoConfiguration.class)` 后
- 即使导入列表顺序为（Dependent, Marker），也应能确保 dependent bean 被注册

## Risk Assessment

- **Risk:** 示例过度工程化导致学习成本上升  
  **Mitigation:** 坚持“最小可控复现”，以 `ApplicationContextRunner` 为主，不对长日志做断言
- **Risk:** 自动配置排序行为在未来版本变化  
  **Mitigation:** 用 `@AutoConfiguration(after/before=...)` 做明确约束；测试以“是否注册”做断言而非断言具体排序实现细节

