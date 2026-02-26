# Change Proposal: 深化 Beans/AOP/Tx/Web 学习体系

## Requirement Background

当前仓库以 Maven 多模块形式组织 Spring Boot / Spring Framework 的学习内容，具备较完整的 `docs/` 与一定数量的 `*LabTest/*ExerciseTest` 作为可验证入口。

基于现状，仍存在这些学习阻力：
1. **覆盖不均衡**：`spring-core-beans` 的文档与 Labs 相对丰富，但 `spring-core-aop` / `spring-core-tx` 的“可断言关键分支”仍偏少；`springboot-web-mvc` 规模较大，关键链路（参数绑定、异常链路、安全交互）需要更体系化的导航与排障入口。
2. **主线叙事不够统一**：不同模块的 Deep Dive Guide、Docs Index、Troubleshooting Checklist 的颗粒度与结构不完全一致，学习者难以形成跨模块的稳定心智模型。
3. **可排障能力不足**：虽然已有 `90-common-pitfalls.md` 等附录，但在“从现象/错误 → 反推机制 → 找到断点/测试入口 → 修复/验证”的闭环上仍可进一步标准化。

本变更旨在按主线逐模块深化：容器（Beans）→ AOP → 事务（Tx）→ Web MVC（含与 Security 的交互），并在每一阶段贯彻“以测试为 SSOT”的可验证学习方法。

## Change Content

1. 补齐并强化四大主题模块的文档主线：从概念到源码链路，再到可断言的关键分支与可复现的排障清单。
2. 新增/强化 `*LabTest/*ExerciseTest`：让关键机制（顺序、边界、分支）可被稳定断言；让“推荐断点/观察点”与文档一一对应。
3. 统一模块学习路径：对齐各模块 `docs/part-00-guide/00-deep-dive-guide.md`、Docs Index、附录结构，并在知识库 `helloagents/wiki/` 形成统一导航。
4. 引入（或补齐）端到端 Debug 主线（可选、收尾用）：用 Web 场景串起 AOP/Tx，以便把跨模块问题定位到明确入口。

## Impact Scope

- **Modules:**
  - `spring-core-beans`
  - `spring-core-aop`
  - `spring-core-tx`
  - `springboot-web-mvc`
  - （交互补齐可能涉及）`springboot-security`
- **Files:**
  - 各模块 `docs/**.md`
  - 各模块 `src/test/java/***LabTest.java` / `***ExerciseTest.java`
  - 知识库：`helloagents/wiki/**.md`（模块导航与一致性）
- **APIs:** 无对外 API 变更（以教学与测试入口为主）
- **Data:** 无生产数据变更；如涉及数据库，限定为测试内嵌/本地方案

## Core Scenarios

### Requirement: R0-learning-path-unification
**Module:** cross-cutting
将四个目标模块的学习路径对齐为统一结构：主线 → 关键分支 → 断点入口 → 常见坑 → 自检清单。

#### Scenario: S0-docs-lab-binding
每个核心结论都能在文档中找到对应的 Lab/Exercise 入口，并能用断言稳定复现。
- 预期：文档章节包含“推荐测试入口 + 推荐断点/观察点”
- 预期：测试命名与文档章节一一对应（可检索、可定位）

### Requirement: R1-beans-container-mainline
**Module:** spring-core-beans
围绕容器启动主线与扩展点（BFPP/BPP/Bean 生命周期），强化“顺序 + 边界 + 排障”三条能力线。

#### Scenario: S1-bpp-ordering
能解释并断言：Post-Processor 的注册/排序/生效时机，以及它对 Bean 定义/实例化/代理的影响。
- 预期：能按 refresh 时间线复述关键步骤（register → invoke → instantiate）
- 预期：能用测试断言 ordering 的关键分支（尤其是 BFPP vs BPP）

#### Scenario: S2-circular-dependency-boundary
能解释并验证：循环依赖“能救/不能救”的边界，以及 early reference 与代理介入的影响。
- 预期：能给出至少 2 组对照实验（可救/不可救）
- 预期：能定位到关键断点（early singleton exposure / getEarlyBeanReference）

#### Scenario: S3-injection-candidate-selection
能从注入报错反推候选选择过程：候选收集、@Primary/@Qualifier、泛型匹配、@Order 等关键规则。
- 预期：面对“无候选/多候选/泛型不匹配”能快速定位原因
- 预期：文档与 Lab 对齐，形成排障 checklist

### Requirement: R2-aop-proxy-and-self-invocation
**Module:** spring-core-aop
围绕代理产生时机、切点表达式、Advisor 链与自调用边界，强化“能解释 + 能断点 + 能定位问题”。

#### Scenario: S1-self-invocation
能解释并验证：同类内自调用导致 Advice 不生效；能给出可控替代方式与边界解释。
- 预期：能对比 this 调用 vs 通过代理调用的差异
- 预期：能说明 `exposeProxy` / `AopContext` 的使用边界（教学 vs 生产）

#### Scenario: S2-auto-proxy-creator-mainline
能复述并断点：AutoProxyCreator 的主线（BPP after-init）以及 Advisor/Advice/Pointcut 的装配与匹配过程。
- 预期：能在测试中给出“为什么这个 Bean 会/不会被代理”的证据链

#### Scenario: S3-jdk-vs-cglib-type-diff
能解释并验证：JDK vs CGLIB 的类型差异、final 限制、接口/类方法差异对 AOP 生效的影响。
- 预期：能给出最小对照实验并断言代理类型

### Requirement: R3-tx-propagation-and-rollback
**Module:** spring-core-tx
围绕 `@Transactional` 的边界、传播行为矩阵、回滚规则，补齐可验证与可排障的知识点。

#### Scenario: S1-tx-boundary-and-proxy
能解释并验证：事务边界依赖代理；与自调用的关系；事务拦截器的执行位置。
- 预期：能从调用链定位 TransactionInterceptor
- 预期：能用测试证明“事务是否真正开启”

#### Scenario: S2-propagation-matrix
能用对照实验解释不同传播行为（REQUIRED/REQUIRES_NEW/NESTED 等）的差异，并给出最小复现与断言点。
- 预期：能给出可断言的矩阵用例（至少覆盖 3 种传播）

#### Scenario: S3-rollback-rules
能解释并验证：RuntimeException vs CheckedException 的默认回滚差异；`rollbackFor/noRollbackFor` 的控制。
- 预期：能给出可断言的回滚规则用例

### Requirement: R4-webmvc-binding-exception-security
**Module:** springboot-web-mvc
围绕请求处理链路、参数绑定/校验、异常处理链路，以及与 Security FilterChain 的交互补齐主线与关键分支。

#### Scenario: S1-argument-binding-and-validation
能解释并验证：参数解析、类型转换、数据绑定、校验失败后错误结构化（400/422 等）与呈现策略。
- 预期：能用 MockMvc（或等价）稳定验证关键分支

#### Scenario: S2-exception-resolver-chain
能解释并断点：异常从 Controller 抛出后如何被解析（多个 Resolver 的顺序、匹配规则、默认错误处理）。
- 预期：能把“错误响应”追溯到具体 resolver/handler

#### Scenario: S3-security-filterchain-interaction
能解释并验证：引入 Security 后 401/403 等在 MVC 之前发生；能从 FilterChainProxy 入手定位与验证。
- 预期：能提供最小对照实验：开启/关闭某个安全分支后响应差异稳定可复现

### Requirement: R5-e2e-storyline
**Module:** springboot-web-mvc + spring-core-aop + spring-core-tx
作为收尾补齐端到端 Debug 主线：从一次 Web 请求出发，串起 MVC → Service(AOP/Tx) →（可选）数据访问，形成跨模块定位路径。

#### Scenario: S1-end-to-end-debug-route
能给出一条“从现象到断点”的跨模块路线图，并用测试/日志前缀验证关键观察点。
- 预期：每一跳都能定位到明确的断点入口与验证测试

## Risk Assessment

- **Risk:** 范围膨胀导致长期维护成本上升  
  **Mitigation:** 采用“主线分阶段 + 每阶段 test-first 证据链”推进；每次改动都要求“文档章节 ↔ 测试入口”对齐。
- **Risk:** 新增/强化测试导致脆弱（依赖内部实现细节）  
  **Mitigation:** 优先断言稳定契约与可观察行为；内部断点作为“建议”，避免把实现细节写成强断言。
