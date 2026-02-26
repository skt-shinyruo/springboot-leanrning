# Change Proposal: spring-core-beans + spring-core-aop 深挖增强 v2

## Requirement Background

当前 `spring-core-beans` 与 `spring-core-aop` 的学习内容已经能跑通基本闭环，但用户反馈“知识点还是太简单不全面”，希望读完后能做到：

- 不仅理解概念，还能把机制落到 **源码断点主线** 并复述
- 能在真实项目里独立定位 **AOP 不生效** 的原因（call path / pointcut / 代理限制 分流）
- 能解释并验证 **JDK vs CGLIB** 的类型/注入边界
- 能解释并验证 **多切面顺序** 与 **多代理叠加** 的运行时结构

本次 v2 的目标是：把 AOP 的关键机制（BPP 主线、拦截器链、pointcut 表达式系统、多代理/多 advisor 叠加）补齐到“可断言 Labs + 可下断点导航 + 可复述主线”的层级，并在 beans 模块补齐容器视角承接，形成跨模块闭环。

## Change Content

1. **AOP 作为 BPP 的源码主线**：补齐 AutoProxyCreator / Advisor / Advice / Pointcut 三层模型、注册与生效时机、断点入口与观察点。
2. **advice 链执行细节**：补齐拦截器链组装、`MethodInvocation#proceed` 的嵌套与顺序、以及 `@Order`/`Ordered` 的可验证结论。
3. **pointcut 表达式系统讲解**：覆盖 `execution/within/this/target/args/@annotation/@within/@target/...` 的语义差异与常见误判（尤其是 this vs target 与代理类型的关系）。
4. **多代理叠加与顺序**：补齐“单代理 + 多 Advisor”与“多层代理（nested proxy）”两种形态的识别、观察与排障路径，并给出真实项目的定位 checklist。
5. **beans 模块承接加强**：在 beans 的 BPP/代理章节中显式引入 AutoProxyCreator 作为典型 BPP（含 early reference 介入边界），把 AOP 现象放回容器时间线解释，并增加跨模块链接。

## Impact Scope

- **Modules:**
  - `spring-core-aop`
  - `spring-core-beans`
- **Files:**
  - `docs/aop/spring-core-aop/*`（新增/扩写若干章节，更新阅读导航）
  - `spring-core-aop/src/test/java/...`（新增/增强 Labs）
  - `docs/beans/spring-core-beans/*`（扩写 BPP/代理章节，补齐 AOP 典型实现承接）
  - `helloagents/wiki/modules/*`（知识库同步）
  - `helloagents/CHANGELOG.md`、`helloagents/history/index.md`（记录与可追溯）
- **APIs:** 无对外 API 变更
- **Data:** 无数据结构变更

## Core Scenarios

### Requirement: AOP 作为 BPP 的源码主线（可断点复述）
**Module:** spring-core-aop / spring-core-beans

#### Scenario: 从 refresh 到 proxy 的最短主线可跑通
前置条件：运行指定 Lab/Test 方法并在关键入口打断点
- 预期结果 1：能从 `registerBeanPostProcessors` 跟到 AutoProxyCreator 被注册
- 预期结果 2：能从 `postProcessAfterInitialization/wrapIfNecessary/createProxy` 看到 proxy 产生
- 预期结果 3：能解释 Advisor/Advice/Pointcut 如何组合成“可执行链”

### Requirement: advice 链执行细节（顺序 + 嵌套）
**Module:** spring-core-aop

#### Scenario: 通过可断言的顺序日志证明 proceed 嵌套结构
- 预期结果 1：能用断言证明 “外层 advice before → 内层 advice before → target → 内层 after → 外层 after”
- 预期结果 2：能解释 `@Order` 与 `AnnotationAwareOrderComparator` 在链条组装时的作用

### Requirement: pointcut 表达式系统（覆盖面 + 常见误判）
**Module:** spring-core-aop

#### Scenario: this vs target 在 JDK/CGLIB 下表现不同
- 预期结果 1：在 JDK proxy 下，`this(实现类)` 不命中但 `target(实现类)` 命中
- 预期结果 2：在 CGLIB proxy 下，`this(实现类)` 命中
- 预期结果 3：能解释 `within/execution/@annotation/...` 的典型误判来源

### Requirement: 多代理叠加与顺序（结构可观察 + 可排障）
**Module:** spring-core-aop / spring-core-beans

#### Scenario: 区分“单 proxy 多 advisor”和“多层 proxy”
- 预期结果 1：能从 `Advised#getAdvisors()` 看到同一个 proxy 上的多个 advisor（AOP/Tx/Cache 等）
- 预期结果 2：能识别 nested proxy（必要时通过 ultimateTargetClass/TargetSource 逐层拆解）
- 预期结果 3：能给出真实项目里“不生效”的分流定位路径

## Risk Assessment

- **Risk:** 文档变长导致读者迷路
  - **Mitigation:** 采用“深挖指南 → 分主题章节 → Labs 入口 → 断点清单”的分层结构，并在每章提供可运行的最小闭环入口。
- **Risk:** 新增 Labs 可能引入不稳定断言（依赖 Spring 内部类名/排序细节）
  - **Mitigation:** 断言尽量基于稳定语义（是否为 proxy、advisor 数量、可观察日志顺序），避免强依赖内部类名与全量排序序列。
