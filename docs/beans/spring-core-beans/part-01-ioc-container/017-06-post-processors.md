# 第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：容器扩展点：BFPP vs BPP（以及它们能/不能做什么）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansContainerLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 16 章：05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）](016-05-lifecycle-and-callbacks.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 18 章：07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）](018-07-configuration-enhancement.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest` / `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansStaticBeanFactoryPostProcessorLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

## 机制主线

这一章是理解 Spring “高级玩法”的关键。很多你觉得像“魔法”的特性，本质都是某个 post-processor 在某个阶段做了事。

先记住一句话：

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

你需要体会的是：**BFPP 并没有直接“改对象”，而是改了“怎么创建对象的配方”。**

### 1.3 常见 BFPP（了解它们存在很重要）

你未来会经常遇到：

- 占位符/属性解析相关（把 `${...}` 换成真实值）
- 配置类处理（把 `@Configuration` / `@Bean` / `@Import` 解析成 BeanDefinition）

也就是说：很多“注解配置能工作”，背后本身就依赖 BFPP/registry post-processor。

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
- 你从容器拿到的最终对象反映出修改

### 2.3 BPP 与“你以为的对象”之间的差距

因为 BPP 有机会把实例替换成代理。

## 3. 顺序（Ordering）：为什么同一个扩展点里顺序也很重要

多个 BFPP/BPP 同时存在时，顺序会决定最终效果。

Spring 通常用这些规则决定顺序：

- `PriorityOrdered`（最优先）
- `Ordered`
- 没有顺序接口（最后）

学习阶段你不需要背接口继承树，但要知道：

- 顺序是可控的
- 顺序问题会导致“某些增强没生效 / 生效得很奇怪”

## 3.1 你必须补齐的第三类：`BeanDefinitionRegistryPostProcessor`（BDRPP）

很多人只分 BFPP 与 BPP，但真正做源码级排障时，你需要补齐第三类：

- **BDRPP：改的是“注册表”（registry）**
  - 能新增/删除/修改 `BeanDefinition`
  - 发生得更早：在 BFPP 之前（因此影响面更大）
  - 典型代表：`ConfigurationClassPostProcessor`（它让 `@Configuration/@Bean/@ComponentScan` 等能工作）

一旦你能分清这三类，你就能回答一类非常常见的问题：

> “这个 bean 到底是在什么时候、被谁注册进来的？”

## 3.2 源码级时间线：refresh 里它们到底在哪发生？

你可以把它们粗略放进 `AbstractApplicationContext#refresh` 的时间线（只记住关键点即可）：

1. **invoke BFPP/BDRPP**：先让“定义”稳定下来（能注册/改 BeanDefinition）
2. **register BPP**：把所有 BPP 注册进容器（后面创建 bean 时会用到）
3. **finishBeanFactoryInitialization**：开始创建非 lazy 的 singleton（此时 BPP 会大量介入）

这也是为什么：

## 3.3 源码解析：`PostProcessorRegistrationDelegate` 的两段核心算法

这一节的目标是把你前面记住的结论（BDRPP 更早、BFPP 改定义、BPP 改实例、顺序受 PriorityOrdered/Ordered 影响），落到 Spring 源码里最核心的两段逻辑：

1) `invokeBeanFactoryPostProcessors`：**定义层**（registry/factory）post-processors 的执行算法  
2) `registerBeanPostProcessors`：**实例层**（BeanPostProcessor）链路的注册算法

### 3.3.1 `invokeBeanFactoryPostProcessors`：为什么 BDRPP 会“先 registry 再 factory”，还要“反复扫描”

这个方法的设计动机其实很朴素：

- **BDRPP 有能力在 registry 阶段注册新的 BeanDefinition**
- 而新注册的 BeanDefinition 里，可能又包含新的 BDRPP/BFPP
- 因此必须先把 registry 相关的事情跑到“稳定”（否则定义层永远不确定）

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

你从这段伪代码应该得到 3 个稳定结论（非常重要）：

1) **BDRPP 的 `postProcessBeanDefinitionRegistry` 可能会多轮执行**：不是因为 Spring “爱绕”，而是为了把 registry 稳定下来  
2) **BDRPP 的 registry 回调一定发生在 BFPP 之前**：否则 BFPP 可能看不到新注册的定义（或改不到正确的定义）  
3) **“顺序接口”在这里才真正产生决定性作用**：PriorityOrdered/Ordered/无序不是装饰，而是直接改变执行顺序

### 3.3.2 `registerBeanPostProcessors`：为什么 BPP 也要分组注册？为什么会出现“没被所有 BPP 处理”的警告？

`BeanPostProcessor` 属于实例层扩展点，但它的注册同样发生在 refresh 的中前段：因为后面一旦进入 `preInstantiateSingletons`，大量 bean 会被创建，必须先把 BPP 链准备好。

精简伪代码（同样只保留关键分叉）：

“为什么会出现没被所有 BPP 处理”的现象？根因只有一句话：

> **BPP 是“创建时拦截链”，不是“创建后补丁”。**  
> 某个 bean 如果在 BPP 链未完整时就被创建，那么后续 BPP 不会 retroactively 生效。

你在资料里经常看到一句建议：

> “BFPP/BPP 这种 post-processor 类型的 @Bean，尽量声明为 `static`。”

这不是编码风格偏好，而是一个非常具体的时机问题：

- BFPP/BDRPP 的实例会在 `invokeBeanFactoryPostProcessors` 阶段被创建
- 如果 BFPP 是一个 **non-static `@Bean` 工厂方法**，Spring 为了调用这个方法，就必须先实例化配置类（`@Configuration` bean）
- 但配置类此时被创建得太早，会错过后续注册的普通 BPP（因为 BPP 链还没完整）
- 如果 BFPP 是 **static `@Bean` 工厂方法**，Spring 可以直接调用静态工厂方法创建 BFPP，不需要提前实例化配置类，从而避免该配置类“过早出生”

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

BFPP 本该在“定义层”工作，如果你在里面直接拿 bean（实例层），可能会触发一些 bean 提前创建，导致：

- 后续的 BPP 没机会介入
- 生命周期回调顺序变得反直觉

### 4.2 BPP 写成“全局修改器”导致不可预测

如果你在 BPP 里对很多 bean 做复杂逻辑，会让系统变得：

学习阶段建议把 BPP 当作“理解容器机制”的窗口，而不是“解决业务问题的日常手段”。

## 面试常问（BFPP / BPP / BDRPP）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPostProcessorOrderingLabTest test`

### 1.1 它在什么时候运行？

在容器已经收集完 `BeanDefinition` 之后、创建大部分 bean 之前运行。

### 1.2 本模块的实验：BFPP 修改定义再生效

- `SpringCoreBeansContainerLabTest.beanFactoryPostProcessorCanModifyBeanDefinitionBeforeInstantiation()`

实验做的事情是：

### 2.1 它在什么时候运行？

### 2.2 本模块的实验：BPP 修改实例

- `SpringCoreBeansContainerLabTest.beanPostProcessorCanModifyBeanInstanceAfterInitialization()`

实验做的事情是：

这也是为什么很多时候你 debug 会发现：

- 你注入的类型看起来是 `MyService`
- 但运行时对象可能是 `MyService$$SpringCGLIB$$...` 或 JDK proxy

在真实项目里，这个“代理/增强”的典型实现就是 AutoProxyCreator（AOP/事务/缓存/安全等最终都会走到“BPP 替换 bean”这一层）。  
对应完整版本的容器主线与断点导航：见 `docs/aop/spring-core-aop/part-02-autoproxy-and-pointcuts/07-autoproxy-creator-mainline.md`。

- BFPP/BDRPP 更像“编译期改元数据”
- BPP 更像“运行期改对象/换代理”

精简伪代码（足够对照断点理解，不追求逐行一致）：

### 3.3.3 为什么很多 BFPP/BPP 建议写成 `static @Bean`（源码级原因 + 最小复现）

本模块提供了最小可运行复现（事件断言，不依赖日志）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansStaticBeanFactoryPostProcessorLabTest.java`

## 3.4 断点闭环（用本仓库 Lab/Test 跑一遍）

- BFPP 影响定义，再影响实例：
  - `SpringCoreBeansContainerLabTest#beanFactoryPostProcessorCanModifyBeanDefinitionBeforeInstantiation`
- BPP 影响实例（甚至换成代理）：
  - `SpringCoreBeansContainerLabTest#beanPostProcessorCanModifyBeanInstanceAfterInitialization`
- 顺序规则（PriorityOrdered/Ordered/无序）：
  - `SpringCoreBeansPostProcessorOrderingLabTest`
- BDRPP 能在注册阶段加定义：
  - `SpringCoreBeansRegistryPostProcessorLabTest`
- 手工注册 BPP 的顺序陷阱：
  - `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`

### 3.5 推荐断点（够用版）

- 难以推理
- 难以测试
- 难以 debug

下一章我们看一个特别常见、也特别容易误解的点：`@Configuration(proxyBeanMethods=...)`。
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
推荐断点：`PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`、`PostProcessorRegistrationDelegate#registerBeanPostProcessors`、`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

## 常见坑与边界

你不需要逐行背源码，但你必须能回答：“为什么它这么设计？这个设计会造成哪些现象/坑？”

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

这也是为什么 [06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](017-06-post-processors.md) 的坑 4.1 会出现：如果你在 BDRPP/BFPP 阶段（定义层）就 `getBean()`，就可能把某些 bean 提前创建出来，导致它错过后续 BPP（包括代理、@Autowired/@PostConstruct 等处理器）。

你可以把它当作面试题的“可证据化答案”：  
**non-static BFPP 迫使配置类早实例化 ⇒ 配置类错过普通 BPP ⇒ 行为/增强出现顺序陷阱。**

## 4. 典型误用与坑

- 常问：BFPP、BPP、BDRPP 分别是什么？分别能做什么？
  - 答题要点：BDRPP 能在“注册阶段”新增定义；BFPP 能在实例化前修改定义；BPP 介入实例创建链路，能改实例，甚至替换成 proxy。
- 常见追问：为什么很多 BFPP 建议写成 `static @Bean`？
  - 答题要点：避免过早实例化配置类/减少循环依赖与顺序陷阱；更贴近“定义层扩展点”的职责。

## 小结与下一章

- 入口时间线：
  - `AbstractApplicationContext#refresh`
  - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- 创建单个 bean 的主线（看 BPP 介入位置）：
  - `AbstractAutowireCapableBeanFactory#doCreateBean`
  - `AbstractAutowireCapableBeanFactory#initializeBean`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest` / `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansStaticBeanFactoryPostProcessorLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

上一章：[05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）](016-05-lifecycle-and-callbacks.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[07. @Configuration 增强：proxyBeanMethods 与 @Bean 语义](018-07-configuration-enhancement.md)

<!-- BOOKIFY:END -->
