# 第 16 章：05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`AbstractAutowireCapableBeanFactory#doCreateBean` / `#populateBean` / `#initializeBean` / `DefaultSingletonBeanRegistry#destroySingletons`
    - 推荐 Lab：`SpringCoreBeansLifecycleCallbackOrderLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）](015-04-scope-and-prototype.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](017-06-post-processors.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）**
- 阅读方式建议：先跑 `SpringCoreBeansLifecycleCallbackOrderLabTest` 把“顺序”变成断言，再回到正文把顺序映射到关键方法。

!!! summary "本章要点"

    - 生命周期不是“一个 init-method”，而是一条稳定骨架：**instantiate → populate → initialize → (use) → destroy**。
    - `@PostConstruct/@PreDestroy` 不是 Java 语法“自带”的生命周期；它们依赖容器注册了相应的 `BeanPostProcessor`（典型是 `CommonAnnotationBeanPostProcessor`）。
    - 最终暴露对象可能是 proxy：初始化后（after-init）BPP 可以返回“另一个对象”。这也是为什么生命周期与代理经常绑在一起讲。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansAwareInfrastructureLabTest` / `SpringCoreBeansPrototypeDestroySemanticsLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansAwareInfrastructureLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPrototypeDestroySemanticsLabTest.java`

## 机制主线

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

这也是为什么要把生命周期和 [06. 容器扩展点：BFPP vs BPP](017-06-post-processors.md) 放在一起理解：

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

1) **Aware 发生在 init callbacks 之前**：很多 init 逻辑需要先拿到 beanName/BeanFactory 等容器信息
2) **`@PostConstruct` 发生在 before-init BPP 链路中**：它不是“硬编码步骤”，而是某个 BPP 触发
3) **after-init 可能返回代理**：最终暴露对象可能不是原始实例

### 1.3.1 回调与代理交织：回调到底发生在谁身上？

- **`@PostConstruct` / `afterPropertiesSet`**：发生在 **raw bean** 上（proxy 还未产生）
- **after-init BPP**：可能返回 **proxy**，此后容器对外暴露的是 proxy
- **`@PreDestroy`**：通常由 `DestructionAwareBeanPostProcessor` 触发，仍然作用在 target 上  
  - 若依赖 `DisposableBean` 接口，且 proxy 不实现该接口，容易出现“销毁回调没进”的误判
- **`SmartInitializingSingleton`**：在单例全部实例化后回调，通常作用于 **最终暴露对象**（可能是 proxy）

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

## 2. Aware 系列回调：真实作用、触发者与发生时机

很多人把 Aware 理解成“知道自己叫什么名字”，但对原理/框架岗来说，更关键的是：

- 它发生在生命周期的哪一段？
- 是谁触发这些回调？
- 如果容器不具备对应的基础设施，会发生什么？

容易说错的一点（面试官很爱追）：

- **Aware 发生在 initialize 阶段，不是 populate（注入）阶段。**
- 注入通常已经完成（字段/构造器参数已经有值），Aware 是在此基础上把“容器信息”补给 bean，然后才进入 `@PostConstruct` 等 init 回调链路。

对应可跑入口：

- `SpringCoreBeansAwareInfrastructureLabTest`

---

## 3. 本模块的“可观测”例子：把顺序固化成断言

若只靠日志理解生命周期，很容易被“并发/代理/顺序差异”骗。

本仓库推荐的方式是：**用事件列表 + 断言把顺序固化**。

可以直接跑：

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

---

## 5. 生命周期与 Scope 的交互（重点）

创建语义：

- singleton：通常在容器 refresh 时创建（除非 `@Lazy`，或没有被预实例化）
- prototype：每次 `getBean` 都 new 一个（可能发生在注入时，也可能发生在 `ObjectProvider.getObject()` 调用时）

销毁语义：

- singleton：容器关闭时触发 destroy callbacks
- prototype：容器通常不触发 destroy callbacks（需要调用方自己管理）

这也是为什么 prototype 更像“容器帮读者 new，一次性交付”，而不是“完整托管生命周期”。

### 5.1 prototype 销毁语义补齐：为什么不会自动销毁？怎么手动触发？

一句话结论：

- **prototype 的销毁不是容器的职责，而是“创建者（调用方）”的职责**

原因并不神秘：

- singleton：容器会缓存实例，并在 close 时统一遍历销毁（`destroySingletons` 主线）
- prototype：容器每次 `getBean` 都 new 一个并返回给调用方，但通常不会把这些实例登记到“待销毁列表”里

若确实需要触发 prototype 的销毁回调（例如释放连接/文件句柄），需要显式调用销毁 API：

- `ConfigurableBeanFactory#destroyBean(beanName, instance)`

对应可跑入口：

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

推荐观察点（watch list）：

- `beanName`
- `bean` vs `exposedObject`（是否被 after-init 替换/代理化）
- `mbd` 上的 `initMethodName` / destroyMethodName（配置级回调是否声明）

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

- 推荐入口（覆盖：回调顺序 / Aware 基础设施 / prototype 销毁语义）：
  - `SpringCoreBeansLifecycleCallbackOrderLabTest`
  - `SpringCoreBeansAwareInfrastructureLabTest`
  - `SpringCoreBeansPrototypeDestroySemanticsLabTest`
- 推荐命令：
  - `mvn -pl :spring-core-beans test`
  - 或单跑：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLifecycleCallbackOrderLabTest test`

## 排障决策表（生命周期/回调：从“没执行”到“证据链”）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| `@PostConstruct` 没执行 | 没有对应 BPP；或 bean 不在容器托管链路里 | `beanFactory.getBeanPostProcessors()` 是否包含 `CommonAnnotationBeanPostProcessor`；断点 `applyBeanPostProcessorsBeforeInitialization` 是否命中目标 beanName | 确保走完整 `ApplicationContext#refresh`；不要绕过容器 new；必要时手动注册注解处理器 | `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansAwareInfrastructureLabTest` |
| `afterPropertiesSet`/`initMethod` 没执行 | bean 没走 `initializeBean`（例如早期返回了短路对象/被替换导致误判） | 断点 `AbstractAutowireCapableBeanFactory#initializeBean`；观察 `exposedObject` 是否被替换 | 先确认创建主线是否命中 `initializeBean`；若被 proxy 替换，分清 raw vs exposed | `SpringCoreBeansLifecycleCallbackOrderLabTest` |
| `@PreDestroy` 没执行 | context 没 close；或是 prototype（默认不托管销毁） | 断点 `AbstractApplicationContext#doClose` / `DefaultSingletonBeanRegistry#destroySingletons`；prototype 不会进入 `disposableBeans` | 确保关闭容器；prototype 需要调用方显式销毁（`destroyBean`） | `SpringCoreBeansPrototypeDestroySemanticsLabTest` |
| 容易误以为“拿到的就是原对象”，但行为像被代理 | after-init BPP 返回了另一个对象（proxy/wrapper） | 断点 `applyBeanPostProcessorsAfterInitialization`；观察 `bean` vs `result` | 把“最终暴露对象”当作事实来源，不要假设 raw 就是 exposed | `SpringCoreBeansLifecycleCallbackOrderLabTest`（结合 creation trace） |
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

## 自检要点
应能够回答：

1) 初始化回调顺序大致如何（Aware / @PostConstruct / afterPropertiesSet / initMethod / after-init BPP）？
2) prototype 的销毁为什么默认不会在 context close 时触发？如何在 Lab/断点里验证？
3) 若怀疑“某个回调没执行/代理没生效”，可先定位到 refresh 的哪一段？下哪两个断点？

<!-- BOOKIFY:START -->

上一章：[第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）](015-04-scope-and-prototype.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06. 容器扩展点：BFPP vs BPP（定义层 vs 实例层）](017-06-post-processors.md)

<!-- BOOKIFY:END -->
