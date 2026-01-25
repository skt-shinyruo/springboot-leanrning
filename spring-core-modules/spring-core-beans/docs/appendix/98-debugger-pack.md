# Debugger Pack（断点包总入口）

> 目标：用最少的入口测试，把“主线时间线 / 关键分支 / 排障策略 / 性能并发”串成可运行的断点闭环。

## 推荐入口（从这里开始）

> 如果你不知道该从哪一章开始，先从这里跑起来：先把“容器主线”看见，再去追分支与边界。

1) 主线调用链入口（refresh → doCreateBean）  
   - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansMainlineCallChainLabTest test`  
   - 建议断点：`AbstractApplicationContext#refresh` / `AbstractAutowireCapableBeanFactory#doCreateBean`
2) 断点包入口（高频分支与排障）  
   - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBreakpointPackLabTest test`  
   - 目标：把“关键分支矩阵”跑成可断点证据
3) 排障 Playbook 入口（现象 → 根因 → 验证）  
   - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansTroubleshootingPlaybookLabTest test`
4) 性能与并发入口（缓存/并发 getBean）  
   - 运行：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansPerformanceConcurrencyLabTest test`

## 断点包索引（文档 ↔ 入口）

> 你可以把“文档章节”当成一张地图，把“入口测试”当成可重复的传送门：  
> **先跑入口测试定位阶段 → 再回对应章节读关键分支/变量含义**。

- 主线时间线：[`part-00-guide/010-03-mainline-timeline.md`](../part-00-guide/010-03-mainline-timeline.md)  
  - 入口：[`SpringCoreBeansMainlineCallChainLabTest`](../../src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansMainlineCallChainLabTest.java)
- 断点地图：[`part-00-guide/013-02-breakpoint-map.md`](../part-00-guide/013-02-breakpoint-map.md)  
  - 入口：[`SpringCoreBeansBreakpointPackLabTest`](../../src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansBreakpointPackLabTest.java)
- 关键分支矩阵：[`part-00-guide/011-04-branch-decision-matrix.md`](../part-00-guide/011-04-branch-decision-matrix.md)  
  - 入口：[`SpringCoreBeansIocBranchMatrixLabTest`](../../src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansIocBranchMatrixLabTest.java)
- Bean 注册入口（扫描 / `@Import`）：[`part-01-ioc-container/02-bean-registration.md`](../part-01-ioc-container/02-bean-registration.md)  
  - 入口：[`SpringCoreBeansComponentScanLabTest`](../../src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansComponentScanLabTest.java) / [`SpringCoreBeansImportLabTest`](../../src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportLabTest.java)
- 依赖注入解析（候选者选择/解析路径）：[`part-01-ioc-container/014-03-dependency-injection-resolution.md`](../part-01-ioc-container/014-03-dependency-injection-resolution.md)  
  - 入口：按本章 “可跑入口（证据链）” 执行（并在 `DefaultListableBeanFactory#resolveDependency` 下断点）
- 循环依赖与 early reference：[`part-03-container-internals/16-early-reference-and-circular.md`](../part-03-container-internals/16-early-reference-and-circular.md)  
  - 入口：[`SpringCoreBeansCircularDependencyBoundaryLabTest`](../../src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansCircularDependencyBoundaryLabTest.java)
- 排障 Playbook：[`appendix/025-90-common-pitfalls.md`](./025-90-common-pitfalls.md)  
  - 入口：[`SpringCoreBeansTroubleshootingPlaybookLabTest`](../../src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansTroubleshootingPlaybookLabTest.java)
- 性能与并发：[`SpringCoreBeansPerformanceConcurrencyLabTest`](../../src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansPerformanceConcurrencyLabTest.java)

## 关键断点建议（主线优先）

> 断点不是越多越好。建议你把断点分成两类：  
> **入口断点**（确定阶段）+ **机制断点**（确定分支）。

### A) 入口断点（确定阶段）

- `AbstractApplicationContext#refresh`（启动主入口）
- `DefaultListableBeanFactory#preInstantiateSingletons`（容器启动阶段：实例化单例）
- `AbstractBeanFactory#doGetBean`（所有 getBean 的汇聚点）

### B) 机制断点（确定分支）

- `AbstractAutowireCapableBeanFactory#doCreateBean`（创建主线：实例化→填充→初始化）
- `AbstractAutowireCapableBeanFactory#populateBean`（依赖注入/属性填充）
- `AbstractAutowireCapableBeanFactory#initializeBean`（Aware/BeforeInit/Init/AfterInit）
- `DefaultListableBeanFactory#resolveDependency`（依赖解析的关键分支点）
- `DefaultSingletonBeanRegistry#getSingleton`（三级缓存/early reference 的主入口）

## 使用策略（1-2 次跳转定位问题）

1) **先定位阶段**：你的现象属于哪一段？（注册 / 注入解析 / 创建 / 初始化 / 代理替换）  
2) **再定位分支**：按 [关键分支矩阵](../part-00-guide/011-04-branch-decision-matrix.md) 把候选路径收敛到 1–2 个分支  
3) **最后用入口测试证明**：跑对应 Lab，在关键变量上“看见分支触发条件”

---

## 观察点速查（Watchpoints：你应该盯哪些变量）

> 这里列的是“容器内部的状态表”。你不需要记住所有，但要知道它们分别回答什么问题。

- `DefaultListableBeanFactory#beanDefinitionMap`：**定义从哪来、注册了多少**
- `DefaultSingletonBeanRegistry#singletonObjects`：**哪些单例已经创建完成**
- `DefaultSingletonBeanRegistry#earlySingletonObjects`：**哪些对象以 early reference 形式被提前暴露**
- `DefaultSingletonBeanRegistry#singletonFactories`：**谁提供 early reference（通常与代理/循环依赖边界相关）**
- `AbstractBeanFactory#alreadyCreated`：**哪些 beanName 已经进入过创建流程**
- `AbstractAutowireCapableBeanFactory#factoryBeanInstanceCache`：**FactoryBean 相关缓存（产品对象 vs 工厂对象边界）**

---

## 症状 → 断点建议（从“现象”跳到“分支”）

- **`NoSuchBeanDefinitionException`**：先看注册阶段  
  - 断点：`ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry` / `BeanDefinitionReaderUtils#registerBeanDefinition`
- **`NoUniqueBeanDefinitionException` / 注入歧义**：看候选者选择 + resolveDependency  
  - 断点：`DefaultListableBeanFactory#resolveDependency`
- **`BeanCurrentlyInCreationException` / 循环依赖**：看三级缓存与 early reference  
  - 断点：`DefaultSingletonBeanRegistry#getSingleton`
- **“BPP 不生效 / Aware 没回调 / init 顺序不对”**：看 post-processor 注册与 initializeBean  
  - 断点：`PostProcessorRegistrationDelegate#registerBeanPostProcessors` / `AbstractAutowireCapableBeanFactory#initializeBean`
