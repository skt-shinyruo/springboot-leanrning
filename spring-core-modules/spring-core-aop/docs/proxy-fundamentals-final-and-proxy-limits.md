# 04. `final` 与代理限制：为什么 final method 拦截不到？
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕`final` 与代理限制：为什么 final method 拦截不到？展开，主线可以概括为：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。

    先运行 `SpringCoreAopProxyMechanicsLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。

    需要下探源码时，可以从 `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. 自调用（self-invocation）：为什么 `this.inner()` 不会被拦截？](proxy-fundamentals-self-invocation.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. exposeProxy：用 `AopContext.currentProxy()` 绕过自调用（进阶）](proxy-fundamentals-expose-proxy.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「04. `final` 与代理限制：为什么 final method 拦截不到？」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreAopProxyMechanicsLabTest`（或文末“对应实验/测试”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreAopProxyMechanicsLabTest`

## 机制主线

Spring AOP 的默认实现依赖“代理”，而代理的实现方式会带来一些**语言层面的硬限制**。

## CGLIB 为什么会受 `final` 限制？

CGLIB 代理的核心是：**生成目标类的子类**，然后覆盖（override）目标方法，把 advice 链插进去。

因此：

- `final class`：不能被继承 → 无法创建子类代理
- `final method`：不能被覆盖 → 该方法无法插入 advice
- `private method`：子类看不到/无法覆盖 → 通常也无法被拦截

## 不止 final：还需要认识的 4 类“天然拦不住”

把下面这几条记住，会少掉一半“预期 AOP 失效了”的误判：

1. **private 方法**
   - 无法 override（CGLIB）/不在接口上（JDK）
2. **static 方法**
   - 本质是“类方法”，不属于对象的虚方法分派，AOP 代理很难接管（也不宜这样设计）
3. **构造器与构造期内部调用**
   - bean 还没被 BPP 换成 proxy 前，构造器里发生的调用不可能被 AOP 拦截
   - 同理，很多初始化阶段（尤其是对象内部 `this.xxx()`）也容易造成误判
4. **同类内部调用（self-invocation）**
   - 不是语言限制，但效果类似：不走 proxy 就不拦截（见 [03. self-invocation](proxy-fundamentals-self-invocation.md)）

它的关键断言是：

- 调用 `nonFinal(...)` 会记录一次（被拦截）
- 再调用 `finalMethod(...)` 记录次数仍然不变（final method 没被拦截）

如果想同时理解“代理类型与限制”，按顺序运行：

## 学习仓库里应当怎么用这个结论？

1. 不要把“需要被 AOP/Tx/Validation 拦截的方法”写成 final
2. 如果更喜欢使用 `final`（例如偏函数式/不可变风格），那就更稳妥的做法：
   - 通过接口 + JDK 代理（拦截接口方法），或
   - 避免依赖基于代理的拦截（在学习仓库里先理解机制，再谈取舍）

会发现这样设计以后：

- “方法已加注解，为什么完全没有效果？”
  很多时候不是注解没生效，而是 **这个方法从代理角度根本拦不住**（final/private/self-invocation）。

## 最小可运行实验（Lab）

- Lab：`SpringCoreAopProxyMechanicsLabTest`
- 运行命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

## 在本模块如何验证

看测试：`SpringCoreAopProxyMechanicsLabTest#finalMethodsAreNotInterceptedByCglibProxies`

- `SpringCoreAopProxyMechanicsLabTest#jdkDynamicProxyIsUsedForInterfaceBasedBeans_whenProxyTargetClassIsFalse`
- `SpringCoreAopProxyMechanicsLabTest#cglibProxyIsUsedForClassBasedBeans_whenProxyTargetClassIsTrue`
- `SpringCoreAopProxyMechanicsLabTest#finalMethodsAreNotInterceptedByCglibProxies`

## 常见坑与边界

### 一个更实用的工程动作：把“可被拦截”的逻辑放在 public 的边界方法上

在真实项目里，最需要被拦截的通常是“业务边界方法”（service public 方法）：

- 权限/审计/事务/缓存一般都挂在边界方法上
- 内部私有方法更多是实现细节，不要依赖 AOP 去“拦住它”

- AOP 的边界更清晰
- 自调用/私有方法/final 等限制影响更小

## 常见误区


## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreAopProxyMechanicsLabTest`

上一章：[03-self-invocation](proxy-fundamentals-self-invocation.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05-expose-proxy](proxy-fundamentals-expose-proxy.md)

<!-- BOOKIFY:END -->
