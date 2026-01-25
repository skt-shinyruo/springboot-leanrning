# 第 29 章：00. 深挖指南：把“代理是怎么来的、advice 链怎么跑”落到源码与断点
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：深挖指南：把“代理是怎么来的、advice 链怎么跑”落到源码与断点
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
    - 原理：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
    - 源码入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 28 章：主线时间线：Spring Core AOP](028-03-mainline-timeline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 30 章：01. AOP 心智模型：代理（Proxy）+ 入口（Call Path）](../part-01-proxy-fundamentals/030-01-aop-proxy-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**00. 深挖指南：把“代理是怎么来的、advice 链怎么跑”落到源码与断点**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopLabTest` / `SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopProxyMechanicsLabTest` / `SpringCoreAopProceedNestingLabTest` / `SpringCoreAopPointcutExpressionsLabTest` / `SpringCoreAopRealWorldStackingLabTest`

## 机制主线

这章的目标很明确：**把 AOP 从“我会写 @Aspect”升级为“我能在源码里看见它、并能定位问题”**。

> 你不需要背源码，但你需要一张“不会迷路的导航图”。

- 只看你关心的 beanName（例如 `tracedBusinessService`、`selfInvocationExampleService`）
- 或只看你关心的包名（例如 `com.learning.springboot.springcoreaop`）

## 1. 深挖时最容易迷路的点（以及正确抓手）

### 1.1 不要试图“背 AOP 源码”，要抓住 3 条主线

> 你会发现：只要这三条主线抓稳，AOP 的“细节”会自动归位。  
> 想把 AOP 放回容器视角（AutoProxyCreator/Advisor 主线）：见 [07. autoproxy-creator-mainline](../part-02-autoproxy-and-pointcuts/036-07-autoproxy-creator-mainline.md)。

## 2. 一张“最小源码导航图”（建议贴在脑子里）

### 2.1 容器阶段：proxy 在哪里被创建？

你只需要记住一句话：

> **AOP 的 proxy 通常在 `postProcessAfterInitialization` 阶段产生。**

最短导航（够用版）：

- `AbstractAdvisorAutoProxyCreator#findEligibleAdvisors`
- `AopUtils#canApply`

代理类型不同，入口不同，但核心一致：

- **JDK proxy：** `JdkDynamicAopProxy#invoke`
- **CGLIB proxy：** `CglibAopProxy.DynamicAdvisedInterceptor#intercept`

之后都会走到：

- `DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice`（组装拦截器链）
- `ReflectiveMethodInvocation#proceed`（执行链条）

而你的 `@Around` 通常会落到类似下面的类（名称可能随版本略有变化）：

- `AspectJAroundAdvice#invoke`
- `MethodInvocationProceedingJoinPoint#proceed`

### 3.1 看清“代理是怎么来的”（proxy creation）

- `AbstractAutoProxyCreator#postProcessAfterInitialization`
- `AbstractAutoProxyCreator#wrapIfNecessary`
- `AbstractAutoProxyCreator#createProxy`
- `DefaultAopProxyFactory#createAopProxy`（选择 JDK vs CGLIB）

推荐观察点（watch list）：

- `beanName`
- `beanClass` / `AopProxyUtils.ultimateTargetClass(bean)`
- `specificInterceptors` / `advisors` 数量（这个 bean 为什么需要 proxy？）

### 3.2 看清“advice 链怎么执行”（advisor chain）

- `DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice`
- `ReflectiveMethodInvocation#proceed`

推荐观察点：

- `interceptorsAndDynamicMethodMatchers`（拦截器链条的实际顺序）
- `currentInterceptorIndex`（链条执行到哪里了）

### 3.3 看清“自调用为什么不拦截”（call path）

- `JdkDynamicAopProxy#invoke` / `CglibAopProxy.DynamicAdvisedInterceptor#intercept`（看看 inner 调用有没有进入这里）

### 3.4 看清“pointcut 为什么命中/不命中”（尤其 this vs target）

- `AopUtils#canApply`（适用性判断常见落点）
- `DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice`（最终链条组装，能看到“这次调用到底挂了哪些拦截器”）

配套章节：

- pointcut 表达式系统：见 [08. pointcut-expression-system](../part-02-autoproxy-and-pointcuts/037-08-pointcut-expression-system.md)

### 3.5 看清“多 advisor vs 多层 proxy（套娃）”

观察建议：

- `bean instanceof Advised`：能否拿到 advisors
- `((Advised) bean).getAdvisors()`：同一个 proxy 上挂了哪些增强（AOP/Tx/Cache/Security 的本质入口）
- `((Advised) bean).getTargetSource().getTarget()`：target 是否还是 proxy（判断是否套娃）
- `AopProxyUtils.ultimateTargetClass(bean)`：追最终目标类型

配套章节：

## 4. 推荐的“深挖练习”（每个练习都能在 30 分钟内闭环）

### 练习 A：看一次“proxy 是怎么被包出来的”

目标：

### 练习 B：看一次“advice 链条”

目标：

- 你能在 `interceptorsAndDynamicMethodMatchers` 里看到链条顺序
- 你能解释 `@Order(1)` 与 `@Order(2)` 谁在外层、谁先执行

### 练习 C：看一次“pointcut 从 @annotation 换到 execution”

目标：

- 你能用测试证明：切点范围变了
- 你能解释“太宽/太窄”会导致怎样的误判

### 练习 D：看一次“this vs target”（JDK vs CGLIB 误判最常见）

目标：

- 你能用断言证明：JDK proxy 下 `this(实现类)` 不命中但 `target(实现类)` 命中
- 你能解释：为什么 “代理类型” 会直接改变 pointcut 的命中结果

### 练习 E：看一次“多 advisor vs 多层 proxy（套娃）”

目标：

- 你能区分：同一个 proxy 上挂多个 advisors（主流形态） vs 多层 proxy 套娃（特殊但要能识别）
- 你能解释：顺序问题到底属于 BPP 顺序还是 advisor 顺序（不要混）

### 练习 F：看一次“真实基础设施叠加”（Tx/Cache/Security 同链路）

目标：

## 5. 读完本章你应该获得什么


## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopLabTest` / `SpringCoreAopMultiProxyStackingLabTest`
- 建议命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 准备工作：把深挖做成可重复的实验

### 0.1 推荐运行方式（精确到方法）

```bash
# 跑整个 aop 模块测试
mvn -pl :spring-core-aop test

# 只跑一个测试类（断点更舒服）
mvn -pl :spring-core-aop -Dtest=SpringCoreAopProxyMechanicsLabTest test

# 只跑一个测试方法（最推荐）
mvn -pl :spring-core-aop -Dtest=SpringCoreAopProxyMechanicsLabTest#jdkDynamicProxyIsUsedForInterfaceBasedBeans_whenProxyTargetClassIsFalse test
```

> 提示：如果你需要“启动后挂起等待 IDE attach”，可以加 `-Dmaven.surefire.debug`（默认监听 5005）。

### 0.2 一个高收益习惯：断点先加“降噪条件”

Spring AOP 相关断点会被非常频繁地命中（尤其是 BPP 与匹配逻辑），建议一开始就加过滤条件：

如果你想把“候选 Advisor 如何筛选、为什么这个 bean 会/不会被代理”也看清楚，继续补齐这两个抓手（命中频繁，建议加条件断点只看目标 beanName）：

### 2.2 调用阶段：advice 链是怎么跑起来的？

## 3. 断点清单：你想“看见什么”，就打哪一类断点

推荐断点（从外到内）：

对应 Lab：

- `SpringCoreAopLabTest#tracedBusinessServiceIsAnAopProxy`
- `SpringCoreAopProxyMechanicsLabTest#jdkDynamicProxyIsUsedForInterfaceBasedBeans_whenProxyTargetClassIsFalse`
- `SpringCoreAopProxyMechanicsLabTest#cglibProxyIsUsedForClassBasedBeans_whenProxyTargetClassIsTrue`
- `SpringCoreAopAutoProxyCreatorInternalsLabTest`（看 BPP 主线：AutoProxyCreator 何时注册、如何产生 proxy）

推荐断点：

对应 Lab：

- `SpringCoreAopLabTest#adviceIsAppliedToTracedMethod`
- `SpringCoreAopProxyMechanicsLabTest#adviceOrderingCanBeControlledWithOrderAnnotation`
- `SpringCoreAopProceedNestingLabTest`（看 `proceed()` 的 before/after 嵌套结构）

推荐断点：

对应 Lab：

- `SpringCoreAopLabTest#selfInvocationDoesNotTriggerAdviceForInnerMethod`
- `SpringCoreAopExerciseTest#exercise_makeSelfInvocationTriggerAdvice`（开启 exposeProxy 后对比）

推荐断点：

对应 Lab：

- `SpringCoreAopPointcutExpressionsLabTest`（重点：this vs target 在 JDK/CGLIB 下的差异）

对应 Lab：

- `SpringCoreAopMultiProxyStackingLabTest`

- 多代理叠加与顺序：见 [09. multi-proxy-stacking](../part-03-proxy-stacking/038-09-multi-proxy-stacking.md)
- 真实项目叠加 Debug Playbook：见 [10. real-world-stacking-playbook](../part-03-proxy-stacking/039-10-real-world-stacking-playbook.md)

跑：`SpringCoreAopLabTest#tracedBusinessServiceIsAnAopProxy`

- 你能在断点里看到：目标对象 → 经过 AutoProxyCreator → 最终变成 proxy
- 你能解释：为什么最终注入到业务里的 bean 可能不是你写的那个 class

跑：`SpringCoreAopProxyMechanicsLabTest#adviceOrderingCanBeControlledWithOrderAnnotation`

跑：`SpringCoreAopExerciseTest#exercise_changePointcutStyle`

跑：`SpringCoreAopPointcutExpressionsLabTest`

跑：`SpringCoreAopMultiProxyStackingLabTest`

跑：`SpringCoreAopRealWorldStackingLabTest`

- 你能用断言证明：未授权会被阻断、缓存命中会短路、目标方法内事务处于激活状态
- 你能在断点里复述：链条组装 → proceed 嵌套 → 哪一层负责鉴权/缓存/事务

- 你知道 proxy 在容器的哪个阶段产生（BPP after-init）
- 你知道 advice 链执行的入口与关键断点
- 你能把“不拦截”的问题分流成：call path / 匹配 / 代理限制 三大类
- 你能进一步识别：单 proxy 多 advisors vs 多层 proxy（套娃），并知道该去哪组断点验证

## 常见坑与边界

1. **代理是怎么产生的？（容器阶段）**
   - AOP 不是“改你写的类”，而是在创建 bean 时由 `BeanPostProcessor` 把它包装成 proxy。
2. **advice 链是怎么执行的？（调用阶段）**
   - 代理对象接管方法调用 → 组装拦截器链 → `proceed()` 一层层执行。
3. **为什么我以为会拦截，结果没拦截？（匹配与边界）**
   - call path 是否走 proxy
   - pointcut 是否命中
   - 代理类型与语言限制（JDK/CGLIB、final/private/self-invocation）

## 小结与下一章

- `AbstractApplicationContext#refresh`（启动主线）
  - 注册并排序 BPP：`PostProcessorRegistrationDelegate#registerBeanPostProcessors`
  - 创建 bean：`AbstractAutowireCapableBeanFactory#doCreateBean`
    - 初始化：`AbstractAutowireCapableBeanFactory#initializeBean`
      - BPP after-init：`BeanPostProcessor#postProcessAfterInitialization`
        - AOP 关键入口：`AbstractAutoProxyCreator#postProcessAfterInitialization`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopLabTest` / `SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopProxyMechanicsLabTest` / `SpringCoreAopProceedNestingLabTest` / `SpringCoreAopPointcutExpressionsLabTest` / `SpringCoreAopRealWorldStackingLabTest`
- Exercise：`SpringCoreAopExerciseTest`

上一章：[主线时间线](028-03-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[代理心智模型](../part-01-proxy-fundamentals/030-01-aop-proxy-mental-model.md)

<!-- BOOKIFY:END -->
