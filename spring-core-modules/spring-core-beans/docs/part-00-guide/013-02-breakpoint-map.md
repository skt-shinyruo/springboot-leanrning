# 第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：断点地图（容器主线：可复用断点/观察点清单）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）](012-01-quickstart-30min.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 14 章：03. 依赖注入解析：类型/名称/@Qualifier/@Primary](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章目标：把 `spring-core-beans` 的“高频断点与观察点”收敛成一页纸，避免散落在多章。
- 使用方式：先跑一个方法级 Lab，然后按本页断点清单逐段观察“定义层 → 实例层 → 代理层”的变化。

!!! summary "本章要点"

    - 容器排障的第一原则：**先证明“发生在 refresh 的哪一段”**（定义注册 / PP 执行 / 单例创建 / 初始化 / 代理替换）。
    - 若希望看清的通常不是“某个注解怎么用”，而是：
      1. **数据结构在哪里被写入**（registry / beanDefinitionMap / singletonObjects）
      2. **哪个分支决定了后续行为**（排序 / 短路 / early reference / candidate 收敛）
      3. **读者拿到的对象到底是谁**（raw instance vs proxy）


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansLabTest`

## 机制主线（按 refresh 时间线组织）

### C1. refresh 总入口（把阶段看清）

- 入口断点：
  - `AbstractApplicationContext#refresh`
- 观察点（Watch List）：
  - 当前阶段（看调用栈即可）
  - `beanFactory`（通常是 `DefaultListableBeanFactory`）

### C2. 定义注册：BeanDefinitionRegistry / 扫描 / @Configuration 解析

- 入口断点：
  - `ConfigurationClassPostProcessor#processConfigBeanDefinitions`
  - `ClassPathBeanDefinitionScanner#doScan`（component-scan）
- 观察点：
  - `registry`（beanDefinitionCount、beanDefinitionNames）
  - `BeanDefinition` 的来源与类型（Annotated / Root / Generic）
- 决定性分支：
  - Full vs Lite（`@Configuration` 是否被增强）

### C3. BFPP/BDRPP：定义层的“最后改写机会”

- 入口断点：
  - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
  - `PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors`
- 观察点：
  - `processedBeans`（已处理列表）
  - 执行顺序分段（PriorityOrdered → Ordered → 无序）

### C4. registerBeanPostProcessors：为什么注解能工作

- 入口断点：
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- 观察点：
  - `beanFactory.getBeanPostProcessorCount()`
  - 已注册的关键 BPP（AABPP/CABPP/Autowired 等）

### C5. 单例预实例化：实例层主线（createBean/doCreateBean）

- 入口断点：
  - `DefaultListableBeanFactory#preInstantiateSingletons`
  - `AbstractAutowireCapableBeanFactory#createBean`
  - `AbstractAutowireCapableBeanFactory#doCreateBean`
- 观察点：
  - `singletonObjects` / `earlySingletonObjects` / `singletonFactories`
  - 当前 beanName 是否在 `singletonsCurrentlyInCreation`
- 决定性分支：
  - 是否触发“实例化前短路”（`postProcessBeforeInstantiation` 返回非 null）
  - 是否触发 early reference（循环依赖/代理介入）

### C6. populateBean：依赖注入与候选收敛

- 入口断点：
  - `AbstractAutowireCapableBeanFactory#populateBean`
  - `DefaultListableBeanFactory#doResolveDependency`
  - `DefaultListableBeanFactory#findAutowireCandidates`
  - `DefaultListableBeanFactory#determineAutowireCandidate`
- 观察点：
  - 候选集合大小变化（歧义/收敛）
  - by-name fallback 是否触发（依赖名匹配 beanName）
- 决定性分支：
  - `@Primary/@Priority/@Qualifier` 的优先级链

### C7. initializeBean：生命周期回调与“代理替换发生点”

- 入口断点：
  - `AbstractAutowireCapableBeanFactory#initializeBean`
  - `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`
  - `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`
- 观察点：
  - `wrappedBean` 与 `bean` 是否发生替换（proxying）
- 决定性分支：
  - `postProcessAfterInitialization` 是否返回代理（这通常决定“读者最终拿到的对象是谁”）

## 阶段内关键对象变化（断点地图补充）

为了让“阶段感”更可见，这里把关键对象变化再压缩成 1 张表：

| 阶段 | 主要变化 | 推荐观察点（最小够用） |
| --- | --- | --- |
| 定义注册 | `beanDefinitionMap` 增长，`BeanDefinition` 来源被标记 | `registry.getBeanDefinitionCount()` / `beanDefinition.getSource()` |
| BFPP/BDRPP | 定义被改写/补齐（占位符、配置类、属性覆盖） | `postProcessBeanFactory` / `processedBeans` |
| 注册 BPP | `beanPostProcessors` 列表最终排序固定 | `beanFactory.getBeanPostProcessorCount()` |
| 单例创建 | `singletonObjects` / `earlySingletonObjects` / `singletonFactories` 发生写入 | `singletonsCurrentlyInCreation` / 三级缓存 |

## 主线高频分支最小集（断点地图版）

无需记住所有分支，但必须能“看见”这 5 个最常见的分支触发点：

1) **singleton vs prototype**：`AbstractBeanFactory#doGetBean` → `mbd.isPrototype()`  
2) **dependsOn 强制顺序**：`AbstractBeanFactory#getBean` → `mbd.getDependsOn()`  
3) **parent BeanFactory 兜底**：`containsBeanDefinition(beanName)` 为 false → `parentBeanFactory.getBean`  
4) **FactoryBean vs 产品对象**：`AbstractBeanFactory#getObjectForBeanInstance`  
5) **类型匹配（含泛型）**：`AbstractBeanFactory#isTypeMatch` / `ResolvableType` 判定

## 源码调用链与断点（建议从 Lab 反推）

更完整的“入口测试 → 断点调用链”建议，优先看：

- 30 分钟快启：`part-00-guide/012-01-quickstart-30min.md`
- 深挖指南：`part-00-guide/011-00-deep-dive-guide.md`

## 最小可运行实验（Lab）

建议先跑这些入口再下断点：

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
   - `beanName.equals(\"xxx\")`
   - `mbd.getBeanClassName().contains(\"Foo\")`（类名过滤）
2. **按阶段过滤（避免打到不相关阶段）**
   - 只在 `isEagerInit` / `isSingletonCurrentlyInCreation` 等关键分支为 true 时停住
3. **按“候选收敛”过滤**
   - 只在 `candidates.size() > 1` 或出现 `@Qualifier/@Primary` 决策点时停住

> 目标：把“能看到”升级成“只看到需要看的”。这也是为什么本模块大量章节都强调 watch list：观察点比断点位置更决定效率。

## 常见误区与边界

- 只盯某个注解：建议先把“发生在 refresh 的哪一段”确定下来（C1-C7）。
- 把 proxy 当成原始对象：建议在 `applyBeanPostProcessorsAfterInitialization` 处观察 `wrappedBean` 替换点。
- 循环依赖只看三层缓存：建议结合“代理介入”与“raw 注入/早期引用”的边界用例一起看。

## 小结与下一章

- 本页作为断点索引页，建议与各章的“源码锚点/入口测试/排障分流”配合使用。
<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：为每个断点补“它在证明什么分支”，避免断点清单变成“背方法名”。
    - B（边界反例）：“断点误用反例”：哪些断点会因为版本/环境差异不稳定，如何替代。
    - C（排障 SOP）：“从症状选择断点组”的决策表：注入/代理/循环依赖/占位符/FactoryBean 各选哪组。
    - D（断点观察）：把 watch list 升级为“判定标准”：变量值如何判断你处在哪条分支。
    - E（面试复述）：“面试追问的断点证明”：给 3 个高频题对应的断点证明路径。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansLabTest`
- Lab：`SpringCoreBeansBootstrapInternalsLabTest`
- Lab：`SpringCoreBeansBeanCreationTraceLabTest`
- Lab：`SpringCoreBeansEarlyReferenceLabTest`

上一章：[part-00-guide/01-quickstart-30min.md](012-01-quickstart-30min.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-ioc-container/01-bean-mental-model.md](../part-01-ioc-container/020-01-bean-mental-model.md)

<!-- BOOKIFY:END -->

## 面试怎么用（把断点地图变成“证据链话术”）

面试里无需“背源码”，但需要能说清：

1) 我会在哪个阶段下断点（refresh 哪一段）
2) 我在断点里看哪 3 个变量就能下结论
3) 我用哪个 Lab 复现并证明它

推荐复习入口：`appendix/93-interview-playbook.md` / `appendix/98-debugger-pack.md`

## 自检要点
应能够做到：

1) 说出 refresh 主线、依赖解析、bean 创建、单例缓存、BPP 代理这五类问题各自的“第一断点”。
2) 解释为什么“断点 + watch list”比“全局搜栈”更高效。
3) 用本模块任意一个 Lab，把断点地图跑通一次并能复述观察到的关键变化。
