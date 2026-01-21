# Change Proposal: 深化主线叙事的“可复用排障套路”（Beans 决策表 + Web MVC error/async 证据链）

## Requirement Background

当前仓库的 `spring-core-beans` 与 `springboot-web-mvc` 已具备“主线叙事”骨架，但在真实排障/复盘时仍容易遇到两个断点：

1. 读者能顺着主线读懂，但遇到具体现象时不确定“现在应该落在哪一段看什么”（缺少把叙事落地成排障套路的速查映射）。
2. Web MVC 侧对于“异常未被 resolver 处理时如何进入 Spring Boot error 机制”与 async 的“两次 dispatch”还缺少一条可连续复述、可断言验证的完整链路。

本次变更目标：在不打散现有章节结构的前提下，把“主线叙事”补齐到“可复用排障套路”，并把关键分支与可观察证据链（LabTest）强绑定。

## Change Content

1. `spring-core-beans`：在 `refresh()` → `doCreateBean()` 主线章补充“分支决策表（现象 → 所在阶段 → 关键方法 → 必看变量 → 对应 LabTest）”，把叙事升级为可复用排障速查表。
2. `springboot-web-mvc`：在 `DispatcherServlet` 主链路章补齐“FilterChain → DispatcherServlet → ExceptionResolvers → Spring Boot error”的完整叙事，重点解释“异常未被 resolver 处理时”的落点；补齐 async 的“两次 dispatch”时间线与可断言证据链说明。

## Impact Scope

- **Modules:**
  - `spring-core-beans`
  - `springboot-web-mvc`
  - `helloagents`（知识库与变更记录）
- **Files:**
  - `docs/beans/spring-core-modules/spring-core-beans/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`
  - `docs/web-mvc/spring-boot-modules/springboot-web-mvc/part-03-web-mvc-internals/067-01-dispatcherservlet-call-chain.md`
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/wiki/modules/springboot-web-mvc.md`
  - `helloagents/CHANGELOG.md`
  - `helloagents/history/index.md`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: 主线叙事可直接用于排障（Beans）
**Module:** spring-core-beans
将“主线叙事”补齐为“现象驱动的排障套路”，读者能从现象反推阶段与关键方法，并能通过 LabTest 复现与断言。

#### Scenario: 从“注入失败/循环依赖/代理不生效”等现象快速定位阶段
- 输入：读者遇到一个具体现象（例如循环依赖、FactoryBean 语义误用、BPP 顺序导致代理缺失）
- 输出：能映射到 refresh 阶段/创建阶段、关键方法、必看变量、对应 LabTest

### Requirement: MVC 异常落点与 Boot error 机制可复述（Web MVC）
**Module:** springboot-web-mvc
补齐“异常未被 resolver 处理 → 进入 Boot error”叙事主线，并把 async 的“两次 dispatch”用时间线讲清楚。

#### Scenario: 未处理异常如何进入 /error（或 error view/json）
- 输入：handler 抛异常且 resolver 未处理
- 输出：能解释异常如何越过 DispatcherServlet 并进入 Boot error（/error、模板、Accept 分支），并能指出对应断言入口

#### Scenario: async 为什么看起来 dispatch 两次
- 输入：DeferredResult/asyncDispatch
- 输出：能解释两次 dispatch（REQUEST vs ASYNC）各自发生了什么，并能列出可断言证据链

## Risk Assessment

- **Risk:** 文档扩写导致链接断裂或章节重复叙事
- **Mitigation:** 只在既有章节内部补充“速查表/时间线”小节；补齐必要的跨章引用；运行模块测试确保证据链可回归
