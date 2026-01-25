# 第 29 章：01：AOP 调用链（从代理入口到 Advice 链执行）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：AOP 调用链（从代理入口到 Advice 链执行）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
    - 原理：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
    - 源码入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopProceedNestingLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 29 章：00. 深挖指南：把“代理产生 + advice 链执行”落到源码与断点](029-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 29 章：02：断点地图（AOP Debugger Pack）](029-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01：AOP 调用链（从代理入口到 Advice 链执行）**
- 目标：把“我知道 AOP 是代理”升级为“我能解释代理什么时候产生、调用怎么进代理、Advice 链怎么跑、为什么会绕过”。
- 基线版本：Spring Framework `6.2.15`（本仓库由 Spring Boot `3.5.9` 管理依赖版本）。

!!! summary "本章要点"

    - AOP 有两条链：**生成链（容器启动期）**与**执行链（运行期调用时）**。排障时先判断你卡在哪条链上。
    - Advice 链的核心抓手只有一个：`ReflectiveMethodInvocation#proceed`（它决定了 before/after 的嵌套顺序）。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreAopProceedNestingLabTest`

## 1. 生成链：代理是怎么在容器里产生的？

Spring AOP 默认不是“编译期织入”，而是“运行期代理”。因此你要先回答一个最常见的排障问题：

> 这个 bean 什么时候变成了 proxy？

在 Spring 里，这个问题大多数时候都能回到同一个答案：

- **AutoProxyCreator 是一个 BeanPostProcessor（BPP）**
- 它在 bean 初始化后（after initialization）有机会把 bean 换成 proxy

主线（高层视角）：

1. 容器创建 bean（原始对象）
2. BPP 链执行
3. 轮到 AutoProxyCreator：如果命中 Advisor/Pointcut → `wrapIfNecessary` → 创建 proxy
4. 容器最终暴露的是 proxy（之后注入/获取到的都是 proxy）

你可以优先从这些入口验证“是否走过生成链”：

- `AbstractAutoProxyCreator#postProcessAfterInitialization`
- `AbstractAutoProxyCreator#wrapIfNecessary`

## 2. 执行链：一次方法调用是怎么进入 Advice 链的？

当你拿到的是代理对象时，调用会先进入代理层，再进入拦截器链。

### 2.1 JDK 代理 vs CGLIB 代理：入口不同，但核心相同

两条入口（常见）：

1. **JDK 动态代理**：`JdkDynamicAopProxy#invoke(...)`
2. **CGLIB 代理**：`CglibAopProxy.DynamicAdvisedInterceptor#intercept(...)`

它们最终都会落到同一件事：

- 构造一个 `MethodInvocation`（通常是 `ReflectiveMethodInvocation`）
- 从 `Advised` 上拿到 `interceptorsAndDynamicMethodMatchers`
- 进入 `proceed()` 执行链

### 2.2 Advice 链执行：`proceed()` 嵌套决定 before/after 顺序

核心逻辑（你要能复述出来）：

1. `proceed()` 每调用一次，就推进到链条下一个拦截器
2. “前置逻辑”发生在 `invocation.proceed()` 之前
3. “后置逻辑”发生在 `invocation.proceed()` 之后
4. 最底层会调用目标方法（reflection invoke）

因此你在调试时应该形成这个直觉：

- 多个 around advice 的执行顺序，本质是多次嵌套的 `proceed()`（像递归一样）

对应证据链建议优先用：

- `SpringCoreAopProceedNestingLabTest`（把顺序固化成断言，不靠日志）

## 3. 三个最常见的“为什么没走 AOP”的分叉点

> 这些分叉点不只是概念，它们都能落到“你能验证的入口”。

1. **自调用绕过（self-invocation）**
   - 现象：`this.inner()` 没有被拦截
   - 根因：调用没有经过代理对象
2. **代理类型不匹配（JDK vs CGLIB）**
   - 现象：按实现类注入失败/pointcut 命中差异（this/target）
   - 根因：JDK 代理只代理接口；CGLIB 基于子类
3. **切点根本没命中（Advisor/Pointcut 未匹配）**
   - 现象：bean 有 proxy，但某个方法没有被拦截
   - 根因：表达式/注解匹配范围不对

这些分叉点对应的“推荐断点清单”见下一章的断点地图：

- [02：断点地图（AOP Debugger Pack）](029-02-breakpoint-map.md)

## 小结与下一章

- 本章把 AOP 的“生成链/执行链”串成了一条可复述叙事；下一章把这些入口收敛为 Debugger Pack。

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「01：AOP 调用链（从代理入口到 Advice 链执行）」的生效时机/顺序/边界；断点/入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「01：AOP 调用链（从代理入口到 Advice 链执行）」的生效时机/顺序/边界；断点/入口：`org.springframework.aop.framework.ProxyFactory`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「01：AOP 调用链（从代理入口到 Advice 链执行）」的生效时机/顺序/边界；断点/入口：`org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreAopProceedNestingLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopProceedNestingLabTest`

上一章：[029-02-breakpoint-map.md](029-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[029-04-branch-decision-matrix.md](029-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
