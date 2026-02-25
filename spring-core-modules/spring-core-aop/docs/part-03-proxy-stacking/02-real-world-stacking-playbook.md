# 02. 真实项目叠加 Debug Playbook：AOP/Tx/Cache/Security 如何叠、如何断点验证
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：真实项目叠加 Debug Playbook：AOP/Tx/Cache/Security 如何叠、如何断点验证
    - 怎么使用：直接跑本章配套集成 Lab，把“鉴权阻断 / 缓存短路 / 事务激活 / 链条可观察”固化成断言；再按本文的断点 Playbook 逐层把 proxy/advisors/chain 看清楚，最后能在真实项目里复用同一套排障路径。
    - 原理：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
    - 源码入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopRealWorldStackingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 多切面/多代理叠加与顺序：AOP/Tx/Cache/Security 代理链如何叠、如何看](01-multi-proxy-stacking.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 常见坑清单（建议反复对照）](../appendix/01-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「10. 真实项目叠加 Debug Playbook：AOP/Tx/Cache/Security 如何叠、如何断点验证」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `SpringCoreAopRealWorldStackingLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreAopRealWorldStackingLabTest` / `SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopProceedNestingLabTest` / `SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopPointcutExpressionsLabTest`

## 机制主线

上一章（[09. multi-proxy-stacking](01-multi-proxy-stacking.md)）我们用“模拟 Tx/Cache/Security 的 Advisors”把两种叠加形态讲清楚了：

- **主流形态：单 proxy + 多 advisors**（同一个代理上挂很多增强）
- **少见但要能识别：多层 proxy（套娃）**

但真实项目里，你最终要面对的是 **真实基础设施**：

- 事务：`@Transactional`
- 缓存：`@Cacheable`
- 安全：`@PreAuthorize`（方法级鉴权）

---

如果你需要在启动后挂起等待 IDE attach（默认 5005）：

```bash
mvn -pl :spring-core-aop -Dmaven.surefire.debug -Dtest=SpringCoreAopRealWorldStackingLabTest test
```

也可以精确到某个方法（断点更舒服）：

```bash
mvn -pl :spring-core-aop -Dmaven.surefire.debug -Dtest=SpringCoreAopRealWorldStackingLabTest#cache_hit_short_circuits_target_but_security_still_applies test
```

## 1. 先建立“真实叠加”的心智模型（1 分钟版本）

把真实叠加想成一句话就够：

> **一个 bean 最终暴露为一个 proxy，而 Tx/Cache/Security/AOP 都只是这个 proxy 上的一组 advisors/interceptors。**

因此你遇到的所有“增强不生效 / 顺序怪 / 被绕过”问题，本质上都能回到下面 **4 个可断言结论（真实语义）**：

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

## 2. 断点 Playbook：四步把链条“看见”

> 目标：不要靠猜。你应该能在断点里直接回答：有没有 proxy、有哪些 advisors、这次调用挂了哪些拦截器、顺序如何。

### 2.1 第一步：确认你拿到的是不是 proxy（不要跳过）

在你的业务入口（controller/service 的调用点）先做一个“事实确认”：

- `AopUtils.isAopProxy(bean)`
- `AopUtils.isJdkDynamicProxy(bean)` / `AopUtils.isCglibProxy(bean)`
- `bean instanceof Advised` → `((Advised) bean).getAdvisors()`

你要得到一个明确结论：

- **没有 proxy**：后面所有 AOP/Tx/Cache/Security 都不用谈（先回到 call path/配置问题）
- **有 proxy 但 advisors 缺失**：优先排查 `@Enable...` 是否生效、bean 是否被排除、是否有多个容器
- **有 proxy 且 advisors 都在**：继续看“链条组装与执行”

### 2.2 第二步：看清“链条组装”（你要看见这次调用挂了哪些拦截器）

- `DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice`

你要看的不是源码细节，而是两个观察点：

- `method` / `targetClass`：这次到底在调用哪个方法、目标类是谁
- 返回的拦截器列表：**这一次调用实际挂了哪些 interceptor，顺序是什么**

> 这一步极其关键：很多“我以为应该生效”其实是“这次压根没把它组装进链条”。

### 2.3 第三步：看清“链条执行”（proceed 嵌套关系）

- `ReflectiveMethodInvocation#proceed`

观察点：

- `currentInterceptorIndex`：现在执行到第几个 interceptor
- “before 的顺序 / after 的逆序”：你应该能解释为什么 finally 会倒着出栈

---

## 3. 真实基础设施入口：Tx / Cache / Security 分别在哪进场

> 目标：你能在断点里“看到”它们分别在哪拦截、在哪短路、在哪抛错，从而把真实项目的行为归因到具体拦截器。

### 3.1 事务（@Transactional）

- `TransactionInterceptor#invoke`
- `TransactionAspectSupport#invokeWithinTransaction`

### 3.2 缓存（@Cacheable）

- `CacheInterceptor#invoke`
- `CacheAspectSupport#execute`

- **缓存未命中**：继续 `proceed()` → 执行目标方法 → 写入 cache
- **缓存命中**：直接返回 cached value（短路），目标方法不会执行

> 当你看到“缓存命中但仍然执行了方法”，优先怀疑：key 计算/缓存名不一致/代理没生效/调用入口绕过 proxy。

### 3.3 方法安全（@PreAuthorize）

方法安全在不同 Spring Security 版本中类名可能不同，但“特征”很稳定：

- 它是一个 **MethodInterceptor**
- 在未授权时会 **直接抛出 AccessDeniedException**
- 它必须发生在“返回结果之前”（否则就已经泄露数据）

---

## 4. 真实项目排障 Checklist（你可以直接照着跑）

当你遇到“AOP/Tx/Cache/Security 不生效/顺序怪”，按这个顺序做，基本不会走弯路：

1) **call path**：入口是否走 Spring bean？是否 self-invocation？是否从 `new` 出来的对象调用？
2) **proxy 存在性**：`AopUtils.isAopProxy(bean)` 是否为 true？代理类型（JDK/CGLIB）是否符合预期？
3) **advisors 存在性**：`bean instanceof Advised` 后看 `getAdvisors()`，期望的增强是否存在？
4) **本次调用是否命中**：在链条组装断点里看，这次方法调用是否把期望的 interceptor 加入链条？
5) **短路与顺序**：缓存命中是否直接返回？鉴权是否在结果返回前发生？事务边界是否包住目标执行？

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

## 5. 练习：把“机制”复述成一条可运行的主线（建议 30 分钟）

建议你按顺序做三次 Debug，每次只完成一个“可复述结论”：

- 想把“叠加形态”与“顺序归属（BPP vs Advisor）”彻底讲透：回看 [09. multi-proxy-stacking](01-multi-proxy-stacking.md) + `SpringCoreAopMultiProxyStackingLabTest`
- 想把“容器视角（AutoProxyCreator/BPP）”拉通：读 [07. autoproxy-creator-mainline](../part-02-autoproxy-and-pointcuts/01-autoproxy-creator-mainline.md) + 跑 `SpringCoreAopAutoProxyCreatorInternalsLabTest`
- 想把“pointcut 误判”系统补齐：读 [08. pointcut-expression-system](../part-02-autoproxy-and-pointcuts/02-pointcut-expression-system.md) + 跑 `SpringCoreAopPointcutExpressionsLabTest`
- 想补齐并发边界直觉：读 [11. proxy-concurrency-perf](../part-02-perf-concurrency/01-proxy-concurrency-perf.md) + 跑 `SpringCoreAopProxyConcurrencyLabTest`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreAopRealWorldStackingLabTest` / `SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopProceedNestingLabTest` / `SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopPointcutExpressionsLabTest`
- 建议命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

1) **入口没走 proxy（call path 问题）**：自调用/`new` 出来的对象/绕过 Spring 管理  
2) **这次调用没命中（pointcut/matcher 问题）**：表达式误判、方法不可代理、可见性限制  
3) **顺序/短路改变了语义（ordering/short-circuit 问题）**：缓存命中直接返回、鉴权提前抛错、事务边界位置不同

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopPointcutExpressionsLabTest` / `SpringCoreAopRealWorldStackingLabTest` / `SpringCoreAopProceedNestingLabTest`

上一章：[09-multi-proxy-stacking](01-multi-proxy-stacking.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[90-common-pitfalls](../appendix/01-common-pitfalls.md)

<!-- BOOKIFY:END -->
