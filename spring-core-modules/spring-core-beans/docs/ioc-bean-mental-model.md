# Bean 运行机制：从 BeanDefinition 到最终暴露对象
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：可先运行章首 Lab，将“定义不等于实例、最终暴露对象不一定等于原始实例”固化为断言；随后回到正文，结合主线与断点完成证据链验证。

    观察对象：Bean 运行机制：从 BeanDefinition 到最终暴露对象。
    主线位置：`ApplicationContext#refresh` 主线：注册定义（BeanDefinition）→ 定义层处理（BFPP/BDRPP）→ 注册 BPP 链 → 创建/注入/初始化（doCreateBean）→ 最终暴露对象（可能是 proxy）。

    对照入口：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansBeanCreationTraceLabTest`。需要下探源码时，可以从 `org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 问题：为什么同一个 “bean” 在调试器里会出现好几种形态

很多容器相关的问题，表面上看是一个简单现象：“已经把类交给 Spring 了，为什么注入的对象却不是预期的类？”
进一步追下去，调试器里又会出现更多对象：`BeanDefinition`、merged `RootBeanDefinition`、原始实例、proxy 等。

这些对象并不是“实现细节噪音”，而是容器机制的分层结果。本章要做的，是把这组分层固定成可复用的心智模型，让排障时的第一个问题变得清晰：

> 当前遇到的问题属于定义层、创建层，还是“最终暴露对象”层？

官方参考（Spring Framework 6.2.x；本仓库基线 6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

!!! example "本章配套实验（先运行，后阅读）"

    - Lab：
      - `SpringCoreBeansContainerLabTest`
      - `SpringCoreBeansBeanCreationTraceLabTest`
    - `SpringCoreBeansProxyingPhaseLabTest`
    - 测试文件：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`

## 解释：三层模型 + 最终暴露对象

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

容器可以被理解为三层结构（这是后续排障分层的基础）：

1. **输入层（inputs）**：注解、`@Bean`、`@Import`、XML、programmatic 注册等。
2. **定义层（definitions）**：解析输入并注册 `BeanDefinition`（“如何构造”的配方与元数据）
3. **实例层（instances）**：按定义创建对象、注入依赖、执行回调、注册销毁钩子

在三层之上，再补一个读者最常在排障时遇到、但最容易被忽略的概念：

4. **最终暴露对象（exposed object）**：容器对外返回的对象（`getBean()`/注入点获取到的对象），它可能在多个阶段被替换/包装为 proxy。

> 需要内化的一句关键表述：
> **定义层回答“有没有/谁注册的/配方是什么”，实例层回答“什么时候创建/注入选了谁/最终是不是 proxy”。**

### 机制阐释：对象为何会被替换（条件 → 分支 → 结果）

- **条件**：bean 是否走完整 `doCreateBean`，以及是否被 BPP/early reference 替换
- **分支**：`applyBeanPostProcessorsAfterInitialization` / `getEarlyBeanReference`
- **结果**：
  - 走完整创建链 → 有机会被 after-init BPP 替换成 proxy
  - 进入 early reference → 最终对象可能不是 raw instance
- **断点入口**：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

## 四类对象对照表：调试对象与语义

| 在调试器里看到的对象 | 它代表什么 | 最直接 API/入口 | 读者通常用它回答什么问题 |
| --- | --- | --- | --- |
| `BeanDefinition` | 原始定义（配方） | `BeanFactory#getBeanDefinition(beanName)` | “到底有没有注册？谁注册的？scope/lazy/dependsOn 是什么？” |
| merged `RootBeanDefinition` | 最终生效配方 | `AbstractBeanFactory#getMergedLocalBeanDefinition(beanName)` | “为什么最终是 Root？parent 合并后哪些元数据生效？” |
| raw instance（原始实例） | 刚创建出来的对象 | `doCreateBean` 内部的 `bean`/`bw.getWrappedInstance()` | “构造器/工厂方法到底有没有执行？注入发生了吗？” |
| exposed object（最终暴露对象） | 容器对外的最终返回 | `initializeBean` 之后的返回值 / `getBean()` 的结果 | “为何获取到的是 proxy？由哪个阶段完成替换？” |

### 1.1 四类对象在典型“变形场景”里的映射（最容易引起混淆的 4 类）

> 目的：不增加术语数量，仅将“调试器中对象所指代的语义”归纳为可复述规则。

1. **FactoryBean：`getBean(name)` 可能返回“产品”，而非“工厂”**
   - raw instance：FactoryBean 本身（工厂对象）
   - exposed object：`FactoryBean#getObject()` 的返回（产品对象）
   - 关键入口：`AbstractBeanFactory#getObjectForBeanInstance`
   - 识别方式：`&name` 可用于获取工厂对象（见 [08](ioc-factorybean.md)）

2. **scoped proxy：获取到的是“代理”，真实目标对象位于另一个 beanName 下**
   - 定义层：通常会出现 `scopedTarget.<beanName>`（目标定义）+ `<beanName>`（代理定义）
   - raw instance：目标对象（按 scope 创建/缓存）
   - exposed object：代理对象（通常是 singleton 代理，但每次调用可路由到不同目标）
   - 关键入口：定义层改写（`ScopedProxyMode`）+ 运行期取值（`ScopedObject#getTargetObject`）
   - 关联章节：scope 主线见 [04](ioc-scope-and-prototype.md)，深入见 [28](wiring-custom-scope-and-scoped-proxy.md)

3. **循环依赖（early reference）：容器给过依赖方“暂时引用”，但最终对外对象可能不同**
   - early：`getEarlyBeanReference` 可能返回 raw，也可能返回 proxy（由 BPP/AOP 决策）
   - final：`applyBeanPostProcessorsAfterInitialization` 可能再次替换成最终 proxy/wrapper
   - 风险点：early 与 final 不一致 → raw 注入绕过代理 / fail-fast（见 [09](ioc-circular-dependencies.md)、[16](internals-early-reference-and-circular.md)）

4. **ResolvableDependency / 外部对象：能注入但不是“可枚举 Bean”**
   - 表象：`@Autowired` 成功，但 `getBeanDefinitionNames`/`getBeansOfType` 找不到
   - 原因：注入走的是依赖解析链路（实例层），不一定依赖 `BeanDefinition`（定义层）
   - 关联章节：见本章「能注入 ≠ 一定是 Bean」与 [20](wiring-resolvable-dependency.md)、[43](aot-autowirecapablebeanfactory-external-objects.md)

## 方法级主线：refresh → doCreateBean → 最终暴露对象

> 落点：无需背全流程，但要能说出“关键窗口在哪、证据链在哪”。

refresh 的骨架（只保留与本章相关的关键节点）：

1. `AbstractApplicationContext#refresh`
2. `invokeBeanFactoryPostProcessors`（定义层处理：BDRPP/BFPP 改定义/加定义）
3. `registerBeanPostProcessors`（把实例层拦截链装好：注解注入/AOP/回调都依赖它）
4. `finishBeanFactoryInitialization` → `preInstantiateSingletons`（批量创建非 lazy 单例）

### 2.1 关键分支解释（围绕 refresh 的 if/then）

- **是否预实例化**：`mbd.isLazyInit()` 决定是否在 `preInstantiateSingletons` 被创建
- **是否走 BPP 链**：BPP 注册发生在 `registerBeanPostProcessors`，过早创建会错过
- **是否进入 early reference**：循环依赖窗口期决定最终暴露对象形态
- **是否为 FactoryBean**：`getObjectForBeanInstance` 决定返回的是工厂还是产品

单个 bean 的创建主线（方法级锚点）：

1. `AbstractBeanFactory#doGetBean(beanName)`
2. `AbstractAutowireCapableBeanFactory#doCreateBean(beanName, mbd, args)`
3. `populateBean(beanName, mbd, bw)`（注入发生点：依赖解析、属性填充、类型转换）
4. `initializeBean(beanName, bean, mbd)`（回调与 BPP：Aware → before-init BPP → init callbacks → after-init BPP）
5. 返回 exposed object（可能是 proxy）

## 可复现闭环（基于 `SpringCoreBeansBeanCreationTraceLabTest`）

运行该 Lab 后，至少需要复述 3 条结论：

1. **raw instance 与最终暴露对象可能不同**
   - 断点：`applyBeanPostProcessorsAfterInitialization`
   - 断言：`result != bean`
2. **注入发生在 populateBean 阶段**
   - 断点：`populateBean`
   - 断言：属性填充发生在初始化之前
3. **最终暴露对象在 initializeBean 之后确定**
   - 断点：`initializeBean`
   - 断言：`getBean()` 返回的是 initialize 之后的返回值

## 三个“最终对象被替换”的高频入口

可将“为何获取到 proxy”归纳为三类入口（明确其存在即可）：

1. **实例化前短路（pre）**：`resolveBeforeInstantiation`
   - 典型：`InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation` 返回替身对象（见 [15](internals-pre-instantiation-short-circuit.md)）
2. **循环依赖窗口期（early）**：`getEarlyBeanReference`
   - 典型：early 引用与最终暴露形态一致性问题（见 [16](internals-early-reference-and-circular.md)）
3. **初始化后替换（after-init）**：`postProcessAfterInitialization`
   - 典型：AOP/事务/懒代理等最常见 proxy 产生点（见 [31](wiring-proxying-phase-bpp-wraps-bean.md)）

> 补充两个“表面上像替换，但本质是 getBean 返回语义不同”的高频来源：
>
> 4) **FactoryBean 产品语义**：`getObjectForBeanInstance` 决定 `getBean(name)` 返回“工厂”还是“产品”
> 5) **scoped proxy 的双定义**：`beanName` 对应 proxy，`scopedTarget.beanName` 对应真实目标（见 [04](ioc-scope-and-prototype.md)）

## 补充：能注入 ≠ 一定是 Bean（ResolvableDependency / 外部对象）

当将“Bean 三层模型”应用于实际排障时，常会遇到一个高频的反预期点：

- **有些对象可以通过 `@Autowired` 注入到字段/参数中，但它并不是一个“可枚举的 Bean”**；
- 这类对象通常来自两条路径：
  1. **ResolvableDependency**：容器预置的一些可注入对象（例如 `ApplicationContext`、`Environment` 等），它们不一定对应一个 `BeanDefinition`；
  2. **外部对象 + AutowireCapableBeanFactory**：对象由应用代码通过 `new` 创建，但可借助容器完成依赖注入/回调（依然会触发依赖解析链路）。

将该边界放回三层模型，可解释“为何在容器中无法枚举该对象，但注入仍可成功”，并避免将问题误判为“未注册 Bean”。

### 方法级证据链（至少运行一次）

1. ResolvableDependency 的证据链（容器预置、但不是 BeanDefinition）
   - 注册位置：`AbstractApplicationContext#prepareBeanFactory`
   - 关键 API：`DefaultListableBeanFactory#registerResolvableDependency`
   - 命中位置：`DefaultListableBeanFactory#doResolveDependency`（会优先检查 resolvableDependencies）

2. 外部对象的证据链（对象不是容器创建，但能力来自容器）
   - 入口：`AutowireCapableBeanFactory#autowireBean` / `initializeBean` / `destroyBean`
   - 关键结论：容器可以给它“注入与回调”，但它不是容器生命周期自动托管的 bean（除非显式注册/销毁）

**关联阅读顺序：**

- `ioc-resolvable-dependency.md`（能注入但不是 Bean）
- `ioc-autowirecapablebeanfactory-external-objects.md`（外部对象如何接入容器能力）

## 排障决策表（将主观判断转化为可验证结论）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


| 现象 | 先分层到哪里 | 证据（断点/观察点） | 最可能根因 | 修复思路 |
| --- | --- | --- | --- | --- |
| `NoSuchBeanDefinitionException` | 定义层 | `containsBeanDefinition` / `getBeanDefinition` 是否存在 | 根本没注册；或条件未满足/被排除 | 回到注册入口与条件：见 [02](ioc-bean-registration.md)、[21](boot-spring-boot-auto-configuration.md) |
| 注入报 `NoUniqueBeanDefinitionException` | 实例层（依赖解析） | `doResolveDependency`→`findAutowireCandidates`→`determineAutowireCandidate` | 候选太多且没收敛信号 | 用 `@Qualifier/@Primary` 收敛；或让 auto-config back-off（见 [03](ioc-dependency-injection-resolution.md)、[33](wiring-autowire-candidate-selection-primary-priority-order.md)） |
| 易误判为原始对象，但行为表现为 proxy | 实例层（最终暴露对象） | `applyBeanPostProcessorsAfterInitialization` 里 `result != bean` | after-init BPP 替换了对象 | 追溯到具体 BPP，再回看其注册顺序与触发条件（见 [31](wiring-proxying-phase-bpp-wraps-bean.md)） |
| 已声明 `@Bean`，但容器中未出现 | 定义层（注解基础设施） | `ConfigurationClassPostProcessor` 是否存在并执行 | 未装配 annotation processors / 配置类未被解析 | 先补齐注解基础设施（见 [22](internals-container-bootstrap-and-infrastructure.md)） |
| `BeanCurrentlyInCreationException`（循环依赖） | 实例层（创建窗口） | `doCreateBean`（`earlySingletonExposure`）+ `getSingleton(..., allowEarlyReference=true)` | 依赖图存在环；或 early/final 一致性保护触发 | 优先消环；其次用 `ObjectProvider/@Lazy` 打断；不宜以“能够启动”为目标进行规避（见 [09](ioc-circular-dependencies.md)） |
| “注入成功但容器里搜不到” | 边界层（ResolvableDependency/外部对象） | `registerResolvableDependency` / `autowireBean` 是否被调用 | 这是容器提供的“可注入能力”，不是普通 bean | 回到本章补充与 [20](wiring-resolvable-dependency.md)、[43](aot-autowirecapablebeanfactory-external-objects.md) |
| 类型不符合预期（注入/获取结果与预期不一致） | 定义层 + 最终暴露层 | `getObjectForBeanInstance` / 是否存在 `scopedTarget.*` | FactoryBean 产品语义 / scoped proxy 双定义 | 先识别是 FactoryBean 还是 scoped proxy，再回到对应章节（见 [08](ioc-factorybean.md)、[04](ioc-scope-and-prototype.md)） |

## 面试常问（标准答案 + 方法级证据链）

### Q1：BeanDefinition、bean instance、最终 `getBean()` 返回的对象分别是什么？

- 标准答案（可复述）：
  - BeanDefinition 是配方；bean instance 是创建出来的原始对象；`getBean()` 返回最终暴露对象。最终暴露对象可能来自 BPP 的替换（pre/early/after-init），也可能来自 `getBean` 的“语义转义”（FactoryBean 产品、scoped proxy）。
- 证据链（方法级）：
  - 定义层：`registerBeanDefinition`
  - 创建层：`doCreateBean` / `populateBean` / `initializeBean`
  - 最终暴露：`applyBeanPostProcessorsAfterInitialization` / `getEarlyBeanReference` / `getObjectForBeanInstance`
- 最小复现：
  - `SpringCoreBeansContainerLabTest.beanDefinitionIsNotTheBeanInstance`
  - `SpringCoreBeansProxyingPhaseLabTest`

### Q2：为什么强调“优先关注定义层，再关注实例层”能显著提升排障效率？

- 标准答案（可复述）：
  - 定义层回答“有没有/谁注册的/配方是什么”，实例层回答“何时创建/注入选了谁/是否被代理替换”；将问题分层后，断点入口和观察点会立刻收敛，避免在巨大调用栈中盲目追踪。

### Q3：BeanFactory vs ApplicationContext 的核心差别是什么？

- 标准答案（可复述）：
  - BeanFactory 是底层容器（创建/注入/生命周期骨架）；ApplicationContext 在其之上增加应用级设施（事件、多语言、资源、环境等），并在 `refresh` 中把这些设施接入主线，让它们变成“可注入、可观测、可协作”的能力。
- 最小复现：
  - `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`

## 验证标准：能把一个 bean 分成四类对象

需要用 3 句说明清楚：

1. 为什么说“注册 bean”注册的第一性对象是 BeanDefinition？
2. 为什么 `getBean()` 返回的不一定是原始实例？（在哪 3 个阶段可能被替换）
3. 看到一个异常时，如何先分层到定义层/实例层，并给出第一个断点入口？

## 收束：Bean 的最终形态不是原始实例那么简单

- 本章把 Bean 的最小分层模型固定成“四个对象”：BeanDefinition / merged RootBeanDefinition / raw instance / exposed object。
- 下一章开始进入 Boot 的自动装配：可以观察到“定义层”的复杂度显著上升，但排障方法论不变（先分层，再证据链）。


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansProxyingPhaseLabTest` / `SpringCoreBeansResolvableDependencyLabTest` / `SpringCoreBeansAutowireCapableBeanFactoryLabTest`
- 测试文件：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanFactoryVsApplicationContextLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResolvableDependencyLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAutowireCapableBeanFactoryLabTest.java`

<!-- BOOKIFY:END -->
