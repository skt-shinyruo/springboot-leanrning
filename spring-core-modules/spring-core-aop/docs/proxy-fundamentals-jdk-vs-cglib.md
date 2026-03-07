# 02. JDK vs CGLIB：代理类型与“可注入类型”差异
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕JDK vs CGLIB：代理类型与“可注入类型”差异展开，主线可以概括为：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。

    先运行 `SpringCoreAopProxyMechanicsLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。

    需要下探源码时，可以从 `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. AOP 心智模型：代理（Proxy）+ 入口（Call Path）](proxy-fundamentals-aop-proxy-mental-model.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. 自调用（self-invocation）：为什么 `this.inner()` 不会被拦截？](proxy-fundamentals-self-invocation.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「02. JDK vs CGLIB：代理类型与“可注入类型”差异」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreAopProxyMechanicsLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreAopProxyMechanicsLabTest`

## 机制主线

Spring AOP 最容易让人困惑的一点是：**代理类型不同，会直接改变“能不能按某个类型拿到 bean”**。

## 两种代理的核心差异

### 1) JDK 动态代理（interface-based）

- 代理对象 **实现接口**，但不会继承实现类
- 优点：不需要 CGLIB；对 final 限制更少（因为不靠继承）
- 典型现象：能按接口类型注入/获取，但 **按实现类类型可能拿不到**

### 2) CGLIB 代理（class-based）

- 代理对象是目标类的 **子类**（通过继承生成）
- 优点：即使没有接口也能代理
- 代价：受到继承规则限制（`final` 类/方法相关，见 [04. final-and-proxy-limits](proxy-fundamentals-final-and-proxy-limits.md)）

## 选择策略：Spring/Boot 到底怎么决定用哪一种？

可以把“选择策略”理解成两句话：

1. **默认倾向：有接口就优先走 JDK proxy（更轻量、语义也更明确）**
2. **如果强制 proxyTargetClass=true，就走 CGLIB（即使有接口）**

常见配置入口：

- Spring（纯容器）：`@EnableAspectJAutoProxy(proxyTargetClass = true/false)`
- Spring Boot：`application.properties` 可设置 `spring.aop.proxy-target-class=true/false`

- `@EnableAspectJAutoProxy(proxyTargetClass = false)`：倾向使用 JDK 代理
- `@EnableAspectJAutoProxy(proxyTargetClass = true)`：强制使用 CGLIB 代理

- `jdkDynamicProxyIsUsedForInterfaceBasedBeans_whenProxyTargetClassIsFalse`
  - `AopUtils.isJdkDynamicProxy(greeter)` 为 true
  - `context.getBean(PlainGreeter.class)` 会失败（按实现类拿不到）
- `cglibProxyIsUsedForClassBasedBeans_whenProxyTargetClassIsTrue`
  - `AopUtils.isCglibProxy(greeter)` 为 true

## 为什么“按实现类拿不到”？

当 Spring 使用 JDK 代理时，最终注册到容器里的对象类型是一个 `com.sun.proxy.$ProxyXX`。
它只“长得像”接口，不是实现类的子类，因此按实现类类型查找会找不到。

## 源码锚点：JDK vs CGLIB 的分岔点在哪里？

- `DefaultAopProxyFactory#createAopProxy`

会看到它根据 `AdvisedSupport` 的配置以及目标类型（是否有接口等）来选择：

- `JdkDynamicAopProxy`
- `CglibAopProxy`

### 推荐观察点（watch list）

- `advisedSupport.isOptimize()` / `advisedSupport.isProxyTargetClass()`（是否强制 class-based）
- `AopProxyUtils.completeProxiedInterfaces(...)`（JDK proxy 代理了哪些接口）
- `AopUtils.isJdkDynamicProxy(bean)` / `AopUtils.isCglibProxy(bean)`（最终结果）

## 实战建议（学习仓库里也适用）

- **想要最稳定的注入方式**：定义接口，用接口注入（能直观看到“JDK 代理”的语义）
- **想要最少样板代码**：没有接口也可以，但要理解更可能得到 CGLIB 代理（以及它的限制）

当看到 `com.sun.proxy.$ProxyXX` 或 `$$SpringCGLIB$$` 时：

- 这不是“Spring 魔法把目标类改坏了”，而是 proxy 的正常形态
- 想看真实目标类型，优先用：
  - `AopProxyUtils.ultimateTargetClass(bean)`
  - 或 `AopUtils.getTargetClass(bean)`

## 最小可运行实验（Lab）

- Lab：`SpringCoreAopProxyMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

## 在本模块如何验证

重点看 `SpringCoreAopProxyMechanicsLabTest` 里的两段配置：

对应断言（建议逐个断点跟进）：

如果想在断点里亲眼看到“它是怎么选的”，建议打在：

### 额外提醒：调试时“看 class”不要看花眼

## 常见坑与边界

> 注意：真实项目里经常并不是“显式启用 AOP”，而是某个 starter（事务/缓存/安全等）带来了代理；此时理解 Boot 的默认策略非常关键。

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopProxyMechanicsLabTest`

上一章：[01-aop-proxy-mental-model](proxy-fundamentals-aop-proxy-mental-model.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03-self-invocation](proxy-fundamentals-self-invocation.md)

<!-- BOOKIFY:END -->
