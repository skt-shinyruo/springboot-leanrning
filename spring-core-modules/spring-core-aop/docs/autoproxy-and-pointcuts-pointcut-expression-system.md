# 02. Pointcut 表达式系统：execution/within/this/target/args/@annotation/...（以及常见误判）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕Pointcut 表达式系统：execution/within/this/target/args/@annotation/...（以及常见误判）展开，主线可以概括为：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。

    先运行 `SpringCoreAopPointcutExpressionsLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。

    需要下探源码时，可以从 `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. AOP 的容器主线：AutoProxyCreator 作为 BPP（Advisor / Advice / Pointcut 三层模型）](autoproxy-and-pointcuts-autoproxy-creator-mainline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. 除 `@EnableAspectJAutoProxy` 之外：BeanNameAutoProxyCreator / ProxyFactoryBean / XML](autoproxy-and-pointcuts-other-configuration-entries.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreAopPointcutExpressionsLabTest`
    - Lab：`SpringCoreAopRuntimePointcutCostLabTest`（args 的运行期匹配/成本模型）

## 机制主线

pointcut 是 Spring AOP 里最容易“看起来懂了、其实误判”的部分。

原因很简单：**pointcut 控制的是“哪些调用会进入 proxy 的拦截器链”**，而 proxy 的类型、调用入口、以及表达式语义都可能造成错觉。

本章的目标是做到两件事：

1. **解释清楚每个常见 designator 的语义**（execution/within/this/target/args/@annotation/...）
2. **在真实项目里遇到 AOP 不生效时，能把问题稳定分流**（call path / pointcut / 代理限制）

---

## 0. 先把“匹配发生在哪里”说清楚

Spring AOP（proxy-based）里，匹配的大前提是：

因此排查 pointcut 的第一句话不应该是“表达式写对了吗”，而应该是：

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

- 范围写太宽导致“以为哪里都被拦截了”，实际只是误命中
- 范围写太窄导致“以为机制不工作”，实际只是没匹配到

### 1.2 `within(...)`：按“声明类型”匹配（更偏静态范围）

`within` 更接近是在说：只在某些类型内部定义的方法里生效。

它常用于：

- 限制某个包/类内部的方法（减少误命中）
- 作为 execution 的辅助条件

典型误判：

- 在接口/JDK proxy 场景下只看“注入的类型”，忽略了“真正声明方法的类型”
- 误以为 within 能解决 call path 问题（不能；不走 proxy 还是没用）

> 提醒：真正落地时，建议优先用 execution 建立稳定基线，再用 within 做范围收敛。

---

## 2. this vs target：最容易把人带沟里的第二组

排查 pointcut 时，应当把这句话作为第一原则：

> **this 看的是“代理对象的类型”，target 看的是“目标对象的类型”。**

而代理对象的类型会随 JDK/CGLIB 改变（见 [02. jdk-vs-cglib](proxy-fundamentals-jdk-vs-cglib.md)）。

假设目标实现类是 `Impl`，接口是 `Api`：

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

- 在 IDE 里看到一个 bean 的实现类是 `Impl`
- 编写了 `this(Impl)`，以为“命中 Impl”
- 但项目里实际用的是 JDK proxy（接口代理）
- 因此 this(Impl) 永远不命中，容易误判为 AOP 失效

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

当怀疑 pointcut 没命中时，不要靠猜，按下面步骤做闭环：

如果能把这四步跑通，真实项目里 80% 的“我以为 AOP 不生效”都会被快速分流定位。

---

## 最小可运行实验（Lab）

- Lab：`SpringCoreAopPointcutExpressionsLabTest`
- 建议命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

> 推荐配套 Labs：`SpringCoreAopPointcutExpressionsLabTest`（重点 this vs target）。

1. **调用必须先进入 proxy**（否则没有机会匹配与执行链，见 [01. aop-proxy-mental-model](proxy-fundamentals-aop-proxy-mental-model.md)）
2. **proxy 在收到方法调用时**，会根据目标方法与 advisors 组装拦截器链（见 [06. debugging](proxy-fundamentals-debugging.md)）
3. pointcut 的匹配决定：哪些 advisor 进入这次调用的链条

### 2.1 结论表（先背结论，再用 Labs 验证）

配套 Lab 会把这个误判做成可断言结论：`SpringCoreAopPointcutExpressionsLabTest`。

## 3. args(...)：按“运行时参数类型”匹配（常被误用）

`args` 的直觉是“参数类型匹配”，但需要注意它更偏 **运行时**：

- 以为 args(String) 匹配“声明是 String 的参数”，但实际可能是运行时传入的子类型/代理类型
- 在泛型/集合参数下，args 往往不是预期语义（更建议把范围收敛到 execution + within）

### 3.1 runtime matcher（成本模型）：`MethodMatcher#isRuntime()`

当 pointcut 需要运行期上下文（入参/this/target）才能确定“命中/不命中”时，它会变成 runtime matcher：

- **命中是否成立可能依赖本次调用的实参**（同一方法，不同实参，命中结果不同）
- 这会引入 **per-invocation 的判断开销**（每次调用都要再判断一次）

你不需要背实现，但要能自证两件事：

1) 这个表达式是否是 runtime matcher（`MethodMatcher#isRuntime()`）  
2) 命中是否确实受实参影响（字符串命中、整数不命中）

对应最小证据链入口：

- `SpringCoreAopRuntimePointcutCostLabTest`

调试入口（更贴近源码观察）：

- `interceptorsAndDynamicMethodMatchers`（链条里出现 dynamic matcher 的典型载体）

- `@annotation(X)`：方法上有注解 X
- `@within(X)`：声明该方法的类上有注解 X（更偏静态）
- `@target(X)`：运行时目标对象的类上有注解 X（更偏运行时）

- 把注解贴在接口方法上，但运行时调用的是实现类方法（代理/桥接方法/合成方法更容易造成误判）
- 把注解贴在类上却用 `@annotation` 匹配方法
- 以为 `@within` 与 `@target` 等价，但在代理/继承/元注解场景下可能不同

1. **确认调用走 proxy**：在 `JdkDynamicAopProxy#invoke` / `CglibAopProxy.DynamicAdvisedInterceptor#intercept` 下断点
2. **确认 proxy 上挂了 advisor**：`bean instanceof Advised`，看 `((Advised) bean).getAdvisors()`
3. **确认这次调用的链条**：在 `DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice` 看拦截器链是否包含期望的 advice
4. **从最小切点开始回退**：`@annotation` → `execution` → 加 within/args/this/target 收敛

## 7. Labs 对应关系（建议按顺序跑）

- this vs target（JDK/CGLIB 差异，可断言）：`SpringCoreAopPointcutExpressionsLabTest`
- args runtime matcher（运行期匹配/成本模型）：`SpringCoreAopRuntimePointcutCostLabTest`
- 从最小切点换到 execution（练习题）：`SpringCoreAopExerciseTest#exercise_changePointcutStyle`
- 代理与链条断点导航：见 [00. 深挖指南](guide-deep-dive-guide.md)、[06. debugging](proxy-fundamentals-debugging.md)

## 常见坑与边界

### 坑点 1：把 this/target 当成同一件事，JDK proxy 下“写对了也不命中”

编写了 `this(实现类)` 以为命中实现类，但实际项目用 JDK proxy（接口代理），导致切面完全不生效

`this` 匹配的是“代理对象类型”，JDK proxy 不是实现类子类；`target` 匹配的才是目标对象类型

`SpringCoreAopPointcutExpressionsLabTest#this_vs_target_differs_between_JdkProxy_and_CglibProxy`

先确定项目是 JDK 还是 CGLIB，再选择 this/target；不确定时用更稳定的 `execution(...)` 建立基线

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopPointcutExpressionsLabTest`
- Exercise：`SpringCoreAopExerciseTest`
- Lab：`SpringCoreAopRuntimePointcutCostLabTest`

上一章：[07-autoproxy-creator-mainline](autoproxy-and-pointcuts-autoproxy-creator-mainline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[09-other-config-entries](autoproxy-and-pointcuts-other-configuration-entries.md)

<!-- BOOKIFY:END -->
