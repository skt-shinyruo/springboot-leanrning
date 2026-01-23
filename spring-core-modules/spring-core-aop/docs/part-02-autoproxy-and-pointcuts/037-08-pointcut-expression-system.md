# 第 37 章：08. Pointcut 表达式系统：execution/within/this/target/args/@annotation/...（以及常见误判）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Pointcut 表达式系统：execution/within/this/target/args/@annotation/...（以及常见误判）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
    - 原理：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
    - 源码入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopPointcutExpressionsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 36 章：07. AOP 的容器主线：AutoProxyCreator 作为 BPP（Advisor / Advice / Pointcut 三层模型）](036-07-autoproxy-creator-mainline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 38 章：09. 多切面/多代理叠加与顺序：AOP/Tx/Cache/Security 代理链如何叠、如何看](../part-03-proxy-stacking/038-09-multi-proxy-stacking.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**08. Pointcut 表达式系统：execution/within/this/target/args/@annotation/...（以及常见误判）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreAopPointcutExpressionsLabTest`

## 机制主线

pointcut 是 Spring AOP 里最容易“看起来懂了、其实误判”的部分。

原因很简单：**pointcut 控制的是“哪些调用会进入 proxy 的拦截器链”**，而 proxy 的类型、调用入口、以及表达式语义都可能让你产生错觉。

这一章的目标是让你能做到两件事：

1. **解释清楚每个常见 designator 的语义**（execution/within/this/target/args/@annotation/...）
2. **在真实项目里遇到 AOP 不生效时，能把问题稳定分流**（call path / pointcut / 代理限制）

---

## 0. 先把“匹配发生在哪里”说清楚

Spring AOP（proxy-based）里，匹配的大前提是：

所以你排查 pointcut 的第一句话不应该是“表达式写对了吗”，而应该是：

> **这次调用有没有先走进 proxy？**

---

## 1. execution vs within：很多人混淆的第一组

### 1.1 `execution(...)`：按“方法签名模式”匹配（最常用）

它描述的是“什么方法执行”：

- 返回值模式
- 包/类/方法名模式
- 参数列表模式

它的优点：

- 直观、覆盖面强
- 能把范围写得很精确（对性能与误命中都友好）

典型误判：

- 范围写太宽导致“你以为哪里都被拦截了”，实际只是误命中
- 范围写太窄导致“你以为机制不工作”，实际只是没匹配到

### 1.2 `within(...)`：按“声明类型”匹配（更偏静态范围）

`within` 更像是在说：只在某些类型内部定义的方法里生效。

它常用于：

- 限制某个包/类内部的方法（减少误命中）
- 作为 execution 的辅助条件

典型误判：

- 在接口/JDK proxy 场景下只看“你注入的类型”，忽略了“真正声明方法的类型”
- 误以为 within 能解决 call path 问题（不能；不走 proxy 还是没用）

> 提醒：真正落地时，建议优先用 execution 建立稳定基线，再用 within 做范围收敛。

---

## 2. this vs target：最容易把人带沟里的第二组

你必须把这句话刻进脑子里：

> **this 看的是“代理对象的类型”，target 看的是“目标对象的类型”。**

而代理对象的类型会随 JDK/CGLIB 改变（见 [02. jdk-vs-cglib](../part-01-proxy-fundamentals/031-02-jdk-vs-cglib.md)）。

假设你的目标实现类是 `Impl`，接口是 `Api`：

| 场景 | proxy 类型 | `this(Impl)` | `target(Impl)` | `this(Api)` |
| --- | --- | --- | --- | --- |
| 目标实现了接口，且 `proxyTargetClass=false` | JDK proxy | ❌（不命中） | ✅（命中） | ✅（命中） |
| 强制 `proxyTargetClass=true` | CGLIB proxy | ✅（命中） | ✅（命中） | ✅（命中） |

直觉解释：

- JDK proxy 不是 Impl 的子类，只实现接口 Api，所以 `this(Impl)` 永远不成立
- CGLIB proxy 是 Impl 的子类，所以 `this(Impl)` 成立
- target 指向真实目标对象 Impl，所以 `target(Impl)` 两种 proxy 都能成立

### 2.2 为什么 this/target 会导致“我以为写对了，实际没生效”？

最典型场景：

- 你在 IDE 里看到一个 bean 的实现类是 `Impl`
- 你写了 `this(Impl)`，以为“命中 Impl”
- 但项目里实际用的是 JDK proxy（接口代理）
- 于是 this(Impl) 永远不命中，导致你误判 AOP 失效

---

- `args(SomeType)` 更关注调用时传入的对象类型
- 与 `execution(.., SomeType, ..)` 这种“签名层面”的静态模式不同

常见误判：

---

## 4. 注解相关：@annotation / @within / @target

这一组的核心差异在于“注解贴在哪里、匹配看的是谁”：

常见误判：

> 学习路径建议：先用 `@annotation` 建立最小闭环（“我能拦截”），再逐步扩展到 execution/within/this/target 等通用表达式。

---

## 5. 组合与优先级：&& / || / !（以及括号）

当表达式开始变复杂时，最容易出错的不是 designator，而是组合逻辑：

- 建议总是用括号显式表达意图（不要赌优先级）
- 尽量把表达式拆小：先用一个最小表达式证明命中，再加一个条件收敛范围

工程经验：

- 越复杂的表达式越难排障
- 复杂不等于精确；很多时候“先写精确的 execution，再用 within 限包”更稳

---

## 6. pointcut 排障闭环：把“误判”变成“可证明”

当你怀疑 pointcut 没命中时，不要靠猜，按下面步骤做闭环：

如果你能把这四步跑通，真实项目里 80% 的“我以为 AOP 不生效”都会被快速分流定位。

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreAopPointcutExpressionsLabTest`
- 建议命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

> 推荐配套 Labs：`SpringCoreAopPointcutExpressionsLabTest`（重点 this vs target）。

1. **调用必须先进入 proxy**（否则没有机会匹配与执行链，见 [01. aop-proxy-mental-model](../part-01-proxy-fundamentals/030-01-aop-proxy-mental-model.md)）
2. **proxy 在收到方法调用时**，会根据目标方法与 advisors 组装拦截器链（见 [06. debugging](../part-01-proxy-fundamentals/035-06-debugging.md)）
3. pointcut 的匹配决定：哪些 advisor 进入这次调用的链条

### 2.1 结论表（先背结论，再用 Labs 验证）

配套 Lab 会把这个误判做成可断言结论：`SpringCoreAopPointcutExpressionsLabTest`。

## 3. args(...)：按“运行时参数类型”匹配（常被误用）

`args` 的直觉是“参数类型匹配”，但你必须注意它更偏 **运行时**：

- 你以为 args(String) 匹配“声明是 String 的参数”，但实际可能是运行时传入的子类型/代理类型
- 在泛型/集合参数下，args 往往不是你想要的语义（更建议把范围收敛到 execution + within）

- `@annotation(X)`：方法上有注解 X
- `@within(X)`：声明该方法的类上有注解 X（更偏静态）
- `@target(X)`：运行时目标对象的类上有注解 X（更偏运行时）

- 你把注解贴在接口方法上，但运行时调用的是实现类方法（代理/桥接方法/合成方法会让你更迷惑）
- 你把注解贴在类上却用 `@annotation` 匹配方法
- 你以为 `@within` 与 `@target` 等价，但在代理/继承/元注解场景下可能不同

1. **确认调用走 proxy**：在 `JdkDynamicAopProxy#invoke` / `CglibAopProxy.DynamicAdvisedInterceptor#intercept` 下断点
2. **确认 proxy 上挂了 advisor**：`bean instanceof Advised`，看 `((Advised) bean).getAdvisors()`
3. **确认这次调用的链条**：在 `DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice` 看拦截器链是否包含你的 advice
4. **从最小切点开始回退**：`@annotation` → `execution` → 加 within/args/this/target 收敛

## 7. Labs 对应关系（建议按顺序跑）

- this vs target（JDK/CGLIB 差异，可断言）：`SpringCoreAopPointcutExpressionsLabTest`
- 从最小切点换到 execution（练习题）：`SpringCoreAopExerciseTest#exercise_changePointcutStyle`
- 代理与链条断点导航：见 [00. 深挖指南](../part-00-guide/029-00-deep-dive-guide.md)、[06. debugging](../part-01-proxy-fundamentals/035-06-debugging.md)

## 常见坑与边界

### 坑点 1：把 this/target 当成同一件事，JDK proxy 下“写对了也不命中”

- Symptom：你写了 `this(实现类)` 以为命中实现类，但实际项目用 JDK proxy（接口代理），导致切面完全不生效
- Root Cause：`this` 匹配的是“代理对象类型”，JDK proxy 不是实现类子类；`target` 匹配的才是目标对象类型
- Verification：`SpringCoreAopPointcutExpressionsLabTest#this_vs_target_differs_between_JdkProxy_and_CglibProxy`
- Fix：先确定项目是 JDK 还是 CGLIB，再选择 this/target；不确定时用更稳定的 `execution(...)` 建立基线

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopPointcutExpressionsLabTest`
- Exercise：`SpringCoreAopExerciseTest`

上一章：[07-autoproxy-creator-mainline](036-07-autoproxy-creator-mainline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[09-multi-proxy-stacking](../part-03-proxy-stacking/038-09-multi-proxy-stacking.md)

<!-- BOOKIFY:END -->
