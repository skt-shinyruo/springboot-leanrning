# 04. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Bean 生命周期骨架（instantiate→populate→initialize→destroy）；初始化回调链（Aware / BPP / `@PostConstruct` / `afterPropertiesSet` / `initMethod` / after-init proxy）；销毁链路（DestructionAwareBPP / `@PreDestroy` / `DisposableBean` / `destroyMethod`）；Scope 语义（prototype 默认不自动销毁）；容器级生命周期钩子（`SmartInitializingSingleton` / `SmartLifecycle` / refresh 事件）。
    - 使用方式：先运行本章推荐 Lab，把“回调顺序/触发窗口/prototype 销毁边界/顺序控制/容器级 start-stop”固化为断言；回到正文用 `doCreateBean`/`initializeBean`/`destroySingletons` 把顺序映射到方法级证据链；最后用断点确认 raw vs exposed（proxy）以及依赖图（dependsOn/phase）是否符合预期。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`AbstractAutowireCapableBeanFactory#doCreateBean` / `#populateBean` / `#initializeBean` / `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization` / `DefaultSingletonBeanRegistry#destroySingletons` / `DisposableBeanAdapter#destroy`
    - 推荐 Lab：`SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansPrototypeDestroySemanticsLabTest` / `SpringCoreBeansDependsOnLabTest` / `SpringCoreBeansSmartInitializingSingletonLabTest` / `SpringCoreBeansSmartLifecycleLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）](03-scope-and-prototype.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](05-post-processors.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）**
- 阅读方式建议：先运行 `SpringCoreBeansLifecycleCallbackOrderLabTest` 把“顺序”变成断言，再回到正文把顺序映射到关键方法。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! summary "本章要点"

    - 生命周期不是“一个 init-method”，而是一条稳定骨架：**instantiate → populate → initialize → (use) → destroy**。
    - `@PostConstruct/@PreDestroy` 不是 Java 语法“自带”的生命周期；它们依赖容器注册了相应的 `BeanPostProcessor`（典型是 `CommonAnnotationBeanPostProcessor`）。
    - 最终暴露对象可能是 proxy：初始化后（after-init）BPP 可以返回“另一个对象”，因此**回调发生在 raw 还是 exposed**是很多误判根源（`@PostConstruct` 在 raw 上发生，after-init 才可能产生 proxy）。
    - prototype 的销毁默认不由容器托管：`close()` 只会统一销毁 singleton，prototype 需要调用方显式销毁（或改造为更合适的生命周期模型）。
    - “顺序控制”有两类：`dependsOn` 只管初始化/销毁顺序；`SmartLifecycle` 通过 `phase` 管 start/stop 顺序（两者都不是注入规则）。

!!! example "本章配套实验（先运行再读）"

    - Lab：
      - `SpringCoreBeansLifecycleCallbackOrderLabTest`
      - `SpringCoreBeansAwareInfrastructureLabTest`
      - `SpringCoreBeansPrototypeDestroySemanticsLabTest`
      - `SpringCoreBeansDependsOnLabTest`
      - `SpringCoreBeansSmartInitializingSingletonLabTest`
      - `SpringCoreBeansSmartLifecycleLabTest`
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansAwareInfrastructureLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPrototypeDestroySemanticsLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartInitializingSingletonLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`

!!! info "本章覆盖的知识点（更全版）"

    - 生命周期骨架：实例化/注入/初始化/销毁分别落在 `doCreateBean` 的哪一步（以及每一步常见“短路/分支条件”）。
    - 初始化回调：Aware、before-init BPP、`@PostConstruct`、`afterPropertiesSet`、`initMethod`、after-init BPP（以及为什么 after-init 常常产出 proxy）。
    - `@PostConstruct/@PreDestroy` 的触发者：并非“语法魔法”，而是 `InitDestroyAnnotationBeanPostProcessor`/`CommonAnnotationBeanPostProcessor` 介入生命周期链路。
    - raw vs exposed：为什么在 `@PostConstruct` 里调用 `@Transactional/@Async` 常常“不生效”，以及应该换到哪个生命周期钩子。
    - Scope 边界：singleton vs prototype 的创建/销毁语义；prototype 的手动销毁入口与风险。
    - 顺序控制：`dependsOn` 只管初始化/销毁顺序（并会“拉起” lazy）；`SmartLifecycle` 用 `phase` 控制 start/stop 顺序。
    - 容器级时机点：`SmartInitializingSingleton`、`LifecycleProcessor`、`ContextRefreshedEvent` 分别处在 refresh 主线的哪个窗口（帮助选型与排障）。
    - 调试策略：用断点 + watch list 把“回调没触发/顺序不对/被代理替换/销毁不执行”变成可定位问题（见第 6 节与排障表）。

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansLifecycleCallbackOrderLabTest`，再用 `SpringCoreBeansPrototypeDestroySemanticsLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“排障决策表（生命周期/回调：从“没执行”到“证据链”）”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/03-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/07-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

这一章解决两个问题：

1) **Bean 从“还不存在”到“可以被使用”经历了什么阶段？**
2) 编写的各种回调（`@PostConstruct` / `@PreDestroy` / `afterPropertiesSet` / `initMethod` …）到底在什么时机执行？

---

## 1. 源码级生命周期骨架：把顺序落到关键方法

若只记住一句话：**生命周期 = instantiate → populate → initialize → (use) → destroy**。

但读者 B/C 需要更具体：至少应能够把“高层阶段”映射到 Spring 的关键方法名，否则一旦遇到代理/循环依赖/后处理器介入，就很难定位“到底是哪一段出了问题”。

### 1.1 从 refresh 到创建：生命周期发生在哪一段？

容器启动主线在：`AbstractApplicationContext#refresh`。

需要记住：**对象创建的大规模发生点**在 refresh 的后半段：

- `finishBeanFactoryInitialization` → `DefaultListableBeanFactory#preInstantiateSingletons`

也就是说：

- 定义层（BeanDefinition）主要在 refresh 前半段完成
- 实例层（create/populate/initialize）主要在 `preInstantiateSingletons` / `getBean()` 触发

这也是为什么要把生命周期和 [06. 容器扩展点：BFPP vs BPP](05-post-processors.md) 放在一起理解：

- BFPP/BDRPP 决定“配方/施工图”
- BPP 决定“对象怎么落地、最终长什么样”

### 1.2 单个 bean 的创建主线：`createBean` / `doCreateBean`

核心入口（常用断点入口）：

- `AbstractAutowireCapableBeanFactory#createBean`
- `AbstractAutowireCapableBeanFactory#doCreateBean`

把它记成一段“足够对照断点的伪代码”：

```text
doCreateBean(beanName, mbd):
  1) createBeanInstance(...)            // 选构造器/工厂方法/默认构造
  2) applyMergedBeanDefinitionPostProcessors
  3) maybeEarlySingletonExposure         // 循环依赖：提前暴露 early reference
  4) populateBean(...)                   // 注入（属性填充）
  5) exposedObject = initializeBean(...) // aware + before-init + init callbacks + after-init
  6) registerDisposableBeanIfNecessary   // 如果需要销毁回调，登记到 disposableBeans
  return exposedObject
```

### 1.2.1 关键分支与变量（不要漏掉这些“条件开关”）

- `mbd.isSingleton()`：是否走 early reference（prototype 通常不会进入）
- `mbd.isSynthetic()`：是否跳过部分 BPP（基础设施 bean 的常见开关）
- `hasInstantiationAwareBeanPostProcessors`：是否允许“实例化前短路”（`postProcessBeforeInstantiation`）
- `mbd.hasPropertyValues()` / `hasAutowiredAnnotation`：是否进入属性填充与依赖注入
- `mbd.hasDestroyMethod()` / `requiresDestruction`：是否登记销毁回调

其中“最易误述/最易出错”的点是第 5 步：**initializeBean 里 after-init BPP 可能返回 proxy**。

### 1.3 `initializeBean`：初始化阶段的稳定回调链

初始化阶段的骨架非常稳定（精简伪代码）：

```text
initializeBean(beanName, bean, mbd):
  invokeAwareMethods(beanName, bean)                      // BeanNameAware/BeanFactoryAware 等
  bean = applyBeanPostProcessorsBeforeInitialization(...) // 这里可能触发 @PostConstruct
  invokeInitMethods(beanName, bean, mbd)                  // afterPropertiesSet / initMethod
  bean = applyBeanPostProcessorsAfterInitialization(...)  // 这里经常产生 proxy（最终暴露对象）
  return bean
```

几个“不要说错”的点（框架岗常追问）：

1) **Aware 发生在 init callbacks 之前**：很多 init 逻辑需要先获取到 beanName/BeanFactory 等容器信息
2) **`@PostConstruct` 发生在 before-init BPP 链路中**：它不是“硬编码步骤”，而是某个 BPP 触发
3) **after-init 可能返回代理**：最终暴露对象可能不是原始实例

### 1.3.1 回调与代理交织：回调到底发生在谁身上？

- **`@PostConstruct` / `afterPropertiesSet`**：发生在 **raw bean** 上（proxy 还未产生）
- **after-init BPP**：可能返回 **proxy**，此后容器对外暴露的是 proxy
- **`@PreDestroy`**：通常由 `DestructionAwareBeanPostProcessor` 触发，仍然作用在 target 上
  - 若依赖 `DisposableBean` 接口，且 proxy 不实现该接口，容易出现“销毁回调没进”的误判
- **`SmartInitializingSingleton`**：在单例全部实例化后回调，通常作用于 **最终暴露对象**（可能是 proxy）

证据入口（可先运行一次再背结论）：

- `SpringCoreBeansLifecycleRawVsProxyLabTest#postConstructRunsOnRaw_butFinalExposedBeanMayBeProxy`

### 1.4 销毁主线：singleton 的 destroy callbacks 在哪触发？

销毁不是“某个注解自动执行”，它同样是一条容器主线：

- `AbstractApplicationContext#close` / `doClose`
- `DefaultSingletonBeanRegistry#destroySingletons`
- `DisposableBeanAdapter#destroy`

同样给一段对照断点的伪代码：

```text
destroySingletons():
  for each disposableBean:
    invokeDestructionAwareBeanPostProcessorsBeforeDestruction(...)
    invoke @PreDestroy (JSR-250)
    invoke DisposableBean#destroy
    invoke custom destroyMethod (e.g., @Bean(destroyMethod=...))
```

### 1.5 为什么 `@PostConstruct/@PreDestroy` 不是“语法自带”？

一句话：**因为它们不是 JVM 的生命周期，而是容器的生命周期。**

读者之所以能写注解就生效，是因为容器在 refresh 主线里注册了处理它们的基础设施 BPP（典型是 `CommonAnnotationBeanPostProcessor`）。

这也解释了一个常见现象：

- 读者用“裸 BeanFactory”手动 new/注册 bean，`@PostConstruct` 可能不生效
- 读者走完整 `ApplicationContext#refresh` 主线后，就正常了

---

## 补充：`@PostConstruct/@PreDestroy` 的“触发者”为 BPP（而不是语法魔法）

很多人能背出“`@PostConstruct` 在初始化阶段执行”，但解释不清两件关键事实：

1. **它是谁触发的？** —— 触发者不是 JVM，也不是注解本身，而是一个 `BeanPostProcessor`（典型实现是 `InitDestroyAnnotationBeanPostProcessor`）。
2. **它发生在回调链的哪个位置？** —— `@PostConstruct` 通常发生在 `postProcessBeforeInitialization` 这一段；而 `@PreDestroy` 则走销毁链路（常见落点：`DestructionAwareBeanPostProcessor#postProcessBeforeDestruction` / `DisposableBeanAdapter`）。

补齐这两个事实后，可在排障时回答“为何此处未执行/执行顺序不符合预期/似乎对代理不生效”：

- **为什么回调像是发生在 raw bean 上？**
  默认链路里，init 回调发生在 `applyBeanPostProcessorsBeforeInitialization` 之后、`applyBeanPostProcessorsAfterInitialization` 之前；如果 proxy 是在 after 阶段生成的，那么回调自然发生在 raw bean 上。
- **什么时候回调会发生在 proxy 上？**
  只有当对象在更早阶段就被替换（例如实例化前短路 / early reference 返回 proxy）时，所观察到的“回调对象”才会发生变化。

**建议把它做成可复用的断点闭环（3 分钟）：**

- `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization(...)`
- `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeDestruction(...)`
- `DisposableBeanAdapter#destroy(...)` / `#invokeDestroyMethods(...)`

## 2. Aware 系列回调：真实作用、触发者与发生时机

很多人把 Aware 理解成“知道自己叫什么名字”，但对原理/框架岗来说，更关键的是：

- 它发生在生命周期的哪一段？
- 是谁触发这些回调？
- 如果容器不具备对应的基础设施，会发生什么？

容易说错的一点（面试官很爱追）：

- **Aware 发生在 initialize 阶段，不是 populate（注入）阶段。**
- 注入通常已经完成（字段/构造器参数已经有值），Aware 是在此基础上把“容器信息”补给 bean，然后才进入 `@PostConstruct` 等 init 回调链路。

对应可运行入口：

- `SpringCoreBeansAwareInfrastructureLabTest`

---

## 3. 本模块的“可观测”例子：把顺序固化成断言

若只靠日志理解生命周期，很容易被“并发/代理/顺序差异”骗。

本仓库推荐的方式是：**用事件列表 + 断言把顺序固化**。

可直接运行：

- `SpringCoreBeansLifecycleCallbackOrderLabTest`

它的设计意图是：

1) 用一个 `RecordingBeanPostProcessor` 在 before/after-init 打点
2) bean 同时实现 Aware、InitializingBean、DisposableBean，并声明 `@PostConstruct/@PreDestroy`
3) 最后断言事件顺序（无需看日志，直接看断言）

应当从这个 Lab 得到的结论是：

- Aware → before-init BPP（可能触发 @PostConstruct）→ init callbacks → after-init BPP（可能产出 proxy）
- destroy 的链路由容器 close 触发，统一走 `DisposableBeanAdapter`

---

## 4. 常见生命周期回调方式（按“推荐度/常见度”）

### 4.1 `@PostConstruct` / `@PreDestroy`（推荐，语义清晰）

优点：

- 与业务代码耦合低（不需要实现 Spring 接口）
- 语义清晰

注意：

- 回调方法通常不应有参数
- 不建议做重 IO/长耗时工作（会拉长启动/关闭时间）

### 4.2 `InitializingBean` / `DisposableBean`（了解即可）

优点：Spring 原生接口；便于在源码层定位

缺点：让业务类依赖 Spring 接口（耦合）

### 4.3 `@Bean(initMethod=..., destroyMethod=...)`（配置级别控制）

适用场景：

- 读者不想修改第三方类源码
- 若希望集中管理初始化/销毁方法

### 4.4 回调来源分型（触发时机 / 优先级）

| 回调类型 | 触发时机 | 优先级/边界 |
| --- | --- | --- |
| JSR-250（`@PostConstruct/@PreDestroy`） | before-init / destroy | 依赖 `CommonAnnotationBeanPostProcessor` |
| 接口回调（`InitializingBean/DisposableBean`） | init / destroy | 明确且可断点定位 |
| 配置回调（`initMethod/destroyMethod`） | init / destroy | 可覆盖第三方类 |
| `SmartInitializingSingleton` | 单例全部实例化完成后 | 适合做“容器就绪后”的动作 |
| `Lifecycle/SmartLifecycle` | 容器启动/停止阶段 | 有 phase，决定启动/停止顺序 |

### 4.5 选型：把“什么时候做事”分成 3 层（Bean / Container / Application）

很多“回调没生效/顺序不对/为什么事务没开”的问题，本质是**把事情放在了错误的生命周期层级**。

把“何时做事”分成 3 层，会更不容易误判：

1) **Bean 内部初始化（bean-level）**：只关心“这个 bean 自己可用”
   - 典型入口：`@PostConstruct` / `afterPropertiesSet` / `initMethod`
   - 关键边界：**依赖已注入，但 proxy 可能还没产生**（见 1.3）
2) **容器就绪（container-level）**：关心“容器里一批 bean 已经就绪”
   - 典型入口：`SmartInitializingSingleton`（非 lazy 单例都创建完之后）
   - 典型入口：`ContextRefreshedEvent`（refresh 收尾事件）
3) **应用就绪/启动停止（application-level）**：关心“应用何时对外服务/如何优雅停机”
   - 典型入口：`SmartLifecycle`（start/stop 纳入容器生命周期，并可 phase 排序）
   - Spring Boot 场景：`ApplicationRunner` / `ApplicationReadyEvent`（更上层的应用生命周期钩子）

为了把时机说清楚，给一个“refresh 尾部窗口”的极简时间线（只保留选型需要的相对顺序）：

```text
finishBeanFactoryInitialization
  -> preInstantiateSingletons (create non-lazy singletons)
     -> SmartInitializingSingleton.afterSingletonsInstantiated
finishRefresh
  -> LifecycleProcessor.onRefresh (SmartLifecycle auto-start)
  -> publish ContextRefreshedEvent
```

选型速查（建议把它当作“把事情放到正确窗口”的 checklist）：

| 需求 | 更合适的钩子 | 为什么 | 在本仓库怎么验证/延伸阅读 |
| --- | --- | --- | --- |
| 初始化自身字段/校验注入完成 | `@PostConstruct` / `afterPropertiesSet` / `initMethod` | 注入已完成，位置稳定 | `SpringCoreBeansLifecycleCallbackOrderLabTest`（本章第 3 节） |
| 需要“非 lazy 单例都创建完”后再做一次性事情（建索引/全量校验） | `SmartInitializingSingleton` | 明确发生在 `preInstantiateSingletons` 收尾 | `SpringCoreBeansSmartInitializingSingletonLabTest`；见 [`26`](../part-04-wiring-and-boundaries/09-smart-initializing-singleton.md) |
| 需要按顺序 start/stop 基础设施组件（消费者/线程池容器等） | `SmartLifecycle`（phase） | start/stop 纳入容器生命周期，且有排序与 stop callback 语义 | `SpringCoreBeansSmartLifecycleLabTest`；见 [`27`](../part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md) |
| 只想强制某些 bean 初始化/销毁顺序（即使没有显式 DI） | `@DependsOn` / `dependsOn` | 只管顺序、不管注入；并会“拉起” lazy | `SpringCoreBeansDependsOnLabTest`；见 [`19`](../part-04-wiring-and-boundaries/02-depends-on.md) |

### 4.6 关键误区：为什么 `@PostConstruct` 里调用 `@Transactional/@Async` 常常“不生效”？

一句话：**因为 `@PostConstruct` 发生在 “before-init BPP” 窗口，而 AOP proxy 往往在 “after-init BPP” 才产生。**

因此下面这种写法经常让人误判：

```java
@PostConstruct
void init() {
  transactionalMethod(); // self-invocation + proxy 尚未产生 => 事务/AOP 通常不会生效
}
```

更可靠的替代方案（按“时机更靠后”排序）：

- **需要等容器里其他单例就绪**：用 `SmartInitializingSingleton`（见上表与 [`26`](../part-04-wiring-and-boundaries/09-smart-initializing-singleton.md)）
- **需要 start/stop 与顺序**：用 `SmartLifecycle`（见 [`27`](../part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md)）
- **Spring Boot 场景需要等应用就绪**：用 `ApplicationRunner` / `ApplicationReadyEvent`

> 经验法则：`@PostConstruct` 适合“让自己可用”，不适合作为“执行一段需要 AOP/事务/异步语义的业务入口”。

### 4.7 顺序控制：`dependsOn` 影响初始化与销毁（但它不是注入规则）

`dependsOn` 的定位非常容易被误用：它解决的是**顺序**，不是“依赖注入”。

- 生效点：`AbstractBeanFactory#doGetBean` 在创建 A 之前先 `getBean(dep)`（因此会强制拉起 lazy bean）
- 销毁顺序：通常是依赖边的逆序（先销毁 dependent，再销毁 dependency）
- 依赖图：关系会被写进 `DefaultSingletonBeanRegistry` 的 `dependentBeanMap` / `dependenciesForBeanMap`

对应可运行证据：

- `SpringCoreBeansDependsOnLabTest#dependsOn_forcesInitializationOrder_evenWithoutDirectDependencies`
- `SpringCoreBeansDependsOnLabTest#dependsOn_triggersLazyDependencyInstantiation`
- `SpringCoreBeansDependsOnLabTest#dependsOn_affectsDestroyOrder_viaDependentBeanMap`

更完整的机制与排障表见：[`19. dependsOn：强制初始化顺序（即使没有显式依赖）`](../part-04-wiring-and-boundaries/02-depends-on.md)

### 4.8 容器级 start/stop：`SmartLifecycle` 的 phase 与 stop(callback) 语义

如果读者关心的是“组件何时启动/何时停止”，而不是“bean 何时初始化/何时销毁”，那其定位更接近容器生命周期问题：

- start：phase 升序
- stop：phase 反序
- 对 `SmartLifecycle`，容器通常调用 `stop(Runnable callback)`（用于支持异步 stop）；**不调用 callback 可能导致关闭阶段等待超时**

对应可运行证据：

- `SpringCoreBeansSmartLifecycleLabTest#smartLifecycleStartsInPhaseOrder_andStopsInReverseOrder`
- `SpringCoreBeansSmartLifecycleLabTest#containerStopsSmartLifecycle_viaStopCallbackMethod_notStopMethod`

延伸阅读：[`27. SmartLifecycle：start/stop 时机与 phase 顺序`](../part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md)

---

## 5. 生命周期与 Scope 的交互（重点）

创建语义：

- singleton：通常在容器 refresh 时创建（除非 `@Lazy`，或没有被预实例化）
- prototype：每次 `getBean` 都 new 一个（可能发生在注入时，也可能发生在 `ObjectProvider.getObject()` 调用时）

销毁语义：

- singleton：容器关闭时触发 destroy callbacks
- prototype：容器通常不触发 destroy callbacks（需要调用方自己管理）

这也是为什么 prototype 更接近“容器帮读者 new，一次性交付”，而不是“完整托管生命周期”。

### 5.1 prototype 销毁语义补齐：为什么不会自动销毁？怎么手动触发？

一句话结论：

- **prototype 的销毁不是容器的职责，而是“创建者（调用方）”的职责**

其原因如下：

- singleton：容器会缓存实例，并在 close 时统一遍历销毁（`destroySingletons` 主线）
- prototype：容器每次 `getBean` 都 new 一个并返回给调用方，但通常不会把这些实例登记到“待销毁列表”里

若确实需要触发 prototype 的销毁回调（例如释放连接/文件句柄），需要显式调用销毁 API：

- `ConfigurableBeanFactory#destroyBean(beanName, instance)`

对应可运行入口：

- `SpringCoreBeansPrototypeDestroySemanticsLabTest`

---

## 6. 调试与断点：把“生命周期”变成可定位问题

推荐断点（够用版）：

- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#initializeBean`
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`
- `DefaultSingletonBeanRegistry#destroySingletons`
- `DisposableBeanAdapter#destroy`

补充断点（把“触发者/顺序/容器级生命周期”也观察到）：

- `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`（`@PostConstruct`）
- `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeDestruction`（`@PreDestroy`）
- `AbstractBeanFactory#doGetBean`（`dependsOn` 生效点：先 `getBean(dep)`）
- `DefaultSingletonBeanRegistry#registerDependentBean`（依赖图与销毁逆序）
- `DefaultListableBeanFactory#preInstantiateSingletons`（`SmartInitializingSingleton` 触发窗口）
- `DefaultLifecycleProcessor#startBeans` / `#stopBeans`（`SmartLifecycle` phase 顺序与 stop(callback)）

推荐观察点（watch list）：

- `beanName`
- `bean` vs `exposedObject`（是否被 after-init 替换/代理化）
- `mbd` 上的 `initMethodName` / destroyMethodName（配置级回调是否声明）
- `mbd.getDependsOn()`（顺序控制输入）
- `dependentBeanMap` / `dependenciesForBeanMap`（依赖图快照，用于解释销毁顺序）

## 可复现闭环（基于 `SpringCoreBeansAwareInfrastructureLabTest`）

用这一组测试把“回调依赖基础设施”的结论固化成断言：

1) **`BeanFactoryAware` 不依赖 BPP，裸 `BeanFactory` 也能触发**
   - 断言：`beanFactory()` 非空
2) **`ApplicationContextAware` 依赖基础设施 BPP**
   - 断言：没有 `ApplicationContextAwareProcessor` 时为 null
3) **回调触发点在 before-init**
   - 断点：`BeanPostProcessor#postProcessBeforeInitialization`

---

## 源码与断点

- 建议优先从 Lab 的断言反推调用链，再定位到关键类/方法设置断点。
- 若在真实项目里遇到“顺序/代理/回调不符合直觉”，先把目标 beanName 加条件断点，再看 `exposedObject` 是否被替换。

## 最小可运行实验（Lab）

- 推荐入口（覆盖：回调顺序 / Aware 基础设施 / prototype 销毁语义 / 顺序控制 / 容器级 start-stop）：
  - `SpringCoreBeansLifecycleCallbackOrderLabTest`
  - `SpringCoreBeansAwareInfrastructureLabTest`
  - `SpringCoreBeansPrototypeDestroySemanticsLabTest`
  - `SpringCoreBeansDependsOnLabTest`
  - `SpringCoreBeansSmartInitializingSingletonLabTest`
  - `SpringCoreBeansSmartLifecycleLabTest`
- 推荐命令：
  - `mvn -pl :spring-core-beans test`
  - 或单独运行：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLifecycleCallbackOrderLabTest test`

## 排障决策表（生命周期/回调：从“没执行”到“证据链”）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| `@PostConstruct` 没执行 | 没有对应 BPP；或 bean 不在容器托管链路里 | `beanFactory.getBeanPostProcessors()` 是否包含 `CommonAnnotationBeanPostProcessor`；断点 `applyBeanPostProcessorsBeforeInitialization` 是否命中目标 beanName | 确保走完整 `ApplicationContext#refresh`；不要绕过容器 new；必要时手动注册注解处理器 | `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansAwareInfrastructureLabTest` |
| `afterPropertiesSet`/`initMethod` 没执行 | bean 没走 `initializeBean`（例如早期返回了短路对象/被替换导致误判） | 断点 `AbstractAutowireCapableBeanFactory#initializeBean`；观察 `exposedObject` 是否被替换 | 先确认创建主线是否命中 `initializeBean`；若被 proxy 替换，分清 raw vs exposed | `SpringCoreBeansLifecycleCallbackOrderLabTest` |
| `@PreDestroy` 没执行 | context 没 close；或是 prototype（默认不托管销毁） | 断点 `AbstractApplicationContext#doClose` / `DefaultSingletonBeanRegistry#destroySingletons`；prototype 不会进入 `disposableBeans` | 确保关闭容器；prototype 需要调用方显式销毁（`destroyBean`） | `SpringCoreBeansPrototypeDestroySemanticsLabTest` |
| 容易误以为“获取到的就是原对象”，但行为像被代理 | after-init BPP 返回了另一个对象（proxy/wrapper） | 断点 `applyBeanPostProcessorsAfterInitialization`；观察 `bean` vs `result` | 把“最终暴露对象”当作事实来源，不要假设 raw 就是 exposed | `SpringCoreBeansLifecycleCallbackOrderLabTest`（结合 creation trace） |
| `@PostConstruct` 里调用 `@Transactional/@Async` 方法不生效（看起来像没进拦截器） | proxy 尚未产生 + self-invocation 绕过 proxy | 断点 `applyBeanPostProcessorsBeforeInitialization` 与 `applyBeanPostProcessorsAfterInitialization` 对照：`@PostConstruct` 在 before-init；after-init 才可能替换为 proxy；对照 `SpringCoreBeansProxyingPhaseLabTest` 的 self-invocation 现象 | 把入口移到更靠后钩子（`SmartInitializingSingleton`/`SmartLifecycle`/Boot ready 事件），或改用显式模板（如 `TransactionTemplate`） | `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansProxyingPhaseLabTest` |
| lazy bean 明明标了 lazy-init，但启动时就被创建 | 被 `dependsOn` 拉起（或被 eager consumer 依赖） | 断点 `AbstractBeanFactory#doGetBean`：读取 `mbd.getDependsOn()` 并 `getBean(dep)`；或在依赖解析 `doResolveDependency` 看到 eager consumer 触发创建 | 去掉 `dependsOn`，优先改显式 DI；或把 lazy 放到注入点（`@Lazy`/`ObjectProvider`） | `SpringCoreBeansDependsOnLabTest` / `SpringCoreBeansLazyLabTest` |
| 组件未 start/stop（或顺序不符合预期） | `SmartLifecycle` 的 `isAutoStartup/phase` 误判，或 stop(callback) 未 callback | 断点 `DefaultLifecycleProcessor#startBeans`/`#stopBeans`，观察 phase 分组与调用点（stop 通常走 `stop(Runnable)`） | 实现正确的 `phase` 与 `isAutoStartup`；对 stop(callback) 必须调用 callback | `SpringCoreBeansSmartLifecycleLabTest` |
| 关闭时卡住/很慢 | destroy 回调做了重 IO/长耗时；或有依赖链导致逐个销毁很慢 | 断点 `DisposableBeanAdapter#destroy`；看具体 bean 的 destroy 方法耗时 | 缩短 destroy；拆依赖；把重任务移出销毁回调 | 结合本章断点闭环复盘 |

## 面试常问（生命周期：顺序、触发者与边界）

### Q1：`initializeBean(...)` 的核心顺序是什么？哪些点最易误述？

- 标准答案（可复述）：
  - Aware → before-init BPP（这里可能触发 `@PostConstruct`）→ init callbacks（`afterPropertiesSet`/`initMethod`）→ after-init BPP（这里经常产生 proxy，决定最终暴露对象）。
- 证据链（方法级）：
  - `AbstractAutowireCapableBeanFactory#initializeBean`
  - `#applyBeanPostProcessorsBeforeInitialization`
  - `#invokeInitMethods`
  - `#applyBeanPostProcessorsAfterInitialization`
- 最小复现：
  - `SpringCoreBeansLifecycleCallbackOrderLabTest`

### Q2：为什么 prototype 默认不会触发 `@PreDestroy`？如何证明？

- 标准答案（可复述）：
  - prototype 的销毁不由容器统一托管；容器不会在 close 时遍历销毁它创建过的所有 prototype 实例，调用方需要显式销毁。
- 证据链（方法级）：
  - singleton 销毁主线：`DefaultSingletonBeanRegistry#destroySingletons`
  - 手动销毁入口：`ConfigurableBeanFactory#destroyBean`
- 最小复现：
  - `SpringCoreBeansPrototypeDestroySemanticsLabTest`

### Q3：为什么说 `@PostConstruct/@PreDestroy` 不是“语法自带生命周期”？

- 标准答案（可复述）：
  - 它们是“容器生命周期”，靠 `BeanPostProcessor` 在创建/销毁链路中触发；脱离 `ApplicationContext` 的 bootstrap（或没有注册对应 BPP）就不会发生。
- 证据链（方法级）：
  - 创建链路：`applyBeanPostProcessorsBeforeInitialization`
  - 销毁链路：`DisposableBeanAdapter#destroy`（包含 DestructionAware BPP 与 JSR-250）

### Q4：`SmartInitializingSingleton` / `SmartLifecycle` / `ContextRefreshedEvent` 分别发生在 refresh 的哪个窗口？如何选？

- 标准答案（可复述）：
  - `SmartInitializingSingleton`：发生在 **`preInstantiateSingletons` 收尾**（非 lazy 单例创建完之后）。
  - `SmartLifecycle`：发生在 **`finishRefresh` 阶段由 `LifecycleProcessor` 统一 start/stop**（且受 phase 影响）。
  - `ContextRefreshedEvent`：发生在 **refresh 收尾事件发布**（通常在 lifecycle start 之后发布）。
- 证据链（方法级）：
  - `DefaultListableBeanFactory#preInstantiateSingletons`（触发 `afterSingletonsInstantiated`）
  - `AbstractApplicationContext#finishRefresh` → `DefaultLifecycleProcessor#onRefresh`（start）
  - `AbstractApplicationContext#publishEvent`（`ContextRefreshedEvent`）
- 最小复现：
  - `SpringCoreBeansSmartInitializingSingletonLabTest`
  - `SpringCoreBeansSmartLifecycleLabTest`
  - `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`（观察 `ContextRefreshedEvent`）

## 自检要点
应能够回答：

1) 初始化回调顺序大致如何（Aware / @PostConstruct / afterPropertiesSet / initMethod / after-init BPP）？
2) prototype 的销毁为什么默认不会在 context close 时触发？如何在 Lab/断点里验证？
3) 为什么在 `@PostConstruct` 里调用 `@Transactional/@Async` 经常“不生效”？应该换到哪个生命周期钩子？
4) `dependsOn` 与 `SmartLifecycle phase` 分别解决哪类“顺序问题”？如何在断点里观察到它们的生效点？
5) 若怀疑“某个回调没执行/代理没生效”，可先定位到 refresh 的哪一段？下哪两个断点？

## 小结与下一章

- 小结：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
- 下一章：[第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](05-post-processors.md)

<!-- BOOKIFY:START -->

上一章：[第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）](03-scope-and-prototype.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06. 容器扩展点：BFPP vs BPP（定义层 vs 实例层）](05-post-processors.md)

<!-- BOOKIFY:END -->
