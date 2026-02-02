# 28. 自定义 Scope + scoped proxy：thread scope 的真实语义
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：28. 自定义 Scope + scoped proxy：thread scope 的真实语义
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`AbstractBeanFactory#doGetBean` / `Scope#get` / `SpringCoreBeansCustomScopeLabTest#threadScope_createsOneInstancePerThread_whenAccessedDirectly`
    - 推荐 Lab：`SpringCoreBeansCustomScopeLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[27. SmartLifecycle：phase 与 start/stop 顺序](27-smart-lifecycle-phase.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[29. FactoryBean 边界误区：泛型/代理/对象类型推断](29-factorybean-edge-cases.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**28. 自定义 Scope + scoped proxy：thread scope 的真实语义**
- 阅读建议：建议先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（Scopes，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html


!!! summary "本章要点"

    - scope 管的不是“对象长什么样”，而是：**容器每次 `getBean` 时如何取对象**（singleton/prototype/custom scope 只是不同分流）。
    - 把短生命周期 scope（prototype/thread/request 等）直接注入 singleton，最易落入“冻结引用”陷阱：注入只发生一次，之后一直用那一个引用。
    - 两个最常见解法：
      - `ObjectProvider<T>`：把解析推迟到“使用时”（每次调用回到容器解析）
      - scoped proxy：注入 proxy，把“回到 scope 找真实对象”的动作隐藏在方法调用里
    - 本仓库已补齐对照实验：thread scope 与 prototype 都能复现“冻结 vs 延迟解析”的差异。


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansCustomScopeLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansCustomScopeLabTest#threadScope_createsOneInstancePerThread_whenAccessedDirectly`，再用 `SpringCoreBeansCustomScopeLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`AbstractBeanFactory#doGetBean` / `Scope#get`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“排障分流：这是定义层问题还是实例层问题？”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，Scopes 与 scope 语义）：https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html

Spring 的 scope 机制是可扩展的：可以注册自定义 scope。

本章用 `SimpleThreadScope`（Spring 提供但默认不注册）演示：

- 自定义 scope 如何注册
- scope 的“每次从容器获取”语义
- 为什么把短生命周期 scope 注入到 singleton 里需要 `ObjectProvider` 或 scoped proxy

### 机制系统阐述：条件 → 分支 → 结果

**条件**：容器 `getBean` 时发现 bean 定义了 scope（`singleton`/`prototype`/自定义）  
**分支**：`AbstractBeanFactory#doGetBean` 按 scope 分流  
**结果**：  
- singleton：从 `singletonObjects` 取/建  
- prototype：每次新建  
- custom scope：委派给 `Scope#get`（语义完全取决于相应的实现）  
**断点建议**：`AbstractBeanFactory#doGetBean` / `Scope#get`

## 1. 注册自定义 scope（thread）

`SimpleThreadScope` 的关键点：

- 同一个 thread 内：同名 bean 返回同一个实例
- 不同 thread 之间：同名 bean 返回不同实例

对应测试：

- `SpringCoreBeansCustomScopeLabTest#threadScope_createsOneInstancePerThread_whenAccessedDirectly`
  - `SpringCoreBeansCustomScopeLabTest#prototypeInjectedIntoSingleton_isResolvedOnce_butObjectProviderCanObtainFreshPrototypeEachCall`（prototype 也会发生同类“冻结”现象）

原因与在 prototype 注入 singleton 看到的现象一致：

- singleton 创建时解析依赖
- 只向容器要一次 scoped bean
- 之后一直用这个引用

---

## 2. 同类现象：prototype 注入 singleton 也会“冻结”

很多人在首次理解 thread/request scope 时会将其视为“自定义 scope 的特殊误区”。但并非如此——它是一个更一般的事实：

> **只要“目标 bean 的生命周期比 consumer 短”，把它直接注入到 singleton 里，就会在注入那一刻被冻结。**

prototype 是最典型的例子。

对应实验（本仓库已补齐）：

- `SpringCoreBeansCustomScopeLabTest#prototypeInjectedIntoSingleton_isResolvedOnce_butObjectProviderCanObtainFreshPrototypeEachCall`

可以观察到：

- direct injection：`PrototypeCounter` 被解析一次，consumer 内部持有固定引用
- `ObjectProvider<PrototypeCounter>`：每次 `getObject()` 都能获取到新的 prototype 实例

## 3. 解法 1：ObjectProvider（推荐，机制最直观）

对应测试：

 - `SpringCoreBeansCustomScopeLabTest#objectProvider_honorsThreadScope_whenUsedInsideSingleton`
读者注入的是 provider（容器句柄），每次调用时再去容器按当前 thread 解析目标对象。

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

若不注册销毁回调，最常见的后果是：

- 线程/请求上下文泄漏  
- 资源未释放（连接、文件句柄等）  

关键方法：

- `Scope#registerDestructionCallback`：注册销毁回调  
- `Scope#remove`：移除并触发回调  

实务建议：对 thread/request 这类 scope，明确“回收点”是排障核心。

## 排障分流：这是定义层问题还是实例层问题？

1) **“同一个 thread 里每次 getBean 都是新对象”** → 多半是 **定义层/注册问题**：确认 `registerScope("thread", ...)` 是否执行（看 `AbstractBeanFactory#registerScope`）。
2) **“不同 thread 里获取到的是同一个对象”** → 多半是 **scope 实现问题**：看 `SimpleThreadScope#get` 是否真的按 thread 隔离缓存。
3) **“注入到 singleton 后看起来像单例（冻结）”** → **实例层语义**：注入只发生一次；用 `ObjectProvider` 或 scoped proxy 把解析推迟到“调用时”。

## 6. 面试常问（Scope / ScopedProxy）

1) 自定义 scope 的语义由谁决定？（提示：scope 的 `get`/缓存策略）
2) 为什么 thread scope 注入到 singleton 会“冻结”？应能够给出两种解法并说明代价吗？
3) scoped proxy 的本质是什么？它为什么会提高 debug 成本？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先运行它们）：
- Lab：`SpringCoreBeansCustomScopeLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先运行通再设置断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansCustomScopeLabTest test`

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`

- `SpringCoreBeansCustomScopeLabTest.threadScope_createsOneInstancePerThread_whenAccessedDirectly()`

- `SpringCoreBeansCustomScopeLabTest.injectingThreadScopedBeanIntoSingleton_withoutProxy_freezesTheTargetAtInjectionTime()`

- `SpringCoreBeansCustomScopeLabTest.objectProvider_honorsThreadScope_whenUsedInsideSingleton()`

- `SpringCoreBeansCustomScopeLabTest.scopedProxy_honorsThreadScope_whenInjectedIntoSingleton()`

- **误区 2：scoped proxy 的调试成本**
  - 观察到的对象类型是 proxy，不是目标类；需要学会区分。

## 源码锚点（建议从这里设置断点）

- `AbstractBeanFactory#doGetBean`：scope 分流入口（singleton/prototype/custom scope 都会在这里分叉）
- `Scope#get`：自定义 scope 的核心回调（从这里决定“怎么获取到对象”）
- `DefaultListableBeanFactory#registerScope`：注册自定义 scope 的入口
- `ScopedProxyFactoryBean#getObject`：scoped proxy 的取值入口（代理如何在每次调用时解析真实目标）
- `ScopedProxyUtils#createScopedProxy`：创建 scoped proxy 定义的辅助入口

## 断点闭环（用本仓库 Lab/Test 运行一次）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`
  - `threadScope_createsOneInstancePerThread_whenAccessedDirectly()`
  - `injectingThreadScopedBeanIntoSingleton_withoutProxy_freezesTheTargetAtInjectionTime()`
  - `objectProvider_honorsThreadScope_whenUsedInsideSingleton()`
  - `scopedProxy_honorsThreadScope_whenInjectedIntoSingleton()`

建议断点：

- “thread scope 没起作用/所有线程都获取到同一个实例” → **优先定义层（scope 注册）**：是否真的 `registerScope("thread", ...)`？（看 `registerScope`）
- “把 scoped bean 注入 singleton 后总是同一个实例” → **实例层（注入时机）**：这是 direct injection 的冻结效应；用 provider 或 scoped proxy（本章第 2/3/4 节）
- “调试时看到的类型是 proxy，不是目标类” → **实例层（代理语义）**：这是 scoped proxy 的预期形态（对照 [31](31-proxying-phase-bpp-wraps-bean.md)）
- “想当然认为 scope 会自动传播到注入点” → **概念澄清**：scope 管的是“容器如何取对象”，不自动改变注入点的解析次数（本章第 5 节）

- 应能够解释清楚：为什么 direct injection 会让 thread scope 失效？
- 应能够解释清楚：ObjectProvider 与 scoped proxy 的差别吗？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`
推荐断点：`AbstractBeanFactory#doGetBean`、`SimpleThreadScope#get`、`ScopedProxyFactoryBean#getObject`

## 常见误区与边界

### 关键陷阱：把 scoped bean 直接注入 singleton，会被冻结在“注入那一刻”

### 常见误区

- **误区 1：以为 scope 会自动传播到注入点**
  - scope 的语义是“容器如何管理对象”；注入点如果不做延迟解析，仍然只取一次。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

## 自检要点
应能够解释清楚：

1) **Scope 契约的 3 个关键方法各自负责什么？**（get/remove/registerDestructionCallback）
2) **为什么 scoped proxy 能解决“把短生命周期对象注入长生命周期对象”的问题？目标对象什么时候才创建？**
3) **自定义 scope 的主要风险点是什么？**（thread-local 泄漏/销毁回调不执行/代理导致类型信息丢失）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansCustomScopeLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`

上一章：[27. SmartLifecycle：phase 与 start/stop 顺序](27-smart-lifecycle-phase.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[29. FactoryBean 边界误区：泛型/代理/对象类型推断](29-factorybean-edge-cases.md)

<!-- BOOKIFY:END -->
