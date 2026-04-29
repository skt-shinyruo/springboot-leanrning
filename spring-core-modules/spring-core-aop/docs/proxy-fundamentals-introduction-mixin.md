# 08. Introduction / Mixin：@DeclareParents 给 Proxy“加接口能力”
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕Introduction / Mixin：@DeclareParents 给 Proxy“加接口能力”展开，主线可以概括为：Spring AOP 的 proxy 不仅能“拦截方法”（advice），还可以“额外实现接口”（introduction）；`@DeclareParents` 会生成一个 `IntroductionAdvisor`，让匹配的 bean 的 proxy 具备新的接口方法。

    先运行 `SpringCoreAopIntroductionDeclareParentsLabTest`，把“一个原本不实现接口的 bean，为什么能 `instanceof NewInterface`”固化成断言；再回到正文理解：它改变的是 proxy 的类型能力，而不是 target 的 class。

    需要下探源码时，可以从 `org.springframework.aop.aspectj.DeclareParentsAdvisor` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.AdvisedSupport#setInterfaces` 这些入口切入。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[07. Advice 全家桶：@Before/@After/@AfterReturning/@AfterThrowing（语义与绑定）](proxy-fundamentals-advice-types-and-binding.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[09. TargetSource：Proxy 到底转发到谁（LazyInit/HotSwap…）](proxy-fundamentals-targetsource-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

Introduction 的典型误区是：以为它也是“拦截方法”。

更准确的说法是：

> **Introduction 解决的是“类型能力”问题**：让 proxy 额外实现一个接口（以及该接口的方法实现）。

这类能力常见于：

- 给一批 bean 加一个“能力接口”（比如 `Auditable` / `Resettable` / `Versioned`）
- 给 legacy 类型加一个适配层（不改原类）

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreAopIntroductionDeclareParentsLabTest`

## 1) proxy vs target：Introduction 改的是谁？

一句话：

- **target 的 class 不变**
- **proxy 的 interfaces 变了**

因此排查时优先断言：

- `bean instanceof NewInterface`（proxy 侧能力）
- `AopProxyUtils.ultimateTargetClass(bean)`（target 侧类型）

## 2) `@DeclareParents` 的最小写法

`@DeclareParents` 写在 `@Aspect` 的字段上：

- 字段类型：需要引入的接口
- `value`：要匹配的目标类型 pattern
- `defaultImpl`：接口的默认实现类

本质产物是一个 `IntroductionAdvisor`（可以在 `((Advised) bean).getAdvisors()` 里看到）。

## 3) 常见坑与边界

1. **以为引入能“加方法到类本身”**
   - 结论：加的是接口能力；外部必须通过“接口类型”调用新增方法（或做显式转型）。
2. **以为引入能绕过 call path**
   - 结论：Introduction 仍然发生在 proxy 上；绕过 proxy 依然无效。
3. **匹配 pattern 写错**
   - 现象：没有 `instanceof NewInterface`，以为 AOP 不生效；实际是 introduction 没命中目标类型。

## 4) 断点入口（可选）

- `ReflectiveAspectJAdvisorFactory#getDeclareParentsAdvisor`（`@DeclareParents` → advisor）
- `ProxyFactory#setInterfaces`（proxy 接口列表如何被组装）

## 小结与下一章

- Introduction 是“改 proxy 的接口能力”，不是“拦截方法”。
- 下一章进入 TargetSource：proxy 到底把调用转发到哪个 target（target 甚至可能会变）。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreAopIntroductionDeclareParentsLabTest`

上一章：[07-advice-types](proxy-fundamentals-advice-types-and-binding.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[09-targetsource](proxy-fundamentals-targetsource-model.md)

<!-- BOOKIFY:END -->

