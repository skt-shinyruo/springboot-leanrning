# 03. 除 `@EnableAspectJAutoProxy` 之外：BeanNameAutoProxyCreator / ProxyFactoryBean / XML
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕除 `@EnableAspectJAutoProxy` 之外：BeanNameAutoProxyCreator / ProxyFactoryBean / XML 展开，主线可以概括为：Spring AOP 的“代理生成”并不只有注解这一条路；无论入口是 AutoProxyCreator、BeanNameAutoProxyCreator、ProxyFactoryBean 还是 XML `<aop:config>`，最终都会落到相同的三件事：选择 advisors → 创建 proxy → 执行拦截器链。

    先运行 `SpringCoreAopBeanNameAutoProxyCreatorLabTest` 与 `SpringCoreAopXmlAopConfigLabTest`，把“不同入口如何产出 proxy”固化成断言；再回到正文建立迁移性：遇到遗留项目/框架集成时，能快速识别 AOP 是通过哪条入口生效的。

    需要下探源码时，可以从 `org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator` / `org.springframework.aop.framework.ProxyFactoryBean` / `org.springframework.aop.config.AopNamespaceUtils` 这些入口切入。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. Pointcut 表达式系统：execution/within/this/target/args/@annotation/...（以及常见误判）](autoproxy-and-pointcuts-pointcut-expression-system.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. `@Aspect` 实例模型：singleton vs perthis/pertarget/pertypewithin（Spring AOP 语境）](autoproxy-and-pointcuts-aspect-instantiation-models.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

“会写 `@Aspect`”不等于“能排障遗留项目/框架集成”。

真实项目里常见的入口包括：

- 没有 `@EnableAspectJAutoProxy`，但 AOP 依然生效（可能是 Boot、XML、或其它 AutoProxyCreator）
- 没有 `@Aspect`，但有一堆 `Advisor`/`MethodInterceptor`（这同样是 AOP）
- 看到 `ProxyFactoryBean`/`BeanNameAutoProxyCreator`，却不知道它们与主线的关系

本章收束点：把这些入口全部收敛到同一套心智模型。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreAopBeanNameAutoProxyCreatorLabTest`
    - Lab：`SpringCoreAopXmlAopConfigLabTest`

## 1) 入口清单（按“在项目里最可能看到什么”排列）

### 1.1 注解入口（本模块主线）

- `@EnableAspectJAutoProxy` + `@Aspect`
- Spring Boot：`spring-boot-starter-aop` 自动开启（以实际配置为准）

### 1.2 按 beanName 规则代理：`BeanNameAutoProxyCreator`

特点：

- 不依赖 `@Aspect`
- 为它一组 beanName pattern + interceptor/advisor 名称，它就会代理匹配的 bean

适用：

- 框架集成、遗留系统、快速“按名字批量包一层”

### 1.3 手工声明一个代理 bean：`ProxyFactoryBean`

特点：

- 显式声明“这个 bean 就是一个 proxy”
- target / advisors 都是配置出来的

适用：

- 需要精确控制某一个 bean 的代理形态（包括 TargetSource、interfaces 等）

### 1.4 XML `<aop:config>` / `<aop:aspectj-autoproxy>`

特点：

- 常见于更老的 Spring 项目，或 XML 配置占主导的系统
- 最容易出现“表面上没写 AOP，但实际上有”这种误判

## 2) 统一归一：它们最后都做了哪三件事？

无论入口是什么，都可以用下面三问收敛：

1. **这个 bean 最终是不是 proxy？**（`AopUtils.isAopProxy`）
2. **proxy 上挂了哪些 advisors？**（`((Advised) bean).getAdvisors()`）
3. **这次调用的拦截器链是什么？**（`interceptorsAndDynamicMethodMatchers` + `proceed()`）

当能把“入口差异”压缩到这三问，排障会快很多。

## 3) 常见坑与边界

- 误以为 AOP 只来自 `@Aspect`
  - 实际：Advisor/Interceptor 也能组成完整的 AOP 能力（本模块已有多处 Lab 用纯 Advisor 演示）。
- 误以为 XML 不会出现在现代项目里
  - 实际：依赖链/旧模块/第三方组件可能仍带 XML AOP 配置。

## 小结与下一章

- 入口可以不同，但落点一致：advisors → proxy → chain。
- 下一章进入 @Aspect 实例模型：perthis/pertarget/pertypewithin 在 Spring AOP 语境下到底意味着什么。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreAopBeanNameAutoProxyCreatorLabTest`
- Lab：`SpringCoreAopXmlAopConfigLabTest`

上一章：[08-pointcut-expression-system](autoproxy-and-pointcuts-pointcut-expression-system.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04-aspect-instantiation-models](autoproxy-and-pointcuts-aspect-instantiation-models.md)

<!-- BOOKIFY:END -->

