# 第 34 章：05. exposeProxy：用 `AopContext.currentProxy()` 绕过自调用（进阶）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：exposeProxy：用 `AopContext.currentProxy()` 绕过自调用（进阶）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
    - 原理：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
    - 源码入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopExposeProxyLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 33 章：04. `final` 与代理限制：为什么 final method 拦截不到？](033-04-final-and-proxy-limits.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 35 章：06. Debug / 观察：如何“看见”代理与切点](035-06-debugging.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. exposeProxy：用 `AopContext.currentProxy()` 绕过自调用（进阶）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreAopExposeProxyLabTest` / `SpringCoreAopLabTest`

## 机制主线

这一章的目标不是鼓励你在项目里大量使用 `AopContext`，而是让你把“代理 = 调用入口”这个概念吃透。

## 解决的是什么问题？

自调用绕过代理（见 [03. self-invocation](032-03-self-invocation.md)）：

- `outer()` 内部调用 `inner()` → 不走 proxy → `inner()` 不被拦截

如果你能在 `outer()` 内部拿到“当前代理对象”，就可以改成：

- `((SelfInvocationExampleService) AopContext.currentProxy()).inner(...)`

这样 `inner(...)` 就会再次走代理链。

## 关键点：`AopContext.currentProxy()` 不是随时可用

它只有在满足两个条件时才工作：

1. **必须开启 exposeProxy**
   - Spring：`@EnableAspectJAutoProxy(exposeProxy = true)`
   - Spring Boot：`application.properties` 里设置 `spring.aop.expose-proxy=true`

2. **必须在 AOP 调用链上下文中调用**
   - 也就是：你需要先进入一个被 AOP 拦截的方法（在 advice 链里），此时 `currentProxy()` 才有意义

### 1) 为什么它会“只有在 advice 链里才可用”？

你可以把它理解成：AOP 在执行 advice 链时会把“当前代理”放进一个 thread-local 里。

所以：

- 没有进入 advice 链 → thread-local 没被设置 → 取不到 currentProxy
- 换线程（例如 `@Async`） → thread-local 不会自动传播 → 也可能取不到/取错

## 在本模块的练习入口

- 它提示你开启 exposeProxy，并在 `outer(...)` 内通过 `AopContext.currentProxy()` 调用 `inner(...)`
- 这是一个很好的“理解机制”练习

## 代价与取舍（必须知道）

- 可读性：`AopContext` 会把代码和 AOP 强绑定，不如“抽出到另一个 bean”清晰
- 线程绑定：`currentProxy()` 基于当前线程上下文（更容易产生隐式依赖）

所以在真实项目里更推荐：

> 把需要被拦截的逻辑抽到另一个 Spring Bean，通过注入调用。

### 一个更工程化的替代方案：自注入（或 ObjectProvider）

如果你确实需要“在同一个类里触发 AOP”，更推荐的写法通常是：

- 让类依赖自己（注入自己这个 bean），必要时配合 `@Lazy` 来避免循环依赖
- 或注入 `ObjectProvider<SelfInvocationExampleService>`，在需要时再获取 proxy 并调用

它们的共同点是：

- 仍然走“通过容器拿到的 bean 引用”，因此会经过 proxy
- 不依赖 `AopContext` 的 thread-local 语义

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreAopExposeProxyLabTest` / `SpringCoreAopLabTest`
- 建议命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

这也是为什么它更像一个“调试/理解机制”的工具，而不是日常业务代码的默认选择。

看 `SpringCoreAopExerciseTest#exercise_makeSelfInvocationTriggerAdvice`：

## 常见坑与边界

### 坑点 1：以为 `AopContext.currentProxy()` “随时可用”，结果线上偶发 NPE/IllegalState

- Symptom：你在方法里调用 `AopContext.currentProxy()`，在某些路径上直接抛异常（或拿不到代理）
- Root Cause：
  - 没有开启 exposeProxy（没有把 proxy 放进 thread-local）
  - 或者当前调用不在 AOP 调用链里（根本没进入 advice）
- Verification：`SpringCoreAopExposeProxyLabTest#exposeProxyAllowsSelfInvocationToTriggerAdvice`
- Fix：工程上优先把被拦截逻辑拆到另一个 bean，通过注入跨 bean 调用；`AopContext` 只作为理解机制/调试手段谨慎使用

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopExposeProxyLabTest` / `SpringCoreAopLabTest`
- Exercise：`SpringCoreAopExerciseTest`

上一章：[04-final-and-proxy-limits](033-04-final-and-proxy-limits.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06-debugging](035-06-debugging.md)

<!-- BOOKIFY:END -->
