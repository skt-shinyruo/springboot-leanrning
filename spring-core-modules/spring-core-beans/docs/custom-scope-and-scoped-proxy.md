# 自定义 Scope + scoped proxy：thread scope 的真实语义
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把现象固化为断言；排查真实问题时，按“定义层/实例层/最终暴露对象”分层，再用断点与观察清单收敛原因。

    观察对象：28. 自定义 Scope + scoped proxy：thread scope 的真实语义。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansCustomScopeLabTest`。需要下探源码时，可以从 `AbstractBeanFactory#doGetBean` / `Scope#get` / `SpringCoreBeansCustomScopeLabTest#threadScope_createsOneInstancePerThread_whenAccessedDirectly` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 问题：自定义 scope 的对象到底由谁决定生命周期

自定义 scope 不是给对象贴一个标签，而是把“如何获取、缓存、销毁目标对象”的策略交给 `Scope#get`。当短生命周期对象要注入 singleton 时，还必须额外考虑 `ObjectProvider` 或 scoped proxy。

先运行 `SpringCoreBeansCustomScopeLabTest`，把核心现象固定为可复现事实；随后围绕入口方法、关键分支和可观察变量阅读正文。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（Scopes，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansCustomScopeLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`


## 机制主线

> 官方参考（Spring Framework 6.2.x，Scopes 与 scope 语义）：https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html

Spring 的 scope 机制是可扩展的：可以注册自定义 scope。

本章用 `SimpleThreadScope`（Spring 提供但默认不注册）演示：

- 自定义 scope 如何注册
- scope 的“每次从容器获取”语义
- 为什么把短生命周期 scope 注入到 singleton 里需要 `ObjectProvider` 或 scoped proxy

### 机制边界：条件、分支与结果

**条件**：容器 `getBean` 时发现 bean 定义了 scope（`singleton`/`prototype`/自定义）
**分支**：`AbstractBeanFactory#doGetBean` 按 scope 分流
**结果**：
- singleton：从 `singletonObjects` 取/建
- prototype：每次新建
- custom scope：委派给 `Scope#get`（语义完全取决于相应的实现）
**断点入口**：`AbstractBeanFactory#doGetBean` / `Scope#get`

## 注册自定义 scope（thread）

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

## 同类现象：prototype 注入 singleton 也会“冻结”

很多人在首次理解 thread/request scope 时会将其视为“自定义 scope 的特殊误区”。但并非如此——它是一个更一般的事实：

> **只要“目标 bean 的生命周期比 consumer 短”，把它直接注入到 singleton 里，就会在注入那一刻被冻结。**

prototype 是最典型的例子。

对应实验（本仓库已补齐）：

- `SpringCoreBeansCustomScopeLabTest#prototypeInjectedIntoSingleton_isResolvedOnce_butObjectProviderCanObtainFreshPrototypeEachCall`

可以观察到：

- direct injection：`PrototypeCounter` 被解析一次，consumer 内部持有固定引用
- `ObjectProvider<PrototypeCounter>`：每次 `getObject()` 都能获取到新的 prototype 实例

## 解法 1：ObjectProvider（机制最直观）

对应测试：

  - `SpringCoreBeansCustomScopeLabTest#objectProvider_honorsThreadScope_whenUsedInsideSingleton`
读者注入的是 provider（容器句柄），每次调用时再去容器按当前 thread 解析目标对象。

## 解法 2：scoped proxy（更“无感”，但引入代理语义）

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

1. `AbstractBeanFactory#registerScope`：确认 thread scope 的注册发生在 refresh 之前
2. `SimpleThreadScope#get`：在同线程/不同线程获取时观察缓存命中与新建
3. direct injection 的 consumer 构造器/字段赋值点：观察“只取一次”导致冻结
4. `ObjectProvider#getObject`：观察 provider 每次调用都会触发一次新的解析（回到 `doGetBean`）
5. `ScopedProxyFactoryBean#getObject`（可选）：观察 proxy 生成与调用时的目标定位

## 销毁语义：prototype 不会自动销毁，自定义 scope 必须显式回收

请记住这条规则：

- **prototype**：容器创建但不管理销毁
- **custom scope**：销毁时机由 scope 自己决定

若不注册销毁回调，最常见的后果是：

- 线程/请求上下文泄漏
- 资源未释放（连接、文件句柄等）

关键方法：

- `Scope#registerDestructionCallback`：注册销毁回调
- `Scope#remove`：移除并触发回调

实务取舍：对 thread/request 这类 scope，明确“回收点”是排障核心。

## 排障分流：这是定义层问题还是实例层问题？
> 官方参考（Spring Framework 6.2.x，Scopes 与 scope 语义）：https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html


1. **“同一个 thread 里每次 getBean 都是新对象”** → 多半是 **定义层/注册问题**：确认 `registerScope("thread", ...)` 是否执行（看 `AbstractBeanFactory#registerScope`）。
2. **“不同 thread 里获取到的是同一个对象”** → 多半是 **scope 实现问题**：看 `SimpleThreadScope#get` 是否真的按 thread 隔离缓存。
3. **“注入到 singleton 后表面上像单例（冻结）”** → **实例层语义**：注入只发生一次；用 `ObjectProvider` 或 scoped proxy 把解析推迟到“调用时”。

## 面试常问（Scope / ScopedProxy）

1. 自定义 scope 的语义由谁决定？（提示：scope 的 `get`/缓存策略）
2. 为什么 thread scope 注入到 singleton 会“冻结”？需要能给出两种解法并说明代价吗？
3. scoped proxy 的本质是什么？它为什么会提高 debug 成本？

## 实验：把现象固定成断言

本章可复核的实验入口：
- Lab：`SpringCoreBeansCustomScopeLabTest`
- 命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 从实验现象看边界

## 运行入口

- 入口测试（先运行通过，再设置断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansCustomScopeLabTest test`

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`

- `SpringCoreBeansCustomScopeLabTest.threadScope_createsOneInstancePerThread_whenAccessedDirectly()`

- `SpringCoreBeansCustomScopeLabTest.injectingThreadScopedBeanIntoSingleton_withoutProxy_freezesTheTargetAtInjectionTime()`

- `SpringCoreBeansCustomScopeLabTest.objectProvider_honorsThreadScope_whenUsedInsideSingleton()`

- `SpringCoreBeansCustomScopeLabTest.scopedProxy_honorsThreadScope_whenInjectedIntoSingleton()`

- **误区 2：scoped proxy 的调试成本**
  - 观察到的对象类型是 proxy，不是目标类；需要学会区分。

## 源码锚点：从这里设置断点

- `AbstractBeanFactory#doGetBean`：scope 分流入口（singleton/prototype/custom scope 都会在这里分叉）
- `Scope#get`：自定义 scope 的核心回调（从这里决定“怎么获取到对象”）
- `DefaultListableBeanFactory#registerScope`：注册自定义 scope 的入口
- `ScopedProxyFactoryBean#getObject`：scoped proxy 的取值入口（代理如何在每次调用时解析真实目标）
- `ScopedProxyUtils#createScopedProxy`：创建 scoped proxy 定义的辅助入口

## 断点闭环（用本仓库实验/测试运行一次）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`
  - `threadScope_createsOneInstancePerThread_whenAccessedDirectly()`
  - `injectingThreadScopedBeanIntoSingleton_withoutProxy_freezesTheTargetAtInjectionTime()`
  - `objectProvider_honorsThreadScope_whenUsedInsideSingleton()`
  - `scopedProxy_honorsThreadScope_whenInjectedIntoSingleton()`

断点入口：

- “thread scope 没起作用/所有线程都获取到同一个实例” → **优先定义层（scope 注册）**：是否真的 `registerScope("thread", ...)`？（看 `registerScope`）
- “把 scoped bean 注入 singleton 后总是同一个实例” → **实例层（注入时机）**：这是 direct injection 的冻结效应；用 provider 或 scoped proxy（本章第 2/3/4 节）
- “调试时看到的类型是 proxy，不是目标类” → **实例层（代理语义）**：这是 scoped proxy 的预期形态（对照 [31](proxying-phase.md)）
- “想当然认为 scope 会自动传播到注入点” → **概念澄清**：scope 管的是“容器如何取对象”，不自动改变注入点的解析次数（本章第 5 节）

- 需要解释清楚：为什么 direct injection 会让 thread scope 失效？
- 需要解释清楚：ObjectProvider 与 scoped proxy 的差别吗？
对应实验/测试：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`
断点入口：`AbstractBeanFactory#doGetBean`、`SimpleThreadScope#get`、`ScopedProxyFactoryBean#getObject`

## 边界：自定义 Scope + scoped proxy：thread scope 的真实语义

### 关键陷阱：把 scoped bean 直接注入 singleton，会被冻结在“注入那一刻”

### 误判点：不要把外层现象当成根因

- **误区 1：以为 scope 会自动传播到注入点**
  - scope 的语义是“容器如何管理对象”；注入点如果不做延迟解析，仍然只取一次。

## 验收口径：自定义 Scope + scoped proxy：thread scope 的真实语义
需要解释清楚：

1. **Scope 契约的 3 个关键方法各自负责什么？**（get/remove/registerDestructionCallback）
2. **为什么 scoped proxy 能解决“把短生命周期对象注入长生命周期对象”的问题？目标对象什么时候才创建？**
3. **自定义 scope 的主要风险点是什么？**（thread-local 泄漏/销毁回调不执行/代理导致类型信息丢失）

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansCustomScopeLabTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`

<!-- BOOKIFY:END -->
