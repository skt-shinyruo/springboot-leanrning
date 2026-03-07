# 断点地图（容器主线：可复用断点/观察点清单）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。

    本章围绕断点地图（容器主线：可复用断点/观察点清单）展开，主线可以概括为：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansLabTest`。需要下探源码时，可以从 `org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 导读

这页的定位很明确：它不试图解释所有概念，而是把 `spring-core-beans` 里最常用的断点与观察点整理成“一页纸”。

读者在真实项目里遇到异常时，往往并不缺“名词”，而是缺两样东西：

1) 这个现象属于 refresh 的哪一段（定义层 / 注册处理器 / 创建单例 / 注入 / 初始化 / 代理替换）？
2) 第一断点应该下在哪个方法，才能最快看到决定性变量？

本页就是为这两个问题服务的。

建议用法（3 步即可）：

1) 先运行一个最小 Lab，确认入口与断言可复现（例如 `SpringCoreBeansLabTest`）。
2) 再按本页的 C1–C7 断点组观察关键数据结构的变化。
3) 最后回到 [知识地图](appendix-knowledge-map.md) 或 [关键分支矩阵](guide-branch-decision-matrix.md)，把观察到的变化收敛成“结论 + 方法级证据链”。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansLabTest`

## 机制主线（按 refresh 时间线组织）

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

> 快速入口：若不确定该从哪组断点开始，可先用“现象 → 断点组”选一个起点，再回到 [知识地图](appendix-knowledge-map.md) 补齐“章节 + 推荐 Lab”。

| 现象（Symptoms） | 首选断点组 | 说明 |
| --- | --- | --- |
| BeanDefinition 没注册 / 扫描不生效 / `NoSuchBeanDefinitionException` | [C2](#c2) | 先确认 registry 是否写入了定义（定义层优先） |
| BFPP/BDRPP 顺序/时机导致“定义被改写不符合预期” | [C3](#c3) | 定义层的“最后改写机会”，先把阶段与排序看清 |
| 注入失败（NoSuch/NoUnique）/ 多候选歧义 / 注入到了不是预期实现 | [C6](#c6) | 候选集合收集与收敛规则都在这里发生 |
| 代理不生效 / self-invocation / “像绕过 AOP” | [C4](#c4) / [C7](#c7) | 先证 BPP 链是否完整（C4），再证 proxy 替换是否发生（C7） |
| 循环依赖 / early reference / raw vs wrapped | [C5](#c5) | 三层缓存与 early exposure 的窗口期在这里最容易被观察到 |
| `@Value` 占位符/SpEL/类型转换：值不对/缺失不失败/原样字符串 | [C3](#c3) / [C6](#c6) | 先证 `resolveEmbeddedValue` 的输出，再看注入点的转换与绑定 |

<a id="c1"></a>
### C1. refresh 总入口（把阶段看清）

- 入口断点：
  - `AbstractApplicationContext#refresh`
- 观察点（Watch List）：
  - 当前阶段（看调用栈即可）
  - `beanFactory`（通常是 `DefaultListableBeanFactory`）

<a id="c2"></a>
### C2. 定义注册：BeanDefinitionRegistry / 扫描 / @Configuration 解析

- 入口断点：
  - `ConfigurationClassPostProcessor#processConfigBeanDefinitions`
  - `ClassPathBeanDefinitionScanner#doScan`（component-scan）
  - `registry`（beanDefinitionCount、beanDefinitionNames）
  - `BeanDefinition` 的来源与类型（Annotated / Root / Generic）
- 决定性分支：
  - Full vs Lite（`@Configuration` 是否被增强）

<a id="c3"></a>
### C3. BFPP/BDRPP：定义层的“最后改写机会”

- 入口断点：
  - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
  - `PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors`
  - `processedBeans`（已处理列表）
  - 执行顺序分段（PriorityOrdered → Ordered → 无序）

<a id="c4"></a>
### C4. registerBeanPostProcessors：为什么注解能工作

- 入口断点：
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
  - `beanFactory.getBeanPostProcessorCount()`
  - 已注册的关键 BPP（AABPP/CABPP/Autowired 等）

<a id="c5"></a>
### C5. 单例预实例化：实例层主线（createBean/doCreateBean）

- 入口断点：
  - `DefaultListableBeanFactory#preInstantiateSingletons`
  - `AbstractAutowireCapableBeanFactory#createBean`
  - `AbstractAutowireCapableBeanFactory#doCreateBean`
  - `singletonObjects` / `earlySingletonObjects` / `singletonFactories`
  - 当前 beanName 是否在 `singletonsCurrentlyInCreation`
- 决定性分支：
  - 是否触发“实例化前短路”（`postProcessBeforeInstantiation` 返回非 null）
  - 是否触发 early reference（循环依赖/代理介入）

<a id="c6"></a>
### C6. populateBean：依赖注入与候选收敛

- 入口断点：
  - `AbstractAutowireCapableBeanFactory#populateBean`
  - `DefaultListableBeanFactory#doResolveDependency`
  - `DefaultListableBeanFactory#findAutowireCandidates`
  - `DefaultListableBeanFactory#determineAutowireCandidate`
  - 候选集合大小变化（歧义/收敛）
  - by-name fallback 是否触发（依赖名匹配 beanName）
- 决定性分支：
  - `@Primary/@Priority/@Qualifier` 的优先级链

<a id="c7"></a>
### C7. initializeBean：生命周期回调与“代理替换发生点”

- 入口断点：
  - `AbstractAutowireCapableBeanFactory#initializeBean`
  - `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`
  - `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`
  - `wrappedBean` 与 `bean` 是否发生替换（proxying）
- 决定性分支：
  - `postProcessAfterInitialization` 是否返回代理（这通常决定“读者最终获取到的对象是谁”）

## 阶段内关键对象变化（断点地图补充）

为了让“阶段感”更可见，这里把关键对象变化再压缩成 1 张表：

| 阶段 | 主要变化 | 推荐观察点（最小够用） |
| --- | --- | --- |
| 定义注册 | `beanDefinitionMap` 增长，`BeanDefinition` 来源被标记 | `registry.getBeanDefinitionCount()` / `beanDefinition.getSource()` |
| BFPP/BDRPP | 定义被改写/补齐（占位符、配置类、属性覆盖） | `postProcessBeanFactory` / `processedBeans` |
| 注册 BPP | `beanPostProcessors` 列表最终排序固定 | `beanFactory.getBeanPostProcessorCount()` |
| 单例创建 | `singletonObjects` / `earlySingletonObjects` / `singletonFactories` 发生写入 | `singletonsCurrentlyInCreation` / 三级缓存 |

## 主线高频分支最小集（断点地图版）

无需记住所有分支，但必须能“观察到”这 5 个最常见的分支触发点：

1) **singleton vs prototype**：`AbstractBeanFactory#doGetBean` → `mbd.isPrototype()`
2) **dependsOn 强制顺序**：`AbstractBeanFactory#getBean` → `mbd.getDependsOn()`
3) **parent BeanFactory 回退**：`containsBeanDefinition(beanName)` 为 false → `parentBeanFactory.getBean`
4) **FactoryBean vs 产品对象**：`AbstractBeanFactory#getObjectForBeanInstance`
5) **类型匹配（含泛型）**：`AbstractBeanFactory#isTypeMatch` / `ResolvableType` 判定

## 源码调用链与断点（建议从 Lab 反推）

更完整的“入口测试 → 断点调用链”建议，优先看：

- 30 分钟快启：`guide-quickstart-30min.md`
- 深入分析指南：`guide-deep-dive-guide.md`

## 最小可运行实验（Lab）

可先运行这些入口再设置断点：

- refresh 主线：`SpringCoreBeansBootstrapInternalsLabTest`
- 依赖解析（候选收敛）：`SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans`
- 代理替换发生点：`SpringCoreBeansBeanCreationTraceLabTest#beanCreationTrace_recordsPhases_andExposesProxyReplacement`
- 循环依赖/early reference：`SpringCoreBeansEarlyReferenceLabTest`

## 证据链样例（现象 → 断点 → 变量 → 结论）

**现象**：明明写了 `@Qualifier`，却还是报 `NoUniqueBeanDefinitionException`
**断点**：`DefaultListableBeanFactory#doResolveDependency` → `findAutowireCandidates` → `determineAutowireCandidate`
**观察变量**：
- `descriptor.getAnnotations()`（是否真的带上 Qualifier）
- `matchingBeans.keySet()`（候选集合是否被正确收集）
- `autowiredBeanName`（最终候选是否收敛成功）
**结论**：如果 Qualifier 未参与收敛，优先检查注入点是否被 `AutowiredAnnotationBeanPostProcessor` 正确解析。

## 条件断点模板（降噪）：让断点“只为目标 bean 服务”

断点地图如果不配“降噪条件”，体验会非常差（一次 refresh 会进同一个方法上千次）。建议把条件断点当成默认习惯：

1. **按 beanName 过滤（最常用）**
   - `beanName.equals("xxx")`
   - `mbd.getBeanClassName().contains("Foo")`（类名过滤）
2. **按阶段过滤（避免打到不相关阶段）**
   - 只在 `isEagerInit` / `isSingletonCurrentlyInCreation` 等关键分支为 true 时停住
3. **按“候选收敛”过滤**
   - 只在 `candidates.size() > 1` 或出现 `@Qualifier/@Primary` 决策点时停住

> 目标：把“能看到”升级成“只看到需要看的”。这也是为什么本模块大量章节都强调 watch list：观察点比断点位置更决定效率。

## 常见误区与边界
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


- 只盯某个注解：建议先把“发生在 refresh 的哪一段”确定下来（C1-C7）。
- 把 proxy 当成原始对象：建议在 `applyBeanPostProcessorsAfterInitialization` 处观察 `wrappedBean` 替换点。
- 循环依赖只看三层缓存：建议结合“代理介入”与“raw 注入/早期引用”的边界用例一起看。

## 小结

- 本页作为断点索引页，建议与各章的“源码锚点/入口测试/排障分流”配合使用。
<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    建议 先跑 `SpringCoreBeansLabTest`，再用 `SpringCoreBeansBootstrapInternalsLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“常见误区与边界”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：遇到具体现象时，回到 [知识地图](appendix-knowledge-map.md) 选“章节 + 断点组 + Lab”；需要固化排障流程时，回到 [生产排障清单](appendix-production-troubleshooting-checklist.md)。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansLabTest`
- Lab：`SpringCoreBeansBootstrapInternalsLabTest`
- Lab：`SpringCoreBeansBeanCreationTraceLabTest`
- Lab：`SpringCoreBeansEarlyReferenceLabTest`

<!-- BOOKIFY:END -->

## 面试使用方式（把断点地图变成“证据链话术”）

面试里无需“背源码”，但需要能说清：

1) 应在哪个阶段设置断点（refresh 哪一段）
2) 在断点中观察哪 3 个变量即可下结论
3) 用哪个 Lab 复现并证明它

推荐复习入口：`appendix-interview-playbook.md` / `appendix-debugger-pack.md`

## 自检要点
应能够做到：

1) 说出 refresh 主线、依赖解析、bean 创建、单例缓存、BPP 代理这五类问题各自的“第一断点”。
2) 解释为什么“断点 + watch list”比“全局搜栈”更高效。
3) 用本模块任意一个 Lab，把断点地图完成验证一次并能复述观察到的关键变化。
