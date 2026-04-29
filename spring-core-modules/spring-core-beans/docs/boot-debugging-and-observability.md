# 调试与自检：如何“观察到”容器正在做什么
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把现象固化为断言；真实项目里常见路径是：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。

    观察对象：调试与自检：如何“观察到”容器正在做什么。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansAutoConfigurationLabTest`。需要下探源码时，可以从 `org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 问题：调试与自检：如何“观察到”容器正在做什么

先运行 `SpringCoreBeansAutoConfigurationLabTest`，把“容器正在做什么”落到可观察事实上：入口方法在哪里、关键分支怎么走、哪些变量能证明结论。

- 官方文档对照（适用版本：Spring Boot 3.5.9）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html
- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansAutoConfigurationLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansMergedBeanDefinitionLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansDependsOnLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansConditionEvaluationReportLabTest` / `SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansPreInstantiationLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansResourceInjectionLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansProxyingPhaseLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/testsupport/BeanGraphDumper.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansExceptionNavigationLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java`


## 读法：先确定观察对象，再选工具

调试类页面容易堆工具，本章按“先看对象，再选工具”的顺序读：

1. 先确认要观察的是 BeanDefinition、候选集合、依赖边、最终对象，还是 Boot 条件报告。
2. 再选择入口：容器 API、断点、条件报告、异常导航、BeanGraph 输出各自解决不同问题。
3. 最后把观察结果写成“现象 → 数据结构 → 结论 → 修复/验证”，避免只贴日志。

## 机制主线

> 官方参考（Spring Boot 3.5.9，Spring Boot Auto-configuration）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html

理解 Spring 容器的最快方式，是让“不可见的机制”变得可观察。

### Boot 自动装配的角色分工（调试时先记住这 4 类）

- **导入清单**：`AutoConfigurationImportSelector#selectImports`
- **排序器**：`AutoConfigurationImportSorter`（决定先后）
- **条件评估**：`ConditionEvaluator#shouldSkip`
- **定义注册**：`ConfigurationClassPostProcessor#processConfigBeanDefinitions`

### 顺序 vs 条件：为什么“偶发失效”往往是顺序问题

排序决定“谁先注册”；条件评估在特定阶段触发。
当一个 Auto-Config 依赖“前一个配置先注册某个 bean”才能通过条件时，**顺序一变就像“偶发失效”**。
调试时优先关注：导入顺序 → 条件报告 → BeanDefinition 来源。

## 观测对象总览：读者通常只是在观察 5 类对象

当读者说“调 Spring 容器”，本质上是在回答 5 类问题。将这 5 类问题固定下来，可避免仅依赖日志与猜测进行试探。

- **定义是否存在？（BeanDefinition）**：回答“到底有没有注册/谁注册的/定义元数据是什么（scope/lazy/dependsOn）”
  - 最直接入口：`BeanFactory#getBeanDefinition(beanName)`
  - 最小复现：`SpringCoreBeansContainerLabTest.beanDefinitionIsNotTheBeanInstance()` / `SpringCoreBeansBeanDefinitionOriginLabTest.beanDefinitionMetadata_canAnswerWhoRegisteredThisBean_andWhereItCameFrom()`
- **最终创建配方是什么？（MergedBeanDefinition / RootBeanDefinition）**：回答“为什么最终看到的是 Root？parent 合并后有哪些属性/回调生效？”
  - 最直接入口：`AbstractBeanFactory#getMergedLocalBeanDefinition(beanName)`
  - 最小复现：`SpringCoreBeansMergedBeanDefinitionLabTest.mergedBeanDefinition_combinesParentAndChildMetadata_andTriggersMergedDefinitionPostProcessor()`
- **候选到底有哪些？（candidates）**：回答“按类型到底找到了谁？为什么候选是这些？”
  - 最直接入口：`DefaultListableBeanFactory#findAutowireCandidates(...)` / `DefaultListableBeanFactory#getBeanNamesForType(...)`
  - 最小复现：`SpringCoreBeansAutowireCandidateSelectionLabTest`（以及本章第 13.2 的 playbook）
- **最终注入的是谁？（最终依赖边）**：回答“为什么注入的是它？容器记录了哪条依赖边？”
  - 最直接入口：`DefaultListableBeanFactory#doResolveDependency(...)`、`DefaultSingletonBeanRegistry#registerDependentBean(...)`
  - 最小复现：`SpringCoreBeansBeanGraphDebugLabTest.dumpBeanGraph_candidatesAndRecordedDependencies_helpTroubleshootWhyItsInjected()`（看 candidates vs recorded dependencies）
- **为什么获取到的是 proxy？（最终暴露对象）**：回答“是谁把对象换成了 proxy/wrapper？换壳发生在哪一段？”
  - 最直接入口：`AbstractAutowireCapableBeanFactory#initializeBean(...)`、`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization(...)`
  - 最小复现：`SpringCoreBeansBeanCreationTraceLabTest.beanCreationTrace_recordsPhases_andExposesProxyReplacement()` / `SpringCoreBeansProxyingPhaseLabTest.beanPostProcessorCanReturnAProxyAsTheFinalExposedBean_andSelfInvocationStillBypassesTheProxy()`

### 观测对象分流：条件 → 分支 → 结果

**条件**：需要解决的是“定义/候选/注入/最终对象”的哪一类问题
**分支**：进入对应入口方法（`getBeanDefinition` / `findAutowireCandidates` / `doResolveDependency` / `applyBeanPostProcessorsAfterInitialization`）
**结果**：把“现象”归位到明确的数据结构变化，而不是停留在日志猜测
**断点入口**：`DefaultListableBeanFactory#doResolveDependency`

## 最简单也最有效：查容器里到底有哪些 Bean

- `applicationContext.getBeansOfType(TextFormatter.class)`

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`
  - `containerCanProvideAllFormatterBeansByType()`（应当看到 `upperFormatter/lowerFormatter` 都在容器里）

- 先确认“容器里有没有容易误以为的 bean”
- 再确认“候选有几个”
- 再回到注入点看 `@Qualifier/@Primary` 等规则

## 进一步：看 BeanDefinition（定义层）

当读者怀疑“注册阶段出了问题”（扫描范围不对、`@Import` 没生效、条件没满足）时，光看实例不够。

需要去看：

- beanName 是否存在对应的 `BeanDefinition`
- scope、lazy、dependsOn 等元数据是什么

- `context.getBeanFactory().getBeanDefinition("exampleBean")`

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
  - `beanDefinitionIsNotTheBeanInstance()`（应当看到：definition 存在，但 instance 可能尚未创建；definition != instance）
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansBeanDefinitionOriginLabTest.java`
  - `beanDefinitionMetadata_canAnswerWhoRegisteredThisBean_andWhereItCameFrom()`（应当看到：factoryBeanName/factoryMethodName/source 等元数据能回答“谁注册的/从哪来的”）

本仓库里已经反复用过两类“最小容器”：

- **纯容器（噪音最少，最适合看 DefaultListableBeanFactory 行为）**：`AnnotationConfigApplicationContext` / `GenericApplicationContext`
  - 代表用例：`SpringCoreBeansContainerLabTest`、`SpringCoreBeansBootstrapInternalsLabTest`
- **Boot 自动装配最小复现（最适合查条件报告/排序/backoff）**：`ApplicationContextRunner`
  - 代表用例：`SpringCoreBeansAutoConfigurationLabTest`、`SpringCoreBeansConditionEvaluationReportLabTest`

## 固定观察点：候选集合 vs 最终注入（以及容器记录的依赖边）

当需要解释“为什么注入的是它”，把问题拆成两步观察：

1. **候选集合（candidates）从哪来？**
   - 最直接的 API：`beanFactory.getBeanNamesForType(requiredType)`
   - 这一步只回答“有哪些候选”，不回答“最终选了谁”。

2. **最终注入（final injection）到底选了谁？容器把依赖边记录到哪？**
   - 最直接的 API：`beanFactory.getDependenciesForBean(beanName)`
   - 可以观察到：容器只会把“最终被注入/被引用”的那个 bean 记为依赖（而不是把所有候选都算进去）。

> 补充：依赖关系表也会影响关闭时的销毁顺序；若想看更底层的 `dependentBeanMap` / `dependenciesForBeanMap`，结合 [19](wiring-depends-on.md) 一起看。

## Spring Boot 的“条件报告”：把自动装配的生效/失效原因打印出来

当读者怀疑“自动配置没生效”或“多了未预期的 bean”时，开启条件评估报告：

它会告诉读者：

- 哪些自动配置生效
- 哪些没生效
- 没生效的原因（哪个条件失败）

学习阶段无需记住每条报告格式，但要知道它存在，并且能回答“为什么”。

### 4.1 可断言诊断（把“生效/失效”做成测试）

- 用 `ApplicationContextRunner` 构建最小上下文
- 用 `ConditionEvaluationReport` 断言某个 Auto-Config 是否生效
- 用 `beanDefinition.getSource()` 反推定义来源（避免误认为由用户代码注册）

## 日志：输出容器行为以便观察

- `org.springframework.beans`
- `org.springframework.context`
- `org.springframework.boot.autoconfigure`

可以观察到：

- bean 创建顺序
- 自动装配导入/条件判断的部分信息

## 一个实用的自检流程（遇到 DI 问题就按这个来）

可以在输出中看到这些线索：

- `BEANS:textFormatters=...` / `BEANS:formattingService.injectedFormatter=...`
- `BEANS:prototype.direct.sameId=...`
- `BEANS:prototype.provider.differentId=...`
- `BEANS:lifecycle.postConstructCalled=...`
- `BEANS:beanDefinitionCount=...`

如果这些输出与相应的理解不一致，优先回到：

- `BEANS:textFormatters=...` → 本章第 1 节（先确认“候选集合到底有哪些”）
- `BEANS:formattingService.injectedFormatter=...` → [03](ioc-dependency-injection-resolution.md)、[33](wiring-autowire-candidate-selection-primary-priority-order.md)（候选收敛为何选中它）
- `BEANS:prototype.*` → [04](ioc-scope-and-prototype.md)（prototype 注入陷阱 vs ObjectProvider/@Lookup）
- `BEANS:lifecycle.*` → [05](ioc-lifecycle-and-callbacks.md)、[17](internals-lifecycle-callback-order.md)（生命周期回调顺序与证据链）
- `BEANS:beanDefinitionCount=...` / “看不到容易误以为注册的 bean” → 本章第 2 节（先确认定义层是否存在，再决定往注册/条件/顺序走）

## 可复现闭环（基于 `SpringCoreBeansAutoConfigurationLabTest`）

完成该组用例后，至少需要复述 3 条结论：

1. **自动装配是否生效可被断言**
   - 断点：`ConditionEvaluationReport#get`
   - 断言：report 中存在对应的 match/no-match 结果
2. **导入顺序会影响条件判断**
   - 断点：`AutoConfigurationImportSorter`
   - 断言：排序改变后某些条件结果不同
3. **定义来源可追溯**
   - 断点：`registerBeanDefinition`
   - 断言：`beanDefinition.getSource()` 指向 Auto-Config 类

## 代理定位闭环：为什么它是 proxy？

对 B 路线读者而言，“为什么是 proxy”最有效的做法不是背概念，而是用一套固定闭环把它查出来：

### 10.1 先判定：这是 JDK proxy 还是 CGLIB（别凭肉眼猜）

- JDK proxy：`java.lang.reflect.Proxy.isProxyClass(bean.getClass()) == true`
- CGLIB：`org.springframework.util.ClassUtils.isCglibProxyClass(bean.getClass()) == true`（或类名包含 `$$`）

### 10.2 再定位：代理替换最常见发生在哪？

最常见的“换壳点”在初始化链路末尾：

固定观察点（watch/evaluate）：

- `beanName`（条件断点：只看相应的目标 bean）
- `bean`（原对象） vs `result`（BPP 链路返回的最终对象）：`result != bean` 就是“发生了替换”的铁证
- `result.getClass()`：用于判定 JDK proxy / CGLIB（配合 10.1 的判定工具）
- `beanFactory.getBeanPostProcessors()`：BPP 执行链（顺序就是“谁先包/谁后包”的常见原因）

### 10.3 最后锁定：到底是哪一个 `BeanPostProcessor` 把它换掉的？

在 `applyBeanPostProcessorsAfterInitialization` 的循环里：

- 看循环变量（当前 BPP）是谁
- 观察 `result` 何时从“原对象”变成“新对象”

## Boot 条件报告：把它当成“可查询的数据结构”（而不仅是日志）

> 条件报告不是日志技巧，它是 `ConditionEvaluationReport` 这份“可查询的数据结构”。

在 `ApplicationContextRunner` 场景里（最小、可控、无全量 Boot 噪音），可以直接获取到它：

```java
ConditionEvaluationReport report = ConditionEvaluationReport.get(context.getBeanFactory());
var outcomes = report.getConditionAndOutcomesBySource().get(AutoConfig.class.getName());
```

最小可运行入口：

- 创建链路（只看一个 bean）：`beanName.equals("yourBeanName")`
- DI 链路（只看某个注入类型）：`descriptor.getDependencyType() == YourType.class`
- 反向过滤（只看业务 bean）：`!beanName.startsWith("org.springframework.")`

### 13.1 现象：`@Autowired/@PostConstruct/@Bean` 不生效（注解为什么能工作？）

1. `AnnotationConfigUtils#registerAnnotationConfigProcessors`（基础设施处理器注册入口）
2. `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（BPP 何时进入主线）
3. `AutowiredAnnotationBeanPostProcessor#postProcessProperties`（`@Autowired` 的解析与注入发生点）
4. `CommonAnnotationBeanPostProcessor#postProcessBeforeInitialization`（`@PostConstruct` 的触发点）

需要解释清楚：

- 注解能力不是“语法自带”，而是容器在 refresh 主线里注册了 BFPP/BPP 才成立

### 13.2 现象：单依赖注入歧义（候选太多）/ 为什么最终注入的是它？

1. `DefaultListableBeanFactory#doResolveDependency`（依赖解析主入口）
2. `DefaultListableBeanFactory#findAutowireCandidates`（候选收集：Map<beanName, candidate>）
3. `DefaultListableBeanFactory#determineAutowireCandidate`（候选收敛：@Qualifier/@Primary/@Priority/name）
4. `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`（Qualifier 过滤与匹配）

需要解释清楚：

- `@Order` 管的是“集合注入排序”，不是“单依赖候选收敛”
- 依赖解析的核心主线是：候选收集 → 候选收敛 → 最终注入

### 13.3 现象：生命周期回调顺序说不清（Aware/BPP/@PostConstruct/afterPropertiesSet 谁先谁后？）

需要解释清楚：

- Aware 发生在 initialize 阶段，且会早于 init callbacks（因此能在 `@PostConstruct` 之前获取到容器信息）
- prototype 的销毁默认不由容器托管（对照同一个类里的 prototype 测试）

### 13.4 现象：这个 bean 为什么变成 proxy？是谁把它换掉了？

需要解释清楚：

- proxy/替换不只是 AOP/事务的“隐式行为”，而是容器在实例阶段允许 BPP 返回“另一个对象”作为最终暴露对象

### Debug 工具箱（对象→问题→断点→观察点）

这一章给读者一个实用的调试工具箱，目标是：当遇到“为什么注入的是它？”“为什么它没注册？”“为什么它是代理？”时，知道从哪里下手。

| 在看什么 | 它回答的问题 | 最小入口断点（条件断点） | 固定观察点（观察清单） | 关联章节 / 可运行实验 |
| --- | --- | --- | --- | --- |
| `BeanDefinition`（原始定义） | “到底有没有注册？”“定义元数据是什么？” | `DefaultListableBeanFactory#getBeanDefinition` | `beanFactory.containsBeanDefinition(beanName)`、`beanFactory.getBeanDefinition(beanName)`（scope/lazy/dependsOn） | [01](ioc-bean-mental-model.md)、[02](ioc-bean-registration.md)、`SpringCoreBeansContainerLabTest.beanDefinitionIsNotTheBeanInstance()` |
| merged `RootBeanDefinition`（最终配方） | “创建时为什么看到的是 Root？”“最终生效配方是什么？” | `AbstractBeanFactory#getMergedLocalBeanDefinition` | `mbd`（`RootBeanDefinition`）、merged 缓存（`mergedBeanDefinitions` 等）、`mbd.getPropertyValues()` | [35](wiring-merged-bean-definition.md)、`SpringCoreBeansMergedBeanDefinitionLabTest` |
| 实例 vs 代理（最终暴露对象） | “为什么注入的是 proxy？”“谁把对象换掉了？” | `AbstractAutowireCapableBeanFactory#initializeBean`、`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` | `beanName`、`bean` vs `result`、`beanFactory.getBeanPostProcessors()` | [06](ioc-post-processors.md)、[31](wiring-proxying-phase-bpp-wraps-bean.md)、`SpringCoreBeansBeanCreationTraceLabTest` |
| 依赖图（两张表） | “为什么注入的是它？”“为什么启动/关闭顺序这样？” | `DefaultListableBeanFactory#doResolveDependency`、`DefaultSingletonBeanRegistry#registerDependentBean`、`DefaultSingletonBeanRegistry#destroySingletons` | `getDependenciesForBean` / `getDependentBeans`、`dependentBeanMap` / `dependenciesForBeanMap` | [03](ioc-dependency-injection-resolution.md)、[19](wiring-depends-on.md)、`SpringCoreBeansBeanGraphDebugLabTest`、`SpringCoreBeansDependsOnLabTest` |
| 单例缓存（循环依赖/提前暴露） | “循环依赖为什么有时能救？”“early reference 发生在哪？” | `DefaultSingletonBeanRegistry#getSingleton`、`AbstractAutowireCapableBeanFactory#getEarlyBeanReference` | `singletonObjects` / `earlySingletonObjects` / `singletonFactories` 的变化 | [16](internals-early-reference-and-circular.md)、`SpringCoreBeansEarlyReferenceLabTest` |

> 经验法则：应先明确“当前观察对象的类别”，再决定断点与观察清单；否则读者容易在巨大调用栈中丢失主线。

本模块的 lab 已经用过：

- `SpringCoreBeansLabTest.containerCanProvideAllFormatterBeansByType()`

可以把它升级为自己的调试习惯：

在本模块的容器实验里已经看过：

- `SpringCoreBeansContainerLabTest.beanDefinitionIsNotTheBeanInstance()`

## 把场景做小：用“最小容器”复现（比运行完成整应用更快）

很多人调 Spring 容器最大的痛点不是“不会打断点”，而是：**断点命中太多、调用栈太深、噪音太大**。

解决办法往往很直接：把问题缩成一个最小可复现的容器。

1. **纯 Spring 场景（更贴近容器机制本身）**：`AnnotationConfigApplicationContext`
   - 典型例子：`SpringCoreBeansDependsOnLabTest`、`SpringCoreBeansBeanGraphDebugLabTest`
   - 优点：观察到的就是 `DefaultListableBeanFactory` 的真实行为，几乎没有 Boot 噪音。

2. **Spring Boot 自动装配场景（更贴近真实工程）**：`ApplicationContextRunner`
   - 典型例子：`SpringCoreBeansAutoConfigurationLabTest`
  - 优点：可以小地验证“某个自动配置为什么生效/为什么没生效”。

> 经验法则：当读者准备去翻一堆日志/追一个巨深的栈时，先问自己一句：能不能把它变成一个 `*LabTest` 的最小复现？

对应实验：

- `SpringCoreBeansBeanGraphDebugLabTest.dumpBeanGraph_candidatesAndRecordedDependencies_helpTroubleshootWhyItsInjected()`
- 辅助工具：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/testsupport/BeanGraphDumper.java`

- 在运行参数里加 `--debug`
- 或在配置里开启 `debug=true`

若想把条件报告当成“可查询的数据结构”（更适合进阶学习、也更容易做成最小复现），见本章第 11 节与 `SpringCoreBeansConditionEvaluationReportLabTest`。

> 进阶提醒：当遇到 `@ConditionalOnBean` 这类“依赖另一个自动配置里注册的 bean”的场景时，除了看报告本身，还要考虑**条件评估时机**与**自动配置顺序**（after/before 元数据）。对应最小复现见 `SpringCoreBeansAutoConfigurationOrderingLabTest`，并对照 [10](boot-spring-boot-auto-configuration.md) 的顺序依赖小节。

当需要更细粒度地看依赖注入/bean 创建细节时，可以临时提高日志级别（只在学习/调试时使用）：

把问题先分流到“层/对象”，再设置断点会快很多：

- **Bean 根本不存在 / `NoSuchBeanDefinitionException`** → 优先进入 **定义层**
  - 优先检查：`containsBeanDefinition(beanName)`、`getBeanDefinition(beanName)`（scope/lazy/dependsOn）
  - 再检查：扫描范围 / `@Import` / 条件装配（见第 5 节）
  - 最小复现：`SpringCoreBeansLabTest.missingBeanLookupsFailFast()`

- **候选太多 / `NoUniqueBeanDefinitionException`** → 走 **依赖解析（候选收敛）**
  - 入口：`DefaultListableBeanFactory#doResolveDependency`
  - 固定观察点：候选集合（by type）→ `@Qualifier/@Primary/@Priority` 收敛点
  - 最小复现：`SpringCoreBeansAutowireCandidateSelectionLabTest`

- **注入能发生，但“为什么注入的是它”** → 进入 **候选集合 vs 最终依赖边（依赖图）**
  - 优先检查候选：`getBeanNamesForType`
  - 再检查最终依赖边：`getDependenciesForBean(beanName)`（容器只记录最终注入的那条边）
  - 最小复现：`SpringCoreBeansBeanGraphDebugLabTest`

- **对象形态不对（获取到 proxy/wrapper）** → 走 **实例替换（BPP）**
  - 入口：`initializeBean → applyBeanPostProcessorsAfterInitialization`
  - 最小复现：`SpringCoreBeansBeanCreationTraceLabTest.beanCreationTrace_recordsPhases_andExposesProxyReplacement()`

- **启动/关闭顺序很怪** → 走 **依赖图 + dependsOn**
  - 入口：`DefaultSingletonBeanRegistry#registerDependentBean`、`#destroySingletons`
  - 最小复现：`SpringCoreBeansDependsOnLabTest`（见第 9 节的 dependsOn 环异常也在这里）

> 可以把它记成一句话：先判断“这是定义层、解析层、还是实例层”，再去打断点。

## 与本模块运行输出对齐

运行本模块：

```bash
mvn -pl :spring-core-beans spring-boot:run
```

## 补充：异常信息背后的“注入点元数据”（DependencyDescriptor / `MethodParameter`）

在真实项目中遇到 `NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException` 时，不宜第一时间推测“是否未扫描到”。
更高收益的起手式是：把异常当成“注入点元数据的摘要”，快速还原三件事：

1. **注入点要的是什么？**（类型/泛型/是否 required）
2. **注入点带了哪些限定信号？**（`@Qualifier/@Primary/@Lazy/@Resource` 等）
3. **注入点来自哪里？**（字段还是构造器/方法参数）

在源码里，这些信息最终都收敛到 `DependencyDescriptor`：

- 字段注入：`descriptor.getField() != null`
- 参数注入：`descriptor.getMethodParameter() != null`（底层是 `org.springframework.core.MethodParameter`）

**的“从异常秒跳断点”路线（稳定、可复用）：**

- 入口：`DefaultListableBeanFactory#doResolveDependency(...)`
- 固定观察点：
  - `descriptor`（看注入点元数据）
  - `dependencyType / resolvableType`（看“到底要什么”）
  - `candidates`（看“有哪些可能”）
  - `autowiredBeanNames`（看“最后选了谁/为什么”）

## 异常 → 断点入口（从异常秒跳到正确抓手）

无需背所有异常，但把“异常类型 → 最有效入口断点”形成肌肉记忆。

| 观察到的异常 | 常见含义（先分流） | 最有效入口断点（优先打条件断点） | 关联章节 / 可运行实验 |
| --- | --- | --- | --- |
| `NoSuchBeanDefinitionException` | 容器里根本没有候选（定义没注册/条件没满足/按 name 找不到） | `DefaultListableBeanFactory#doResolveDependency`、`DefaultListableBeanFactory#getBeanNamesForType` | [03](ioc-dependency-injection-resolution.md)、`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`（`missingBeanLookupsFailFast()`） |
| `NoUniqueBeanDefinitionException` | 候选太多且无法唯一化（典型：单依赖注入时同类型有多个候选） | `DefaultListableBeanFactory#doResolveDependency`、`DefaultListableBeanFactory#determineAutowireCandidate`、`DefaultListableBeanFactory#determinePrimaryCandidate` | [03](ioc-dependency-injection-resolution.md)、[33](wiring-autowire-candidate-selection-primary-priority-order.md)、`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`（`orderAnnotation_doesNotResolveSingleInjectionAmbiguity()`） |
| `UnsatisfiedDependencyException` | “注入失败”的总包装：可能是没有候选、候选太多、类型不匹配、创建链路失败（它经常包着真正 root cause） | `DefaultListableBeanFactory#doResolveDependency`、`AutowiredAnnotationBeanPostProcessor#postProcessProperties`、`AbstractAutowireCapableBeanFactory#populateBean` | [03](ioc-dependency-injection-resolution.md)、[30](wiring-injection-phase-field-vs-constructor.md)、`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansExceptionNavigationLabTest.java`（`unsatisfiedDependency_failsFast()`） |
| `BeanCurrentlyInCreationException` | 循环依赖/提前暴露相关：某个 bean 正在创建中又被请求（构造器循环依赖最常见） | `DefaultSingletonBeanRegistry#getSingleton`、`DefaultSingletonBeanRegistry#beforeSingletonCreation`、`AbstractBeanFactory#doGetBean` | [09](ioc-circular-dependencies.md)、[16](internals-early-reference-and-circular.md)、`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`（`circularDependencyWithConstructorsFailsFast()`） |
| `Circular depends-on relationship`（message） | **定义层拓扑环**：人为写了 `dependsOn A -> B -> A`；不要误判成“循环依赖/三级缓存” | `AbstractBeanFactory#doGetBean`、`DefaultSingletonBeanRegistry#registerDependentBean`、`DefaultSingletonBeanRegistry#isDependent` | [19](wiring-depends-on.md)、`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java`（`dependsOn_cycle_failsFast()`） |
| `BeanCreationException` | bean 创建链路失败（构造器异常 / init 回调异常 / BPP 包装失败 / 循环依赖失败等都会落到这里） | `AbstractAutowireCapableBeanFactory#doCreateBean`、`AbstractAutowireCapableBeanFactory#createBeanInstance`、`AbstractAutowireCapableBeanFactory#initializeBean` | [00](guide-deep-dive-guide.md)、[12](internals-container-bootstrap-and-infrastructure.md)、`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`（`withoutBeforeInstantiationShortCircuit_refreshFailsAndConstructorWasCalled()`） |
| `BeanDefinitionStoreException` | definition 解析/注册阶段失败（XML/注解解析/占位符等；通常发生在 refresh 前半段） | `XmlBeanDefinitionReader#loadBeanDefinitions`、`DefaultListableBeanFactory#registerBeanDefinition`、`AbstractApplicationContext#refresh` | [02](ioc-bean-registration.md)、[12](internals-container-bootstrap-and-infrastructure.md)、`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansExceptionNavigationLabTest.java`（`beanDefinitionStoreException_invalidXml()`） |
| （无异常）`@Autowired/@Resource/@PostConstruct` 不生效（字段为 null / 回调未执行） | 容器没装“注解能力基础设施”（annotation processors 未注册/未生效）；常见于 `GenericApplicationContext` 手工启动 | `AnnotationConfigUtils#registerAnnotationConfigProcessors`、`PostProcessorRegistrationDelegate#registerBeanPostProcessors`、`AutowiredAnnotationBeanPostProcessor#postProcessProperties`、`CommonAnnotationBeanPostProcessor#postProcessProperties` | [12](internals-container-bootstrap-and-infrastructure.md)、`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`（`withoutAnnotationConfigProcessors_autowiredAndPostConstructAreNotApplied()`）、`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java`（`withoutAnnotationConfigProcessors_resourceIsIgnored()`） |

> 小技巧：如果断点命中次数太多，先加条件（例如 `beanName.equals("xxx")`），再去看调用栈；深入分析路线见 [00](guide-deep-dive-guide.md)。

对应实验/测试：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java`
断点入口：`DefaultListableBeanFactory#doResolveDependency`、`DefaultSingletonBeanRegistry#getSingleton`、`DefaultListableBeanFactory#preInstantiateSingletons`

> 无需先知道“为什么会代理”，先把“代理类型”判定出来，后面的断点路径会短很多。

- `beanName`（加条件断点只看相应的目标 bean）
- `bean`（原对象） vs `result`（BPP 链路返回的最终对象）
- `beanFactory.getBeanPostProcessors()`（执行链，顺序就是“谁先包/谁后包”的原因）

最小可运行入口（本仓库专门为这套闭环提供的实验）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java`
  - `beanCreationTrace_recordsPhases_andExposesProxyReplacement()`

`--debug` 的条件报告很好用，但对进阶学习者更高收益的理解框架是：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansConditionEvaluationReportLabTest.java`

## 高收益条件断点模板（降噪）

断点命中太多时，先套这些“模板条件”，能把噪音降一个数量级：

> 小技巧：若不确定 `beanName` 是什么，先用 `getBeanDefinitionNames()` 或 `getBeansOfType()` 把名字找出来，再回到断点加条件。

## IoC/DI 与生命周期 Debug Playbook（最小断点闭环）

> 落点：给读者一套“遇到现象即可快速定位到断点入口”的固定方法。每条 playbook 都绑定本仓库的最小复现入口（先运行验证，再设置断点）。

可以用下面的 Lab 把“注解基础设施是否注册”这件事跑出来：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`
  - `withoutAnnotationConfigProcessors_autowiredAndPostConstructAreNotApplied()`
  - `registerAnnotationConfigProcessors_enablesAutowiredAndPostConstruct()`

断点入口（按顺序）：

1. `AnnotationConfigUtils#registerAnnotationConfigProcessors`：观察“注解能力基础设施”如何被注册为 BeanDefinition
2. `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：观察 `ConfigurationClassPostProcessor` 等定义层处理器何时运行
3. `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：观察 `AutowiredAnnotationBeanPostProcessor/CommonAnnotationBeanPostProcessor` 何时进入 BeanFactory
4. `AutowiredAnnotationBeanPostProcessor#postProcessProperties`：观察 `@Autowired/@Value` 注入发生点
5. `CommonAnnotationBeanPostProcessor#postProcessBeforeInitialization`：观察 `@PostConstruct` 的触发点

依赖注入歧义这条线，可以用下面这些 Lab 做最小对照：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionAmbiguityLabTest.java`
  - `singleInjectionFailsFast_whenMultipleCandidatesExist_andNoPrimaryOrQualifierIsPresent()`
  - `primary_canResolveSingleInjectionAmbiguity_byChoosingTheDefaultWinner()`
  - `qualifier_canResolveSingleInjectionAmbiguity_byExplicitlySelectingTheTargetBean()`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`
  - `orderAnnotation_doesNotResolveSingleInjectionAmbiguity()`

断点入口（闭环版）：

1. `DefaultListableBeanFactory#doResolveDependency`：依赖解析总入口（优先检查 descriptor 想要的类型/是否 required）
2. `DefaultListableBeanFactory#findAutowireCandidates`：候选收集（看 `Map<String, Object> matchingBeans`）
3. `DefaultListableBeanFactory#determineAutowireCandidate`：候选收敛总入口（最终 winner 在这里确定）
4. `DefaultListableBeanFactory#determinePrimaryCandidate`：`@Primary` 分支（为什么它赢）
5. `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`：`@Qualifier` 的过滤与匹配（为什么其它候选被剔除）

生命周期回调顺序这条线的最小入口如下：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`
  - `singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization()`

断点入口（闭环版）：

1. `AbstractAutowireCapableBeanFactory#doCreateBean`：bean 创建主线（哪些阶段会触发回调/后处理）
2. `AbstractAutowireCapableBeanFactory#initializeBean`：initialize 串联点（Aware + init callbacks）
3. `AbstractAutowireCapableBeanFactory#invokeAwareMethods`：Aware 发生点（证明它早于 init callbacks）
4. `CommonAnnotationBeanPostProcessor#postProcessBeforeInitialization`：`@PostConstruct` 的触发点
5. `AbstractAutowireCapableBeanFactory#invokeInitMethods`：`afterPropertiesSet` / initMethod 的触发点

如果要把“代理替换发生在哪一步”看清楚，可以从下面这些 Lab 作为入口：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java`
  - `beanCreationTrace_recordsPhases_andExposesProxyReplacement()`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`
  - `beanPostProcessorCanReturnAProxyAsTheFinalExposedBean_andSelfInvocationStillBypassesTheProxy()`

断点入口（闭环版）：

1. `AbstractAutowireCapableBeanFactory#initializeBean`：从这里进入“最终暴露对象”的产生链路
2. `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`：在循环里观察 `bean` → `result` 的第一次替换
3. 自己的 `BeanPostProcessor#postProcessAfterInitialization`（如果运行 `SpringCoreBeansProxyingPhaseLabTest`，就在它的 post-processor 上加断点）
4. （可选）`AbstractAutoProxyCreator#postProcessAfterInitialization`：若将来定位 AOP/Tx 代理，常从这里命中

## 面试常问（排障方法论：分层后再设置断点）

- 常问：遇到“注解不生效/bean 不存在/注入错了/对象变成 proxy”如何排查？
  - 答题要点：先分层：定义层（注册/条件/顺序）vs 实例层（注入/生命周期/代理）；再用最小上下文/最小复现把现象固化为断言。
- 常见追问：如何定位“是谁把对象换成了 proxy”？
  - 答题要点：从 `initializeBean` → `applyBeanPostProcessorsAfterInitialization` 追到具体 BPP；再回到 BPP 的注册顺序与匹配条件（Advisor/类型/注解）。
- 常见追问：条件装配导致 bean 有/没有怎么定位？
  - 答题要点：查看 ConditionEvaluationReport（或 `--debug`）；先回答“为什么 match/why skip”，再核对是否被用户 bean 覆盖或被排除。

## 边界：调试与自检：如何“观察到”容器正在做什么
> 官方参考（Spring Boot 3.5.9，Spring Boot Auto-configuration）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html


- [依赖注入解析](ioc-dependency-injection-resolution.md)
- [Scope 与 prototype 注入陷阱](ioc-scope-and-prototype.md)
- [生命周期](ioc-lifecycle-and-callbacks.md)

## 验收口径：调试与自检：如何“观察到”容器正在做什么
- 是否能把一个现象先分层：定义层（注册/条件/顺序）vs 实例层（注入/生命周期/代理）？
- 能否把“主观判断”转化为“可验证结论”：给出一个最小 LabTest 入口 + 断点入口 + 必看变量？
- 能否在 1 分钟内回答：该 bean “是否已注册/由谁注册/最终暴露对象是什么/为何为 proxy”？

## 小结：调试与自检：如何“观察到”容器正在做什么

- 这章的目标是把“调不动/看不见/栈太深”的问题收敛为一套固定流程：先分层（定义层 vs 实例层），再选对象（定义/候选/依赖边/最终暴露对象），最后用条件断点将噪音压到最小。
- 三个最高收益断点（记住）：
  1. `DefaultListableBeanFactory#doResolveDependency`（注入为什么选中它）
  2. `AbstractAutowireCapableBeanFactory#populateBean`（注入发生点）
  3. `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`（谁把它换成 proxy）
- Boot 自动装配相关问题优先用 `ConditionEvaluationReport`（或 `--debug`）先回答“为什么 match/why skip”，再决定是条件问题还是覆盖/排除问题。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansAutoConfigurationLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansMergedBeanDefinitionLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansDependsOnLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansConditionEvaluationReportLabTest` / `SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansPreInstantiationLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansResourceInjectionLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansProxyingPhaseLabTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/testsupport/BeanGraphDumper.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansExceptionNavigationLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java`
- （另有 9 个 test file 路径引用，略）

<!-- BOOKIFY:END -->
