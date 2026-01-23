# 第 21 章：10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Spring Boot 自动装配如何影响 Bean（Auto-configuration）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansAutoConfigurationBackoffTimingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 20 章：01. Bean 心智模型与注册入口：从 BeanDefinition 到 Bean 实例](../part-01-ioc-container/020-01-bean-mental-model.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationImportOrderingLabTest` / `SpringCoreBeansAutoConfigurationLabTest` / `SpringCoreBeansConditionEvaluationReportLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansAutoConfigurationOverrideMatrixLabTest` / `SpringCoreBeansBeanDefinitionOriginLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationImportOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansConditionEvaluationReportLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationBackoffTimingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansBeanDefinitionOriginLabTest.java`

## 机制主线

你会发现它并不神秘：它本质上就是一套更系统化的 **配置导入（@Import）+ 条件判断（@Conditional...）+ bean 注册**。

## 1. 先说结论：Boot 做了什么？

当你写下 `@SpringBootApplication` 并启动应用时，Boot 至少做了这些与 Bean 相关的事：

1) 创建 `ApplicationContext`
2) 准备 `Environment`（配置、profiles、属性）
3) 通过一系列机制把大量“配置类”导入进来（自动配置）
4) 自动配置类在条件满足时注册大量 bean
5) 你的显式配置（组件扫描、`@Bean`、`@Import`）与自动配置一起决定最终 bean graph

所以你看到的现象是：

- 你没写某个 bean，但容器里确实有（自动配置注册的）
- 你写了某个 bean，自动配置反而“没生效”（条件失败，例如 `@ConditionalOnMissingBean` 不成立）

## 2. 自动装配的入口：`@SpringBootApplication` / `@EnableAutoConfiguration`

`@SpringBootApplication` 里包含 `@EnableAutoConfiguration`。

理解上你可以把它当作：

- “请帮我导入一堆自动配置类”

而“导入一堆类”的技术手段，与 [02 章](../part-01-ioc-container/02-bean-registration.md) 的 `@Import` 思想一致。

## 3. 自动配置类从哪里来？（类清单的来源）

Boot 会从依赖的 jar 包里读取“自动配置类清单”，然后把这些配置类导入容器。

在 Spring Boot 3.x 的体系里，你会看到类似：

- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

> 这类文件本质上是“列出一批配置类”，让 Boot 在启动时统一导入。

你不需要背文件名，但建议知道：

- 自动装配是“可发现”的：starter/依赖带来的 jar 里提供了清单
- 自动装配是“可控制”的：可以 exclude、可以用条件让它不生效

### 3.1 自动配置如何排序？（after/before 主线）

很多人以为 auto-config 的顺序是“文件顺序/列表顺序/记忆顺序”，但真实情况更接近：

- Boot 会对 auto-config 列表做排序（处理 `@AutoConfiguration(after/before=...)` 这类依赖关系）
- 这一步发生在“导入并处理配置类”的主线里
- 排序结果会直接影响后续条件评估与最终注册（尤其是跨 auto-config 的条件/覆盖场景）

学习阶段你不需要背排序实现，但你要能做到：

## 4. 为什么自动配置不是“全都生效”？——条件（Conditions）

自动配置类几乎都带条件，例如（只记语义）：

- `@ConditionalOnClass`：classpath 上存在某个类才装配
- `@ConditionalOnProperty`：某个配置打开才装配
- `@ConditionalOnMissingBean`：容器里没有某个 bean 才装配（让用户可覆盖）

所以最终的 bean graph 是：

> 你写的配置 + 自动配置清单 - 条件失败的部分

很多人背得出 `@ConditionalOnProperty`，但一到 `matchIfMissing` 就容易“凭感觉答题”。

你只要记住一句话：

> `matchIfMissing=true` 不是“没配置就不生效”，而是“没配置也算匹配”。

典型语义（只看行为）：

- property 缺失：如果 `matchIfMissing=true`，条件依然匹配（默认开启特性）
- property=false：明确关闭（条件不匹配）
- property=true：明确开启（条件匹配）

这个问题很适合用来区分“背概念”与“理解容器/自动装配时机”的人：

这通常不是“Spring 乱了”，而是你没把两个概念分开：

1) **条件评估发生在注册阶段**（不是应用 fully refreshed 后）
2) **auto-configuration 的导入/处理顺序**会影响“当下能否看见某个 bean/定义”

所以你应该能回答：

- 为什么“最终容器状态”不能反推“条件评估当时的状态”？
- 如何把这种顺序/时机敏感，变成确定性行为？（答案通常是：`@AutoConfiguration(after/before=...)`）

## 5. 你如何“覆盖”自动配置？

最常见、也最推荐的覆盖方式是：

- 自己提供一个同类型/同语义的 bean
- 自动配置常用 `@ConditionalOnMissingBean`，因此会自动退让

除此之外还有：

- 用 `exclude` 排除某个自动配置类（更强硬、更危险）
- 用 properties 控制条件（更温和、更常用）

这也是为什么“看懂条件”比“背自动配置有哪些”更重要。

### 5.1 back-off 的判断时机：为什么“我写了 Bean 但没有退让”？（排障闭环）

一个非常常见的工程现象：

- 你写了“同类型”的覆盖 bean（或者你以为你写了）
- 但 auto-config 并没有 back-off（导致容器里出现两个同类型 bean，后续注入可能歧义/非预期）

面试官最喜欢追问你能不能把它解释成“时机问题”，而不是背一句“用 @ConditionalOnMissingBean”。

题目：`@ConditionalOnMissingBean` 的判断到底发生在什么时候？它是看“最终容器状态”吗？

追问（加分点）：

- 条件评估入口：`ConditionEvaluator#shouldSkip`
- Bean 条件细节：`OnBeanCondition#getMatchOutcome`
- refresh 主线定位：`AbstractApplicationContext#refresh` → `invokeBeanFactoryPostProcessors`

### 5.2 覆盖/back-off 场景矩阵：重复候选 → 注入失败 → 两类修复

把面试题翻译成工程问题通常是：

- 为什么容器里会有两个同类型候选？（auto-config 没退让 / 覆盖太晚 / 注册了两份）
- 为什么有时应用能启动、有时会直接挂？（取决于是否存在“单注入点”触发候选收敛）
- 你怎么修复？（两条路径：**确定化选择** vs **让退让真正发生**）

题目：当容器里出现两个 `DemoGreeting` 候选时，单注入为什么会 fail-fast？

追问：你有哪些修复方式？分别有什么 trade-off？

1) `@Primary/@Qualifier`：让注入变成确定性选择（候选可能仍然有多个）  
2) 让 back-off 生效：确保覆盖 bean 在条件评估前就可见（更干净）

## 6. 你如何“看见”自动装配做了什么？

学习阶段建议掌握两种手段：

### 6.1 Bean 来源追踪：这个 bean 到底是谁注册的？

当你看到一个 beanName（或一个注入点类型），你必须能回答：

- 它来自哪一个配置类/auto-config？
- 是 `@Bean` 工厂方法注册的，还是“直接类定义/扫描”注册的？
- 为什么它会在容器里出现（条件 match 了吗？有没有覆盖/back-off）？

最通用的入口是：**看 BeanDefinition**。

## 8. 与本模块的关系：你应该带走什么

学完本章，你至少要能把下面这句话解释清楚：

## 面试常问（自动配置与条件装配怎么定位）


## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationImportOrderingLabTest` / `SpringCoreBeansAutoConfigurationLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

这一章的目标是：把 Spring Boot 的自动装配从“玄学”变成“可解释、可调试、可覆盖”的机制。

1) 能观测排序结果（排序后 class 序列是什么）  
2) 能解释排序为什么会影响条件/覆盖  
3) 能给出断点入口（从排序到条件评估）

复现入口（可断言）：
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationImportOrderingLabTest.java`

复现入口（可断言）：
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansConditionEvaluationReportLabTest.java`
  - `conditionalOnProperty_matchesWhenPropertyIsMissing_ifMatchIfMissingIsTrue`
  - `conditionalOnProperty_doesNotMatchWhenPropertyIsExplicitlyFalse_evenIfMatchIfMissingIsTrue`

### 4.2 `@ConditionalOnBean`：为什么“运行时有 bean，但条件仍不生效”？（顺序/时机）

- 你在容器里确实能看到某个 bean（运行时存在）
- 但另一个 auto-config 上的 `@ConditionalOnBean(ThatBean)` 却没有 match（导致 dependent bean 缺失）

复现入口（可断言）：
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOrderingLabTest.java`
  - `conditionalOnBean_canFailAcrossAutoConfigurations_whenOrderingIsNotDefined`
  - `autoConfigurationAfter_canMakeCrossAutoConfigConditionsDeterministic_evenIfImportOrderIsReversed`

1) 为什么“运行时 bean 已存在”不能推出“当时条件就能看到它”？
2) 哪些方式会让覆盖 bean 出现得太晚？（例如某些 `BeanDefinitionRegistryPostProcessor` 在 `ConfigurationClassPostProcessor` 之后注册定义）
3) 你如何用断点证明：条件评估发生在 refresh 前半段（注册阶段），而不是 after refresh？

复现入口（可断言）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationBackoffTimingLabTest.java`
  - `lateBeanDefinitionRegistration_canBypassConditionalOnMissingBean_andCauseDuplicateCandidates`
  - `earlyBeanDefinitionRegistration_runsBeforeConfigurationClassPostProcessor_soAutoConfigurationBacksOffDeterministically`

推荐断点（从现象到闭环）：

- `ApplicationContextRunner#run`：先把调试范围缩到“这一轮最小 context”（降噪）
- 自动配置导入入口：`AutoConfigurationImportSelector#selectImports`（找到“这批 auto-config 是怎么进来的”）
- 条件评估主线：`ConditionEvaluator#shouldSkip`（任何 `@Conditional*` 都会汇入这里）
- Bean 条件细节：`OnBeanCondition#getMatchOutcome`（`@ConditionalOnMissingBean/@ConditionalOnBean` 的核心分支）
- 定义注册：`DefaultListableBeanFactory#registerBeanDefinition`（观察“同名/同类型定义”何时进入 registry）
- 定义层时机（关键闭环）：`PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
  - 结合你自己的 registrar：`BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`
  - 用它证明：**early registrar 能在条件评估前把 override 定义放进去；late registrar 则会绕过 back-off**
- 最终炸点（当重复候选遇到单注入点）：`DefaultListableBeanFactory#doResolveDependency`
  - 继续走到：`findAutowireCandidates` → `determineAutowireCandidate`（Primary/Qualifier/name 的收敛分支）

复现入口（可断言）：
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java`

1) **打开调试报告**（Condition Evaluation Report）  
2) **直接在运行时查询容器**（beans by type/name、BeanDefinition 等）

具体做法放在下一章：[11. 调试与自检](019-11-debugging-and-observability.md)。

复现入口（可断言）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansBeanDefinitionOriginLabTest.java`
  - 用 `BeanDefinitionOriginDumper` 输出 beanDefinition 的 class/factoryMethod/resource/source/role 等关键信息

## 7. 在本模块里如何“跑起来验证”

本模块提供了几组 Boot 自动装配实验（Labs），用最小可控的方式把“条件生效/失效、覆盖、定位、顺序”跑出来：

- 对应测试：`src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationLabTest.java`
  - 使用 `ApplicationContextRunner`：更快、更聚焦，不需要启动完整应用
  - 覆盖点：
    - `@ConditionalOnProperty`：属性缺失 vs 属性开启
    - `@ConditionalOnClass`：类存在 vs 类缺失（用 `FilteredClassLoader` 模拟“可选依赖不存在”）
    - `@ConditionalOnMissingBean`：用户自定义 bean 覆盖（auto-config 自动退让）

- 对应测试：`src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansConditionEvaluationReportLabTest.java`
  - 覆盖点：
    - 把 Condition Evaluation Report 当成“可查询数据结构”（而不是只会开 `--debug`）
    - `matchIfMissing=true` 的缺省值语义（missing/false/true 三态）

- 对应测试：`src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOrderingLabTest.java`
  - 覆盖点：
    - 自动配置之间的顺序依赖：为什么某些 `@ConditionalOnBean` 会“看起来没生效”
    - 如何用 `@AutoConfiguration(after/before=...)` 把行为确定化（避免依赖“列表顺序/文件顺序/记忆”）

- 对应测试：`src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationBackoffTimingLabTest.java`
  - 覆盖点：
    - back-off 的判断时机：为什么你“写了覆盖 Bean”但 auto-config 没退让
    - 用 early/late registrar 对照把“时机差异”跑成可断言结论，并给出断点闭环入口

- 对应测试：`src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationImportOrderingLabTest.java`
  - 覆盖点：
    - after/before 的排序主线：排序后 class 序列如何影响后续的条件与注册

- 对应测试：`src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansBeanDefinitionOriginLabTest.java`
  - 覆盖点：
    - BeanDefinition 来源追踪：factory method vs direct class、resource/source 元信息

- 对应测试：`src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java`
  - 覆盖点：
    - 重复候选矩阵：NoUnique fail-fast + 两类修复（primary/qualifier vs back-off）

运行方式：

```bash
mvn -pl :spring-core-beans test
```

运行时你会在测试输出里看到以 `OBSERVE:` 开头的少量提示行，解释“哪个条件命中、最终注册/选择了哪个 bean”。

> Spring Boot 自动装配不是“替你注入”，而是“替你导入配置并注册 BeanDefinition”，最终依赖注入仍遵循 Spring 容器的解析规则（类型、`@Qualifier`、`@Primary`、scope、生命周期……）。
对应 Lab/Test：
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationLabTest.java`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansConditionEvaluationReportLabTest.java`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOrderingLabTest.java`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationBackoffTimingLabTest.java`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationImportOrderingLabTest.java`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansBeanDefinitionOriginLabTest.java`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java`

推荐断点（按“从入口到决策”）：
- 自动配置入口：`AutoConfigurationImportSelector#selectImports`
- 条件评估主线：`ConditionEvaluator#shouldSkip`
- 条件细节（Bean 条件）：`OnBeanCondition#getMatchOutcome`
- 注册定义：`DefaultListableBeanFactory#registerBeanDefinition`

- 常问：Spring Boot 自动装配到底做了什么？它和依赖注入是什么关系？
  - 答题要点：自动装配本质是“按条件导入配置并注册 BeanDefinition”；注入仍走 Spring 容器的依赖解析规则（type/qualifier/primary/scope/lifecycle）。
- 常见追问：当一个 bean “有/没有”时，你怎么解释与定位？
  - 答题要点：看 `ConditionEvaluator#shouldSkip` 的条件评估；结合 ConditionEvaluationReport（或 `--debug`）回答“为什么 match/why skip”，再看是否被用户定义 bean 覆盖、是否被排除自动配置。

- 常问：`matchIfMissing=true` 到底是什么意思？它会造成什么坑？
  - 答题要点：缺省即匹配（默认开启），当你以为“没配就关闭”时会踩坑；要能讲清 missing/false/true 三态行为。
  - 复现入口：`SpringCoreBeansConditionEvaluationReportLabTest`（missing/false/true 三态）
- 常问：为什么某个 `@ConditionalOnBean` 看起来没生效？（明明运行时 bean 已存在）
  - 追问：你如何解释“条件评估时机”与“容器最终状态”的差异？如何把它变成确定性行为？
  - 答题要点：条件评估发生在注册阶段；跨自动配置依赖必须考虑顺序与 after/before 元数据；用 `ConditionEvaluationReport` + `OnBeanCondition#getMatchOutcome` 定位“为什么不 match”。
  - 复现入口：`SpringCoreBeansAutoConfigurationOrderingLabTest`（失败对照 + after 修复）

- 常问：这个 bean 到底是谁注册的？（来自哪个 auto-config / 哪个 @Bean 方法？）
  - 追问：你如何在不看一堆日志的情况下快速定位？
  - 答题要点：看 BeanDefinition（factoryBeanName/factoryMethodName/resource/source/role），再结合条件报告定位“为什么出现”。
  - 复现入口：`SpringCoreBeansBeanDefinitionOriginLabTest`（BeanDefinitionOriginDumper）

- 常问：为什么容器里会出现两个同类型候选？为什么有时能启动、有时会因为 NoUnique 直接挂？
  - 追问：你有哪些修复方式？怎么选？
  - 答题要点：重复候选不一定立刻炸；只要出现单注入点就会触发候选收敛并 fail-fast。修复分两类：`@Primary/@Qualifier` 确定化选择（候选仍可能多个） vs 让 auto-config back-off 生效（更干净）。
  - 复现入口：`SpringCoreBeansAutoConfigurationOverrideMatrixLabTest`

## 常见坑与边界

### 4.1 `matchIfMissing`：缺省值语义（面试高频坑）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationImportOrderingLabTest` / `SpringCoreBeansAutoConfigurationLabTest` / `SpringCoreBeansConditionEvaluationReportLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansAutoConfigurationOverrideMatrixLabTest` / `SpringCoreBeansBeanDefinitionOriginLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationImportOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansConditionEvaluationReportLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationBackoffTimingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansBeanDefinitionOriginLabTest.java`
- （另有 1 个 test file 路径引用，略）

上一章：[09. 循环依赖概览：三级缓存与现象分类](../part-01-ioc-container/09-circular-dependencies.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[11. 调试与可观察性：从异常到断点入口](019-11-debugging-and-observability.md)

<!-- BOOKIFY:END -->
