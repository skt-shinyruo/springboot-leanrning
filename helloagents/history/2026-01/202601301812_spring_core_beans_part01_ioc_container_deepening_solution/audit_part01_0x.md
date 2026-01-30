# 0.* 审计报告：spring-core-beans Part 01（IoC Container）

- 范围：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/*.md`（9 章）
- 目标：在不改正文的前提下，先把“错链/漏链/重复/术语冲突/证据链缺口”定位清楚，并给出后续修改的落点（对应 task.md 的 1.*）

## A. 自动化审计摘要（链接/路径/重复/超长行）

| 章节文件 | 标题数 | 重复标题 | 链接数 | broken link | 引用 Java 路径 | missing Java | 超长行(>240) |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| `014-03-dependency-injection-resolution.md` | 31 | 0 | 9 | 0 | 6 | 0 | 2 |
| `015-04-scope-and-prototype.md` | 31 | 0 | 8 | 0 | 10 | 0 | 3 |
| `016-05-lifecycle-and-callbacks.md` | 36 | 0 | 14 | 0 | 6 | 0 | 10 |
| `017-06-post-processors.md` | 29 | 0 | 6 | 0 | 8 | 0 | 5 |
| `018-07-configuration-enhancement.md` | 25 | 0 | 6 | 0 | 5 | 0 | 3 |
| `02-bean-registration.md` | 55 | 0 | 12 | 0 | 3 | 0 | 10 |
| `020-01-bean-mental-model.md` | 18 | 0 | 15 | 0 | 4 | 0 | 4 |
| `08-factorybean.md` | 27 | 0 | 8 | 0 | 8 | 0 | 5 |
| `09-circular-dependencies.md` | 27 | 0 | 12 | 0 | 6 | 0 | 0 |

> 说明：此处只做“文件存在性”校验；对 `#anchor` 的锚点是否能跳转（Markdown slug 规则）暂不自动判定，后续在 2.2/2.3 里人工补齐。

## B. 逐章审计明细（自动化结果）

### 014-03-dependency-injection-resolution.md

- 重复标题：无
- broken link：无
- 引用的 Java 路径缺失：无
- 超长行（>240 chars）：
  - 行号：29, 30

### 015-04-scope-and-prototype.md

- 重复标题：无
- broken link：无
- 引用的 Java 路径缺失：无
- 超长行（>240 chars）：
  - 行号：8, 30, 312

### 016-05-lifecycle-and-callbacks.md

- 重复标题：无
- broken link：无
- 引用的 Java 路径缺失：无
- 超长行（>240 chars）：
  - 行号：5, 8, 9, 476, 477, 478, 479, 480 ...

### 017-06-post-processors.md

- 重复标题：无
- broken link：无
- 引用的 Java 路径缺失：无
- 超长行（>240 chars）：
  - 行号：8, 29, 30, 411, 412

### 018-07-configuration-enhancement.md

- 重复标题：无
- broken link：无
- 引用的 Java 路径缺失：无
- 超长行（>240 chars）：
  - 行号：8, 259, 260

### 02-bean-registration.md

- 重复标题：无
- broken link：无
- 引用的 Java 路径缺失：无
- 超长行（>240 chars）：
  - 行号：169, 170, 173, 175, 534, 535, 536, 537 ...

### 020-01-bean-mental-model.md

- 重复标题：无
- broken link：无
- 引用的 Java 路径缺失：无
- 超长行（>240 chars）：
  - 行号：8, 13, 144, 203

### 08-factorybean.md

- 重复标题：无
- broken link：无
- 引用的 Java 路径缺失：无
- 超长行（>240 chars）：
  - 行号：32, 261, 262, 263, 274

### 09-circular-dependencies.md

- 重复标题：无
- broken link：无
- 引用的 Java 路径缺失：无
- 超长行：无

## C. 人工审计要点（待执行阶段落地）

> 这一节是“审计结论 SSOT”：按章节列出需要 **纠错 / 扩写 / 重排 / 互链 / 补证据链** 的具体点，并标注应落到 `task.md` 哪些 1.* 任务执行。
> 注意：本节不直接修改正文，只给出“落地位置与策略”，后续按 1.* 执行时逐条消化。

### C.0 跨章级问题（影响所有章节）

1) **术语一致性风险（Definition / Instance / Exposed）**
- 现状：Part 01 各章总体已遵循“三层+最终对象”口径，但仍存在局部“注册=实例化/注入”的措辞残留，容易让读者把问题定位错层。
- 影响：读者排障时会把“候选收敛失败（NoUnique）”误判成“注册没生效”，把 “getBean 拿到的是 proxy”误判成“beanClass 变了”。
- 落点：`task.md` → `1.1.7`、`1.9.4`、`2.3`（全章逐段校对 + 统一术语表）。

2) **证据链闭环不稳定（结论没有固定抓手）**
- 现状：多章已经给出断点/方法名，但 watch list 与“结论句式”并不完全统一；读者容易在调用栈里漫游、看日志猜。
- 影响：同一结论在不同章节写法不一致，会降低可迁移性（读者无法复用调试套路）。
- 落点：`task.md` → `0.12`（证据链模板统一）+ 各章新增/补强 “固定 watch list”小节（主要集中在 1.1/1.2/1.4/1.5/1.8/1.9）。

3) **行过长（可读性与站点渲染风险）**
- 现状：所有章节均存在少量超长行（主要来自导航条与 “Test file：A / B / C…” 一行串太多路径）。
- 影响：Markdown 渲染/移动端阅读体验差；也不利于 diffs 审阅与后续维护。
- 落点：`task.md` → `2.3`（统一换行风格：长路径垂直列表化；导航条断行）。

4) **章节桥接（“现象→去哪读”）需要更强的可点击导航**
- 现状：每章都有上一章/下一章，但缺少“遇到某类现象应跳哪章”的跳转入口（尤其是：注入失败、代理替换、循环依赖、FactoryBean、scoped proxy）。
- 影响：读者容易把问题卡在当前章，缺少“下一步怎么收敛”的路线。
- 落点：`task.md` → `0.4`（先在审计里给出建议链接清单）+ 各章 1.* 执行时落地（在正文插入 3–5 个桥接链接）。

### C.1 逐章审计结论（按 Part 01 九章）

#### 02-bean-registration.md

- 发现 1：**“BeanDefinition 字段→后续行为”映射仍偏概念化**
  - 影响：读者在 `registerBeanDefinition` 断点能看到字段，但不知道这些字段后续在哪被读取/导致什么行为变化。
  - 落点：`task.md` → `1.1.1.2`、`1.1.9.*`、`1.1.10.*`（增强 dumper + registration diff Lab，把字段差异固化为断言）。

- 发现 2：**命名/alias 的“入口→影响”桥接不足**
  - 影响：读者在 14 章遇到 by-name fallback / `@Resource(name)` 时，很难回溯到“别名从哪里来、是否参与匹配”。
  - 落点：`task.md` → `1.1.2.2`、`1.1.6.1`（把 `@Bean(name={...})` 的 alias 规则与排障桥接补齐）。

- 发现 3：**实例层注册（registerSingleton）的“补救策略边界”需要更明确**
  - 影响：读者容易误以为“registerSingleton 后调用 getBean 就会自动补注入/BPP”，造成错误期待。
  - 落点：`task.md` → `1.1.4.1`（补充 autowire/initialize 的补救路径 + 风险说明）。

#### 014-03-dependency-injection-resolution.md

- 发现 1：**候选收敛优先级在同一章内存在不一致表述**
  - 证据：文末“可复现闭环”写了 `Qualifier > Primary > Priority > by-name`，但本章决策树/伪代码明确 by-name 在 @Priority 之前。
  - 影响：读者复述与调试时会出现“说法与断点观察不一致”，降低可信度。
  - 落点：`task.md` → `1.2.3.1`（统一口径：Qualifier 更像过滤信号；随后 primary/name/suggestedName；再到 @Priority tie-break）。

- 发现 2：**“suggestedName”（例如 Qualifier 值）在决策树里被弱化**
  - 影响：读者会把 Qualifier 误当成“最后才看”，或误解 “Qualifier=改名”。
  - 落点：`task.md` → `1.2.2`、`1.2.3.1`（明确 resolver 介入点与 suggestedName 的含义）。

- 发现 3：**本章 Lab/Test 引用列表缺少“泛型坑位”对应 test file 路径**
  - 影响：读者知道有 Lab，但找不到入口文件；降低“可跑闭环”体验。
  - 落点：`task.md` → `2.4`（补齐 `appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java` 的 test file 引用）。

- 建议桥接链接（审计输出，执行阶段落地到正文）
  - “候选太多/歧义”：链接到 `33-autowire-candidate-selection-primary-priority-order.md`
  - “@Resource name-first”：链接到 `32-resource-injection-name-first.md`
  - “FactoryBean 影响 type matching”：链接到本 Part01 `08-factorybean.md`
  - “scoped proxy / provider”：链接到本 Part01 `015-04-scope-and-prototype.md`

#### 015-04-scope-and-prototype.md

- 发现 1：**scoped proxy 语义在 Part01 的“证据链抓手”仍可更强**
  - 现状：章节已给出 scoped proxy 作为方案，但对 `scopedTarget.*` 这类真实 BeanDefinition 双名结构缺少“可断言/可观察”的抓手。
  - 影响：读者只知道“它是代理”，但无法解释“容器里到底注册了什么/名字是什么/为什么 getBean/按类型会变”。
  - 落点：`task.md` → `1.3.3` + 去重后执行策略（见 C.2）：优先扩展既有 `SpringCoreBeansCustomScopeLabTest` 增加 `scopedTarget.*` 断言，再在文档引用。

- 发现 2：**自定义 scope 的“destroy 回调谁触发”需要明确**
  - 影响：读者会把“destroy 没触发”误判为 Spring bug，而不是 scope 实现缺失回收触发点。
  - 落点：`task.md` → `1.3.4`、`1.3.7`（补齐注册/存储/回收/回调触发的最小实现要点）。

#### 016-05-lifecycle-and-callbacks.md

- 发现 1：**raw vs exposed（proxy）虽然解释了，但缺少“最短可断言反例”入口**
  - 影响：读者只能理解概念，无法用一个小实验证明“@PostConstruct 在 raw 上发生、after-init 才可能换壳”。
  - 落点：`task.md` → `1.4.7.*`（新增最小 LabTest：记录 raw identity 与 exposed identity 并断言不同）。

- 发现 2：**prototype 销毁语义已讲清，但建议统一引用到既有 Lab**
  - 现状：本章已有手动 destroyBean 的说明；仓库里已有 `SpringCoreBeansPrototypeDestroySemanticsLabTest`（Part03）。
  - 落点：`task.md` → `1.4.9`（优先复用现有 Lab，并在正文引用）。

#### 017-06-post-processors.md

- 发现 1：**“介入点地图”目前偏概念，缺少四类 BPP 接口的明确锚点**
  - 影响：读者知道 BFPP/BPP/BDRPP，但不知道“为什么 early reference/merged definition/销毁前”也属于 BPP 体系，导致排障断点入口不稳定。
  - 落点：`task.md` → `1.5.1.1~1.5.1.5`（补齐接口→方法→典型风险→互链）。

- 发现 2：**“错过 BPP”排障闭环需要一个可断言入口**
  - 影响：读者只能背“过早 getBean 会错过”，但无法证明“错过不会 retroactive 补上”。
  - 落点：`task.md` → `1.5.7.*`（新增 EarlyGetBeanMissesBppLabTest）+ `1.5.3.1`（解释 BeanPostProcessorChecker 信号）。

#### 018-07-configuration-enhancement.md

- 发现 1：**存在明显“迁移残留式结构重复”**
  - 证据：同一章同时出现 “最小可运行实验（Lab）”→“复现入口”→“本模块的实验” 三套入口描述，信息重复但分散。
  - 影响：读者阅读路径不清晰；维护时也更容易出现结论不一致。
  - 落点：`task.md` → `0.8`（审计已定位）+ `1.6.7`（执行阶段去重合并成一条主线）。

- 发现 2：**“参数注入不依赖增强”的结论需要可断言证据**
  - 影响：读者可能把“参数注入能工作”误归因于增强，而不是依赖解析本身。
  - 落点：`task.md` → `1.6.8`（扩展既有 ContainerLabTest 新增用例）+ `1.6.10`（文档引用互链 14 章）。

#### 08-factorybean.md

- 发现 1：**本章 example 中列出 EdgeCases Lab，但 Test file 列表未包含对应源文件路径**
  - 影响：读者找不到入口文件；降低“先跑再读”体验。
  - 落点：`task.md` → `2.4`（补齐 test file 引用：`part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`）。

- 发现 2：**“错误 getObjectType 污染候选”缺少本章语境的反例**
  - 影响：读者知道 getObjectType 重要，但不知道“错在哪会导致什么注入/发现异常”。
  - 落点：`task.md` → `1.7.7.*`（在真实用例文件 Part04 中新增反例 + 文档引用）。

#### 09-circular-dependencies.md

- 发现 1：**已提到 Boot 开关，但缺少“纯 Spring 侧的显式设置入口”**
  - 证据：出现 `spring.main.allow-circular-references`，但未明确 `DefaultListableBeanFactory#setAllowCircularReferences` 的断点/配置位置。
  - 影响：读者在非 Boot 容器（`AnnotationConfigApplicationContext`）里不知道如何复现实验差异。
  - 落点：`task.md` → `1.8.1`、`1.8.11`（补齐两侧开关与调用入口，形成对照表）。

- 发现 2：**缺少 `allowRawInjectionDespiteWrapping` 风险边界说明**
  - 影响：读者会把“能启动”当作“对象形态一致”，忽略 early vs final 不一致的工程风险。
  - 落点：`task.md` → `1.8.1`、`1.8.10`（补文档 + 在 Part03 boundary Lab 增加对照用例）。

#### 020-01-bean-mental-model.md

- 发现 1：**ResolvableDependency/外部对象的边界已解释，但缺少“就近可跑入口”**
  - 现状：本章引用了相关深挖章节，但没有明确给出 `SpringCoreBeansResolvableDependencyLabTest` 的入口路径。
  - 影响：读者无法快速验证“能注入但不是 BeanDefinition”的结论。
  - 落点：`task.md` → `1.9.7`（直接引用现有 LabTest）/ `1.9.8`（可选：Part01 wrapper 入口）。

### C.2 去重/复用决策（0.11）——避免重复造轮子

> 结论：本仓库已经存在若干“跨 Part 的证据链 Lab”，应优先复用并在 Part01 文档互链；只有当“缺少最小入口/缺少关键断言点”时再新增 Part01 专属 Lab。

- Alias 相关：
  - 已有：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`
  - 已有：`.../SpringCoreBeansResourceInjectionLabTest.java`（@Resource name-first）
  - 建议：Part01 文档优先引用现有用例；若要补齐“alias 参与 by-name fallback”的边界，再新增/扩展一个最小用例（更推荐扩展 `SpringCoreBeansAutowireCandidateSelectionLabTest` 而非新建多份类似测试）。
  - 落点：需在执行阶段同步更新 `task.md` 的 1.1.11/1.2.11（改为“复用+扩展”策略）。

- by-name fallback：
  - 已有：`.../SpringCoreBeansAutowireCandidateSelectionLabTest.java`（包含 by-name fallback、Primary 与排序边界）
  - 建议：Part01 14 章以互链方式复用；若补 alias 交叉，用“在原有 Lab 增量加入 1 个用例”的方式完成。

- scoped proxy/custom scope：
  - 已有：`.../SpringCoreBeansCustomScopeLabTest.java`（thread scope + scoped proxy + ObjectProvider 对照）
  - 建议：优先扩展该 Lab 增加 `scopedTarget.*` 命名与 INTERFACES/TARGET_CLASS 类型边界断言；Part01 文档引用该 Lab。必要时提供 Part01 @Suite wrapper 入口，避免读者跨目录找。

- prototype destroy：
  - 已有：`.../part03_container_internals/SpringCoreBeansPrototypeDestroySemanticsLabTest.java`
  - 建议：Part01 16 章直接引用该 Lab；不再新增 Part01 重复实验。

- ResolvableDependency：
  - 已有：`.../part04_wiring_and_boundaries/SpringCoreBeansResolvableDependencyLabTest.java`
  - 建议：Part01 20 章直接引用；可选增加 Part01 wrapper 入口。

### C.3 Suite 策略建议（0.7）

- 现状：`SpringCoreBeansBookMatrixLabTest` 目前只收敛 4 个入口（Container / BeanFactoryVsApplicationContext / Import / ComponentScan）。
- 建议：
  1) **保持 BookMatrix 稳定且小**：仅纳入“Part01 的入口级实验”（不把矩阵膨胀成全量回归）。
  2) 对跨 Part 的证据链（FactoryBean、CircularDependency、ResolvableDependency、CustomScope 等），优先采用 **Part01 wrapper suite** 的方式提供入口，而不是把真实用例搬到 Part01。
  3) 新增 Lab 只有在“当前仓库无可复用入口”时才创建，并且默认不直接进 BookMatrix，除非它是关键总入口。

### C.4 证据链模板（0.12，建议写入各章正文的小节）

建议每章至少出现一次以下模板（格式可微调，但信息要齐）：

- 现象（1 句）：你看到了什么？
- 第一性分层：这是 Definition / Instance / Exposed 的哪一层问题？
- 断点入口（方法级）：命中哪个方法才能“看见变化”？
- watch list（固定 5 项以内）：用哪几个变量解释“为什么”？
- 结论句式（可复述）：结论 + 证据链 + 反例/边界
- 下一步跳转（3–5 个链接）：遇到 X 去 Y

## D. 现有 Lab/Test ↔ Part 01 九章映射（0.5 的审计输出）

> 目的：保证每章至少 1 个“可跑入口”，并且文档引用时能给出“具体文件路径 + 推荐测试方法名/观察点”。

### D.1 入口级映射表（建议作为文档引用的优先入口）

| 文档章节 | 推荐入口（类名） | 实际文件路径（SSOT） | 主要用于证明 |
| --- | --- | --- | --- |
| `02-bean-registration.md` | `SpringCoreBeansComponentScanLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansComponentScanLabTest.java` | 扫描入口→注册落点 |
| `02-bean-registration.md` | `SpringCoreBeansImportLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportLabTest.java` | @Import/selector/registrar |
| `02-bean-registration.md` | `SpringCoreBeansProgrammaticRegistrationLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticRegistrationLabTest.java` | 定义层 vs 实例层注册对照 |
| `014-03-dependency-injection-resolution.md` | `SpringCoreBeansAutowireCandidateSelectionLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java` | 候选收集/收敛（Primary/Qualifier/by-name/@Order/@Priority） |
| `014-03-dependency-injection-resolution.md` | `SpringCoreBeansBeanGraphDebugLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java` | 候选集合与依赖边可观测性 |
| `015-04-scope-and-prototype.md` | `SpringCoreBeansContainerLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java` | `@Lookup` 获取 prototype（最小入口） |
| `015-04-scope-and-prototype.md` | `SpringCoreBeansCustomScopeLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java` | custom scope + scoped proxy + provider 对照 |
| `016-05-lifecycle-and-callbacks.md` | `SpringCoreBeansLifecycleCallbackOrderLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java` | 初始化/销毁回调顺序（方法级） |
| `016-05-lifecycle-and-callbacks.md` | `SpringCoreBeansPrototypeDestroySemanticsLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPrototypeDestroySemanticsLabTest.java` | prototype destroy 默认不托管 + 手动 destroyBean |
| `017-06-post-processors.md` | `SpringCoreBeansPostProcessorOrderingLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java` | processor 顺序与分组 |
| `017-06-post-processors.md` | `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansStaticBeanFactoryPostProcessorLabTest.java` | non-static BFPP 时机陷阱 |
| `018-07-configuration-enhancement.md` | `SpringCoreBeansContainerLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java` | proxyBeanMethods=true/false + lite/full 对照 |
| `08-factorybean.md` | `SpringCoreBeansFactoryBeanEdgeCasesLabTest`（Part01 wrapper） | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java` | 统一入口（实际用例在 Part04） |
| `08-factorybean.md` | `SpringCoreBeansFactoryBeanDeepDiveLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java` | product/type matching/缓存语义 |
| `09-circular-dependencies.md` | `SpringCoreBeansCircularDependencyBoundaryLabTest`（Part01 wrapper） | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansCircularDependencyBoundaryLabTest.java` | 统一入口（实际用例在 Part03） |
| `020-01-bean-mental-model.md` | `SpringCoreBeansBeanCreationTraceLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java` | doCreateBean 主线可观测性 |
| `020-01-bean-mental-model.md` | `SpringCoreBeansProxyingPhaseLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java` | after-init 换壳（proxy）与 self-invocation |
| `020-01-bean-mental-model.md` | `SpringCoreBeansResolvableDependencyLabTest` | `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResolvableDependencyLabTest.java` | 能注入但不是 Bean（无 BeanDefinition） |

### D.2 审计备注（入口覆盖缺口）

- Part01 “alias/by-name/@Resource” 的关键入口已存在于 Part04（alias/@Resource/by-name fallback），因此本方案已将相关任务调整为“复用 + 增量扩展”的策略（见 `task.md` 的 1.1.11/1.2.11）。
- Part01 “scoped proxy 语义”关键入口已存在于 Part04（`SpringCoreBeansCustomScopeLabTest`），因此本方案已将 scoped proxy 任务调整为“扩展既有 Lab 增加断言”（见 `task.md` 的 1.3.8）。
