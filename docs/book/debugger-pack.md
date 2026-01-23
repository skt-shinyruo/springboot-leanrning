# Debugger Pack（Entrypoints / Watchpoints / Decisive Branch）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Debugger Pack（Entrypoints / Watchpoints / Decisive Branch）
    - 怎么使用：本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
    - 原理：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
    - 源码入口：N/A（本页为索引/工具页）
    - 推荐 Lab：N/A
<!-- CHAPTER-CARD:END -->


本页目标：让你在读书/跑 Lab 时，能快速把断点装在“最有信息密度”的位置上：入口方法、关键分支、以及能观察到核心数据结构变化的观察点（watchpoints）。

> 说明：不同版本的 Spring/Spring Boot 细节可能不同；当文档与代码冲突时，以代码为准。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Debugger Pack（Entrypoints / Watchpoints / Decisive Branch） —— 本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
- 回到主线：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 怎么用这页

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Debugger Pack（Entrypoints / Watchpoints / Decisive Branch）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 建议先带着问题顺读一遍正文，再按证据链回到源码/断点验证。
<!-- BOOKLIKE-V2:INTRO:END -->

## 模块深挖最小闭环（验收口径）

本仓库采用“模块集合目录（tutorials 风格）”组织代码：`spring-boot-modules/` 与 `spring-core-modules/`。每个模块要满足同一套“可导航 + 可验证 + 可断点 + 可排障 + 可并发复现”的最小闭环，避免出现“文档看完了，但不知道从哪里下断点 / 没有可运行入口 / 无法复现实验”的断链体验。

**每模块 Doc 入口（A 类）**

- **deep-dive guide**：读前置、目标、推荐顺序，并明确“本模块的可跑入口”（Book/Branch/Perf）
- **call-chain**：给出主链路的“调用链证据链入口”（从公共 API → 内部关键节点），用于断点落点与追栈
- **breakpoint map（Debugger Pack for module）**：列出本模块最值钱的入口断点（entrypoints）、观察点（watchpoints）、关键分支（decisive branches）
- **branch decision matrix**：把关键分支压缩成 If/Then 表格（触发条件 → 走向 → 证据/断点 → 可跑用例）
- **pitfalls / playbook**：常见坑与排障 checklist（能对照现象快速定位到分支/配置/边界）
- **self-check**：自检清单（最小可跑命令 + 常见环境问题的快速确认项）

**每模块 Tests 入口（B/C/D 类）**

- **Book Matrix Lab**：主线聚合入口（默认参与回归）
- **Branch Matrix Lab**：关键分支/边界覆盖入口（默认参与回归）
- **Exercise（默认禁用）**：练习题（通常 `@Disabled`，用于读者动手改写，不作为回归门禁）
- **ExerciseSolution（参与回归）**：练习题答案（默认参与回归，避免练习题长期漂移）

**每模块并发/性能可复现实验（E 类）**

- 至少 1 个 `part02_perf_concurrency/*LabTest`（或同等语义目录）
- **禁止“耗时阈值断言”**（例如 “必须 < 50ms”）：改用稳定信号（latch、可控时钟、失败路径计数、隔离性断言）
- 可以使用超时作为“防挂死保护”（例如 `await(1, SECONDS)`），但不把它当作性能 KPI

**一键自检（推荐）**

- 扫描 22 个模块的 docs/tests/perf 入口清单与缺失项：`scripts/audit-module-deep-dive.sh`
- 逐模块回归：`scripts/test-module.sh :<artifactId>`

## 1) Boot / Environment（配置与最终值）

Entrypoints：

- `org.springframework.core.env.AbstractEnvironment#getActiveProfiles`
- `org.springframework.core.env.PropertySourcesPropertyResolver#getProperty`
- `org.springframework.boot.context.properties.bind.Binder#bind`（需要深挖绑定时）

---

## 2) IoC Container（定义层 → 实例层）

Entrypoints：

- `org.springframework.context.support.AbstractApplicationContext#refresh`
- `org.springframework.beans.factory.support.DefaultListableBeanFactory#registerBeanDefinition`
- `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean`

Watchpoints：

- `DefaultListableBeanFactory#beanDefinitionMap`（定义是否真的注册进来）
- `DefaultSingletonBeanRegistry#singletonObjects`（实例是否真的创建出来）

Decisive Branch（常见分支）：

- 是否走 `BeanFactoryPostProcessor`/`BeanPostProcessor`
- 是否进入 early reference（循环依赖相关）

---

## 3) AOP / Proxy（代理与拦截链）

Entrypoints：

- `org.springframework.aop.framework.ProxyFactory#getProxy`
- `org.springframework.aop.framework.JdkDynamicAopProxy#invoke`
- `org.springframework.aop.framework.CglibAopProxy.DynamicAdvisedInterceptor#intercept`

Watchpoints：

- `org.springframework.aop.framework.AdvisedSupport#advisors`（拦截链到底有哪些）

Decisive Branch：

- JDK vs CGLIB（是否有接口、是否强制 CGLIB、final 限制）
- 自调用是否绕过代理（调用点是否在代理外部）

---

## 4) Tx（事务边界）

Entrypoints：

- `org.springframework.transaction.interceptor.TransactionInterceptor#invoke`
- `org.springframework.transaction.support.AbstractPlatformTransactionManager#getTransaction`

Decisive Branch：

- 传播行为：REQUIRED/REQUIRES_NEW/NESTED 等
- 回滚规则：Runtime vs Checked、rollbackFor/noRollbackFor

---

## 5) Web MVC（一次请求的关键节点）

Entrypoints：

- `org.springframework.web.servlet.DispatcherServlet#doDispatch`
- `org.springframework.web.method.support.InvocableHandlerMethod#invokeForRequest`

Decisive Branch：

- 参数绑定分支：`@RequestBody` vs `@ModelAttribute`
- 400 的根因分流：BindException vs MethodArgumentNotValidException vs HttpMessageNotReadableException

---

## 6) Data JPA（持久化上下文）

Watchpoints：

- `org.hibernate.engine.spi.PersistenceContext`（实体是否在一级缓存里）
- `org.hibernate.internal.SessionImpl`（flush/dirty checking 相关）

---

## 7) Security（过滤器链）

Entrypoints：

- `org.springframework.security.web.FilterChainProxy#doFilterInternal`

Decisive Branch：

- 认证是否成功、授权是否通过
- 401 vs 403 的分支根因
