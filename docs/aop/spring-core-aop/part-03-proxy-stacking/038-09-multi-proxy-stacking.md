# 第 38 章：09. 多切面/多代理叠加与顺序：AOP/Tx/Cache/Security 代理链如何叠、如何看
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：多切面/多代理叠加与顺序：AOP/Tx/Cache/Security 代理链如何叠、如何看
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
    - 原理：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
    - 源码入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopMultiProxyStackingLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 37 章：08. Pointcut 表达式系统：execution/within/this/target/args/@annotation/...（以及常见误判）](../part-02-autoproxy-and-pointcuts/037-08-pointcut-expression-system.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 39 章：10 real world stacking playbook](039-10-real-world-stacking-playbook.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**09. 多切面/多代理叠加与顺序：AOP/Tx/Cache/Security 代理链如何叠、如何看**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopRealWorldStackingLabTest`

## 机制主线

真实项目里你几乎一定会遇到“叠加”：

- AOP（自定义切面）
- 事务（`@Transactional`）
- 缓存（`@Cacheable`）
- 安全（方法级鉴权）

这一章的目标是让你能做到：

---

## 1. 先把“叠加”的两种形态分清楚

### 1.1 形态 A：单个 proxy + 多个 advisor（真实项目的主流形态）

这才是你在 Spring 里最常见的结构：

- 一个 bean 最终被暴露为 **一个 proxy**
- proxy 内部挂了 **多个 advisors**
- 每次方法调用：
  - 根据方法与 pointcut 选择适用的 advisors
  - 组装拦截器链
  - `proceed()` 嵌套执行

事务/缓存/安全在机制上并不“特殊”：

> 它们最终都会以 Advisor/Interceptor 的形式出现在同一个 proxy 的 advisors 列表里。

### 1.2 形态 B：多个 proxy 套娃（nested proxy）

多层 proxy 并不是默认形态，但在下面场景可能出现：

- 你显式用 `ProxyFactory` 再包一层（手工代理/二次包装）
- 存在多个“会返回替身对象”的 BPP，且顺序导致“你包我、我再包你”
- scoped proxy（某些 scope 的注入代理）等基础设施包装

它的典型特征是：

- `outerProxy` 是 AOP proxy
- `outerProxy` 的 target **也是** AOP proxy（继续套娃）

---

## 2. 顺序到底由谁决定？不要把两种顺序混在一起

### 2.1 顺序 1：BPP 顺序（容器阶段）

这决定：

- proxy 是不是会“套娃”
- 谁先包谁后包（外层/内层 proxy 的归属）

相关入口（容器时间线）：

对应 beans 深挖：

- `docs/beans/spring-core-beans/part-03-container-internals/14-post-processor-ordering.md`
- `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`

### 2.2 顺序 2：Advisor/Interceptor 顺序（调用阶段）

这决定：

- 同一个 proxy 内部，哪个 advice 在外层、哪个先执行
- `proceed()` 的嵌套关系（before 的顺序与 after 的逆序）

相关入口（调用链）：

- `DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice`
- `ReflectiveMethodInvocation#proceed`

顺序来源通常包含：

- `@Order` / `Ordered` / `PriorityOrdered`
- `@Priority`
- 默认顺序（没有声明顺序的 advisor 往往排在后面）

> 重点：**BPP 顺序与 Advisor 顺序是两套系统**。  
> 你必须先判断自己在排查哪一套顺序问题，否则必然走弯路。

---

## 3. 真实项目里的“叠加”是怎么来的？

你可以用一句话描述大多数情况：

> **AutoProxyCreator 收集容器里所有候选 Advisors（包括事务/缓存/安全），筛选后统一挂到目标 bean 的 proxy 上。**

这解释了为什么：

- 你只看到一个 proxy，但它能同时实现事务、缓存、安全、AOP 切面
- “顺序问题”很多时候不是“代理层级”，而是“advisor 顺序”

AutoProxyCreator 主线详见：[07 - AutoProxyCreator 主线](../part-02-autoproxy-and-pointcuts/036-07-autoproxy-creator-mainline.md)。

---

### 4.1 第一步：确认你拿到的是不是 proxy

- `AopUtils.isAopProxy(bean)`
- `AopUtils.isJdkDynamicProxy(bean)` / `AopUtils.isCglibProxy(bean)`

### 4.2 第二步：确认 proxy 上挂了哪些 advisors

大多数 Spring AOP proxy 都实现了 `Advised`：

- `bean instanceof Advised`
- `((Advised) bean).getAdvisors()`：你会看到 advisor 列表（这就是“叠加”的实体）

### 4.3 第三步：确认到底是“单 proxy 多 advisor”还是“多层 proxy”

一个简单但高收益的思路：

- 先看 `outer` 是否是 AOP proxy
- 再看 `outer` 的 target 是否还是 AOP proxy

同时配合：

- `AopProxyUtils.ultimateTargetClass(bean)`：追到最终目标类型（对多层代理更友好）

### 4.4 第四步：看执行链条（proceed 嵌套）

- `DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice`（链条组装）
- `ReflectiveMethodInvocation#proceed`（链条执行）

观察点：

- `interceptorsAndDynamicMethodMatchers`
- `currentInterceptorIndex`

---

1) **单 proxy 多 advisors**：同一个 proxy 上挂多个“模拟 Tx/Cache/Security” 的 advisors，顺序与 proceed 嵌套可断言  
2) **多层 proxy**：显式再包一层 proxy，演示 nested proxy 的识别与拆解

如果你要进一步把“模拟 advisors”升级为“真实基础设施”：

- 容器阶段：`AbstractAutoProxyCreator#wrapIfNecessary/createProxy`
- 调用阶段：`ReflectiveMethodInvocation#proceed`

---

## 6. 最实用的排障 checklist（按顺序）

当你遇到“某个增强不生效/顺序不对”，按下面顺序稳定分流：

1. **call path**：入口是否走 Spring 管理的 bean？是否 self-invocation？
2. **proxy 形态**：有没有 proxy？是 JDK 还是 CGLIB？有没有 nested proxy？
3. **advisor 是否存在**：proxy 上是否挂了你期望的 advisor？（`Advised#getAdvisors()`）
4. **pointcut 是否命中**：这次调用的链条是否包含该 advisor？（看链条组装）
5. **顺序问题归位**：是 BPP 顺序导致套娃/包裹顺序，还是 advisor 顺序导致 proceed 嵌套顺序？

如果你能把这 5 步跑通，基本就能独立定位真实项目里 AOP/Tx/Cache/Security “不生效”与“顺序怪”的大多数原因。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopRealWorldStackingLabTest`
- 建议命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

- 能解释“叠加”到底是 **一个 proxy 上多个 advisor**，还是 **多个 proxy 套娃**
- 能解释顺序：谁在外层、谁先执行、为什么
- 能在 debug 时把链条“看见”：从 proxy 到 advisors，再到拦截器链

> 推荐配套 Labs：`SpringCoreAopMultiProxyStackingLabTest`（同时覆盖“多 advisor”与“多层 proxy”）。
>
> 如果你希望把“叠加”落到真实基础设施（`@Transactional/@Cacheable/@PreAuthorize`）并用断点验证语义，
> 继续读 [10. real-world-stacking-playbook](039-10-real-world-stacking-playbook.md) + 跑 `SpringCoreAopRealWorldStackingLabTest`。

这种形态如果你没意识到，很容易导致调试误判（比如你在某一层看不到期望的 advisors）。

## 4. Debug 方法：怎么把链条“看见”

断点：

## 5. Labs：把“叠加”做成可断言结论

本模块提供一个专门的 Lab：

- `SpringCoreAopMultiProxyStackingLabTest`

它会同时验证：

- 真实叠加集成 Lab：`SpringCoreAopRealWorldStackingLabTest`
- 配套 Debug Playbook：见 [10. real-world-stacking-playbook](039-10-real-world-stacking-playbook.md)

建议断点配合：

## 常见坑与边界

### 坑点 1：把“顺序问题”一股脑归到 `@Order`，忽略了 BPP 顺序与 advisor 顺序是两套系统

- Symptom：你调 `@Order` 发现顺序没变，或看起来变了但实际只是换了“外层/内层 proxy”而不是拦截器链顺序
- Root Cause：
  - 容器阶段：BPP 顺序影响“有没有套娃/谁包谁”
  - 调用阶段：advisor/interceptor 顺序影响 `proceed()` 嵌套关系
- Verification：
  - 单 proxy 多 advisors（主流形态）：`SpringCoreAopMultiProxyStackingLabTest#multiple_advisors_are_applied_within_a_single_proxy_by_default`
  - 多层 proxy（套娃）可被识别：`SpringCoreAopMultiProxyStackingLabTest#nested_proxy_can_wrap_an_existing_proxy_and_is_detectable_via_target_introspection`
- Fix：先判断你在排查“容器阶段顺序”还是“调用阶段顺序”，再选对观察点（BPP 列表 vs advisors/interceptors）

## 小结与下一章

- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（决定 BPP 注册顺序）
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`（按列表顺序依次应用 BPP）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopRealWorldStackingLabTest`

上一章：[切点表达式系统](../part-02-autoproxy-and-pointcuts/037-08-pointcut-expression-system.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[叠加排障手册](039-10-real-world-stacking-playbook.md)

<!-- BOOKIFY:END -->
