# 03. 自调用（self-invocation）：为什么 `this.inner()` 不会被拦截？
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕自调用（self-invocation）：为什么 `this.inner()` 不会被拦截？展开，主线可以概括为：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。

    先运行 `SpringCoreAopLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。

    需要下探源码时，可以从 `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. JDK vs CGLIB：代理类型与“可注入类型”差异](proxy-fundamentals-jdk-vs-cglib.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. `final` 与代理限制：为什么 final method 拦截不到？](proxy-fundamentals-final-and-proxy-limits.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreAopLabTest`

## 机制主线

看 `SelfInvocationExampleService`：

- `outer(...)` 和 `inner(...)` 都标了 `@Traced`
- `outer(...)` 内部调用 `inner(...)`

- 调用 `selfInvocationExampleService.outer("Bob")`
- 会看到 `InvocationLog` 只记录了一次（只拦截了 `outer`）

## 原因（一句话版本）

> **内部调用没有经过代理对象**，因此不会进入 advice 链。

更精确地说：

- 外部调用：`proxy.outer(...)` → advice → `target.outer(...)`（此时生效）
- `target.outer(...)` 内部：`this.inner(...)` → 直接调用目标对象方法（绕过 proxy）

## 常见解决思路（按“学习成本”排序）

1. **把 `inner(...)` 抽到另一个 Spring Bean**
   - 外部通过注入另一个 bean 调用，调用链自然会走代理
   - 最稳妥（最符合“依赖注入”的风格）

2. **通过“注入自己的代理”来调用自己（中级，工程里常见）**
   - 思路：不要用 `this.inner()`，而是注入一个“指向自己 bean 的引用”，并通过它调用 `inner()`
   - 常见实现：
     - 自己依赖自己（自注入），必要时配合 `@Lazy` 避免循环依赖
     - 用 `ObjectProvider<SelfInvocationExampleService>` 延迟拿到 proxy 再调用
   - 优点：不需要依赖 `AopContext` 的 thread-local 语义，可读性也通常更好

3. **通过代理对象调用自己（进阶，理解机制用）**
   - `exposeProxy` + `AopContext.currentProxy()`（见 [05. expose-proxy](proxy-fundamentals-expose-proxy.md)）
   - 代价：更“技巧化”，容易滥用；但非常适合用来理解“call path 必须走 proxy”

4. **AspectJ 编译期/加载期织入**
   - 不是代理模型，能拦截“类内部调用”
   - 代价：配置更重，不适合作为学习仓库的默认路径

## 应当得到的结论

当遇到 “AOP 不生效” 的问题时，排查顺序是：

1. bean 是否被代理（`AopUtils.isAopProxy`）
2. 调用入口是否走代理（是否发生自调用）
3. 代理类型/限制（JDK/CGLIB、`final` 等）

- JDK proxy：`JdkDynamicAopProxy#invoke`
- CGLIB proxy：`CglibAopProxy.DynamicAdvisedInterceptor#intercept`

然后跑：

会看到：

- 外部调用 `outer(...)` 会命中代理入口
- `outer(...)` 内部的 `this.inner(...)` 根本不会命中代理入口（因为它是目标对象内部的普通方法调用）

## 最小可运行实验（Lab）

- Lab：`SpringCoreAopLabTest`
- 运行命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

## 现象（在本模块如何复现）

在测试 `SpringCoreAopLabTest#selfInvocationDoesNotTriggerAdviceForInnerMethod` 里：

如果想把“怎么修复”也做成可验证的闭环，直接看练习：

- `SpringCoreAopExerciseTest#exercise_makeSelfInvocationTriggerAdvice`

## 源码锚点：怎么在断点里证明“inner 根本没进代理”？

最直接的方法是把断点打在“代理接管调用”的入口：

- `SpringCoreAopLabTest#selfInvocationDoesNotTriggerAdviceForInnerMethod`

## 常见坑与边界

这是 Spring AOP 的经典“入门必踩坑”，而且它不止影响 AOP：事务（`@Transactional`）也会踩同一个坑。

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreAopLabTest`
- Exercise：`SpringCoreAopExerciseTest`

上一章：[02-jdk-vs-cglib](proxy-fundamentals-jdk-vs-cglib.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04-final-and-proxy-limits](proxy-fundamentals-final-and-proxy-limits.md)

<!-- BOOKIFY:END -->
