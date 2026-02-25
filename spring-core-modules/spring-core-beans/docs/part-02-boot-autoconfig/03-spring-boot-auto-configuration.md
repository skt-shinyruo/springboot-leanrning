# 03. Spring Boot 自动装配如何影响 Bean（Auto-configuration）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Spring Boot 自动装配如何影响 Bean（Auto-configuration）
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansAutoConfigurationBackoffTimingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[09. Bean 运行机制：从 BeanDefinition 到最终暴露对象](../part-01-ioc-container/09-bean-mental-model.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 容器启动与基础设施处理器：为什么注解能工作？](../part-03-container-internals/01-container-bootstrap-and-infrastructure.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

- 官方文档对照（适用版本：Spring Boot 3.5.9）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html
- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! summary "本章要点"

    - 读完本章，应能够用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见误区在哪里”。
    - 如果只看一眼：请先运行一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationImportOrderingLabTest` / `SpringCoreBeansAutoConfigurationLabTest` / `SpringCoreBeansConditionEvaluationReportLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansAutoConfigurationOverrideMatrixLabTest` / `SpringCoreBeansBeanDefinitionOriginLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationImportOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansConditionEvaluationReportLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationBackoffTimingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansBeanDefinitionOriginLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`，再用 `SpringCoreBeansAutoConfigurationImportOrderingLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`ConditionEvaluator#shouldSkip`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“常见误区与边界”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/03-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/07-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Boot 3.5.9，Spring Boot Auto-configuration）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html

可以将其机制概括为一套更系统化的 **配置导入（@Import）+ 条件判断（@Conditional...）+ bean 注册**。

### 自动装配角色分工（先记住 4 个入口）

- 导入：`AutoConfigurationImportSelector#selectImports`  
- 排序：`AutoConfigurationImportSorter`  
- 条件评估：`ConditionEvaluator#shouldSkip`  
- 定义注册：`ConfigurationClassPostProcessor#processConfigBeanDefinitions`

## 1. 先说结论：Boot 做了什么？

当读者写下 `@SpringBootApplication` 并启动应用时，Boot 至少做了这些与 Bean 相关的事：

1) 创建 `ApplicationContext`
2) 准备 `Environment`（配置、profiles、属性）
3) 通过一系列机制把大量“配置类”导入进来（自动配置）
4) 自动配置类在条件满足时注册大量 bean
5) 相应的显式配置（组件扫描、`@Bean`、`@Import`）与自动配置一起决定最终 bean graph

所以观察到的现象是：

- 读者没写某个 bean，但容器里确实有（自动配置注册的）
- 读者写了某个 bean，自动配置反而“没生效”（条件失败，例如 `@ConditionalOnMissingBean` 不成立）

### 1.1 机制系统阐述：条件 → 分支 → 结果（Boot 版）

**条件**：是否满足 `@Conditional*`（classpath/属性/已有 bean）  
**分支**：`ConditionEvaluator#shouldSkip` 决定跳过/注册  
**结果**：  
- 条件通过 → 注册 BeanDefinition  
- 条件不通过 → 自动配置被跳过（即使类在导入清单里）  
**断点建议**：`ConditionEvaluator#shouldSkip`

## 2. 自动装配的入口：`@SpringBootApplication` / `@EnableAutoConfiguration`

`@SpringBootApplication` 里包含 `@EnableAutoConfiguration`。

理解上可以把它当作：

- “导入一组自动配置类”

而“导入一组类”的技术手段，与 [02 章](../part-01-ioc-container/01-bean-registration.md) 的 `@Import` 思想一致。

## 3. 自动配置类从哪里来？（类清单的来源）

Boot 会从依赖的 jar 包里读取“自动配置类清单”，然后把这些配置类导入容器。

在 Spring Boot 3.x 的体系里，可以观察到类似：

- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

> 这类文件本质上是“列出一批配置类”，让 Boot 在启动时统一导入。

无需背文件名，但建议知道：

- 自动装配是“可发现”的：starter/依赖带来的 jar 里提供了清单
- 自动装配是“可控制”的：可以 exclude、可以用条件让它不生效

### 3.1 自动配置如何排序？（after/before 主线）

很多人以为 auto-config 的顺序是“文件顺序/列表顺序/记忆顺序”，但真实情况更接近：

- Boot 会对 auto-config 列表做排序（处理 `@AutoConfiguration(after/before=...)` 这类依赖关系）
- 这一步发生在“导入并处理配置类”的主线里
- 排序结果会直接影响后续条件评估与最终注册（尤其是跨 auto-config 的条件/覆盖场景）

学习阶段无需背排序实现，但应能够做到：

- 能在 `ConditionEvaluationReport` 里观察到某个 auto-config 的 match/no-match 结果（先回答“为什么”）
- 能在断点里定位：auto-config 列表是在哪一步被导入、在哪一步被排序、在哪一步被条件过滤

### 3.2 源码调用链（方法级）：导入清单 → 排序 → 条件评估 → 注册定义

> 目标：把“自动装配”从概念落到方法级证据链。无需记全，只要记住 4 个稳定锚点：导入（selectImports）→ 条件（shouldSkip）→ 注册（registerBeanDefinition）→ 最终注入（doResolveDependency）。

一条足够实用的主链是：

1) 自动配置导入（决定“导入哪些配置类”）
   - `AutoConfigurationImportSelector#selectImports`
   - `#getCandidateConfigurations`（读取 imports 清单并做过滤/去重）
2) 条件评估（决定“哪些配置类/哪些 @Bean 方法要跳过”）
   - `ConditionEvaluator#shouldSkip`（任何 `@Conditional*` 都会汇入这里）
   - 细分：`OnBeanCondition#getMatchOutcome` / `SpringBootCondition#matches`
3) 定义注册（决定“哪些 BeanDefinition 真正进入 registry”）
   - `DefaultListableBeanFactory#registerBeanDefinition`
   - 观察点：beanName 冲突、是否允许 overriding、是否已有同类定义
4) 最终注入（当有注入点出现时，候选才会被收敛/可能 fail-fast）
   - `DefaultListableBeanFactory#doResolveDependency` → `findAutowireCandidates` → `determineAutowireCandidate`

> 关键结论：自动装配解决的是“把定义导入并注册进容器”；依赖注入解决的是“在注入点从候选里选一个”。两者是不同阶段的问题。

### 3.3 Boot 2 vs Boot 3：自动配置类清单来源的关键差异（排障必备）
> 官方参考（Spring Boot 3.5.9，Spring Boot Auto-configuration）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html


自动配置“从哪里来”是很多偶发问题的根：同样一个 starter、同样一段条件，为什么在不同版本/不同构建方式下行为不一致？

建议把清单来源记成两代模型（不要求背细节，但要知道排障入口）：

- **Boot 2.x 常见入口**：`spring.factories`（历史机制，仍可能在一些场景里被兼容读取）
- **Boot 3.x 主流入口**：`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

这会直接影响排障时应检查的文件类别与断点入口，并解释为何“已引入依赖但自动配置未生效”有时并非条件问题，而是“清单未被导入”。

> 实战建议：当怀疑“自动配置类清单未导入”时，不宜从 `@Conditional` 开始推测；应先通过断点或条件报告验证“清单是否被导入 + 导入顺序”。

## 4. 为什么自动配置不是“全都生效”？——条件（Conditions）

自动配置类几乎都带条件，例如（只记语义）：

- `@ConditionalOnClass`：classpath 上存在某个类才装配
- `@ConditionalOnProperty`：某个配置打开才装配
- `@ConditionalOnMissingBean`：容器里没有某个 bean 才装配（让用户可覆盖）

所以最终的 bean graph 是：

> 编写的配置 + 自动配置清单 - 条件失败的部分

很多人背得出 `@ConditionalOnProperty`，但一到 `matchIfMissing` 就容易“凭感觉答题”。

读者只要记住一句话：

> `matchIfMissing=true` 不是“没配置就不生效”，而是“没配置也算匹配”。

典型语义（只看行为）：

- property 缺失：如果 `matchIfMissing=true`，条件依然匹配（默认开启特性）
- property=false：明确关闭（条件不匹配）
- property=true：明确开启（条件匹配）

这个问题很适合用来区分“背概念”与“理解容器/自动装配时机”的人：

这通常不是 Spring 行为异常，而是读者未将两个概念区分清楚：

1) **条件评估发生在注册阶段**（不是应用 fully refreshed 后）
2) **auto-configuration 的导入/处理顺序**会影响“当下能否观察到某个 bean/定义”

所以应能够回答：

- 为什么“最终容器状态”不能反推“条件评估当时的状态”？
- 如何把这种顺序/时机敏感，变成确定性行为？（答案通常是：`@AutoConfiguration(after/before=...)`）

## 5. 如何“覆盖”自动配置？

最常见、也最推荐的覆盖方式是：

- 自己提供一个同类型/同语义的 bean
- 自动配置常用 `@ConditionalOnMissingBean`，因此会自动退让

除此之外还有：

- 用 `exclude` 排除某个自动配置类（更强硬、更危险）
- 用 properties 控制条件（更温和、更常用）

这也是为什么“看懂条件”比“背自动配置有哪些”更重要。

### 5.1 back-off 的判断时机：为什么“定义了 Bean 但未触发退让”？（排障闭环）

一个非常常见的工程现象：

- 读者写了“同类型”的覆盖 bean（或者容易误以为读者写了）
- 但 auto-config 并没有 back-off（导致容器里出现两个同类型 bean，后续注入可能歧义/非预期）

面试官最喜欢追问：应能够把它解释成“时机问题”，而不是仅背诵“用 @ConditionalOnMissingBean”。

题目：`@ConditionalOnMissingBean` 的判断到底发生在什么时候？它是看“最终容器状态”吗？

追问（加分点）：

- 条件评估入口：`ConditionEvaluator#shouldSkip`
- Bean 条件细节：`OnBeanCondition#getMatchOutcome`
- refresh 主线定位：`AbstractApplicationContext#refresh` → `invokeBeanFactoryPostProcessors`

### 5.2 覆盖/back-off 场景矩阵：重复候选 → 注入失败 → 两类修复

把面试题翻译成工程问题通常是：

- 为什么容器里会有两个同类型候选？（auto-config 没退让 / 覆盖太晚 / 注册了两份）
- 为什么有时应用能启动、有时会直接挂？（取决于是否存在“单注入点”触发候选收敛）
- 如何修复？（两条路径：**确定化选择** vs **让退让真正发生**）

题目：当容器里出现两个 `DemoGreeting` 候选时，单注入为什么会 fail-fast？

追问：读者有哪些修复方式？分别有什么 trade-off？

1) `@Primary/@Qualifier`：让注入变成确定性选择（候选可能仍然有多个）
2) 让 back-off 生效：确保覆盖 bean 在条件评估前就可见（更干净）

## 可复现闭环（基于 `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`）

运行完成该 Lab，至少应能够复述 3 条结论：

1) **back-off 是定义层时机问题**  
   - 断点：`ConditionEvaluator#shouldSkip`  
   - 断言：覆盖 bean 是否在条件评估时已可见
2) **顺序影响条件判断**  
   - 断点：`AutoConfigurationImportSorter`  
   - 断言：排序变化会改变 back-off 结果
3) **定义来源可追溯**  
   - 断点：`registerBeanDefinition`  
   - 断言：`beanDefinition.getSource()` 能定位到 auto-config 类

## 6. 如何“观察到”自动装配做了什么？

学习阶段建议掌握两种手段：

### 6.1 Bean 来源追踪：这个 bean 到底是谁注册的？

当读者看到一个 beanName（或一个注入点类型），必须能回答：

- 它来自哪一个配置类/auto-config？
- 是 `@Bean` 工厂方法注册的，还是“直接类定义/扫描”注册的？
- 为什么它会在容器里出现（条件 match 了吗？有没有覆盖/back-off）？

最通用的入口是：**看 BeanDefinition**。

## 7. 与本模块的关系：应当带走什么

学完本章，至少应能够把下面这句话解释清楚：

> **Spring Boot 自动装配不是“运行时自动注入”，而是在“注册阶段”把一批候选配置类导入进来，并在条件评估阶段决定哪些配置/BeanDefinition 真正落进容器；当用户显式提供同类能力时，自动配置应当 back-off（让用户配置优先）。**

## 面试常问（自动配置与条件装配怎么定位）

1) **如何定位“为什么某个自动配置生效/不生效”？（不靠猜日志）**
   - 要点：先看 `ConditionEvaluationReport`（报告告诉读者 match / no match 的理由），再到 `OnBeanCondition#getMatchOutcome` / `SpringBootCondition#matches` 设置断点确认“评估时机与输入是什么”。需要跨配置依赖时，再回到排序与 after/before 元数据（见本模块 ordering labs）。

2) **如何定位“某个 bean 到底是谁注册的”？**
   - 要点：看 `BeanDefinition` 的来源字段（factoryBeanName/factoryMethodName/resource/source/role），把“来自哪个 auto-config / 哪个 @Bean 方法”变成可观测事实，而不是翻日志。

3) **如何解释“为什么有时能启动、有时会因为 NoUnique 直接挂”？**
   - 要点：重复候选不一定立即暴露问题，只有当出现单注入点时才需要收敛候选；修复要么确定化选择（`@Primary/@Qualifier`），要么让自动配置 back-off（从根源消除多余候选）。

## 8. 在本模块里如何“运行验证”（最小复现 + 断点闭环）

这一章的目标是：把 Spring Boot 的自动装配从“黑箱”变成“可解释、可调试、可覆盖”的机制。

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

- 在容器里确实能看到某个 bean（运行时存在）
- 但另一个 auto-config 上的 `@ConditionalOnBean(ThatBean)` 却没有 match（导致 dependent bean 缺失）

复现入口（可断言）：
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOrderingLabTest.java`
  - `conditionalOnBean_canFailAcrossAutoConfigurations_whenOrderingIsNotDefined`
  - `autoConfigurationAfter_canMakeCrossAutoConfigConditionsDeterministic_evenIfImportOrderIsReversed`

1) 为什么“运行时 bean 已存在”不能推出“当时条件就能看到它”？
2) 哪些方式会让覆盖 bean 出现得太晚？（例如某些 `BeanDefinitionRegistryPostProcessor` 在 `ConfigurationClassPostProcessor` 之后注册定义）
3) 如何用断点证明：条件评估发生在 refresh 前半段（注册阶段），而不是 after refresh？

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
  - 结合读者自己的 registrar：`BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`
  - 用它证明：**early registrar 能在条件评估前把 override 定义放进去；late registrar 则会绕过 back-off**
- 最终触发点（当重复候选遇到单注入点）：`DefaultListableBeanFactory#doResolveDependency`
  - 继续走到：`findAutowireCandidates` → `determineAutowireCandidate`（Primary/Qualifier/name 的收敛分支）

复现入口（可断言）：
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java`

1) **打开调试报告**（Condition Evaluation Report）
2) **直接在运行时查询容器**（beans by type/name、BeanDefinition 等）

具体做法放在下一章：[11. 调试与自检](01-debugging-and-observability.md)。

复现入口（可断言）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansBeanDefinitionOriginLabTest.java`
  - 用 `BeanDefinitionOriginDumper` 输出 beanDefinition 的 class/factoryMethod/resource/source/role 等关键信息

### 8.1 Labs 清单（按主题）

本模块提供了几组 Boot 自动装配实验（Labs），以最小可控的方式复现“条件生效/失效、覆盖、定位、顺序”：

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
    - back-off 的判断时机：为什么读者“写了覆盖 Bean”但 auto-config 没退让
    - 用 early/late registrar 对照将“时机差异”整理为可断言结论，并给出断点闭环入口

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

运行时可以在测试输出里看到以 `OBSERVE:` 开头的少量提示行，解释“哪个条件命中、最终注册/选择了哪个 bean”。

> Spring Boot 自动装配不是“替读者注入”，而是“替读者导入配置并注册 BeanDefinition”，最终依赖注入仍遵循 Spring 容器的解析规则（类型、`@Qualifier`、`@Primary`、scope、生命周期……）。
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

## 常见误区与边界

### 4.1 `matchIfMissing`：缺省值语义（面试高频误区）

- `matchIfMissing=true` 常见于“debug 开关/观测开关”：**读者没配并不代表关闭**，而是“缺省即匹配”（默认开启）。
- 应能够区分三态：
  - **missing**：属性未配置（会触发 matchIfMissing 的语义）
  - **false**：显式关闭
  - **true**：显式开启
- 复现入口：`SpringCoreBeansConditionEvaluationReportLabTest`（missing/false/true 三态对照）

## 自检要点
- 应能够用一句话解释：自动装配（auto-configuration）主要发生在定义阶段还是创建阶段吗？为什么？
- 应能够说出：定位“为什么生效/为什么不生效”的最短证据链是什么吗？（提示：ConditionEvaluationReport + 断点到 matchOutcome）
- 应能够区分：overriding（同名定义冲突）和 NoUnique（同类型注入歧义）吗？它们分别怎么修？

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationImportOrderingLabTest` / `SpringCoreBeansAutoConfigurationLabTest` / `SpringCoreBeansConditionEvaluationReportLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansAutoConfigurationOverrideMatrixLabTest` / `SpringCoreBeansBeanDefinitionOriginLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationImportOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansConditionEvaluationReportLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationBackoffTimingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansBeanDefinitionOriginLabTest.java`
- （另有 1 个 test file 路径引用，略）

上一章：[09. 循环依赖概览：三级缓存与现象分类](../part-01-ioc-container/08-circular-dependencies.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[11. 调试与可观察性：从异常到断点入口](01-debugging-and-observability.md)

<!-- BOOKIFY:END -->
