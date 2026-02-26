# Change Proposal: spring-core-aop 真实多代理叠加（Tx/Cache/Security）+ AOP 内核深化 + 学习路线

## Requirement Background

在上一轮 `spring-core-aop`/`spring-core-beans` 深挖中，我们已经把 AOP 的核心机制补齐到“可断点复述主线 + 可断言 Labs”（AutoProxyCreator 主线、`proceed()` 嵌套、pointcut 误判、以及多 advisor vs 套娃 proxy）。

本轮你明确提出“123 都要”，意味着需要把学习体验从“单一 AOP”再推到真实项目里最常见、也最容易排障失败的组合场景：

- `@Transactional`（事务）
- `@Cacheable`（缓存）
- `@PreAuthorize`（方法鉴权）
- 自定义 AOP（例如 `@Traced` 日志/链路/计时）

并且同时要：

1) 给出**真实叠加的可运行可断言 Labs**（不是只讲概念）  
2) 继续加深 AOP 内核层面的“链条组装/执行/观察”能力  
3) 给出一份可执行的**学习路线 + 断点清单**（按顺序跑，能定位问题）

## Change Content

1. 在 `spring-core-aop` 模块内新增“真实叠加”集成实验：
   - 同一个 bean 方法同时叠加 `@Transactional/@Cacheable/@PreAuthorize/@Traced`
   - 提供稳定断言：鉴权阻断、事务激活、缓存命中/短路、以及 advisors/拦截链可观察
2. 增强 AOP 内核观察能力：
   - 提供“如何从 proxy 拿到 advisors，再组装成 method-level interceptor chain”的工具化演示
   - 将这一能力复用到“真实叠加”集成实验中
3. 输出学习路线与断点清单：
   - 从“单 AOP”→“多 advisor”→“真实叠加（Tx/Cache/Security）”的分层路线
   - 每一步都给出可运行入口（测试方法）与建议断点（call path / chain / matching）

## Impact Scope

- **Modules:**
  - `spring-core-aop`（主要变更：新增文档与集成 Labs；可能新增少量学习依赖）
  - `helloagents`（SSOT 同步、变更记录与归档）
- **Files:**
  - `spring-core-aop/pom.xml`（新增学习用依赖：事务与方法鉴权）
  - `docs/aop/spring-core-aop/*`（新增 10 号章节 + 更新现有导航/链接）
  - `spring-core-aop/src/test/java/...`（新增真实叠加 Labs + 链条观察工具/断言）
  - `helloagents/wiki/modules/*`、`helloagents/CHANGELOG.md`、`helloagents/history/index.md`
- **APIs:** 无对外 API 变更
- **Data:** 无业务数据变更（如需事务管理器，优先使用无资源事务管理器，避免引入真实 DB 依赖）

## Core Scenarios

### Requirement: 真实项目式多代理叠加（Tx/Cache/Security/AOP）
**Module:** spring-core-aop

#### Scenario: 非管理员被方法鉴权阻断（目标方法不执行）
前置条件：同一方法叠加 `@PreAuthorize` 与其他增强
- 预期结果 1：非管理员调用抛出 `AccessDeniedException`
- 预期结果 2：目标方法的“可观察执行计数”保持为 0（证明被安全拦截器短路）
- 预期结果 3：可在 `Advised#getAdvisors()` 里观察到安全相关拦截器存在

#### Scenario: 管理员调用成功，且缓存命中后短路目标方法
前置条件：同一方法叠加 `@Cacheable`（固定 key）
- 预期结果 1：首次调用执行目标方法（计数=1），返回结果进入 cache
- 预期结果 2：第二次同 key 调用不再执行目标方法（计数仍=1）
- 预期结果 3：可在“method-level interceptor chain”里定位 cache/tx/security 的相对位置（不强绑定内部类名）

#### Scenario: 事务确实激活（可用断言验证）
前置条件：同一方法叠加 `@Transactional` 且提供事务管理器
- 预期结果 1：在目标方法内部 `TransactionSynchronizationManager.isActualTransactionActive()==true`
- 预期结果 2：可在 proxy 的 advisors/链条中观察到事务拦截器存在

### Requirement: 学习路线 + 断点清单（可按顺序跑通）
**Module:** spring-core-aop

#### Scenario: 从单 AOP 到真实叠加的逐级路线
- 预期结果 1：每个阶段给出“推荐阅读章节 + 测试入口（类/方法）”
- 预期结果 2：每个阶段给出“建议断点 + 观察点（watch list）”

## Risk Assessment

- **Risk:** 为 `spring-core-aop` 增加依赖可能引入额外基础设施噪音，影响已有测试可读性
  - **Mitigation:** 依赖范围尽量最小（优先 `spring-tx` + `spring-security-config`），集成实验使用 `AnnotationConfigApplicationContext`，避免 Boot 自动装配干扰；断言聚焦稳定语义。
- **Risk:** 内部 advisor/拦截器实现细节随版本变化导致断言脆弱
  - **Mitigation:** 不断言“全序列/具体类名列表”，只断言关键行为（是否阻断、是否缓存短路、事务是否激活）+ 关键类型存在（tx/cache/security 的核心拦截器）。
