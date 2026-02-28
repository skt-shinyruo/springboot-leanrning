# 01. AOP 心智模型：代理（Proxy）+ 入口（Call Path）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕AOP 心智模型：代理（Proxy）+ 入口（Call Path）展开，主线可以概括为：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。

    先运行 `SpringCoreAopLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。

    需要下探源码时，可以从 `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南：把“代理是怎么来的、advice 链怎么跑”落到源码与断点](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. JDK vs CGLIB：代理类型与“可注入类型”差异](02-jdk-vs-cglib.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「01. AOP 心智模型：代理（Proxy）+ 入口（Call Path）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreAopLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! tip "前置：把“代理替换发生在哪个阶段”放回容器主线（建议只读一次）"

    很多读者在 AOP 里卡住的根因是：对 **Bean 创建阶段** 没有稳定心智模型（什么时候允许“换对象”、early reference 如何参与循环依赖）。

    - Beans 基础问题索引（Why Index）：`../../../spring-core-beans/docs/part-00-guide/01-why-index.md`
    - Beans：代理替换发生在哪个阶段（exposed object / BPP 替身对象）：`../../../spring-core-beans/docs/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`
    - Beans：early reference 与循环依赖（raw vs wrapped 一致性）：`../../../spring-core-beans/docs/part-03-container-internals/05-early-reference-and-circular.md`

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreAopLabTest`

## 机制主线

Spring AOP 学习最关键的不是“会写一个 `@Aspect`”，而是建立一个稳定的心智模型：

> **AOP 默认基于代理实现**：只有“通过代理对象发起的调用”才会被 advice 包起来。

## 需要记住的 3 件事

1. **代理对象可能不是目标对象（Proxy ≠ Target）**
   - 注入/拿到的 bean 可能是一个 proxy，而不是实现类本身。
   - 这会影响：类型判断（`instanceof`）、`getClass()`、以及调试时看到的调用栈。
2. **advice 生效的前提：调用必须“经过代理”（Call Path）**
   - 外部（其它 bean / Controller / Test）调用：通常会经过 proxy → advice 生效。
   - 同类内部 `this.xxx()` 调用：不会经过 proxy → advice 不生效（见 [03. self-invocation](03-self-invocation.md)）。
3. **AOP 的本质是“在调用链上插一段逻辑”**
   - `@Around` 最直观：`joinPoint.proceed()` 前后都能做事（计时、鉴权、日志、事务等）。

## 2. 容器视角：AOP 不是“改类”，而是 BPP 把 Bean 换成 Proxy

当把 AOP 放回到 IoC 容器里看，会更清晰：

- Bean 的创建过程里会执行很多 `BeanPostProcessor`
- AOP 的关键基础设施（AutoProxyCreator）就是一个 **BeanPostProcessor**
- 它会在初始化后阶段判断：这个 bean 是否需要被增强？如果需要，就 **返回一个 proxy** 作为“最终暴露的 bean”

这也是为什么：

> 深挖入口：如果想在源码里“看见”这一段，建议先读 [00. 深挖指南](../part-00-guide/02-deep-dive-guide.md)。

重点看这些断言：

- proxy 产生（容器阶段）：
  - `AbstractAutoProxyCreator#postProcessAfterInitialization`
  - `AbstractAutoProxyCreator#wrapIfNecessary`
- advice 链执行（调用阶段）：
  - `JdkDynamicAopProxy#invoke` / `CglibAopProxy.DynamicAdvisedInterceptor#intercept`
  - `ReflectiveMethodInvocation#proceed`

### 推荐观察点（watch list）

- `beanName`：当前观察哪个 bean 的代理决策
- `AopUtils.isAopProxy(bean)` / `AopUtils.isJdkDynamicProxy(bean)` / `AopUtils.isCglibProxy(bean)`：最终形态
- `interceptorsAndDynamicMethodMatchers`：这次调用的拦截器链条（顺序非常关键）

可以按这个顺序跑（每个都能快速闭环）：

1. `SpringCoreAopLabTest#tracedBusinessServiceIsAnAopProxy`：确认拿到的是 proxy
2. `SpringCoreAopLabTest#adviceIsAppliedToTracedMethod`：确认 advice 真的执行
3. `SpringCoreAopLabTest#selfInvocationDoesNotTriggerAdviceForInnerMethod`：确认 call path 决定生效与否

## 对照代码（最小闭环）

- `spring-core-modules/spring-core-aop/src/main/java/.../Traced.java`：标记注解
- `spring-core-modules/spring-core-aop/src/main/java/.../TracingAspect.java`：`@Around("@annotation(...@Traced)")`
- `spring-core-modules/spring-core-aop/src/main/java/.../TracedBusinessService.java`：被拦截的目标方法
- `spring-core-modules/spring-core-aop/src/main/java/.../InvocationLog.java`：用来做“可断言的观察点”（比日志更稳定）

当觉得 AOP “没生效”时，先不要怀疑注解/表达式，第一时间问自己：

> **这次调用有没有走代理？**

## 排障分流：这是调用路径问题，还是匹配/限制问题？

当遇到 “AOP 没生效” 的问题，建议先用分流思路避免走弯路：

把问题落到这三类里，就会发现排障会稳定很多。

## 下一步（把理解推进到“源码级可复述”）

- 想看清 AOP 作为容器扩展点的主线：AutoProxyCreator/Advisor/Advice/Pointcut → 见 [07. autoproxy-creator-mainline](../part-02-autoproxy-and-pointcuts/01-autoproxy-creator-mainline.md)
- 想系统掌握 pointcut 表达式并避免误判（execution/within/this/target/...）→ 见 [08. pointcut-expression-system](../part-02-autoproxy-and-pointcuts/02-pointcut-expression-system.md)
- 想看懂“多切面/多代理叠加与顺序”（AOP/Tx/Cache/Security）→ 见 [09. multi-proxy-stacking](../part-03-proxy-stacking/01-multi-proxy-stacking.md)

## 最小可运行实验（Lab）

- Lab：`SpringCoreAopLabTest`
- 建议命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

1. **代理对象 ≠ 目标对象**
   - 注入的 bean 很可能是一个“包装对象”（proxy），而不是实现类本身。
   - 这会影响：类型判断（`instanceof`）、`getClass()`、调试时看到的调用栈。

2. **advice 生效的前提：调用必须“经过代理”**
   - 外部（其它 bean / Controller / Test）调用 bean 方法：通常会经过代理 → advice 生效。
   - 同一个类内部 `this.xxx()` 调用：不会经过代理 → advice **不生效**（见 [03. self-invocation](03-self-invocation.md)）。

- 注入到业务里的对象可能不是所写的实现类
- `getClass()`/类型匹配/调试栈会出现业务代码里没写过的 proxy 类
- 同一套 proxy 限制会同时影响 AOP、事务（`@Transactional`）、缓存（`@Cacheable`）等（它们底层都离不开 proxy）

## 在本模块如何验证（先运行这个）

运行测试：

```bash
mvn -pl :spring-core-aop test
```

- `SpringCoreAopLabTest#tracedBusinessServiceIsAnAopProxy`：证明注入的 bean 是代理
- `SpringCoreAopLabTest#adviceIsAppliedToTracedMethod`：证明 `@Traced` 方法被 `TracingAspect` 拦截

## 源码锚点（建议从这里下断点）

如果只想抓主线，不想在 AOP 源码里迷路，这几个断点足够覆盖 80% 的理解与排障：

## 断点闭环（用本仓库 Lab/Test 跑一遍）

1. `SpringCoreAopLabTest#tracedBusinessServiceIsAnAopProxy`：先确认“拿到的是 proxy”
2. `SpringCoreAopLabTest#adviceIsAppliedToTracedMethod`：再确认“advice 确实包住了方法调用”
3. `SpringCoreAopLabTest#selfInvocationDoesNotTriggerAdviceForInnerMethod`：最后确认“call path 决定是否拦截”

## 常见坑与边界

1. **调用路径（call path）**
   - 是否自调用？是否绕过 Spring 容器（`new` 出来/静态方法）？
2. **匹配（pointcut）**
   - 切点是否命中？是否命中到了以为的方法？
3. **代理限制（proxy limits）**
   - final/private/static/构造期调用 等边界是否踩中？

## 小结与下一章
<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：AOP 心智模型：代理（Proxy）+ 入口（Call Path） —— 先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
- 回到主线：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopLabTest`

上一章：[00-deep-dive-guide](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02-jdk-vs-cglib](02-jdk-vs-cglib.md)

<!-- BOOKIFY:END -->
