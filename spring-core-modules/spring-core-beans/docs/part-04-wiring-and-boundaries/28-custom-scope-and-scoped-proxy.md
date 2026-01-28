# 28. 自定义 Scope + scoped proxy：thread scope 的真实语义

## 导读

- 本章主题：**28. 自定义 Scope + scoped proxy：thread scope 的真实语义**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - scope 管的不是“对象长什么样”，而是：**容器每次 `getBean` 时如何取对象**（singleton/prototype/custom scope 只是不同分流）。
    - 把短生命周期 scope（prototype/thread/request 等）直接注入 singleton，最容易踩“冻结引用”坑：注入只发生一次，之后一直用那一个引用。
    - 两个最常见解法：
      - `ObjectProvider<T>`：把解析推迟到“使用时”（每次调用回到容器解析）
      - scoped proxy：注入 proxy，把“回到 scope 找真实对象”的动作隐藏在方法调用里
    - 本仓库已补齐对照实验：thread scope 与 prototype 都能复现“冻结 vs 延迟解析”的差异。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansCustomScopeLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`

## 机制主线

Spring 的 scope 机制是可扩展的：你可以注册自定义 scope。

本章用 `SimpleThreadScope`（Spring 提供但默认不注册）演示：

- 自定义 scope 如何注册
- scope 的“每次从容器获取”语义
- 为什么把短生命周期 scope 注入到 singleton 里需要 `ObjectProvider` 或 scoped proxy

### 机制讲透：条件 → 分支 → 结果

**条件**：容器 `getBean` 时发现 bean 定义了 scope（`singleton`/`prototype`/自定义）  
**分支**：`AbstractBeanFactory#doGetBean` 按 scope 分流  
**结果**：  
- singleton：从 `singletonObjects` 取/建  
- prototype：每次新建  
- custom scope：委派给 `Scope#get`（语义完全取决于你的实现）  
**断点建议**：`AbstractBeanFactory#doGetBean` / `Scope#get`

## 1. 注册自定义 scope（thread）

`SimpleThreadScope` 的关键点：

- 同一个 thread 内：同名 bean 返回同一个实例
- 不同 thread 之间：同名 bean 返回不同实例

对应测试：

- `SpringCoreBeansCustomScopeLabTest#threadScope_createsOneInstancePerThread_whenAccessedDirectly`
  - `SpringCoreBeansCustomScopeLabTest#prototypeInjectedIntoSingleton_isResolvedOnce_butObjectProviderCanObtainFreshPrototypeEachCall`（prototype 也会发生同类“冻结”现象）

原因与你在 prototype 注入 singleton 看到的现象一致：

- singleton 创建时解析依赖
- 只向容器要一次 scoped bean
- 之后一直用这个引用

---

## 2. 同类现象：prototype 注入 singleton 也会“冻结”

很多人第一次理解 thread/request scope 时会觉得“这是自定义 scope 的特殊坑”。其实不是——它是一个更一般的事实：

> **只要“目标 bean 的生命周期比 consumer 短”，把它直接注入到 singleton 里，就会在注入那一刻被冻结。**

prototype 是最典型的例子。

对应实验（本仓库已补齐）：

- `SpringCoreBeansCustomScopeLabTest#prototypeInjectedIntoSingleton_isResolvedOnce_butObjectProviderCanObtainFreshPrototypeEachCall`

你会观察到：

- direct injection：`PrototypeCounter` 被解析一次，consumer 内部持有固定引用
- `ObjectProvider<PrototypeCounter>`：每次 `getObject()` 都能拿到新的 prototype 实例

## 3. 解法 1：ObjectProvider（推荐，机制最直观）

对应测试：

 - `SpringCoreBeansCustomScopeLabTest#objectProvider_honorsThreadScope_whenUsedInsideSingleton`
你注入的是 provider（容器句柄），每次调用时再去容器按当前 thread 解析目标对象。

## 4. 解法 2：scoped proxy（更“无感”，但引入代理语义）

对应测试：

 - `SpringCoreBeansCustomScopeLabTest#scopedProxy_honorsThreadScope_whenInjectedIntoSingleton`
本质：

- singleton 注入到的是一个 proxy
- proxy 在每次方法调用时从当前 scope 找到真实目标再转发

- `AbstractBeanFactory#registerScope`：注册自定义 scope 的入口（没有注册就不会走 scope 分发）
- `AbstractBeanFactory#doGetBean`：按 scope 分发的主入口（singleton/prototype/custom scope 都会在这里分流）
- `SimpleThreadScope#get`：thread scope 的核心（同线程缓存、跨线程隔离）
- `ObjectProvider#getObject`：provider 的延迟解析入口（每次调用都回到容器重新解析目标）
- `ScopedProxyFactoryBean#getObject`：scoped proxy 的生成入口（注入的是 proxy，调用时再定位目标）

入口：

1) `AbstractBeanFactory#registerScope`：确认 thread scope 的注册发生在 refresh 之前
2) `SimpleThreadScope#get`：在同线程/不同线程获取时观察缓存命中与新建
3) direct injection 的 consumer 构造器/字段赋值点：观察“只取一次”导致冻结
4) `ObjectProvider#getObject`：观察 provider 每次调用都会触发一次新的解析（回到 `doGetBean`）
5) `ScopedProxyFactoryBean#getObject`（可选）：观察 proxy 生成与调用时的目标定位

## 5. 销毁语义：prototype 不会自动销毁，自定义 scope 必须显式回收

请记住这条规则：

- **prototype**：容器创建但不管理销毁  
- **custom scope**：销毁时机由 scope 自己决定  

如果你不注册销毁回调，最常见的后果是：

- 线程/请求上下文泄漏  
- 资源未释放（连接、文件句柄等）  

关键方法：

- `Scope#registerDestructionCallback`：注册销毁回调  
- `Scope#remove`：移除并触发回调  

实务建议：对 thread/request 这类 scope，明确“回收点”是排障核心。

## 排障分流：这是定义层问题还是实例层问题？

1) **“同一个 thread 里每次 getBean 都是新对象”** → 多半是 **定义层/注册问题**：确认 `registerScope("thread", ...)` 是否执行（看 `AbstractBeanFactory#registerScope`）。
2) **“不同 thread 里拿到的是同一个对象”** → 多半是 **scope 实现问题**：看 `SimpleThreadScope#get` 是否真的按 thread 隔离缓存。
3) **“注入到 singleton 后看起来像单例（冻结）”** → **实例层语义**：注入只发生一次；用 `ObjectProvider` 或 scoped proxy 把解析推迟到“调用时”。

## 6. 面试常问（Scope / ScopedProxy）

1) 自定义 scope 的语义由谁决定？（提示：scope 的 `get`/缓存策略）
2) 为什么 thread scope 注入到 singleton 会“冻结”？你能给出两种解法并说明代价吗？
3) scoped proxy 的本质是什么？它为什么会提高 debug 成本？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansCustomScopeLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansCustomScopeLabTest test`

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`

- `SpringCoreBeansCustomScopeLabTest.threadScope_createsOneInstancePerThread_whenAccessedDirectly()`

- `SpringCoreBeansCustomScopeLabTest.injectingThreadScopedBeanIntoSingleton_withoutProxy_freezesTheTargetAtInjectionTime()`

- `SpringCoreBeansCustomScopeLabTest.objectProvider_honorsThreadScope_whenUsedInsideSingleton()`

- `SpringCoreBeansCustomScopeLabTest.scopedProxy_honorsThreadScope_whenInjectedIntoSingleton()`

- **坑 2：scoped proxy 的调试成本**
  - 你看到的对象类型是 proxy，不是目标类；需要学会区分。

## 源码锚点（建议从这里下断点）

- `AbstractBeanFactory#doGetBean`：scope 分流入口（singleton/prototype/custom scope 都会在这里分叉）
- `Scope#get`：自定义 scope 的核心回调（从这里决定“怎么拿到对象”）
- `DefaultListableBeanFactory#registerScope`：注册自定义 scope 的入口
- `ScopedProxyFactoryBean#getObject`：scoped proxy 的取值入口（代理如何在每次调用时解析真实目标）
- `ScopedProxyUtils#createScopedProxy`：创建 scoped proxy 定义的辅助入口

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`
  - `threadScope_createsOneInstancePerThread_whenAccessedDirectly()`
  - `injectingThreadScopedBeanIntoSingleton_withoutProxy_freezesTheTargetAtInjectionTime()`
  - `objectProvider_honorsThreadScope_whenUsedInsideSingleton()`
  - `scopedProxy_honorsThreadScope_whenInjectedIntoSingleton()`

建议断点：

- “thread scope 没起作用/所有线程都拿到同一个实例” → **优先定义层（scope 注册）**：是否真的 `registerScope("thread", ...)`？（看 `registerScope`）
- “把 scoped bean 注入 singleton 后总是同一个实例” → **实例层（注入时机）**：这是 direct injection 的冻结效应；用 provider 或 scoped proxy（本章第 2/3/4 节）
- “调试时看到的类型是 proxy，不是目标类” → **实例层（代理语义）**：这是 scoped proxy 的预期形态（对照 [31](31-proxying-phase-bpp-wraps-bean.md)）
- “想当然认为 scope 会自动传播到注入点” → **概念澄清**：scope 管的是“容器如何取对象”，不自动改变注入点的解析次数（本章第 5 节）

- 你能解释清楚：为什么 direct injection 会让 thread scope 失效？
- 你能解释清楚：ObjectProvider 与 scoped proxy 的差别吗？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`
推荐断点：`AbstractBeanFactory#doGetBean`、`SimpleThreadScope#get`、`ScopedProxyFactoryBean#getObject`

## 常见坑与边界

### 关键陷阱：把 scoped bean 直接注入 singleton，会被冻结在“注入那一刻”

### 常见坑

- **坑 1：以为 scope 会自动传播到注入点**
  - scope 的语义是“容器如何管理对象”；注入点如果不做延迟解析，仍然只取一次。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansCustomScopeLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`

上一章：[27. SmartLifecycle：phase 与 start/stop 顺序](27-smart-lifecycle-phase.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[29. FactoryBean 边界坑：泛型/代理/对象类型推断](29-factorybean-edge-cases.md)

<!-- BOOKIFY:END -->
