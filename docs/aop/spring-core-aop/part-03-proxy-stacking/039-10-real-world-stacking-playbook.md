# 第 39 章：10 real world stacking playbook
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：10 real world stacking playbook
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
    - 原理：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
    - 源码入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 38 章：09. 多切面/多代理叠加与顺序：AOP/Tx/Cache/Security 代理链如何叠、如何看](038-09-multi-proxy-stacking.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 40 章：90. 常见坑清单（建议反复对照）](../appendix/040-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**10 real world stacking playbook**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopPointcutExpressionsLabTest` / `SpringCoreAopRealWorldStackingLabTest` / `SpringCoreAopProceedNestingLabTest`

## 机制主线

上一章（[09. multi-proxy-stacking](038-09-multi-proxy-stacking.md)）我们用“模拟 Tx/Cache/Security 的 Advisors”把两种叠加形态讲清楚了：

- **主流形态：单 proxy + 多 advisors**（同一个代理上挂很多增强）
- **少见但要能识别：多层 proxy（套娃）**

但真实项目里，你最终要面对的是 **真实基础设施**：

- 事务：`@Transactional`
- 缓存：`@Cacheable`
- 安全：`@PreAuthorize`（方法级鉴权）

---

如果你需要在启动后挂起等待 IDE attach：

---

## 1. 先建立“真实叠加”的心智模型（1 分钟版本）

把真实叠加想成一句话就够：

> **一个 bean 最终暴露为一个 proxy，而 Tx/Cache/Security/AOP 都只是这个 proxy 上的一组 advisors/interceptors。**

因此你遇到的所有“增强不生效”问题，本质上都能被稳定分流成 3 类：

> 这个分流框架建议背下来。它能让你在真实项目里“少猜、快定位”。

---

1) **鉴权阻断（AccessDenied）**  
   - 未授权调用会在增强链上被阻断，目标方法不执行，缓存不写入。
2) **事务激活（transaction active）**  
   - 授权 + 缓存未命中时，目标方法执行，且在方法体内能断言事务处于激活状态。
3) **缓存短路（cache short-circuit）**  
   - 同参二次调用直接命中缓存，目标方法不再执行（返回第一次结果）。
4) **安全不会被缓存绕过（security before cache）**  
   - 即使缓存里已经有值，未授权调用也必须抛出 `AccessDenied`，不能“偷到缓存结果”。

这些结论都对应真实项目里最常见的“疑难杂症”：

- “我打了 `@Transactional`，怎么没进事务？”
- “`@Cacheable` 好像没生效/命中缓存了但还是执行了方法？”
- “`@PreAuthorize` 不生效/被绕过了？”
- “到底是谁在外层？为什么顺序变了行为就变了？”

---

### 3.1 第一步：确认你拿到的是不是 proxy（不要跳过）

在你的业务入口（controller/service 的调用点）先做一个“事实确认”：

- `AopUtils.isAopProxy(bean)`
- `AopUtils.isJdkDynamicProxy(bean)` / `AopUtils.isCglibProxy(bean)`
- `bean instanceof Advised` → `((Advised) bean).getAdvisors()`

你要得到一个明确结论：

- **没有 proxy**：后面所有 AOP/Tx/Cache/Security 都不用谈（先回到 call path/配置问题）
- **有 proxy 但 advisors 缺失**：优先排查 `@Enable...` 是否生效、bean 是否被排除、是否有多个容器
- **有 proxy 且 advisors 都在**：继续看“链条组装与执行”

### 3.2 第二步：看清“链条组装”（你要看见这次调用挂了哪些拦截器）

- `DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice`

你要看的不是源码细节，而是两个观察点：

- `method` / `targetClass`：这次到底在调用哪个方法、目标类是谁
- 返回的拦截器列表：**这一次调用实际挂了哪些 interceptor，顺序是什么**

> 这一步极其关键：很多“我以为应该生效”其实是“这次压根没把它组装进链条”。

### 3.3 第三步：看清“链条执行”（proceed 嵌套关系）

- `ReflectiveMethodInvocation#proceed`

观察点：

- `currentInterceptorIndex`：现在执行到第几个 interceptor
- “before 的顺序 / after 的逆序”：你应该能解释为什么 finally 会倒着出栈

---

### 4.1 事务（@Transactional）

- `TransactionInterceptor#invoke`
- `TransactionAspectSupport#invokeWithinTransaction`

### 4.2 缓存（@Cacheable）

- `CacheInterceptor#invoke`
- `CacheAspectSupport#execute`

- **缓存未命中**：继续 `proceed()` → 执行目标方法 → 写入 cache
- **缓存命中**：直接返回 cached value（短路），目标方法不会执行

> 当你看到“缓存命中但仍然执行了方法”，优先怀疑：key 计算/缓存名不一致/代理没生效/调用入口绕过 proxy。

### 4.3 方法安全（@PreAuthorize）

方法安全在不同 Spring Security 版本中类名可能不同，但“特征”很稳定：

- 它是一个 **MethodInterceptor**
- 在未授权时会 **直接抛出 AccessDeniedException**
- 它必须发生在“返回结果之前”（否则就已经泄露数据）

---

## 5. 真实项目排障 Checklist（你可以直接照着跑）

当你遇到“AOP/Tx/Cache/Security 不生效/顺序怪”，按这个顺序做，基本不会走弯路：

如果你能把这 5 步跑通，基本就具备了在真实项目里独立定位 AOP/Tx/Cache/Security 问题的能力。

---

1) 跑：`unauthorized_call_is_denied_and_does_not_invoke_target_or_cache`  
   - 你能指出：是谁抛出了 `AccessDeniedException`？目标方法有没有执行？
2) 跑：`authorized_call_invokes_target_with_transaction_and_populates_cache`  
   - 你能指出：事务是在链条的哪一层开始的？目标方法里为什么能断言事务 active？
3) 跑：`cache_hit_short_circuits_target_but_security_still_applies`  
   - 你能指出：缓存命中时是谁提前返回的？为什么未授权也不能拿到缓存结果？

如果你能把这三条复述清楚，你会发现：真实项目里大多数 AOP “玄学问题”，已经不再玄学了。

---

## 7. 下一步推荐

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopPointcutExpressionsLabTest`
- 建议命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

---
tagline: 把 AOP/Tx/Cache/Security 拉到同一条调用链里，用断点与可断言实验定位“不生效/顺序怪/被绕过”
---

# 10. 真实项目叠加 Debug Playbook：AOP/Tx/Cache/Security 如何叠、如何断点验证

这一章的目标不是“再讲一遍概念”，而是把它做成 **可复现、可断言、可断点复述主线** 的实验闭环：

- 你能把“叠加机制”落到断点，并复述调用主线（A）
- 你能在真实项目里定位 AOP/Tx/Cache/Security 不生效的原因（B）
- 你能解释并验证 JDK vs CGLIB 的类型/注入边界（C）
- 你能解释并验证多切面顺序与多代理叠加（D）

> 本章配套 Lab：`SpringCoreAopRealWorldStackingLabTest`  
> 它用最小 `AnnotationConfigApplicationContext` 组装 Tx/Cache/Security 的基础设施，避免 Spring Boot 自动装配带来的噪音。

## 0. 推荐的最小运行方式（精确到方法）

```bash
# 只跑这一组集成 Lab（最推荐）
mvn -pl :spring-core-aop -Dtest=SpringCoreAopRealWorldStackingLabTest test

# 精确到某个方法（断点更舒服）
mvn -pl :spring-core-aop -Dtest=SpringCoreAopRealWorldStackingLabTest#cache_hit_short_circuits_target_but_security_still_applies test
```

```bash
mvn -pl :spring-core-aop -Dmaven.surefire.debug -Dtest=SpringCoreAopRealWorldStackingLabTest test
```

## 2. Lab 入口：你要验证的 4 个真实结论

打开 `SpringCoreAopRealWorldStackingLabTest`，你会看到它刻意覆盖四类“真实语义”：

## 3. 断点 Playbook：从“能跑”到“能复述主线”

下面给你一套按问题类型组织的断点清单。你不需要全打，一次只打你当前要验证的那一段。

核心断点（调用阶段）：

核心断点：

如果你已经读过 [06. debugging](../part-01-proxy-fundamentals/035-06-debugging.md) 与 `docs/Proceed` Lab，这一步会非常顺滑；如果不顺滑，建议回去先把：

- `SpringCoreAopProceedNestingLabTest`

跑通，再回到这个集成 Lab。

## 4. 真实基础设施断点：Tx / Cache / Security 分别在哪“进场”

这一节的目标是：你能在断点里“看到”它们分别在哪拦截、在哪短路、在哪抛错。

常见断点（版本差异不大）：

你要验证的事实：

- 目标方法执行前，事务已开始（或已加入已有事务）
- 目标方法内 `TransactionSynchronizationManager.isActualTransactionActive()` 为 true
- 异常时 rollback 规则由 `TransactionAttribute` 决定（本 Lab 不做回滚细节，但建议你能在断点里看到 attribute）

常见断点：

你要验证两个分支：

建议断点策略：

1) 先在 IDE 里全局搜索 `AccessDeniedException` 的 throw 点（或 Debug 时 “Break on Exception”）
2) 再回到链条组装断点里确认：链条中确实包含来自 `org.springframework.security...` 的拦截器

你要验证的事实：

- **未授权调用**：异常抛出点发生在目标方法执行之前
- **授权调用**：鉴权通过后才允许继续 proceed
- **缓存命中场景**：未授权也必须抛错，不能返回缓存结果（本章 Lab 已用断言固化）

1) **call path**：入口是否走 Spring bean？是否 self-invocation？是否从 `new` 出来的对象调用？  
2) **proxy 存在性**：`AopUtils.isAopProxy(bean)` 是否为 true？代理类型（JDK/CGLIB）是否符合预期？  
3) **advisors 存在性**：`bean instanceof Advised` 后看 `getAdvisors()`，期望的增强是否存在？  
4) **本次调用是否命中**：在链条组装断点里看，这次方法调用是否把期望的 interceptor 加入链条？  
5) **短路与顺序**：缓存命中是否直接返回？鉴权是否在结果返回前发生？事务边界是否包住目标执行？

## 6. 练习：把“机制”复述成一条可运行的主线（建议 30 分钟）

建议你按顺序做三次 Debug，每次只完成一个“可复述结论”：

- 想把“叠加形态”与“顺序归属（BPP vs Advisor）”彻底讲透：回看 [09. multi-proxy-stacking](038-09-multi-proxy-stacking.md) + `SpringCoreAopMultiProxyStackingLabTest`
- 想把“容器视角（AutoProxyCreator/BPP）”拉通：读 [07. autoproxy-creator-mainline](../part-02-autoproxy-and-pointcuts/036-07-autoproxy-creator-mainline.md) + 跑 `SpringCoreAopAutoProxyCreatorInternalsLabTest`
- 想把“pointcut 误判”系统补齐：读 [08. pointcut-expression-system](../part-02-autoproxy-and-pointcuts/037-08-pointcut-expression-system.md) + 跑 `SpringCoreAopPointcutExpressionsLabTest`

## 常见坑与边界

1) **入口没走 proxy（call path 问题）**：自调用/`new` 出来的对象/绕过 Spring 管理  
2) **这次调用没命中（pointcut/matcher 问题）**：表达式误判、方法不可代理、可见性限制  
3) **顺序/短路改变了语义（ordering/short-circuit 问题）**：缓存命中直接返回、鉴权提前抛错、事务边界位置不同

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopPointcutExpressionsLabTest` / `SpringCoreAopRealWorldStackingLabTest` / `SpringCoreAopProceedNestingLabTest`

上一章：[09-multi-proxy-stacking](038-09-multi-proxy-stacking.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[90-common-pitfalls](../appendix/040-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
