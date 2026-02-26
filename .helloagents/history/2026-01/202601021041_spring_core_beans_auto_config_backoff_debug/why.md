# Change Proposal: spring-core-beans 自动装配 back-off/覆盖“为何没生效”排障闭环（源码岗向）

## Requirement Background

`spring-core-beans` 的 Boot 自动装配章节已经覆盖了条件注解与顺序依赖（matchIfMissing / @ConditionalOnBean after/before），但在真实工程与面试场景里还有一个更常见、更容易误判的问题：

> “我明明写了自定义 Bean（或注册了一个 BeanDefinition），为什么自动配置没有 back-off？为什么最后容器里出现了两个同类型 Bean？”

很多人把它误以为是“Spring 随机选择/不稳定”，但本质通常是：

- `@ConditionalOnMissingBean` 的判断发生在**注册阶段**（条件评估时机）
- 你提供的“覆盖 BeanDefinition”如果出现得太晚（例如通过某些 BDRPP 方式在 `ConfigurationClassPostProcessor` 之后注册），就不会被条件看到，从而导致自动配置仍然注册默认 Bean

需要把这个现象落地成一个**可断言的最小复现**，并给出：
- 如何用 ConditionEvaluationReport 定位（why match/why skip）
- 如何用断点闭环证明“判断发生在什么时候”
- 如何修复成确定性行为（让 override 发生在条件评估之前）

## Change Content

1. 新增一个 Lab：用“同一个 registrar 早/晚执行”的对照，复现 `@ConditionalOnMissingBean` back-off 的时机差异
2. 在 `docs/10-spring-boot-auto-configuration.md` 的“覆盖”章节补齐面试点：题目 + 追问 + 本仓库 Lab/Test 入口 + 推荐断点
3. 在模块 README 的 Labs 索引表中补齐入口，确保学习路径可发现

## Impact Scope

- **Modules:** `spring-core-beans`
- **Files:** `spring-core-beans/src/test/java/...`、`docs/beans/spring-core-beans/10-*`、`spring-core-beans/README.md`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: back-off/覆盖不生效的排障闭环可复现
**Module:** spring-core-beans
把 “@ConditionalOnMissingBean 没 back-off” 变成可断言最小复现，且能解释根因与修复方式。

#### Scenario: 覆盖 BeanDefinition 过晚导致 auto-config 仍注册默认 Bean
- auto-config 的 `@ConditionalOnMissingBean` 条件匹配（fullMatch=true）
- 后置注册的覆盖 BeanDefinition 也存在
- 结果：容器里出现两个同类型 bean（后续注入可能歧义/非预期）

#### Scenario: 覆盖 BeanDefinition 提前出现，auto-config 正确 back-off
- 覆盖 BeanDefinition 先于 auto-config 条件评估出现
- auto-config 的 `@ConditionalOnMissingBean` 条件不匹配（fullMatch=false 或相关 bean method 被 skip）
- 结果：容器中只存在覆盖 bean，行为确定

## Risk Assessment

- **Risk:** 过度绑定 Boot 内部实现细节  
  **Mitigation:** 测试只断言“是否注册/数量”，并用 `OBSERVE:` 输出辅助提示；不对 report message 文本做强断言

