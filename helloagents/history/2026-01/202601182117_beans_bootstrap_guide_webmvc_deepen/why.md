# Change Proposal: 继续深化文档（Beans：主线/Bootstrap/Guide；Web MVC：主链路）

## Requirement Background

当前仓库的文档体系已经具备“书籍化”的骨架（章节导航 + Lab 证据链 + 主线叙事），但要进一步达到“像书一样讲透、能用于排障”的目标，还需要把以下四处关键章节继续往下压到**关键方法/关键分支**层面：

1. Beans 主线章（18）：`refresh()` → `doCreateBean()` 已能顺读，但对 `preInstantiateSingletons()`、`doGetBean()` 的关键分支仍可进一步“落到源码分岔点与必看变量”。
2. 容器 bootstrap 章（022）：解释了“为什么注解能工作”，但还可以更明确地把“注解能力”拆到 **处理器类型（BDRPP/BFPP/BPP）** 与 **关键方法**，并把“时机差异”讲成连续时间线。
3. 深挖指南（011）：作为入口导航，应进一步把“主线阅读”变成“现象驱动的导航”，让读者在遇到具体现象时能快速跳到正确章节与 Lab。
4. Web MVC 主链路（067）：已补齐异常→Boot error 与 async 时间线，但仍可进一步补齐“ERROR dispatch vs ASYNC dispatch”的对照与断点抓手，降低排障时的误判成本。

## Change Content

1. 扩写 Beans 主线章（18）：补齐 `finishBeanFactoryInitialization` / `preInstantiateSingletons` / `doGetBean` 的关键分支与伪代码，强化“断点+变量”解释力。
2. 扩写容器 bootstrap 章（022）：补齐“注解能力处理器表（功能→处理器→类型→关键方法→refresh 阶段）”与“时机时间线”。
3. 扩写深挖指南（011）：补齐“按现象选章节”的快速导航（现象→章节→断点→LabTest），让指南更像一本书的“索引+路线图”。
4. 扩写 Web MVC 主链路章（067）：补齐 ERROR dispatch 与 ASYNC dispatch 的对照时间线，并给出“从 DispatcherType 钉死观察事实”的排障套路。

## Impact Scope

- **Modules:**
  - `spring-core-beans`
  - `springboot-web-mvc`
  - `helloagents`（知识库与变更记录）
- **Files (expected):**
  - `docs/beans/spring-core-modules/spring-core-beans/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`
  - `docs/beans/spring-core-modules/spring-core-beans/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md`
  - `docs/beans/spring-core-modules/spring-core-beans/part-00-guide/011-00-deep-dive-guide.md`
  - `docs/web-mvc/spring-boot-modules/springboot-web-mvc/part-03-web-mvc-internals/067-01-dispatcherservlet-call-chain.md`
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/wiki/modules/springboot-web-mvc.md`
  - `helloagents/CHANGELOG.md`
  - `helloagents/history/index.md`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: 主线章可覆盖“启动即报错/首次调用才报错/早期暴露/代理替换”等真实分支
**Module:** spring-core-beans
在主线叙事内补齐关键分支，使读者能从现象反推 `preInstantiateSingletons` 与 `doGetBean` 的真实走向。

#### Scenario: 预实例化与 getBean 分支落点可被解释
- 能解释哪些 bean 会被预实例化，以及为什么（lazy-init/abstract/FactoryBean/SmartFactoryBean 等）
- 能解释 doGetBean 的关键分支：缓存命中/dependsOn/parent factory fallback/prototype 窗口

### Requirement: 注解能力的“处理器/时机/关键方法”可复述
**Module:** spring-core-beans
把“为什么注解能工作”讲成：处理器是谁、什么时候注册、什么时候生效、关键方法是什么、如何断点证明。

### Requirement: 深挖指南具备“症状驱动导航”能力
**Module:** spring-core-beans
把指南升级为“读者遇到问题时的索引”：现象→章节→断点→LabTest。

### Requirement: MVC 主链路排障能区分 ERROR vs ASYNC dispatch
**Module:** springboot-web-mvc
把“同一个请求为什么像走了两次”的两类原因拆清楚，并提供可断言证据链。

## Risk Assessment

- **Risk:** 文档过度膨胀导致主线阅读被打断
- **Mitigation:** 只在关键节点新增“分支伪代码/对照表/时间线”，其余保持叙事连续；引用已有章节承接，避免重复写一遍
