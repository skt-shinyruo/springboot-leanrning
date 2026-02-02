# spring-core-beans

## Purpose

讲透 Spring Framework IoC 容器与 Bean：从定义注册 → 注入解析 → 生命周期 → 扩展点 → 代理/循环依赖边界，做到“能解释、能断点、能定位问题”。

## Module Overview

- **Responsibility:** 提供 Bean 机制的系统文档与可运行 Labs/Exercises，用于建立源码级抓手与排障能力。
- **Docs Reading:** 推荐从 `spring-core-modules/spring-core-beans/docs/README.md` 开始（书本目录 + Part 划分）；主线可按 Part 顺读，每章顶部提供“上一章｜目录｜下一章”导航，降低章节切换成本。
- **Docs Tone:** `spring-core-beans` 文档全章采用教材化书面语，避免第二人称、俚语与口语化表达；保留术语、代码标识与引用路径的准确性。
- **Why Index（基础问题索引 / SSOT）:** `spring-core-modules/spring-core-beans/docs/part-00-guide/009-00-why-index.md`（覆盖：三级缓存/three level cache、early reference、raw vs wrapped、proxy 替换；并提供跨模块回链到 AOP）
- **内容级再加深（逐章可执行策略）:** `spring-core-modules/spring-core-beans/docs/deepening-strategies/README.md`（按 Part/章节给出“入口实验（Lab/Test）+ 第一断点入口 + 如何收敛 + 最短下一跳”的加深建议，避免统一模板作文）
- **内容级再加深（已写入正文）:** 各章节已内嵌 `AE-DEEPENING` 提示块（位于章末/BOOKIFY 前），用于提示读者“第一断点在哪里、如何把结论自证、下一跳去哪里”，避免停留在口号式或维度清单式建议
- **症状快速定位（目录页入口）:** `spring-core-modules/spring-core-beans/docs/README.md`（新增“症状驱动导航（快速定位）”，用于从现象直达章节与证据链入口）
- **Start Here（30 分钟快启）:** 先运行 3 个最小实验建立容器主线直觉，再进入深潜：`spring-core-modules/spring-core-beans/docs/part-00-guide/012-01-quickstart-30min.md`。
- **Auto-Config 顺序（Boot/容器交汇点）:** `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/020-09-auto-config-ordering.md`
- **断点地图（可复用清单）:** `spring-core-modules/spring-core-beans/docs/part-00-guide/013-02-breakpoint-map.md`
- **知识地图 ↔ 断点地图互链:** 知识地图表新增“断点组（C1–C7）”链接；断点地图补齐 `#c1..#c7` 稳定锚点并提供“现象 → 断点组”快速入口，便于从现象直接跳到可复用断点组
- **循环依赖（现象→窗口期→规避）:** `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`
- **Early Reference 深挖（getEarlyBeanReference）:** `spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md`
- **Explore/Debug（可选启用）:** `spring-core-modules/spring-core-beans/docs/appendix/97-explore-debug-tests.md`
- **手工注册 BPP（顺序/时机陷阱）:** `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`
- **`@Resource` 注入（name-first）:** `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/32-resource-injection-name-first.md`
- **`@Value("${...}")` 占位符（strict vs non-strict）:** `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`
- **类型转换（BeanWrapper/ConversionService）:** `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`
- **Debugger Pack（断点包总入口）:** `spring-core-modules/spring-core-beans/docs/appendix/98-debugger-pack.md`（聚合主线/分支/排障/性能并发入口）
- **团队内训讲义（可直接用于授课）:** `spring-core-modules/spring-core-beans/docs/appendix/99-team-training-kit.md`（60/90/120 分钟课时脚本 + Labs/断点/互动题）
- **关键分支矩阵（If/Then 决策表）:** `spring-core-modules/spring-core-beans/docs/part-00-guide/011-04-branch-decision-matrix.md`
- **排障 playbook:** `spring-core-modules/spring-core-beans/docs/appendix/025-90-common-pitfalls.md`
- **自检清单:** `spring-core-modules/spring-core-beans/docs/appendix/026-99-self-check.md`
- **生产排障清单（最短 SOP）:** `spring-core-modules/spring-core-beans/docs/appendix/94-production-troubleshooting-checklist.md`（新增 3 类高频事故的 3–5 步最短诊断路径，并回链到断点组/章节/Lab）
- **主线叙事（源码级）:** `spring-core-modules/spring-core-beans/docs/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`（`refresh()` → `doCreateBean()`，关键方法 + 关键分支）
- **排障速查（分支决策表）:** 同章内新增“现象 → 阶段 → 关键方法 → 必看变量 → LabTest”对照表，把主线叙事压缩成可复用排障套路（注入失败/循环依赖/代理形态/FactoryBean/预实例化等）
- **注解为何生效（bootstrap）:** `spring-core-modules/spring-core-beans/docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md`（新增“处理器速查表 + 时机时间线 + 过早 getBean 反例”）
- **深挖指南（症状驱动导航）:** `spring-core-modules/spring-core-beans/docs/part-00-guide/011-00-deep-dive-guide.md`（新增“按现象选章节/断点/Lab”的速查表）
- **Learning Path（路线图）:** `helloagents/wiki/learning-path.md`（主线：Beans → AOP → Tx → Web MVC）
- **第一个可运行入口（3 分钟启动）:**
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans test`
  - 对应测试类：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`
- **Docs 教程一致性修复（2026-01-26）:** 完成 beans 模块 docs 全章“教程化”补齐：统一补齐排障分流/常见坑/面试常问/一句话自检，清理空标题与层级问题；并将 `refresh() → doCreateBean()` 主线章书本化（导读/实验入口/分支决策表/BOOKIFY 导航）。
- **Highlights:** 在补齐类型转换/泛型匹配章节与 Labs 闭环的基础上，进一步统一 docs 的“上一章｜目录｜下一章”导航与“复现入口（可运行）”块；新增 JSR-330 `@Inject`/`Provider<T>` 对照 Lab，并增强 testsupport dumper 让排障输出更结构化；补齐 3 类易翻车边界机制 Labs（编程式注册差异 / allowRawInjectionDespiteWrapping / prototype 销毁语义），并将入口落位到 docs/04、docs/05、docs/16、docs/25；新增 Part 05（AOT/RuntimeHints/XML/容器外对象/SpEL/自定义 Qualifier）与对应 Labs，并新增面试复述模板与生产排障清单用于体系化复盘；同时为 Exercises 补齐对应 Solution（默认参与回归），并在 docs/README 收敛“章节↔Lab↔Exercise↔Solution”对照表与运行建议，补强 ImportSelector 等新手高频卡点的“源码主线/断点/观察点”；进一步补齐 Spring Framework `spring-beans` 体系的 5 组“真实世界常见但容易缺失”的机制闭环（docs 46–50：XML namespace 扩展 / Properties+Groovy Reader / replaced-method 方法注入 / 内置 FactoryBean / PropertyEditor+值解析），并新增对应 Labs（默认参与回归）；补齐 Spring Framework `BeanFactory API` 与 `Environment Abstraction` 两类常用但容易“只会用不会解释”的主题：新增 docs/38–39 与对应可断言 Labs（默认参与回归）；新增 spring-beans Public API 索引（docs Appendix 95/96）用于“按类型检索/可审计”，并补齐 aot.factories/AotServices 与 ServiceLoader*FactoryBean 的闭环，新增 Explore/Debug 用例（docs Appendix 97，显式开关启用，不影响默认回归）；并补齐 `org.springframework.beans.support`（ArgumentConvertingMethodInvoker/ResourceEditorRegistrar/PropertyComparator/PagedListHolder/SortDefinition）闭环，新增可运行 Lab，并将 Appendix 96 Gap 归零；本次进一步把 Part 05 与 Appendix（90/99/91–95）从“要点”升级为“机制讲透 + 方法级调用链 + 排障/面试复述模板”，并补齐 RuntimeHints/AOT/SpEL/FactoryBean/值解析等章节的可复现闭环；并对 `spring-core-beans/docs` 与 `spring-core-beans/README.md` 全量完成书面化改写（去第二人称/俚语/口语化措辞），提升“可复述/可归档/可交付”的文本风格一致性。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-29

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`
  - 对应测试类：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBookMatrixLabTest.java`

- **Branch Matrix（关键分支入口）：**
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansIocBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInternalsBranchMatrixLabTest test`
  - 对应测试类：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansIocBranchMatrixLabTest.java`
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansInternalsBranchMatrixLabTest.java`
- **Solutions（Exercises 对应答案回归）：** `mvn -q -pl :spring-core-beans -Dtest=*ExerciseSolutionTest test`
- **Lab（并发/性能：同一 BeanFactory 并发 getBean）：** `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansConcurrentGetBeanLabTest test`

## Source Layout（与 docs Part 对齐）

为保证“像书本一样”的可发现性与可复现性，`spring-core-beans` 的源码与测试代码按 docs 的 Part 结构分组：

- `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/**` ⇔ `src/main/java/.../part01_ioc_container/**` + `src/test/java/.../part01_ioc_container/**`
- `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/**` ⇔ `src/test/java/.../part02_boot_autoconfig/**`
- `spring-core-modules/spring-core-beans/docs/part-03-container-internals/**` ⇔ `src/test/java/.../part03_container_internals/**`
- `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/**` ⇔ `src/test/java/.../part04_wiring_and_boundaries/**`
- `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/**` ⇔ `src/test/java/.../part05_aot_and_real_world/**`
- `spring-core-modules/spring-core-beans/docs/appendix/**` ⇔ `src/test/java/.../appendix/**`
- 跨 Part 的测试支撑：`src/test/java/.../testsupport/**`
- 并发/性能 Labs（可复现实验范式）：`src/test/java/.../part02_perf_concurrency/**`
- 断点包入口（聚合回归）：`src/test/java/.../part00_guide/SpringCoreBeansBreakpointPackLabTest.java`
- 主线调用链入口：`src/test/java/.../part00_guide/SpringCoreBeansMainlineCallChainLabTest.java`
- 排障 Playbook 入口：`src/test/java/.../appendix/SpringCoreBeansTroubleshootingPlaybookLabTest.java`
- 性能/并发入口：`src/test/java/.../appendix/SpringCoreBeansPerformanceConcurrencyLabTest.java`

约束（必须遵守）：

- 必须保留 `com.learning.springboot.springcorebeans.SpringCoreBeansApplication` 的包名不变（便于 Spring Boot 测试向上包查找 `@SpringBootConfiguration`）。

## Specifications

### Requirement: spring-core-beans docs 官方参考下压到关键分支（Round 3）

- 本轮方案包（关键分支/关键结论段落就地对照）：`helloagents/history/2026-02/202602021237_spring_core_beans_docs_deepen_inline_refs_round3/`
- 状态：已完成实现与回归测试并归档（2026-02-02）
**Module:** spring-core-beans
在上一轮已将“官方参考”下压到各章“机制主线”开头的基础上，本轮进一步把对照入口下压到正文里的高价值位置：关键分支触发条件、排障决策表、常见误区与边界、以及关键结论段落旁边。目标是让读者在读到结论的同一屏就能对齐官方语义，减少“读完再去翻文档”的上下文切换。

#### Scenario: 每章至少两处可就地对照
- 机制主线：保留主语义锚点（已有）
- 关键分支/排障/误区：新增 1 处更贴近“结论/分支”的对照入口（按章主题选取链接）

#### Scenario: Reference 页面更贴合主题
- 对 Environment/PropertySource、类型转换（ConversionService）等主题，补齐并使用更准确的官方 Reference 页面（避免一律落在 `beans.html`）
- 对 `@Resource`（name-first 注入）等易误判主题，修正对照入口为注解驱动/注入语义页面

#### Scenario: 质量门禁（必须可回归）
- `mvn -q -pl :spring-core-beans test` 回归通过

### Requirement: spring-core-beans docs 官方参考下压到正文（就地对照）

- 本轮方案包（正文就地下压官方参考入口）：`helloagents/history/2026-02/202602021135_spring_core_beans_docs_deepen_body_refs/`
- 状态：已完成实现与回归测试并归档（2026-02-02）
**Module:** spring-core-beans
在不引入统一模板/固定栏目、不破坏既有章节叙事的前提下，将 Spring/Spring Boot 官方 Reference 的对照入口从“导读/顶部”进一步下压到正文关键结论附近（优先落在“机制主线”段落内），并按章节主题选择最相关的 Reference 页面，降低读者在机制/源码/排障阅读时的上下文切换成本。

#### Scenario: 每章“机制主线”就地对照入口
- 大多数章节在 `## 机制主线` 段落开头补充 `> 官方参考（适用版本语境）`，把本章主语义锚定到官方定义
- AOT/Boot/SpEL/Resources 等跨主题章节按内容选择对应 Reference 页面（避免全章机械重复同一链接）

#### Scenario: 质量门禁（必须可回归）
- `mvn -q -pl :spring-core-beans test` 回归通过

### Requirement: spring-core-beans docs Round 2（全量逐章差异化继续深化）

- 本轮方案包（全量逐章深化）：`helloagents/history/2026-02/202602011541_beans_docs_deepen_round2_allchapters/`
- 状态：已完成实现与回归测试并归档（2026-02-01）
- 入口/工具页追加强化包：`helloagents/history/2026-02/202602011503_beans_docs_deepen_round2/`（已归档 2026-02-01）
**Module:** spring-core-beans
在不改变目录结构与章节编号的前提下，对 `spring-core-modules/spring-core-beans/docs/**` 做第二轮全量继续深化：强化“工具页互链 + 逐章可验证入口 + 最短下一跳”，并通过断链/引用自检与模块测试回归守住质量门禁。

#### Scenario: 工具页中枢化（知识地图 ↔ 断点地图 ↔ 排障清单）
- `appendix/92-knowledge-map.md` 表格新增“断点组（C1–C7）”链接，能从现象直达可复用断点组
- `part-00-guide/013-02-breakpoint-map.md` 补齐 `#c1..#c7` 稳定锚点，并新增“现象 → 断点组”快速入口
- `appendix/94-production-troubleshooting-checklist.md` 增补 3 类高频事故的 3–5 步最短诊断路径，并回链章节与 Lab

#### Scenario: 逐章继续加深提示块（AE-DEEPENING）
- 统一把“断点主线”表述收敛为“第一断点入口”，避免空泛的 watch list 口号；并补齐“最短下一跳”指引

#### Scenario: 质量门禁（必须可回归）
- beans docs 相对链接目标存在性检查：missing targets = 0
- beans docs 引用的测试类/文件路径存在性检查通过
- `mvn -pl spring-core-modules/spring-core-beans test` 回归通过

### Requirement: 深化 spring-core-beans 文档与 Labs（源码级）

- 本轮方案包（Part 01 IoC Container 深化）：`helloagents/history/2026-01/202601301812_spring_core_beans_part01_ioc_container_deepening_solution/`
- 状态：已完成实现与回归测试并归档（2026-01-30）
**Module:** spring-core-beans
将 `spring-core-beans` 文档从“概念解释”升级为“源码级可验证”：每个关键主题都能通过可运行的测试实验复现，并在文档中给出断点入口与观察点。

#### Scenario: 能复述容器启动主线（refresh 时间线）
- 给出 `refresh()` 的关键阶段与“你应该在哪一段看见什么”的映射
- 提供最小 Lab，使用户能在本地打断点观察 BFPP/BPP/单例实例化发生的顺序

#### Scenario: 能从注入报错反推候选选择过程
- 文档给出“异常文本 → 依赖解析主线 → 收敛点”的可复述路径（`NoSuchBeanDefinition` / `NoUniqueBeanDefinition` / `UnsatisfiedDependency`）
- 能明确：候选收集（`findAutowireCandidates`）与候选收敛（`determineAutowireCandidate`）的边界
- 对应可复现闭环入口：
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/testsupport/DependencyDescriptorDumperLabTest.java`

#### Scenario: 能区分 Field vs MethodParameter 注入点元数据，并用断点解释候选收敛差异
- 文档明确候选收集与缩小过程（@Qualifier/@Primary/by-name fallback（依赖名匹配 beanName）/@Priority/名称匹配/集合注入排序）
- 提供 Lab 覆盖：多实现歧义、@Primary、@Qualifier、by-name fallback、泛型收敛、集合注入排序、以及 `ObjectProvider#getIfUnique()` 的可选/多候选语义

#### Scenario: 能把 Environment/PropertySource 放回容器主线解释（含覆盖优先级与时机）
- 能解释 PropertySources 的优先级与“占位符解析”如何接入 BeanFactory 的值解析链路
- 能解释：refresh 前/后修改 Environment 对 Bean 的影响边界（不会 retroactive 影响已创建 bean）
- 对应可复现闭环入口：
  - `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/38-environment-and-propertysource.md`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansEnvironmentPropertySourceLabTest.java`

#### Scenario: 能把 BeanFactory API 当作“最小容器”理解（并解释与 ApplicationContext 的边界）
- 能解释：plain BeanFactory 不会自动启用注解注入/生命周期（需要显式安装 BPP），以及 BPP 安装顺序/时机的影响
- 能给出最小可运行路径：`DefaultListableBeanFactory` + 手动注册 annotation processors + `addBeanPostProcessor` 的可断言对照
- 对应可复现闭环入口：
  - `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanFactoryApiLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanFactoryVsApplicationContextLabTest.java`

#### Scenario: 能解释 scoped proxy 的双 Bean 名语义（beanName vs scopedTarget.*）与 ScopedProxyMode 的取舍
- 能解释：scoped proxy 会产生“双定义”（`<beanName>` 代理 + `scopedTarget.<beanName>` 目标），以及它为何是“用一致性换可用性”
- 能解释：`ScopedProxyMode.INTERFACES` vs `TARGET_CLASS` 的取舍（类型可注入性 / 代理形态 / 调试成本）
- 对应可复现闭环入口：
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md`
  - `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`

#### Scenario: 能讲清循环依赖“能救/不能救”的边界（含代理介入）
- 文档解释三层缓存与 early reference 的真实语义
- 提供 Lab 覆盖：构造器循环失败、setter 循环可能成功、以及 `allowRawInjectionDespiteWrapping` 对 early==final 一致性的影响
- 对应可复现闭环入口：
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`
  - `spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansCircularDependencyBoundaryLabTest.java`

#### Scenario: 能把 Bean 三层模型映射到关键类与扩展点
- 文档明确：BeanDefinition/实例/生命周期 三层与关键参与者的关系
- 术语上避免口号化抽象标签，统一用可验证的运行机制表述（抓手/结论/入口理解）
- 提供 Lab 使用户能在断点里看到这些对象在何时出现与被修改
- 对应可复现闭环入口：
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanCreationTraceLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/testsupport/BeanDefinitionOriginDumperLabTest.java`

#### Scenario: 能把 AOP/事务等“代理能力”放回容器时间线解释（BPP 视角）
- 能解释 AutoProxyCreator 作为典型 BPP 如何在 pre/early/after-init 介入，导致最终暴露对象可能是 proxy
- 能分清“BPP 包裹顺序（容器阶段）”与“advisor/interceptor 顺序（调用阶段）”，并能给出跨模块的断点闭环路径

#### Scenario: 能把 post-processor 的“顺序与时机”讲成源码算法（Ordering + programmatic 注册）
 - 能用 `PostProcessorRegistrationDelegate` 的两段算法解释：为什么 BFPP/BDRPP 更早、为什么 BPP 注册发生在 refresh 中前段、以及顺序如何由“三段分组 + comparator”决定
 - 能解释 `BeanPostProcessorChecker` 的信号含义：哪些 bean “错过了后续 BPP”，以及如何从日志回到过早 `getBean()` 的证据链
 - 对应可复现闭环入口：
   - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md`
   - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansEarlyGetBeanMissesBppLabTest.java`

#### Scenario: 能识别基础设施 Bean（ROLE_INFRASTRUCTURE）并用于注解能力/处理器排障
- 能用 `PostProcessorRegistrationDelegate` 的两段算法解释：为什么 BFPP/BDRPP 更早、为什么 BPP 注册发生在 refresh 中前段、以及顺序如何由“三段分组 + comparator”决定
- 能解释 `addBeanPostProcessor` 的 list 语义：为什么它绕过容器排序、为什么执行顺序 = 注册顺序、以及“BPP 不会 retroactive”的时机陷阱
- 对应可复现闭环入口：
  - `spring-core-modules/spring-core-beans/docs/part-03-container-internals/14-post-processor-ordering.md`
  - `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java`

#### Scenario: 能解释 AOT/Native 约束，并把 RuntimeHints 变成可断言结论
- 能说清：AOT/Native 的关键是“构建期契约”，RuntimeHints 用于声明反射/代理/资源需求
- 能用 JVM 单测验证 hints 的存在性（不必构建 native image）
- 对应可复现闭环入口：
  - `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/40-aot-and-native-overview.md`
  - `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/41-runtimehints-basics.md`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java`

#### Scenario: 能补齐“真实世界高频但易忽略”的机制（XML/容器外对象/SpEL/自定义 Qualifier）
- 能把 XML 输入归一为 BeanDefinition（定义层分型），并给出断点入口
- 能解释容器外对象的注入/初始化/销毁三段能力与边界（AutowireCapableBeanFactory）
- 能解释 `@Value("#{...}")` 的 SpEL 链路（与 `${...}` 占位符的职责边界）
- 能用自定义 Qualifier（meta-annotation）把候选收敛规则提升为业务语义
- 对应可复现闭环入口：
  - `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/42-xml-bean-definition-reader.md`
  - `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md`
  - `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/44-spel-and-value-expression.md`
  - `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/*LabTest.java`

## Dependencies

- 无跨模块硬依赖（该模块是 Spring Core 学习底座）

## Change History

- [202602020952_spring_core_beans_docs_deepen](../../history/2026-02/202602020952_spring_core_beans_docs_deepen/) - ✅ 已执行：为 `spring-core-beans` 文档全章补齐 Spring 官方 Reference 对照链接与版本语境（Spring Framework `6.2.x` / Spring Boot `3.5.9`），用于对齐权威定义与边界；并通过模块测试回归
- [202602011343_beans_docs_deepen_all](../../history/2026-02/202602011343_beans_docs_deepen_all/) - ✅ 已执行：beans docs 全量“继续深化”：将章内 `AE-DEEPENING` 与 deepening-strategies 从维度清单降模板化为“入口用例 + 断点主线 + 自证/排错收敛点”的可执行路线，并补齐 Beans → AOP 跳转的“为什么跳/验证什么”说明
- [202602011249_beans_docs_term_refine](../../history/2026-02/202602011249_beans_docs_term_refine/) - ✅ 已执行：beans docs 术语降噪（去口号化抽象标签，统一“运行机制/结论/抓手/前置理解”），并完成链接/引用/回归自检
- [202601312157_ioc_container_docs_deepen_v2](../../history/2026-01/202601312157_ioc_container_docs_deepen_v2/) - ✅ 已执行：对 Part-01 IoC Container 文档做内容级微调（术语降噪与表述更直白），并完成链接/引用/回归自检
- [202601281326_spring_core_beans_docs_formalize](../../history/2026-01/202601281326_spring_core_beans_docs_formalize/) - ✅ 已执行：对 `spring-core-beans/docs` 与 `spring-core-beans/README.md` 全量做书面化处理（去口语化与第二人称），保持机制深度不变，提升可复述性与严谨观感
- [202601281041_spring_core_beans_deepen_per_chapter](../../history/2026-01/202601281041_spring_core_beans_deepen_per_chapter/) - ✅ 已执行：逐章深化 Part-05 与 Appendix（90/99/91–95），补齐机制讲透/方法级调用链/排障决策表/面试复述模板，形成可复现闭环
- [202601222155_solutions_all_remaining_modules](../../history/2026-01/202601222155_solutions_all_remaining_modules/) - ✅ 已执行：补齐 Solutions/Labs 文档入口，并新增并发/性能可复现实验（同一 BeanFactory 并发 getBean）+ 补齐 Exercises 对应 Solution 缺口（part04）
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601061556_spring_core_modules_teaching_rollout](../../history/2026-01/202601061556_spring_core_modules_teaching_rollout/) - ✅ 已执行：清理 docs 正文残留的 `docs/NN` 缩写引用，统一替换为“章节名 + 真实相对路径”的 Markdown 链接，并通过断链检查与教学覆盖检查
- [202601010649_spring-core-beans-deep-dive](../../history/2026-01/202601010649_spring-core-beans-deep-dive/) - ✅ 已执行：深化 DI/生命周期/PostProcessor/循环依赖/@Configuration/FactoryBean，并补齐坑点与自测题的闭环指引
- [202601010845_beans-aop-deep-dive-v2](../../history/2026-01/202601010845_beans-aop-deep-dive-v2/) - ✅ 已执行：在 BPP/代理/顺序章节补齐 AutoProxyCreator 承接，并补齐与 AOP 模块的多代理叠加闭环链接
- [202601020725_enhance_spring_core_fundamentals](../../history/2026-01/202601020725_enhance_spring_core_fundamentals/) - ✅ 已执行：把“新增面试点”嵌入正文对应小节，并补齐可断言复现入口（BeanFactory vs ApplicationContext/Aware/泛型匹配坑/CGLIB 对照）
- [202601020934_spring_core_beans_learning_route](../../history/2026-01/202601020934_spring_core_beans_learning_route/) - ✅ 已执行：补齐 README 学习路线与 Start Here（含 refresh 主线一页纸/运行态观察点），并新增注入歧义 Lab + 对应 Exercise
- [202601021002_spring_core_beans_auto_config_ordering](../../history/2026-01/202601021002_spring_core_beans_auto_config_ordering/) - ✅ 已执行：补齐 matchIfMissing（三态）与自动配置顺序依赖（after/before）Lab，并把面试点落到 docs/10 与 docs/11 的正文入口
- [202601021023_spring_core_beans_auto_config_exercises](../../history/2026-01/202601021023_spring_core_beans_auto_config_exercises/) - ✅ 已执行：深化 Boot 自动装配 Exercises（matchIfMissing 三态 / 顺序确定化 / 条件报告 helper），并在 docs/10 条件正文补齐 `@ConditionalOnBean` 顺序/时机差异小节
- [202601021041_spring_core_beans_auto_config_backoff_debug](../../history/2026-01/202601021041_spring_core_beans_auto_config_backoff_debug/) - ✅ 已执行：补齐 auto-config back-off/覆盖“为何没生效”的时机差异 Lab（early/late registrar 对照），并在 docs/10 的“覆盖”章节补齐排障闭环入口
- [202601021144_spring_core_beans_auto_config_mainline_debug](../../history/2026-01/202601021144_spring_core_beans_auto_config_mainline_debug/) - ✅ 已执行：补齐 Boot 自动装配主线（import/排序/条件可断言）与排障可观察性（BeanDefinition 来源追踪 Dumper + 覆盖/back-off 场景矩阵 Lab），并同步 docs/10 与模块 README 入口
- [202601030641_spring-core-beans-first-pass](../../history/2026-01/202601030641_spring-core-beans-first-pass/) - 🚫 已撤回：原计划新增的 First Pass 闭环文档已按反馈删除，仅保留方案包作为学习清单归档
- [202601031327_first-pass-content-merge-into-existing-docs](../../history/2026-01/202601031327_first-pass-content-merge-into-existing-docs/) - ✅ 已执行：把 First Pass 的“10 个最小实验入口”融入 docs/00 与 docs/99（不新增独立文件）
- [202601030652_spring-core-beans-source-deep-dive](../../history/2026-01/202601030652_spring-core-beans-source-deep-dive/) - ✅ 已执行：在 docs/01、02、03、05、09 补齐 Spring 源码解析（refresh 主线/注册入口/依赖解析/生命周期/循环依赖），并用仓库 src 最小片段辅助理解
- [202601030731_spring-core-beans-post-processors-bootstrap-source-deepening](../../history/2026-01/202601030731_spring-core-beans-post-processors-bootstrap-source-deepening/) - ✅ 已执行：深化 docs/06 与 docs/12 的源码解析（PostProcessorRegistrationDelegate 算法/annotation processors bootstrap），并新增 “static @Bean BFPP” 最小可运行 Lab
- [202601030752_spring-core-beans-ordering-programmatic-bpp-deepening](../../history/2026-01/202601030752_spring-core-beans-ordering-programmatic-bpp-deepening/) - ✅ 已执行：把 docs/14 与 docs/25 补成“算法级 + 可复现”版本（排序器规则/分段执行/手工 addBeanPostProcessor 的 list 语义与时机陷阱），并增强 ordering Lab 覆盖 order 数值与 @Order 反例
- [202601031508_spring-core-beans-docs-coherence](../../history/2026-01/202601031508_spring-core-beans-docs-coherence/) - ✅ 已执行：优化 docs/01-03 连贯性（本章定位/主线 vs 深挖/下一章预告），让 01→02→03 主线阅读更顺畅且不丢知识点
- [202601032012_spring-core-beans-bookify-docs](../../history/2026-01/202601032012_spring-core-beans-bookify-docs/) - ✅ 已执行：docs 书本化（目录页 + Part 结构 + 全章结构统一（A–G） + 上下章导航），并全局修复 docs 内链与模块 README 入口
- [202601032124_spring-core-beans-src-part-grouping](../../history/2026-01/202601032124_spring-core-beans-src-part-grouping/) - ✅ 已执行：src/main 与 src/test 按 docs Part 分组（分包 + testsupport），并同步修复 docs/README/知识库中的源码路径引用
- [202601041013_spring-core-beans-src-part-naming](../../history/2026-01/202601041013_spring-core-beans-src-part-naming/) - ✅ 已执行：将 src 分组目录命名语义化（partXX → partXX_<topic>），进一步对齐 docs Part 的具名章节域
- [202601051050_spring_core_beans_deepen](../../history/2026-01/202601051050_spring_core_beans_deepen/) - ✅ 已执行：补齐 docs 目录页索引与跳读地图，新增类型转换/泛型匹配章节，并新增 component-scan/profile/optional injection/type conversion Labs 形成可复现实验闭环
- [202601051252_spring_core_beans_finish_all_tasks](../../history/2026-01/202601051252_spring_core_beans_finish_all_tasks/) - ✅ 已执行：统一 docs 全章导航与复现入口块，补齐 JSR-330 注入对照 Lab，并增强 testsupport dump 工具提升可观察性
- [202601051339_spring_core_beans_edge_case_labs](../../history/2026-01/202601051339_spring_core_beans_edge_case_labs/) - ✅ 已执行：补齐编程式注册差异 / raw injection despite wrapping / prototype 销毁语义三类边界机制，并同步 docs 入口与断点锚点
- [202601051507_spring_core_beans_aot_playbook](../../history/2026-01/202601051507_spring_core_beans_aot_playbook/) - ✅ 已执行：新增 Part 05（AOT/RuntimeHints/XML/容器外对象/SpEL/自定义 Qualifier）与对应 Labs，并新增面试复述模板/生产排障清单用于体系化复盘
- [202601052057_spring_core_beans_teaching_upgrade](../../history/2026-01/202601052057_spring_core_beans_teaching_upgrade/) - ✅ 已执行：为 Exercises 补齐对应 Solution（默认参与回归）并在 docs/README 收敛“章节↔Lab↔Exercise↔Solution”对照表；补强 ImportSelector 新手闭环与 Part05（42–45）的“源码/断点建议”与观察点
- [202601052200_spring_core_beans_beans_package_full_coverage](../../history/2026-01/202601052200_spring_core_beans_beans_package_full_coverage/) - ✅ 已执行：补齐 Spring Framework `spring-beans` 包 5 组机制闭环（XML namespace 扩展 / Properties+Groovy Reader / `replaced-method` 方法注入 / 内置 FactoryBean / PropertyEditor+值解析），新增 docs 46–50 与对应 Labs（默认参与回归）
- [202601060957_spring_core_beans_environment_beanfactory_deepening](../../history/2026-01/202601060957_spring_core_beans_environment_beanfactory_deepening/) - ✅ 已执行：补齐 Spring Framework `BeanFactory API` 与 `Environment Abstraction` 深挖闭环（docs 38–39 + Labs）
- [202601061038_spring_core_beans_spring_beans_api_full_coverage](../../history/2026-01/202601061038_spring_core_beans_spring_beans_api_full_coverage/) - ✅ 已执行：新增 spring-beans Public API 索引（95/96）+ AOT/ServiceLoader* 补齐 + Explore/Debug 用例（97）
- [202601061359_spring_core_beans_beans_support_utils](../../history/2026-01/202601061359_spring_core_beans_beans_support_utils/) - ✅ 已执行：补齐 `org.springframework.beans.support` support 工具类闭环（ArgumentConvertingMethodInvoker/ResourceEditorRegistrar/PropertyComparator/PagedListHolder/SortDefinition）并新增可运行 Lab，Appendix 96 Gap 归零
- [20260106_docs-crossref-fix](../../../spring-core-modules/spring-core-beans/docs/part-00-guide/011-00-deep-dive-guide.md) - ✅ 已执行：将 `docs/01`、`docs/06/12/14/31/16/15` 这类缩写引用替换为真实章节链接，避免误解为路径
- [202601131039_teaching-experience-webmvc-beans](../../history/2026-01/202601131039_teaching-experience-webmvc-beans/) - ✅ 已执行：spring-core-beans：新增 30 分钟快启 + docs 知识点补齐（Start Here/断点观察点/自检/索引坑点）
- [202601181724_spring_core_beans_refresh_mainline_deepen](../../history/2026-01/202601181724_spring_core_beans_refresh_mainline_deepen/) - ✅ 已执行：新增 `refresh()` → `doCreateBean()` 源码主线叙事章节，并补齐 docs/README 与深挖导读入口（docs + wiki + changelog）
- [202601182033_beans_branch_decision_table_webmvc_error_async_deepen](../../history/2026-01/202601182033_beans_branch_decision_table_webmvc_error_async_deepen/) - ✅ 已执行：在主线叙事章新增“分支决策表”（现象→阶段→关键方法→必看变量→LabTest），把叙事压缩为可复用排障套路
- [202601182117_beans_bootstrap_guide_webmvc_deepen](../../history/2026-01/202601182117_beans_bootstrap_guide_webmvc_deepen/) - ✅ 已执行：继续下压主线章关键分支（preInstantiateSingletons/doGetBean：dependsOn/parent/prototype guard 等）；bootstrap 章新增处理器速查表+时机时间线；深挖指南新增症状驱动导航
- [202601271554_spring_core_beans_docs_tutorial_upgrade](../../history/2026-01/202601271554_spring_core_beans_docs_tutorial_upgrade/) - ✅ 已执行：新增团队内训讲义（60/90/120 分钟课时脚本），并收敛入口到 docs/README 与知识地图
- [202601271739_spring_core_beans_bean_registration_deepen](../../history/2026-01/202601271739_spring_core_beans_bean_registration_deepen/) - ✅ 已执行：深化 02-bean-registration（入口对照表/最短调用链/证据链/面试与内训复述模板），用于源码进阶与可教学交付
- [202601271944_spring_core_beans_bean_registration_callchain_and_playbook](../../history/2026-01/202601271944_spring_core_beans_bean_registration_callchain_and_playbook/) - ✅ 已执行：进一步深化 02-bean-registration（方法级调用链/排障决策表/面试标准答案 + 属性填充入口），把“能看懂”升级为“能跟断点/能排障/能复述”
- [202601272227_spring_core_beans_docs_tutorial_v2_all](../../history/2026-01/202601272227_spring_core_beans_docs_tutorial_v2_all/) - ✅ 已执行：docs 全量“教程化 v2”（70 篇）补齐面试标准答案/排障决策表/方法级调用链，并强化 Appendix 工具页（面试题库/生产排障清单/Debugger Pack）以支持源码进阶、面试复述与团队内训
