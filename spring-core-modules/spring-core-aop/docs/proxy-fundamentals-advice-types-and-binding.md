# 07. Advice 全家桶：@Before/@After/@AfterReturning/@AfterThrowing（语义与绑定）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕 Advice 全家桶：@Before/@After/@AfterReturning/@AfterThrowing（语义与绑定）展开，主线可以概括为：Spring AOP 把 advice 适配成拦截器链；不同 advice 类型只是“插入点不同”；参数绑定（args/@annotation/returning/throwing/JoinPoint）决定“在 advice 里能看到什么”。

    先运行 `SpringCoreAopAdviceTypesAndBindingLabTest`，把“正常返回 vs 抛异常”两条路径的执行顺序与绑定结果固化成断言；再回到正文理解：哪些 advice 必定执行、哪些只在成功/失败时执行，以及绑定失败时为什么会表现为“切面不生效/参数为 null/启动时报错”。

    需要下探源码时，可以从 `org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvice` / `org.springframework.aop.aspectj.AbstractAspectJAdvice#calculateArgumentBindings` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed` 这些入口切入。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[06. Debug / 观察：如何“看见”代理与切点](proxy-fundamentals-debugging.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[08. Introduction / Mixin：@DeclareParents 给 Proxy“加接口能力”](proxy-fundamentals-introduction-mixin.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章解决一类特别常见的误判：

- “我写了切面，为什么没进 advice？”（其实进了，但绑定条件不满足/只在异常分支执行）
- “我用 `@AfterReturning(returning="x")`，为什么 x 拿不到值？”（argNames/参数名绑定失败）
- “我用 `args(name)`，为什么 name 为 null 或根本不匹配？”（运行期匹配 + 绑定名不一致）

本章不追求“背注解”，只追求把行为跑成事实。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreAopAdviceTypesAndBindingLabTest`

## 1) 五种 advice：只是在调用链里的“插入点”不同

你可以把它们统一成一张“插入点表”（只看行为，不看名词）：

| 类型 | 什么时候执行 | 能不能拿到返回值 | 能不能拿到异常 | 能不能阻止调用 |
| --- | --- | --- | --- | --- |
| `@Before` | 目标方法执行前 | ❌ | ❌ | ✅（通过抛异常） |
| `@AfterReturning` | 目标方法正常返回后 | ✅（returning 绑定） | ❌ | ❌ |
| `@AfterThrowing` | 目标方法抛异常后 | ❌ | ✅（throwing 绑定） | ❌ |
| `@After` | finally（不管成功/失败） | ❌ | ❌ | ❌ |
| `@Around` | 最外层包裹（可前可后） | ✅（自己决定返回） | ✅（自己决定吞/改/抛） | ✅（不调用 proceed） |

> 本仓库主线大量使用 `@Around`，因为它最直观（`proceed()` 前后都能做事）。本章补齐其它四种的语义与绑定，避免“只会 Around，一换类型就误判”。

## 2) 两条核心对照路径：正常返回 vs 抛异常

把调用分成两条路径，就不容易混：

### 2.1 正常返回（success path）

典型顺序（忽略多个切面叠加，仅看同一切面内的语义）：

1. `@Before`
2. 目标方法
3. `@AfterReturning`
4. `@After`（finally）

### 2.2 抛异常（error path）

1. `@Before`
2. 目标方法抛异常
3. `@AfterThrowing`
4. `@After`（finally）

`@AfterReturning` 不会执行，这是最常见的误判来源之一。

## 3) 绑定（Binding）：决定 “advice 能看到哪些上下文”

### 3.1 `JoinPoint` / `ProceedingJoinPoint`

- `JoinPoint`：可以读到签名、参数，但不能 `proceed()`
- `ProceedingJoinPoint`：只有 `@Around` 能用，能 `proceed()`

### 3.2 `args(...)`：按“运行期参数类型/值”参与匹配 + 绑定

`args(name)` 做了两件事：

1) 参与匹配（可能是运行期匹配，见第 8 章的补充与 Lab）。  
2) 把参数绑定到 advice 的形参（靠参数名/argNames 对齐）。

### 3.3 `@annotation(...)`：绑定方法注解实例

这类绑定常用于：

- 从注解上读开关/级别/策略
- 避免在 advice 里重复写“注解解析”模板代码

### 3.4 `returning` / `throwing`：返回值/异常绑定

两条铁律：

- `returning` 只在 success path 生效
- `throwing` 只在 error path 生效

### 3.5 最容易踩的坑：参数名不可见导致绑定失败

当你写了 `args(name)`、`returning="result"`、`throwing="ex"`，但 advice 方法的参数名在运行期不可见时：

- 有的场景会直接启动失败（绑定计算阶段报错）
- 有的场景会表现为“匹配不到/拿不到绑定值”

因此在“教程/实验”里最稳的写法是：在注解上显式写 `argNames`（本仓库 Lab 会这么做，确保结果不依赖编译器参数）。

## 4) 推荐断点（可选）

想把“绑定失败/绑定成功”看成事实，推荐从这两个入口下断点：

- `ReflectiveAspectJAdvisorFactory#getAdvice`（advice 类型 + returning/throwing 名称）
- `AbstractAspectJAdvice#calculateArgumentBindings`（最终绑定决策）

## 小结与下一章

- Advice 类型差异 = 插入点差异；要避免误判，先按 success/error 两条路径分流。
- 绑定是否成功，决定你在 advice 里能看到什么；不稳定时优先显式写 `argNames`。
- 下一章进入 Introduction：不是拦截，而是“给 proxy 加接口能力”。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopAdviceTypesAndBindingLabTest`

上一章：[06-debugging](proxy-fundamentals-debugging.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[08-introduction-mixin](proxy-fundamentals-introduction-mixin.md)

<!-- BOOKIFY:END -->

