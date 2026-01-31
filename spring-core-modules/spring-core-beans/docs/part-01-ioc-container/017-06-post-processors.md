# 第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：容器扩展点：BFPP vs BPP（以及它们能/不能做什么）
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansContainerLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 16 章：05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）](016-05-lifecycle-and-callbacks.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 18 章：07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）](018-07-configuration-enhancement.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）**
- 阅读建议：建议先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

!!! summary "本章要点"

    - 读完本章，应能够用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见误区在哪里”。
    - 如果只看一眼：请先运行一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest` / `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansStaticBeanFactoryPostProcessorLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：“能/不能做什么”的方法级证明：为什么 BPP 不可靠改定义，为什么 BFPP 拿不到实例态。
    - B（边界反例）：反例：错误时机 getBean 导致 BPP 链不完整、手工注册破坏顺序的误区。
    - C（排障 SOP）：排障：某注解不生效/某增强不生效/代理不出现时，先定位缺哪个处理器与顺序问题。
    - D（断点观察）：断点：processor 收集/排序/注册/执行的关键入口方法。
    - E（面试复述）：面试追问：BDRPP 为什么更强？与 ImportBeanDefinitionRegistrar 的边界如何说明。
<!-- AE-DEEPENING:END -->
## 机制主线

这一章是理解 Spring “高级用法”的关键。许多看似“隐式行为”的特性，本质都是某个 post-processor 在某个阶段做了事。

先记住两句话：

- **BFPP 改定义**（`BeanDefinition`）
- **BPP 改实例**（bean object / proxy）

## 1. BFPP：`BeanFactoryPostProcessor`

因此它的典型能力是：

- 修改已有 `BeanDefinition`（属性、scope、依赖、lazy 等）
- （通过更底层的接口）注册额外的 `BeanDefinition`

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
  - `beanFactoryPostProcessorCanModifyBeanDefinitionBeforeInstantiation()`（定义层：先改配方，再实例化）

- 先注册 `ExampleBean` 的定义
- BFPP 在实例化前把 `value` 属性写进定义里
- 最终创建的实例读到了被修改的值

需要体会的是：**BFPP 并没有直接“改对象”，而是改了“怎么创建对象的配方”。**

### 1.3 常见 BFPP（了解它们存在很重要）

在实际项目中将频繁遇到：

- 占位符/属性解析相关（把 `${...}` 换成真实值）
- 配置类处理（把 `@Configuration` / `@Bean` / `@Import` 解析成 BeanDefinition）

也就是说：很多“注解配置能工作”，背后本身就依赖 BFPP/registry post-processor。

### 1.4 机制系统阐述：BFPP 如何改变最终行为（可运行示例）

- **条件**：定义层被改写（BeanDefinition 里的属性/占位符被替换）
- **分支**：`postProcessBeanFactory` 在实例化前执行
- **结果**：最终实例读到的是“被改写后的配方”，而不是原始定义
- **可运行证据**：`SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` / `SpringCoreBeansContainerLabTest`

## 2. BPP：`BeanPostProcessor`

在每个 bean 初始化前后都会被调用（更准确地说：在 bean 创建流程的某些钩子点）。

它的典型能力是：

- 修改 bean 实例的属性
- 用代理包装 bean（AOP 的基础）

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
  - `beanPostProcessorCanModifyBeanInstanceAfterInitialization()`（实例层：初始化后可改对象/甚至替换对象）

- 工厂方法先创建一个 `ExampleBean`
- BPP 在初始化后把它的 `value` 改成新值
- 从容器获取到的最终对象反映出修改

### 2.3 BPP 与“容易误以为的对象”之间的差距

因为 BPP 有机会把实例替换成代理。

### 2.4 常见基础设施处理器（谁让注解真正生效）

| 处理器 | 作用 | 层级 |
| --- | --- | --- |
| `ConfigurationClassPostProcessor` | 解析 `@Configuration/@Bean/@Import` | 定义层（BDRPP/BFPP） |
| `PropertySourcesPlaceholderConfigurer` | 解析 `${...}` 占位符 | 定义层（BFPP） |
| `AutowiredAnnotationBeanPostProcessor` | `@Autowired/@Value` 注入 | 实例层（BPP） |
| `CommonAnnotationBeanPostProcessor` | `@PostConstruct/@PreDestroy` | 实例层（BPP/DestructionAware） |
| `ApplicationContextAwareProcessor` | Aware 系列回调 | 实例层（BPP） |

### 2.5 进阶：四类 BPP 介入点速查（把“能改什么”说清楚）

当读者开始使用 BPP 进行业务扩展或排障时，常见误区并非“不会编写”，而是**不清楚 BPP 的介入位置及其可影响的层次**。因此，可按“介入点”将 BPP 概括为四类能力，而非仅记忆接口名称：

1. **实例化前（可能短路）**：典型接口 `InstantiationAwareBeanPostProcessor`
   - 方法级锚点（建议断点）：
     - `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`（返回非 null 会短路：跳过目标 bean 的实例化与后续 populate/initialize 主线）
     - `InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation`（返回 false 会跳过属性填充）
     - `InstantiationAwareBeanPostProcessor#postProcessProperties`（属性注入介入点：不要误当 init 回调）
   - 能做：在对象还没创建前返回一个替代对象（经常导致“完全跳过 doCreateBean 的后半段”）
2. **early reference（循环依赖救援/代理提前）**：典型接口 `SmartInstantiationAwareBeanPostProcessor`
   - 方法级锚点（建议断点）：
     - `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`（三级缓存/early exposure 的代理入口；与循环依赖强相关）
   - 能做：在三级缓存阶段就提供 early bean reference（常见于 AOP 提前暴露代理）
3. **merged definition（定义合并后再补充）**：典型接口 `MergedBeanDefinitionPostProcessor`
   - 方法级锚点（建议断点）：
     - `MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition`（定义层与实例层的桥：在 RootBeanDefinition 就绪后触发）
   - 能做：在“最终生效的 RootBeanDefinition”层面做准备工作（影响注入/回调/缓存判断）
4. **销毁前（清理/回调补齐）**：典型接口 `DestructionAwareBeanPostProcessor`
   - 方法级锚点（建议断点）：
     - `DestructionAwareBeanPostProcessor#postProcessBeforeDestruction`（destroy 链路入口之一；与 16 章销毁链路互链）
   - 能做：在 destroy 链路触发前执行回调（很多 `@PreDestroy`/资源清理问题会落在这里）

将该“介入点地图”贯通后，可回答：为何某个 bean 似乎“错过了某些处理器”？为何某些代理发生较早，而另一些发生较晚？

**关联阅读（把这张图落到本仓库可断言闭环）：**

- 实例化前短路：`15-pre-instantiation-short-circuit.md`
- early reference：`16-early-reference-and-circular.md`
- merged definition：`35-merged-bean-definition.md`
- 销毁链路：`17-lifecycle-callback-order.md`

## 3. 顺序（Ordering）：为什么同一个扩展点里顺序也很重要

多个 BFPP/BPP 同时存在时，顺序会决定最终效果。

Spring 通常用这些规则决定顺序：

- `PriorityOrdered`（最优先）
- `Ordered`
- 没有顺序接口（最后）

学习阶段无需背接口继承树，但要知道：

- 顺序是可控的
- 顺序问题会导致“某些增强没生效 / 生效得很奇怪”

## 3.1 必须补齐的第三类：`BeanDefinitionRegistryPostProcessor`（BDRPP）

很多人只分 BFPP 与 BPP，但真正做源码级排障时，需要补齐第三类：

- **BDRPP：改的是“注册表”（registry）**
  - 能新增/删除/修改 `BeanDefinition`
  - 发生得更早：在 BFPP 之前（因此影响面更大）
  - 典型代表：`ConfigurationClassPostProcessor`（它让 `@Configuration/@Bean/@ComponentScan` 等能工作）

一旦能够分清这三类，即可回答一类非常常见的问题：

> “这个 bean 到底是在什么时候、被谁注册进来的？”

## 3.2 源码级时间线：refresh 里它们到底在哪发生？

可以把它们粗略放进 `AbstractApplicationContext#refresh` 的时间线（只记住关键点即可）：

1. **invoke BFPP/BDRPP**：先让“定义”稳定下来（能注册/改 BeanDefinition）
2. **register BPP**：把所有 BPP 注册进容器（后面创建 bean 时会用到）
3. **finishBeanFactoryInitialization**：开始创建非 lazy 的 singleton（此时 BPP 会大量介入）

这也是为什么：

- 许多“注解能工作”，本质是在 `invokeBeanFactoryPostProcessors` 阶段把注解世界翻译成 BeanDefinition，并注册了后续所需的基础设施处理器。
- `BeanPostProcessor` 必须在大规模创建 bean 之前完成注册：否则部分 bean 可能过早实例化，从而错过后续 BPP 的处理（典型表现是 BeanPostProcessorChecker 提示）。
- 在 BDRPP/BFPP 阶段调用 `getBean()` 会触发实例化，导致时序错乱：容易误以为在“改定义”，实际已经在“造对象”了。

## 3.3 源码解析：`PostProcessorRegistrationDelegate` 的两段核心算法

这一节的目标是将前文结论（BDRPP 更早、BFPP 改定义、BPP 改实例、顺序受 PriorityOrdered/Ordered 影响），落到 Spring 源码里最核心的两段逻辑：

1) `invokeBeanFactoryPostProcessors`：**定义层**（registry/factory）post-processors 的执行算法
2) `registerBeanPostProcessors`：**实例层**（BeanPostProcessor）链路的注册算法

### 3.3.1 `invokeBeanFactoryPostProcessors`：为什么 BDRPP 会“先 registry 再 factory”，还要“反复扫描”

该方法的设计动机较为直接：

- **BDRPP 有能力在 registry 阶段注册新的 BeanDefinition**
- 而新注册的 BeanDefinition 里，可能又包含新的 BDRPP/BFPP
- 因此必须先将 registry 相关处理推进至“稳定状态”（否则定义层将长期处于不确定）

```text
invokeBeanFactoryPostProcessors(beanFactory):
  // 0) 先执行“外部手工注册”的 processors（例如 context.addBeanFactoryPostProcessor）

  // 1) 处理 BeanDefinitionRegistryPostProcessor（BDRPP）
  processed = set()

  // 1.1) PriorityOrdered BDRPP：可能注册新的 BDRPP，因此需要循环扫描
  repeat:
    current = find BDRPP names not in processed and implementing PriorityOrdered
    instantiate + sort(current)
    invoke postProcessBeanDefinitionRegistry on each
    processed.addAll(current)
  until no more

  // 1.2) Ordered BDRPP（同理可能注册新的 BDRPP）
  repeat ... Ordered ...

  // 1.3) Unordered BDRPP（同理）
  repeat ... remaining ...

  // 1.4) registry 阶段结束后，再统一调用所有 BDRPP 的 postProcessBeanFactory

  // 2) 再处理普通 BeanFactoryPostProcessor（BFPP）
  bfpp = find BFPP names not in processed
  group by PriorityOrdered / Ordered / unordered
  instantiate + sort each group, then invoke postProcessBeanFactory
```

从这段伪代码应该得到 3 个稳定结论（非常重要）：

1) **BDRPP 的 `postProcessBeanDefinitionRegistry` 可能会多轮执行**：不是因为 Spring “爱绕”，而是为了把 registry 稳定下来
2) **BDRPP 的 registry 回调一定发生在 BFPP 之前**：否则 BFPP 可能看不到新注册的定义（或改不到正确的定义）
3) **“顺序接口”在这里才真正产生决定性作用**：PriorityOrdered/Ordered/无序不是装饰，而是直接改变执行顺序

### 3.3.2 `registerBeanPostProcessors`：为什么 BPP 也要分组注册？为什么会出现“没被所有 BPP 处理”的警告？

`BeanPostProcessor` 属于实例层扩展点，但它的注册同样发生在 refresh 的中前段：因为后面一旦进入 `preInstantiateSingletons`，大量 bean 会被创建，必须先把 BPP 链准备好。

精简伪代码（同样只保留关键分叉）：

“为什么会出现没被所有 BPP 处理”的现象？根因可概括为：

> **BPP 是“创建时拦截链”，不是“创建后补丁”。**
> 某个 bean 如果在 BPP 链未完整时就被创建，那么后续 BPP 不会 retroactively 生效。

识别信号（典型表现）：

当在日志中看到 `BeanPostProcessorChecker` 的类似提示时，通常意味着“某个 bean 过早创建，错过了部分 BPP（尤其是 auto-proxying）”：

> Bean 'xxx' of type [...] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)

它**意味着**：

- 该 bean 的创建时机早于某些 BPP 的注册完成，后续 BPP 不会 retroactively 补偿生效

它**不意味着**：

- 一定是循环依赖
- 一定是 AOP/事务失效
- 一定需要采取强制性修复措施（有时只是基础设施 bean 的正常时序）

在资料里经常看到一句建议：

> “BFPP/BPP 这种 post-processor 类型的 @Bean，尽量声明为 `static`。”

这不是编码风格偏好，而是一个非常具体的时机问题：

- BFPP/BDRPP 的实例会在 `invokeBeanFactoryPostProcessors` 阶段被创建
- 如果 BFPP 是一个 **non-static `@Bean` 工厂方法**，Spring 为了调用这个方法，就必须先实例化配置类（`@Configuration` bean）
- 但配置类此时被创建得太早，会错过后续注册的普通 BPP（因为 BPP 链还没完整）
- 如果 BFPP 是 **static `@Bean` 工厂方法**，Spring 可以直接调用静态工厂方法创建 BFPP，不需要提前实例化配置类，从而避免配置类过早实例化

最小片段（对比关键点：static vs non-static）：

```java
@Configuration
static class NonStaticBfppConfig {
    @Bean
    BeanFactoryPostProcessor bfpp() { ... } // 需要先实例化配置类才能调用工厂方法
}

@Configuration
static class StaticBfppConfig {
    @Bean
    static BeanFactoryPostProcessor bfpp() { ... } // 不需要实例化配置类即可创建 BFPP
}
```

建议用这些测试把“时机”变成手感（每个都对应非常典型的真实问题）：

### 4.1 在 BFPP 里 `getBean()` 触发提前实例化

BFPP 本该在“定义层”工作，若在里面直接拿 bean（实例层），可能会触发一些 bean 提前创建，导致：

- 后续的 BPP 没机会介入
- 生命周期回调顺序变得反直觉

证据入口（对照最清晰）：

- `SpringCoreBeansEarlyGetBeanMissesBppLabTest`
  - 对照结论：正常情况下目标 bean 会被 after-init BPP 包装成 proxy；但在 BFPP 阶段过早 `getBean` 会让它以 raw 形态进入 singleton cache，从而错过后续 BPP（且不会 retroactive 补上）

### 4.2 BPP 写成“全局修改器”导致不可预测

若在 BPP 里对很多 bean 做复杂逻辑，会让系统变得：

- 难以推理（对象形态/回调顺序难以静态分析）
- 难以测试（全局副作用，单测很难隔离）
- 难以 debug（问题表现“漂移”，定位成本极高）

学习阶段建议把 BPP 当作“理解容器机制”的窗口，而不是“解决业务问题的日常手段”。

## 源码调用链（方法级）：把三类处理器放回 refresh 主线

在排障/面试里最常被追问的不是“名词解释”，而是：

> “它发生在 refresh 的哪一段？应能够说出 2–3 个关键方法把调用链串起来吗？”

最短调用链（够用版）：

1) `AbstractApplicationContext#refresh`
2) `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`（BDRPP/BFPP：定义层）
3) `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（BPP：实例层链路准备）
4) `DefaultListableBeanFactory#preInstantiateSingletons` → `AbstractAutowireCapableBeanFactory#doCreateBean`（创建阶段：BPP 介入点大量发生）

如果应能够把这 4 个点说清楚，再补一句“BDRPP 为什么要循环扫描”，基本就是“做过源码排障”的信号。

## 面试常问（BFPP / BPP / BDRPP）

> 目标：避免停留在概念记忆，应能够将“它发生在 refresh 哪一段 / 改了什么数据结构 / 为什么会导致某个现象”讲清楚。

- BFPP、BPP、BDRPP 分别是什么？分别能做什么？
  - BDRPP：registry 阶段可新增/修改定义（让“图继续长大”）；BFPP：实例化前改定义（改配方）；BPP：创建链路中改实例/换 proxy（改最终暴露对象）。
- 为什么很多 BFPP/BDRPP 建议写成 `static @Bean`？
  - 让 post-processor 在定义阶段创建时不必先实例化配置类，避免配置类过早实例化而错过后续 BPP（顺序陷阱可用本仓库 Lab 进行证据化验证）。
- 为什么会出现“某个 bean 没被所有 BPP 处理”的提示？
  - BPP 是创建时拦截链；bean 过早创建就会错过后续 BPP，后面的 BPP 不会 retroactively 生效。
- 为什么在 BDRPP/BFPP 里 `getBean()` 很危险？
  - 容易误以为在定义层“改配方”，但 `getBean()` 直接将调用推入实例层“造对象”，导致时序错乱、错过 BPP、回调顺序反直觉。
- BPP 到底能不能“换掉对象”？
  - 能。初始化后链路（after-init）返回值就是最终暴露对象；这就是 AOP/事务等“换壳”的根。

## 断点闭环（用本仓库 Lab/Test 运行一次）

- BFPP 改定义（改配方，再影响实例）：
  - `SpringCoreBeansContainerLabTest#beanFactoryPostProcessorCanModifyBeanDefinitionBeforeInstantiation`
- BFPP 过早 `getBean`（实例层提前创建，导致错过后续 BPP）：
  - `SpringCoreBeansEarlyGetBeanMissesBppLabTest`
- BPP 改实例（初始化后改对象/替换对象）：
  - `SpringCoreBeansContainerLabTest#beanPostProcessorCanModifyBeanInstanceAfterInitialization`
- 顺序规则（PriorityOrdered/Ordered/无序）：
  - `SpringCoreBeansPostProcessorOrderingLabTest`
- static `@Bean` 的时机陷阱复现（事件断言）：
  - `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`
- BDRPP 在 registry 阶段扩张定义：
  - `SpringCoreBeansRegistryPostProcessorLabTest`
- 手工注册 BPP 的顺序陷阱：
  - `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`

### 推荐断点（够用版）

- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

### 推荐观察点（watch list）

- `beanFactory.getBeanDefinitionCount()`（registry 阶段是否扩张）
- `beanFactory.getBeanPostProcessors()`（BPP 链是否已就位、顺序如何）
- `result != bean`（after-init 是否发生“换壳”）

## 可复现闭环（基于 `SpringCoreBeansContainerLabTest`）

完成该 Lab 后，至少应能够用 3 条结论解释 BFPP/BPP 的差异：

1) **BFPP 改定义，不改实例**
   - 断点：`postProcessBeanFactory` → `createBean`
   - 断言：实例读到的是“被改写后的配方”
2) **BPP 改实例，可替换对象**
   - 断点：`applyBeanPostProcessorsAfterInitialization`
   - 断言：`result != bean` 时暴露对象发生替换
3) **时机决定是否生效**
   - 断点：`invokeBeanFactoryPostProcessors` vs `registerBeanPostProcessors`
   - 断言：过早 `getBean` 会让目标 bean 错过后续 BPP

## 常见误区与边界（补一段“能落到源码的答案”）

无需逐行背源码，但必须能回答：“为什么它这么设计？这个设计会造成哪些现象/误区？”

```text
registerBeanPostProcessors(beanFactory):
  names = getBeanNamesForType(BeanPostProcessor)

  // A) 先注册一个“检查器”（BeanPostProcessorChecker）
  //    用于提示：某些 bean 在 BPP 链尚未完整时就被创建了，因此无法被所有 BPP 处理

  // B) 分三组：PriorityOrdered / Ordered / unordered
  //    先注册 PriorityOrdered，再注册 Ordered，最后注册无序
  //    注意：注册过程会 instantiate BPP（BPP 本身也是 bean）

  // C) internal BPP 往往会被最后再补一遍（确保排序稳定）
```

把上面这段伪代码翻译成一句“工程答案”：

> **non-static BFPP 迫使配置类早实例化 ⇒ 配置类错过普通 BPP ⇒ 行为/增强出现顺序陷阱。**

## 最小可运行实验（Lab）

- 本章推荐先运行这 5 个入口（覆盖定义层/实例层/顺序/时机/registry 扩张）：
  - `SpringCoreBeansContainerLabTest`
  - `SpringCoreBeansPostProcessorOrderingLabTest`
  - `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`
  - `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`
  - `SpringCoreBeansRegistryPostProcessorLabTest`
- 推荐命令：
  - `mvn -pl :spring-core-beans test`
  - 或者单独运行：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPostProcessorOrderingLabTest test`

## 小结与下一章

- 简要复述：
  - BDRPP/BFPP 改定义（改配方）；BPP 改实例（换壳/增强）
- 入口时间线（定位发生阶段）：
  - `AbstractApplicationContext#refresh`
  - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- 单个 bean 的主线（定位 BPP 介入点）：
  - `AbstractAutowireCapableBeanFactory#doCreateBean`
  - `AbstractAutowireCapableBeanFactory#initializeBean`

下一章将看一个非常容易误解但又极常见的点：`@Configuration(proxyBeanMethods=...)` 与 `@Bean` 的“方法调用语义”。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyGetBeanMissesBppLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest` / `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansStaticBeanFactoryPostProcessorLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansEarlyGetBeanMissesBppLabTest.java`

上一章：[05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）](016-05-lifecycle-and-callbacks.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[07. @Configuration 增强：proxyBeanMethods 与 @Bean 语义](018-07-configuration-enhancement.md)

<!-- BOOKIFY:END -->

## 自检要点
应能够用 3 句复述：

1) BFPP/BDRPP 与 BPP 的核心差异是什么（改定义 vs 改实例）？
2) 为什么“过早 getBean”会导致 bean 错过后续 BPP？如何用 Lab/断点证明？
3) 如何用 refresh 时间线定位：某个处理器应该在什么时候生效？
