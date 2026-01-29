# 第 40 章：90. 常见坑清单（建议反复对照）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：常见坑清单（建议反复对照）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
    - 原理：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
    - 源码入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 39 章：10. 真实项目叠加 Debug Playbook：AOP/Tx/Cache/Security 如何叠、如何断点验证](../part-03-proxy-stacking/039-10-real-world-stacking-playbook.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 41 章：99. 自测题：你是否真的理解了 AOP？](041-99-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当你遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：你看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
   - Branch Matrix - Proxy 基础：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyBranchMatrixLabTest test`
   - Branch Matrix - AutoProxy：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyBranchMatrixLabTest test`
   - Branch Matrix - 多代理叠加：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopStackingBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[029-02-breakpoint-map.md](../part-00-guide/029-02-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[029-04-branch-decision-matrix.md](../part-00-guide/029-04-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[041-99-self-check.md](041-99-self-check.md)

- 本章主题：**90. 常见坑清单（建议反复对照）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreAopLabTest` / `SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopPointcutExpressionsLabTest` / `SpringCoreAopProxyMechanicsLabTest`

## 机制主线

本章不是“再讲一遍机制”，而是把 Spring AOP 最常见的误判收敛成一份 **可复现、可对照、可验证** 的排障手册。

建议你用一个统一心智模型读本章（不会迷路）：

1. **先确认 call path**：调用有没有走进 proxy？（自调用/绕过 Spring bean 是最高频根因）
2. **再确认 proxy 事实**：有没有 proxy？是什么 proxy？（JDK/CGLIB 会影响类型边界与 this/target 命中）
3. **再确认 advisor/chain**：proxy 上有没有你期望的 advisor？这次调用的拦截器链里有没有它？
4. **最后再谈边界与叠加**：final/private/static/构造期调用、多 advisor 顺序、多层 proxy（套娃）、以及并发下的上下文隔离

本章的每个坑条目都尽量给出：

- 可复现入口（优先用本仓库的 Lab/Test）
- 推荐断点 / 观察点（来自断点地图）
- 推荐回读章节（从“排障”回到“理解”的闭环）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreAopLabTest` / `SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopPointcutExpressionsLabTest`
- 建议命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界


## 坑 1：自调用绕过代理

- 现象：同类内部调用的方法不被拦截
- 章节：见 [03. self-invocation](../part-01-proxy-fundamentals/032-03-self-invocation.md)
- 解决：抽到另一个 bean；或 self 注入/`ObjectProvider`；或 exposeProxy（进阶，见 [05. expose-proxy](../part-01-proxy-fundamentals/034-05-expose-proxy.md)）
- 对应 Lab：`SpringCoreAopLabTest#selfInvocationDoesNotTriggerAdviceForInnerMethod`
- 对应 Lab（exposeProxy 对照）：`SpringCoreAopExposeProxyLabTest#exposeProxyAllowsSelfInvocationToTriggerAdvice`
- 推荐断点：`JdkDynamicAopProxy#invoke` / `CglibAopProxy.DynamicAdvisedInterceptor#intercept`

## 坑 2：JDK 代理导致“按实现类拿不到 bean”

- 现象：按接口能注入/获取，按实现类类型获取失败
- 章节：见 [02. jdk-vs-cglib](../part-01-proxy-fundamentals/031-02-jdk-vs-cglib.md)
- 解决：用接口注入；或强制 CGLIB（理解取舍后再决定）
- 对应 Lab：`SpringCoreAopProxyMechanicsLabTest#jdkDynamicProxyIsUsedForInterfaceBasedBeans_whenProxyTargetClassIsFalse`
- 推荐断点：`DefaultAopProxyFactory#createAopProxy`

## 坑 3：`final` 方法/类拦截不到

- 现象：明明加了注解，但方法完全不进入 advice
- 章节：见 [04. final-and-proxy-limits](../part-01-proxy-fundamentals/033-04-final-and-proxy-limits.md)
- 解决：避免把需要拦截的方法写成 final；或走接口代理
- 对应 Lab：`SpringCoreAopProxyMechanicsLabTest#finalMethodsAreNotInterceptedByCglibProxies`

## 坑 4：只有 Spring 管理的 bean 才能被拦截

- 现象：`new SomeService()` 出来的对象怎么都不进 advice
- 原因：代理只发生在容器创建 bean 的阶段
- 排查要点：确认调用入口拿到的是“容器里的 bean”，而不是你手动 new 的对象

## 坑 5：切点写错导致“你以为学到了机制，其实是误命中”

- 建议：从最小切点起步（`@annotation`），再逐步扩大范围（`execution`）
- 章节：见 [06. debugging](../part-01-proxy-fundamentals/035-06-debugging.md)
- 对应 Exercise：`SpringCoreAopExerciseTest#exercise_changePointcutStyle`

## 坑 6：在 `@PostConstruct` / 构造器里调用被拦截方法，结果“怎么不生效？”

典型误判：

- “我都加了注解/切面了，为什么初始化阶段就是不进 advice？”

事实：

- proxy 通常在 **初始化后阶段** 产生（BPP after-init）
- 构造器与 `@PostConstruct` 发生时，bean 还没有被替换成最终 proxy
- 并且这类调用经常还是 `this.xxx()`（自调用），会再次绕过 proxy

建议：

- 不要依赖 AOP 去拦截构造期/初始化期内部调用
- 把需要被拦截的逻辑放到真正的业务入口（例如 service public 方法），并确保调用链走 bean proxy

深挖入口：见 [00. 深挖指南](../part-00-guide/029-00-deep-dive-guide.md)

## 坑 7：开启 exposeProxy 以后，“换线程/异步”突然拿不到 currentProxy

事实：

- `AopContext.currentProxy()` 基于 thread-local，上下文不自动跨线程传播

建议：

- exposeProxy 主要用于“理解机制/偶发修复”，不要作为常规架构方案
- 工程上更推荐：重构拆分 bean 或自注入/`ObjectProvider`（见 [03. self-invocation](../part-01-proxy-fundamentals/032-03-self-invocation.md)、[05. expose-proxy](../part-01-proxy-fundamentals/034-05-expose-proxy.md)）

## 坑 8：多个切面顺序搞反，以为 `@Order` “越大越先执行”

事实（建议用测试验证结论，不要靠记忆）：

- `@Order` 值越小，优先级通常越高（更靠外层/更早进入）

对应 Lab：

- `SpringCoreAopProxyMechanicsLabTest#adviceOrderingCanBeControlledWithOrderAnnotation`

推荐观察点：

- 断点进 `DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice` 看拦截器链顺序

## 坑 9：this/target 写反，在 JDK proxy 下永远不命中

典型误判：

- 你知道实现类是 `Impl`，于是写了 `this(Impl)`，以为“会命中 Impl”
- 但项目实际是 JDK proxy（接口代理），proxy 不是 Impl 的子类
- 结果：`this(Impl)` 永远不命中，导致你误判 AOP 失效

解决：

- 优先用更稳的切点（`execution`/`@annotation`），再逐步引入 this/target 收敛范围
- 或明确理解代理类型与 this/target 的差异（用 Lab 固化结论）

章节与 Lab：

- 章节：见 [08. pointcut-expression-system](../part-02-autoproxy-and-pointcuts/037-08-pointcut-expression-system.md)
- Lab：`SpringCoreAopPointcutExpressionsLabTest`

## 坑 10：把“叠加”误认为“很多层 proxy”，排障方向跑偏

事实：

- 真实项目主流形态是：**一个 proxy 上挂多个 advisors**（事务/缓存/安全/切面都在 advisors 列表里）
- 多层 proxy（套娃）并非默认，但在某些场景会出现（手工 ProxyFactory、多个包装型 BPP、scoped proxy 等）

解决：

- 用 `Advised#getAdvisors()` 直接把“叠加实体”看见
- 再判断 target 是否还是 proxy，确认是否存在套娃

章节与 Lab：

- 章节：见 [09. multi-proxy-stacking](../part-03-proxy-stacking/038-09-multi-proxy-stacking.md)
- Lab：`SpringCoreAopMultiProxyStackingLabTest`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopLabTest` / `SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopPointcutExpressionsLabTest` / `SpringCoreAopProxyMechanicsLabTest`
- Exercise：`SpringCoreAopExerciseTest`

上一章：[10-real-world-stacking-playbook](../part-03-proxy-stacking/039-10-real-world-stacking-playbook.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99-self-check](041-99-self-check.md)

<!-- BOOKIFY:END -->
