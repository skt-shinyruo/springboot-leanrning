# 04. `final` 与代理限制：为什么 final method 拦截不到？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：`final` 与代理限制：为什么 final method 拦截不到？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
    - 原理：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
    - 源码入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopProxyMechanicsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. 自调用（self-invocation）：为什么 `this.inner()` 不会被拦截？](03-self-invocation.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. exposeProxy：用 `AopContext.currentProxy()` 绕过自调用（进阶）](05-expose-proxy.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「04. `final` 与代理限制：为什么 final method 拦截不到？」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `SpringCoreAopProxyMechanicsLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreAopProxyMechanicsLabTest`

## 机制主线

Spring AOP 的默认实现依赖“代理”，而代理的实现方式会带来一些**语言层面的硬限制**。

## CGLIB 为什么会受 `final` 限制？

CGLIB 代理的核心是：**生成目标类的子类**，然后覆盖（override）目标方法，把 advice 链插进去。

因此：

- `final class`：不能被继承 → 无法创建子类代理
- `final method`：不能被覆盖 → 该方法无法插入 advice
- `private method`：子类看不到/无法覆盖 → 通常也无法被拦截

## 不止 final：你还需要认识的 4 类“天然拦不住”

把下面这几条记住，你会少掉一半“我以为 AOP 失效了”的误判：

1. **private 方法**
   - 无法 override（CGLIB）/不在接口上（JDK）
2. **static 方法**
   - 本质是“类方法”，不属于对象的虚方法分派，AOP 代理很难接管（也不建议这样设计）
3. **构造器与构造期内部调用**
   - bean 还没被 BPP 换成 proxy 前，构造器里发生的调用不可能被 AOP 拦截
   - 同理，很多初始化阶段（尤其是对象内部 `this.xxx()`）也容易让你误判
4. **同类内部调用（self-invocation）**
   - 不是语言限制，但效果类似：不走 proxy 就不拦截（见 [03. self-invocation](03-self-invocation.md)）

它的关键断言是：

- 调用 `nonFinal(...)` 会记录一次（被拦截）
- 再调用 `finalMethod(...)` 记录次数仍然不变（final method 没被拦截）

如果你想同时理解“代理类型与限制”，建议顺序跑：

## 学习仓库里你应该怎么用这个结论？

1) 不要把“需要被 AOP/Tx/Validation 拦截的方法”写成 final  
2) 如果你更喜欢使用 `final`（例如偏函数式/不可变风格），那就更推荐：
   - 通过接口 + JDK 代理（拦截接口方法），或
   - 避免依赖基于代理的拦截（在学习仓库里先理解机制，再谈取舍）

你会发现这样设计以后：

- “我给方法加了注解，但为什么完全没效果？”  
  很多时候不是注解没生效，而是 **这个方法从代理角度根本拦不住**（final/private/self-invocation）。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreAopProxyMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证

看测试：`SpringCoreAopProxyMechanicsLabTest#finalMethodsAreNotInterceptedByCglibProxies`

- `SpringCoreAopProxyMechanicsLabTest#jdkDynamicProxyIsUsedForInterfaceBasedBeans_whenProxyTargetClassIsFalse`
- `SpringCoreAopProxyMechanicsLabTest#cglibProxyIsUsedForClassBasedBeans_whenProxyTargetClassIsTrue`
- `SpringCoreAopProxyMechanicsLabTest#finalMethodsAreNotInterceptedByCglibProxies`

## 常见坑与边界

### 一个更实用的工程建议：把“可被拦截”的逻辑放在 public 的边界方法上

在真实项目里，你最需要被拦截的通常是“业务边界方法”（service public 方法）：

- 权限/审计/事务/缓存一般都挂在边界方法上
- 内部私有方法更多是实现细节，不要依赖 AOP 去“拦住它”

- AOP 的边界更清晰
- 自调用/私有方法/final 等限制影响更小

## 常见误区


## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopProxyMechanicsLabTest`

上一章：[03-self-invocation](03-self-invocation.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05-expose-proxy](05-expose-proxy.md)

<!-- BOOKIFY:END -->
