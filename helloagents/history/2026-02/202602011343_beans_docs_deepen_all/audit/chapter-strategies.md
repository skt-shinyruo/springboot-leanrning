# spring-core-beans docs：逐章“继续深化”策略清单（按现有内容差异化）

说明：本文件是对 `spring-core-modules/spring-core-beans/docs/**` 的逐章阅读产物。
- 不引入统一小标题/固定模板；每章按主题与现有内容决定补强点。
- 每章策略尽量引用该章已出现的入口方法与测试用例名，便于后续执行时直接落地。


## spring-core-modules/spring-core-beans/docs

### spring-core-modules/spring-core-beans/docs/README.md

- 主题：spring-core-beans 文档导航（Docs TOC）
- 文内结构线索：导读 / 四条阅读路线（按读者分层：源码进阶 + 面试） / 章节契约（教程化验收口径：10/30/3） / 如何运行（最小闭环） / 可从此处开始 / 症状驱动导航（快速定位） / 目录 / 自检要点
- 文内已出现的入口用例：未显式标注（后续建议补最小可复现入口或回链到相关用例）
- 文内已出现的源码入口/锚点：未显式标注（后续建议补关键入口方法，便于断点验证）
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理目录页中的占位/待补痕迹：用“可验证入口（章节/用例/断点）”替换。
  - 将目录页的价值从“列链接”提升为“给路径”：为关键节点补一句“为什么现在读它”，并在 proxy/事务/自调用等处给出 Beans→AOP 的最短跳转与目的说明。


## spring-core-modules/spring-core-beans/docs/appendix

### spring-core-modules/spring-core-beans/docs/appendix/01-common-pitfalls.md

- 主题：第 25 章：90. 常见误区清单（建议反复对照）
- 文内结构线索：导读 / 机制主线 / 源码与断点 / 最小可运行实验（Lab） / 常见误区与边界 / 面试常问（把“误区”说成标准答案） / 自检要点 / 小结与下一章
- 文内已出现的入口用例：SpringCoreBeansAutowireCandidateSelectionLabTest, SpringCoreBeansContainerLabTest, SpringCoreBeansEarlyReferenceLabTest, SpringCoreBeansLabTest, SpringCoreBeansLifecycleCallbackOrderLabTest, SpringCoreBeansProxyingPhaseLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, AbstractBeanFactory#doGetBean, AbstractAutowireCapableBeanFactory#populateBean, DefaultListableBeanFactory#doResolveDependency ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/待补痕迹：用可验证的说明替代（指向具体章节/用例/断点入口）。
  - 将坑点从“现象清单”收敛为“最短诊断路线”：每类现象给出第一入口断点与第一条排除项，并回链到对应章节/用例。

### spring-core-modules/spring-core-beans/docs/appendix/11-self-check.md

- 主题：第 26 章：99. 自测题：是否能够真的理解了？
- 文内结构线索：小结与下一章 / 导读 / 从 Book Matrix 进入（主线最小集合） / 从 Branch Matrix 进入（关键分支最小集合） / 0. 复现入口（可运行） / 基础抓手（对应 01/06） / 依赖注入（对应 03） / Scope 与生命周期（对应 04/05） ...
- 文内已出现的入口用例：SpringCoreBeansLabTest, SpringCoreBeansContainerLabTest, SpringCoreBeansBootstrapInternalsLabTest, SpringCoreBeansInjectionAmbiguityLabTest, SpringCoreBeansAutowireCandidateSelectionLabTest, SpringCoreBeansLifecycleCallbackOrderLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, ApplicationContext#refresh(), DefaultListableBeanFactory#doResolveDependency, AbstractAutowireCapableBeanFactory#doCreateBean ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将自检题的“答案”改为“验证路线”：每题后给出最短回链（去哪个章节/跑哪个用例/在哪个入口断点验证）。

### spring-core-modules/spring-core-beans/docs/appendix/02-glossary.md

- 主题：91. 术语表（Glossary）
- 文内结构线索：导读 / 机制主线 / 容器与定义 / 注入与候选 / 生命周期与扩展点 / 代理、FactoryBean、循环依赖 / 值解析与类型转换 / AOT 与真实世界补齐 ...
- 文内已出现的入口用例：SpringCoreBeansContainerLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, DefaultListableBeanFactory#registerBeanDefinition, DefaultSingletonBeanRegistry#getSingleton
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 术语表减少抽象解释，补“落到代码里是什么”：每个术语给出关键类/方法/数据结构，并回链到首次出现的章节。

### spring-core-modules/spring-core-beans/docs/appendix/03-knowledge-map.md

- 主题：92. 知识地图（Knowledge Map）：从现象直达章节/断点/Lab
- 文内结构线索：导读 / 面试复盘入口（把“地图”变成“可复述答案”） / 机制主线：用“症状驱动”组织知识点 / 0. 核心七件套（概念 → 章节 → Lab） / 1. 现象 → 章节 → 断点入口（建议收藏） / 1.1 误归因对照（避免把问题看错层） / 2. 推荐顺读路线（从“可运行”到“能解释”） / 自检要点
- 文内已出现的入口用例：SpringCoreBeansBreakpointPackLabTest, SpringCoreBeansIocBranchMatrixLabTest, SpringCoreBeansInternalsBranchMatrixLabTest, SpringCoreBeansContainerLabTest, SpringCoreBeansBeanNameAliasLabTest, SpringCoreBeansBeanDefinitionOverridingLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, DefaultSingletonBeanRegistry#getSingleton, AbstractBeanFactory#resolveEmbeddedValue, AbstractApplicationContext#refresh, CommonAnnotationBeanPostProcessor#postProcessProperties, CachedIntrospectionResults#forClass
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/待补痕迹：用可验证的说明替代（指向具体章节/用例/断点入口）。
  - 知识地图优先服务“快速定位”：把每个节点压缩为“常见现象 → 对应章节 → 最小可跑入口（测试方法名）”，避免过多枚举。

### spring-core-modules/spring-core-beans/docs/appendix/04-interview-playbook.md

- 主题：93. 面试复述模板（Interview Playbook）：用“证据链”回答 Spring IoC
- 文内结构线索：导读 / 机制主线：面试答题的“标准结构” / 0. 面试常见误归因对照（先纠错再答题） / 1. 容器主线：refresh 到底干了什么？ / 2. 注入解析：为什么会 NoSuch / NoUnique？ / 3. 生命周期：初始化回调顺序应能够讲到证据吗？ / 4. Post-Processor：BFPP vs BPP 到底差在哪？ / 5. 循环依赖：三级缓存到底解决了什么？ ...
- 文内已出现的入口用例：SpringCoreBeansIocBranchMatrixLabTest, SpringCoreBeansInternalsBranchMatrixLabTest, SpringCoreBeansBreakpointPackLabTest, SpringCoreBeansMainlineCallChainLabTest, SpringCoreBeansAutowireCandidateSelectionLabTest, SpringCoreBeansResourceInjectionLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractApplicationContext#refresh, PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors, PostProcessorRegistrationDelegate#registerBeanPostProcessors, AbstractAutowireCapableBeanFactory#initializeBean, AbstractAutowireCapableBeanFactory#getEarlyBeanReference ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/待补痕迹：用可验证的说明替代（指向具体章节/用例/断点入口）。
  - 面试复述页为每个高频题补“可验证证据”：明确可以用哪个测试 + 哪个断点证明，而不是只给口头答案。

### spring-core-modules/spring-core-beans/docs/appendix/05-production-troubleshooting-checklist.md

- 主题：94. 生产排障清单（Troubleshooting Checklist）：从症状到证据链
- 文内结构线索：导读 / 机制主线：先把问题放回 refresh 的哪一段 / 0. 总分流表（先选最短入口） / 1. 排障 SOP（建议固定为团队模板） / 1.7 误归因对照（生产最常见三错） / 2. 常见事故分类（现象 → 证据链入口） / 3. Debugger Pack：排障时的“第一入口” / 最短调用链（方法级）：把“Evidence”写成可执行路线 ...
- 文内已出现的入口用例：SpringCoreBeansBreakpointPackLabTest, SpringCoreBeansIocBranchMatrixLabTest, SpringCoreBeansInternalsBranchMatrixLabTest, SpringCoreBeansComponentScanLabTest, SpringCoreBeansImportLabTest, SpringCoreBeansAutowireCandidateSelectionLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, DefaultListableBeanFactory#registerBeanDefinition, DefaultListableBeanFactory#doResolveDependency, DefaultSingletonBeanRegistry#getSingleton, AbstractBeanFactory#resolveEmbeddedValue, AbstractBeanFactory#doGetBean ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/待补痕迹：用可验证的说明替代（指向具体章节/用例/断点入口）。
  - 生产排障清单按症状给分流：注入失败/代理不生效/循环依赖/配置不生效等，每类给出第一入口断点与对应章节/用例。

### spring-core-modules/spring-core-beans/docs/appendix/06-spring-beans-public-api-index.md

- 主题：95. spring-beans Public API 索引（Spring Framework 6.2.15）
- 文内结构线索：导读 / 机制主线：索引如何服务学习与排障 / 包索引（按 package 分组） / org.springframework.beans / org.springframework.beans.factory / org.springframework.beans.factory.annotation / org.springframework.beans.factory.aot / org.springframework.beans.factory.config ...
- 文内已出现的入口用例：SpringCoreBeansBreakpointPackLabTest, SpringCoreBeansIocBranchMatrixLabTest, SpringCoreBeansInternalsBranchMatrixLabTest, SpringCoreBeansTypeConversionLabTest.java, SpringCoreBeansBeanFactoryApiLabTest.java, SpringCoreBeansAutowireCandidateSelectionLabTest.java ...
- 文内已出现的源码入口/锚点：org.springframework.beans.factory.support.DefaultListableBeanFactory#getBean
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/待补痕迹：用可验证的说明替代（指向具体章节/用例/断点入口）。
  - 把该页从“信息堆”变成“可用入口”：每个条目尽量落到“去哪里验证/怎么验证”，避免只列名词。

### spring-core-modules/spring-core-beans/docs/appendix/07-spring-beans-public-api-gap.md

- 主题：96. spring-beans Public API Gap 清单（按包/机制域分批深化）
- 文内结构线索：导读 / 机制主线：为什么要维护 Gap？ / 1. 覆盖标准（本仓库的“教程级”验收口径） / 2. 当前清单（建议按需扩展） / 源码调用链（方法级）定位模板（Gap 场景） / 排障分流（Gap 视角：读者到底缺的是哪一段） / 自检要点
- 文内已出现的入口用例：SpringCoreBeansBreakpointPackLabTest, SpringCoreBeansIocBranchMatrixLabTest, SpringCoreBeansInternalsBranchMatrixLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/待补痕迹：用可验证的说明替代（指向具体章节/用例/断点入口）。
  - 把该页从“信息堆”变成“可用入口”：每个条目尽量落到“去哪里验证/怎么验证”，避免只列名词。

### spring-core-modules/spring-core-beans/docs/appendix/08-explore-debug-tests.md

- 主题：97. Explore/Debug 用例（可选启用，不影响默认回归）
- 文内结构线索：导读 / 0. 为什么需要 Explore 用例？ / 1. 如何开启 Explore 用例？ / 2. Explore 用例清单：入口 & 观察点 / 3. 如何把 Explore 结果“用回主线”（让它真的变成教程） / 面试使用方式（将“观察结果”组织为可复述答案） / 常见误区 / 自检要点 ...
- 文内已出现的入口用例：SpringCoreBeansSingletonCacheExploreTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, DefaultSingletonBeanRegistry#getSingleton, DefaultSingletonBeanRegistry#addSingleton, DefaultSingletonBeanRegistry#addSingletonFactory
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 把该页从“信息堆”变成“可用入口”：每个条目尽量落到“去哪里验证/怎么验证”，避免只列名词。

### spring-core-modules/spring-core-beans/docs/appendix/09-debugger-pack.md

- 主题：98. Debugger Pack（断点包总入口）
- 文内结构线索：导读 / 机制主线：将“主观判断”转化为“可观察事实” / 1. 使用方式（3 步闭环） / 1.2 教程化验收（10/30/3）：把 Debugger Pack 当“能力训练器” / 1.1 团队内训如何用（可选） / 1.3 面试使用方式（建议读者形成固定话术） / 2. 最常用断点入口（按“主线 → 分支 → 现象”组织） / 3. Watch List（最小够用版） ...
- 文内已出现的入口用例：SpringCoreBeansBreakpointPackLabTest, SpringCoreBeansMainlineCallChainLabTest, SpringCoreBeansIocBranchMatrixLabTest, SpringCoreBeansInternalsBranchMatrixLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractApplicationContext#refresh, PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors, PostProcessorRegistrationDelegate#registerBeanPostProcessors
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/待补痕迹：用可验证的说明替代（指向具体章节/用例/断点入口）。
  - 将断点包写成“路线”：每条路线明确起点（测试方法）→ 关键断点 → 需要确认的变量/状态，读者可以按路线复刻结论。

### spring-core-modules/spring-core-beans/docs/appendix/10-team-training-kit.md

- 主题：99. 团队内训讲义（Training Kit）：可直接用于授课的课时脚本
- 文内结构线索：导读 / 0. 讲师准备清单（上课前 10 分钟） / 1. 60 分钟速成（适合团队统一“排障语言”） / 2. 90 分钟进阶（适合“源码进阶 + 排障方法”） / 3. 120 分钟深入分析（适合“面试 + 源码 + 真实排障闭环”） / 4. 常见课堂易错点（讲师备忘） / 5. 课程扩展（把内训变成长期资产） / 6. 课后验收（把“听懂了”变成“能复述/能排障”） ...
- 文内已出现的入口用例：SpringCoreBeansBreakpointPackLabTest, SpringCoreBeansMainlineCallChainLabTest, SpringCoreBeansCircularDependencyBoundaryLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, DefaultListableBeanFactory#doResolveDependency, DefaultListableBeanFactory#determineAutowireCandidate, AbstractAutowireCapableBeanFactory#populateBean, AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/待补痕迹：用可验证的说明替代（指向具体章节/用例/断点入口）。
  - 把该页从“信息堆”变成“可用入口”：每个条目尽量落到“去哪里验证/怎么验证”，避免只列名词。


## spring-core-modules/spring-core-beans/docs/deepening-strategies

### spring-core-modules/spring-core-beans/docs/deepening-strategies/README.md

- 主题：spring-core-beans：内容级再加深策略（按章节）
- 文内结构线索：使用方式（推荐） / 落地示例（把“策略”变成“正文内容”） / 验证方式（避免“写了很多但不可用”） / 策略文件索引
- 文内已出现的入口用例：未显式标注（后续建议补最小可复现入口或回链到相关用例）
- 文内已出现的源码入口/锚点：未显式标注（后续建议补关键入口方法，便于断点验证）
- 继续深化策略：
  - 将“按维度分条”的表达改为“按读者最常卡住的问题组织”：每章只留下最关键的 1–2 个误判点，并明确对应的最短验证入口（测试/断点/关键变量）。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/appendix.md

- 主题：逐章内容级再加深建议（appendix 工具章节）
- 文内结构线索：执行化提示（工具页的“复用价值”）
- 文内已出现的入口用例：未显式标注（后续建议补最小可复现入口或回链到相关用例）
- 文内已出现的源码入口/锚点：未显式标注（后续建议补关键入口方法，便于断点验证）
- 继续深化策略：
  - 优先在“执行化提示（工具页的“复用价值”）”这一段把策略写成可执行路线：先跑什么、再看什么、最后如何验证结论；避免罗列概念或维度名。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/docs-root.md

- 主题：逐章内容级再加深建议（Docs TOC / 目录页）
- 文内结构线索：执行化提示（把目录页与章节闭环对齐）
- 文内已出现的入口用例：未显式标注（后续建议补最小可复现入口或回链到相关用例）
- 文内已出现的源码入口/锚点：未显式标注（后续建议补关键入口方法，便于断点验证）
- 继续深化策略：
  - 优先在“执行化提示（把目录页与章节闭环对齐）”这一段把策略写成可执行路线：先跑什么、再看什么、最后如何验证结论；避免罗列概念或维度名。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/module-readme.md

- 主题：逐章内容级再加深建议（模块 README）
- 文内结构线索：执行化提示（本轮落地的默认结构）
- 文内已出现的入口用例：未显式标注（后续建议补最小可复现入口或回链到相关用例）
- 文内已出现的源码入口/锚点：未显式标注（后续建议补关键入口方法，便于断点验证）
- 继续深化策略：
  - 优先在“执行化提示（本轮落地的默认结构）”这一段把策略写成可执行路线：先跑什么、再看什么、最后如何验证结论；避免罗列概念或维度名。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/part-00-guide.md

- 主题：逐章内容级再加深建议（part-00-guide 指南）
- 文内结构线索：执行化提示（Guide 的落地位置）
- 文内已出现的入口用例：未显式标注（后续建议补最小可复现入口或回链到相关用例）
- 文内已出现的源码入口/锚点：未显式标注（后续建议补关键入口方法，便于断点验证）
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 继续深化策略：
  - 优先在“执行化提示（Guide 的落地位置）”这一段把策略写成可执行路线：先跑什么、再看什么、最后如何验证结论；避免罗列概念或维度名。
  - 清理占位/待补提示：用可验证的说明替代（例如：推荐入口测试、断点入口、关键变量）。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/part-01-ioc-container.md

- 主题：逐章内容级再加深建议（part-01-ioc-container）
- 文内结构线索：执行化提示（IoC 核心章的“深度落点”）
- 文内已出现的入口用例：未显式标注（后续建议补最小可复现入口或回链到相关用例）
- 文内已出现的源码入口/锚点：未显式标注（后续建议补关键入口方法，便于断点验证）
- 继续深化策略：
  - 优先在“执行化提示（IoC 核心章的“深度落点”）”这一段把策略写成可执行路线：先跑什么、再看什么、最后如何验证结论；避免罗列概念或维度名。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/part-02-boot-autoconfig.md

- 主题：逐章内容级再加深建议（part-02-boot-autoconfig）
- 文内结构线索：执行化提示（把“看不见的条件”变成“可证明事实”）
- 文内已出现的入口用例：未显式标注（后续建议补最小可复现入口或回链到相关用例）
- 文内已出现的源码入口/锚点：未显式标注（后续建议补关键入口方法，便于断点验证）
- 继续深化策略：
  - 优先在“执行化提示（把“看不见的条件”变成“可证明事实”）”这一段把策略写成可执行路线：先跑什么、再看什么、最后如何验证结论；避免罗列概念或维度名。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/part-03-container-internals.md

- 主题：逐章内容级再加深建议（part-03-container-internals）
- 文内结构线索：执行化提示（Internals 章的最低交付）
- 文内已出现的入口用例：未显式标注（后续建议补最小可复现入口或回链到相关用例）
- 文内已出现的源码入口/锚点：未显式标注（后续建议补关键入口方法，便于断点验证）
- 继续深化策略：
  - 优先在“执行化提示（Internals 章的最低交付）”这一段把策略写成可执行路线：先跑什么、再看什么、最后如何验证结论；避免罗列概念或维度名。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/part-04-wiring-and-boundaries.md

- 主题：逐章内容级再加深建议（part-04-wiring-and-boundaries）
- 文内结构线索：执行化提示（边界章的“可复现反例”优先）
- 文内已出现的入口用例：未显式标注（后续建议补最小可复现入口或回链到相关用例）
- 文内已出现的源码入口/锚点：未显式标注（后续建议补关键入口方法，便于断点验证）
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 继续深化策略：
  - 优先在“执行化提示（边界章的“可复现反例”优先）”这一段把策略写成可执行路线：先跑什么、再看什么、最后如何验证结论；避免罗列概念或维度名。
  - 清理占位/待补提示：用可验证的说明替代（例如：推荐入口测试、断点入口、关键变量）。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/part-05-aot-and-real-world.md

- 主题：逐章内容级再加深建议（part-05-aot-and-real-world）
- 文内结构线索：执行化提示（Real World 的“可运行契约”）
- 文内已出现的入口用例：未显式标注（后续建议补最小可复现入口或回链到相关用例）
- 文内已出现的源码入口/锚点：未显式标注（后续建议补关键入口方法，便于断点验证）
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 继续深化策略：
  - 优先在“执行化提示（Real World 的“可运行契约”）”这一段把策略写成可执行路线：先跑什么、再看什么、最后如何验证结论；避免罗列概念或维度名。
  - 清理占位/待补提示：用可验证的说明替代（例如：推荐入口测试、断点入口、关键变量）。


## spring-core-modules/spring-core-beans/docs/part-00-guide

### spring-core-modules/spring-core-beans/docs/part-00-guide/01-why-index.md

- 主题：第 09 章：00. 基础问题索引（Why Index）：把高频“为什么”做成可验证闭环
- 文内结构线索：这页解决什么问题 / 使用方式（30 秒定位） / Why-01：为什么 Spring 使用三级缓存（three level cache）？ / Why-02：为什么不是“二级缓存”就够？（2-level vs 3-level） / Why-03：为什么需要 `getEarlyBeanReference`？（early reference 的形态：raw vs proxy） / Why-04：为什么最终暴露对象（exposed object）可能变成 proxy/wrapper？ / Why-05：为什么 self-invocation 会让 AOP/事务“不生效”？（call path） / 面试常问（Why Index） ...
- 文内已出现的入口用例：SpringCoreBeansCircularDependencyBoundaryLabTest, SpringCoreBeansEarlyReferenceLabTest, SpringCoreBeansRawInjectionDespiteWrappingLabTest, SpringCoreBeansProxyingPhaseLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton, org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#addSingletonFactory, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#getEarlyBeanReference, AbstractAutowireCapableBeanFactory#getEarlyBeanReference, AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization ...
- 跨模块互链：../../../spring-core-aop/docs/part-01-proxy-fundamentals/01-aop-proxy-mental-model.md, ../../../spring-core-aop/docs/part-02-autoproxy-and-pointcuts/01-autoproxy-creator-mainline.md, ../../../spring-core-aop/docs/part-01-proxy-fundamentals/03-self-invocation.md
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 对跨模块链接补“跳转目的”：在链接附近用 1–2 句说明为什么此处需要 AOP/TX 视角，以及跳过去应验证的关键点（例如代理创建点/自调用行为/拦截器链顺序）。
  - 将文内提到的关键入口方法串成可读主线（例如：ApplicationContext#refresh → org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton），让读者知道从哪进、在哪些分支停。

### spring-core-modules/spring-core-beans/docs/part-00-guide/02-mainline-timeline.md

- 主题：第 10 章：主线时间线：IoC 容器从 refresh 到创建 Bean
- 文内结构线索：导读 / 机制主线：把所有章节放回同一条时间线 / 1. refresh 主线时间线（粗粒度分段） / 2. 这条时间线使用方式来排障（3 个经典分流） / 面试常问（refresh 时间线） / 自检要点
- 文内已出现的入口用例：SpringCoreBeansMainlineCallChainLabTest, SpringCoreBeansBreakpointPackLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractApplicationContext#refresh, AbstractApplicationContext#prepareBeanFactory, PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors, PostProcessorRegistrationDelegate#registerBeanPostProcessors, DefaultListableBeanFactory#addBeanPostProcessor ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线：把所有章节放回同一条时间线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“2. 这条时间线使用方式来排障（3 个经典分流）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-00-guide/03-deep-dive-guide.md

- 主题：第 11 章：00. 深入分析指南：将“Bean 三层模型”落实到源码与断点
- 文内结构线索：导读 / 学习目标与自检标准（10/30/3） / 0. 机制主线：由概念到可验证结论 / 1. 深入分析的入口选择与主线把握 / 2. 最小源码导航图（定义层 / 实例层 / 缓存层） / 3. 源码入口与观察点（从主线分支切入） / 4. 实践路线：将分析过程落实为可复现实验 / 5. 本章学习收获 ...
- 文内已出现的入口用例：SpringCoreBeansAutowireCandidateSelectionLabTest, SpringCoreBeansContainerLabTest, SpringCoreBeansBeanCreationTraceLabTest, SpringCoreBeansBeanGraphDebugLabTest, SpringCoreBeansMergedBeanDefinitionLabTest, SpringCoreBeansResolvableDependencyLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, AbstractBeanFactory#getMergedBeanDefinition, DefaultListableBeanFactory#doResolveDependency, AbstractAutowireCapableBeanFactory#populateBean ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“0. 机制主线：由概念到可验证结论”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“1. 深入分析的入口选择与主线把握”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“3. 源码入口与观察点（从主线分支切入）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-00-guide/04-branch-decision-matrix.md

- 主题：第 11 章：关键分支矩阵（Branch Decision Matrix）
- 文内结构线索：导读 / 机制主线：把“排障经验”压缩成决策表 / 0. 先学会读异常 cause chain（别被外层异常骗了） / 1. 分支矩阵（现象 → 阶段 → 方法 → 观察点 → Lab） / 2. 如何使用这张表（推荐流程） / 面试使用方式这张表（将排障流程复用为答题流程） / 自检要点
- 文内已出现的入口用例：SpringCoreBeansIocBranchMatrixLabTest, SpringCoreBeansInternalsBranchMatrixLabTest, SpringCoreBeansContainerLabTest, SpringCoreBeansAutowireCandidateSelectionLabTest, SpringCoreBeansResourceInjectionLabTest, SpringCoreBeansValuePlaceholderResolutionLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, DefaultListableBeanFactory#doResolveDependency, AbstractBeanFactory#resolveEmbeddedValue, DefaultSingletonBeanRegistry#getSingleton, AbstractAutowireCapableBeanFactory#populateBean, AbstractAutowireCapableBeanFactory#doCreateBean ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线：把“排障经验”压缩成决策表”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → DefaultListableBeanFactory#doResolveDependency），并在关键分支处点明触发条件与结果形态。
  - 在“1. 分支矩阵（现象 → 阶段 → 方法 → 观察点 → Lab）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“机制主线：把“排障经验”压缩成决策表”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-00-guide/05-quickstart-30min.md

- 主题：第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）
- 文内结构线索：导读 / 章节验收口径（10/30/3：快启闭环） / 30 分钟内要抓住的最小抓手（5 个对象 + 4 条入口） / 快启路线（按顺序运行） / 新手易卡点与修复路径（快启版） / 小结与下一章 / 证据链（调用链 + 断点 + 断言） / BreakpointPack 深入复盘（可选：把“快启”升级为“可排障”） ...
- 文内已出现的入口用例：SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans, SpringCoreBeansLabTest#demonstratesPrototypeScopeBehavior, SpringCoreBeansBeanCreationTraceLabTest#beanCreationTrace_recordsPhases_andExposesProxyReplacement, SpringCoreBeansLabTest, SpringCoreBeansMainlineCallChainLabTest, SpringCoreBeansBreakpointPackLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, DefaultListableBeanFactory#registerBeanDefinition, DefaultListableBeanFactory#doResolveDependency, AbstractAutowireCapableBeanFactory#populateBean ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“30 分钟内要抓住的最小抓手（5 个对象 + 4 条入口）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“证据链（调用链 + 断点 + 断言）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“BreakpointPack 深入复盘（可选：把“快启”升级为“可排障”）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-00-guide/06-applicationcontext-refresh-call-chain.md

- 主题：第 13 章：01：`refresh()` 调用链（容器从“定义”到“实例”的主线）
- 文内结构线索：导读 / 章节验收口径（10/30/3：refresh 主线） / 主线伪代码（把 refresh 当成“时间线”读） / 阶段内关键对象变化（断点可验证） / 把调用链落到“应能够设置断点的锚点” / 主线高频分支最小集（必须能一眼定位） / 排障分流（refresh 入口版） / 证据链样例（现象 → 断点 → 变量 → 结论） ...
- 文内已出现的入口用例：SpringCoreBeansContainerLabTest, SpringCoreBeansBootstrapInternalsLabTest, SpringCoreBeansPostProcessorOrderingLabTest, SpringCoreBeansBeanCreationTraceLabTest
- 文内已出现的源码入口/锚点：AbstractApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, AbstractApplicationContext#prepareBeanFactory, PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“章节验收口径（10/30/3：refresh 主线）”附近把关键入口串成更清晰的主线（例如：AbstractApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“排障分流（refresh 入口版）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“阶段内关键对象变化（断点可验证）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障分流（refresh 入口版）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-00-guide/07-breakpoint-map.md

- 主题：第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）
- 文内结构线索：导读 / 机制主线（按 refresh 时间线组织） / 阶段内关键对象变化（断点地图补充） / 主线高频分支最小集（断点地图版） / 源码调用链与断点（建议从 Lab 反推） / 最小可运行实验（Lab） / 证据链样例（现象 → 断点 → 变量 → 结论） / 条件断点模板（降噪）：让断点“只为目标 bean 服务” ...
- 文内已出现的入口用例：SpringCoreBeansLabTest, SpringCoreBeansBootstrapInternalsLabTest, SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans, SpringCoreBeansBeanCreationTraceLabTest#beanCreationTrace_recordsPhases_andExposesProxyReplacement, SpringCoreBeansEarlyReferenceLabTest, SpringCoreBeansBeanCreationTraceLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, AbstractApplicationContext#refresh, PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors, PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线（按 refresh 时间线组织）”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“阶段内关键对象变化（断点地图补充）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。


## spring-core-modules/spring-core-beans/docs/part-01-ioc-container

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-dependency-injection-resolution.md

- 主题：第 14 章：03. 依赖注入解析：类型/名称/@Qualifier/@Primary
- 文内结构线索：导读 / 机制主线：候选收集 → 候选收敛 → 最终注入 / 1. 本模块里的最小例子：两个 `TextFormatter` / 2. 候选收集（collect）：先回答“有哪些可能的候选？” / 3. 候选收敛（narrow down）：从候选集合缩到唯一候选 / 4. 可选依赖与延迟解析：Optional / required=false / ObjectProvider / 5. JSR-330 对照：`@Inject` / `@Named` / `Provider<T>` / 6. 调试闭环：从异常到下一步断点 ...
- 文内已出现的入口用例：SpringCoreBeansAutowireCandidateSelectionLabTest, SpringCoreBeansBeanGraphDebugLabTest, SpringCoreBeansOptionalInjectionLabTest, SpringCoreBeansJsr330InjectionLabTest, SpringCoreBeansGenericTypeMatchingPitfallsLabTest, SpringCoreBeansDependencyDescriptorMetadataLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency, DefaultListableBeanFactory#resolveDependency(...), MethodParameter#getParameterName()
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线：候选收集 → 候选收敛 → 最终注入”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansAutowireCandidateSelectionLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“6. 调试闭环：从异常到下一步断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/03-scope-and-prototype.md

- 主题：第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）
- 文内结构线索：导读 / 机制主线 / 1. singleton vs prototype：到底“一”指什么？ / 2. 本模块里应能够直接观察到的现象 / 2.1 prototype 的关键边界（创建 guard / 循环依赖 / 缓存差异） / 3. 为什么“prototype 注入 singleton”会看起来像单例？ / 4. 解决方案 1：`ObjectProvider`（推荐，简单有效） / 5. 解决方案 2：`@Lookup`（方法注入，适合“每次调用都要新的”） ...
- 文内已出现的入口用例：SpringCoreBeansContainerLabTest, SpringCoreBeansLabTest, SpringCoreBeansPrototypeDestroySemanticsLabTest, SpringCoreBeansCustomScopeLabTest#scopedProxy_registersScopedTargetBeanDefinition_andInterfacesProxyRequiresInterfaceInjection, SpringCoreBeansCustomScopeLabTest#customScope_canTriggerDestructionCallbacks_whenScopeEnds, SpringCoreBeansContainerLabTest.lookupMethodCanObtainFreshPrototypeEachCall ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, AbstractBeanFactory#doGetBean, DefaultListableBeanFactory#getBeanProvider, DefaultSingletonBeanRegistry#destroySingletons ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansContainerLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“2. 本模块里应能够直接观察到的现象”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“9. 排障决策表（scope/prototype：从“像单例”到“证据链”）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
  - 代理主题建议补“Beans 侧换壳点”与“AOP 侧调用链”的最短互链：让读者能同时解释“对象何时被替换”与“为何调用时经过拦截器”。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/04-lifecycle-and-callbacks.md

- 主题：第 16 章：05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）
- 文内结构线索：导读 / 机制主线 / 1. 源码级生命周期骨架：把顺序落到关键方法 / 补充：`@PostConstruct/@PreDestroy` 的“触发者”为 BPP（而不是语法魔法） / 2. Aware 系列回调：真实作用、触发者与发生时机 / 3. 本模块的“可观测”例子：把顺序固化成断言 / 4. 常见生命周期回调方式（按“推荐度/常见度”） / 5. 生命周期与 Scope 的交互（重点） ...
- 文内已出现的入口用例：SpringCoreBeansLifecycleCallbackOrderLabTest, SpringCoreBeansPrototypeDestroySemanticsLabTest, SpringCoreBeansDependsOnLabTest, SpringCoreBeansSmartInitializingSingletonLabTest, SpringCoreBeansSmartLifecycleLabTest, SpringCoreBeansAwareInfrastructureLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractAutowireCapableBeanFactory#doCreateBean, DefaultSingletonBeanRegistry#destroySingletons, AbstractApplicationContext#refresh, DefaultListableBeanFactory#preInstantiateSingletons, AbstractAutowireCapableBeanFactory#createBean ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractAutowireCapableBeanFactory#doCreateBean），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansAwareInfrastructureLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“6. 调试与断点：把“生命周期”变成可定位问题”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障决策表（生命周期/回调：从“没执行”到“证据链”）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/05-post-processors.md

- 主题：第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）
- 文内结构线索：导读 / 机制主线 / 1. BFPP：`BeanFactoryPostProcessor` / 2. BPP：`BeanPostProcessor` / 3. 顺序（Ordering）：为什么同一个扩展点里顺序也很重要 / 3.1 必须补齐的第三类：`BeanDefinitionRegistryPostProcessor`（BDRPP） / 3.2 源码级时间线：refresh 里它们到底在哪发生？ / 3.3 源码解析：`PostProcessorRegistrationDelegate` 的两段核心算法 ...
- 文内已出现的入口用例：SpringCoreBeansContainerLabTest, SpringCoreBeansPostProcessorOrderingLabTest, SpringCoreBeansProgrammaticBeanPostProcessorLabTest, SpringCoreBeansStaticBeanFactoryPostProcessorLabTest, SpringCoreBeansRegistryPostProcessorLabTest, SpringCoreBeansEarlyGetBeanMissesBppLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, AbstractApplicationContext#refresh, InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation, InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansContainerLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“断点闭环（用本仓库 Lab/Test 运行一次）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界（补一段“能落到源码的答案”）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
  - PostProcessor 主题建议把“顺序/时机误判”压缩到一条时间线：过早 getBean / programmatic 注册 / internal BPP 的差异如何在同一主线上被观察到。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/06-configuration-enhancement.md

- 主题：第 18 章：07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）
- 文内结构线索：导读 / 机制主线 / 配置类解析主线（定义层发生了什么） / 增强机制细节（proxyBeanMethods=true 才发生） / 1. 两种配置方式的核心差异 / 3. 最推荐的写法：用“方法参数”声明依赖 / 5. 应能够回答的 2 个问题 / 面试常问（`@Configuration(proxyBeanMethods=...)` 的语义） ...
- 文内已出现的入口用例：SpringCoreBeansContainerLabTest, SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsFalse_stillPreservesSingleton_whenUsingMethodParameterInjection, SpringCoreBeansContainerLabTest#liteConfiguration_stillPreservesSingleton_whenUsingMethodParameterInjection, SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsFalseAllowsDirectMethodCallToCreateExtraInstance, SpringCoreBeansContainerLabTest#liteConfiguration_componentWithBeanMethods_doesNotEnhance_beanMethodInterCallsCreateExtraInstance
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, ConfigurationClassPostProcessor#processConfigBeanDefinitions, ConfigurationClassParser#parse, ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansContainerLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
  - 代理主题建议补“Beans 侧换壳点”与“AOP 侧调用链”的最短互链：让读者能同时解释“对象何时被替换”与“为何调用时经过拦截器”。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/01-bean-registration.md

- 主题：02. Bean 注册入口：扫描、@Bean、@Import、registrar（已合并）
- 文内结构线索：导读 / 章节验收口径（10/30/3：教程化闭环） / 机制系统阐述：注册入口的条件 → 分支 → 结果（可断点证明） / 关键分支解释（围绕 refresh 的 if/then） / 机制主线：注册 = 先注册定义，再按定义造实例 / 1. BeanDefinition 是什么？（先把名词变成可观察对象） / 2. 四类常见注册入口（在项目里 99% 会遇到） / 可复现闭环（基于 `SpringCoreBeansComponentScanLabTest`） ...
- 文内已出现的入口用例：SpringCoreBeansComponentScanLabTest, SpringCoreBeansBeanDefinitionRegistrationDiffLabTest, SpringCoreBeansImportLabTest, SpringCoreBeansProgrammaticRegistrationLabTest, SpringCoreBeansContainerLabTest, SpringCoreBeansBeanNameAliasLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, DefaultListableBeanFactory#registerBeanDefinition, DefaultSingletonBeanRegistry#registerSingleton, DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(true), AbstractBeanFactory#doGetBean, DefaultListableBeanFactory#preInstantiateSingletons ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制系统阐述：注册入口的条件 → 分支 → 结果（可断点证明）”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → DefaultListableBeanFactory#registerBeanDefinition），并在关键分支处点明触发条件与结果形态。
  - 在“机制系统阐述：注册入口的条件 → 分支 → 结果（可断点证明）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“机制系统阐述：注册入口的条件 → 分支 → 结果（可断点证明）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“5. 排障决策表（注册相关：现象 → 分层 → 证据 → 修复）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-bean-mental-model.md

- 主题：第 20 章：01. Bean 运行机制：从 BeanDefinition 到最终暴露对象
- 文内结构线索：导读 / 机制主线：三层模型 + 一个“最终对象”概念 / 1. 四类对象对照表：调试对象与语义 / 2. 方法级主线：refresh → doCreateBean → 最终暴露对象 / 可复现闭环（基于 `SpringCoreBeansBeanCreationTraceLabTest`） / 3. 三个“最终对象被替换”的高频入口 / 补充：能注入 ≠ 一定是 Bean（ResolvableDependency / 外部对象） / 4. 排障决策表（将主观判断转化为可验证结论） ...
- 文内已出现的入口用例：SpringCoreBeansContainerLabTest, SpringCoreBeansBeanCreationTraceLabTest, SpringCoreBeansProxyingPhaseLabTest, SpringCoreBeansContainerLabTest.beanDefinitionIsNotTheBeanInstance, SpringCoreBeansBeanFactoryVsApplicationContextLabTest, SpringCoreBeansBootstrapInternalsLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization, AbstractBeanFactory#getMergedLocalBeanDefinition(beanName), AbstractBeanFactory#getObjectForBeanInstance ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线：三层模型 + 一个“最终对象”概念”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansBeanCreationTraceLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“1. 四类对象对照表：调试对象与语义”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“4. 排障决策表（将主观判断转化为可验证结论）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/07-factorybean.md

- 主题：08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）
- 文内结构线索：导读 / 机制主线 / 1. `FactoryBean` 的核心语义 / FactoryBean 与代理/循环依赖的交叉 / 3. `FactoryBean` 常见用途（理解即可） / 面试常问（FactoryBean） / 可复现闭环（基于 `SpringCoreBeansFactoryBeanDeepDiveLabTest`） / 源码与断点 ...
- 文内已出现的入口用例：SpringCoreBeansContainerLabTest, SpringCoreBeansFactoryBeanDeepDiveLabTest, SpringCoreBeansFactoryBeanEdgeCasesLabTest, SpringCoreBeansContainerLabTest#factoryBeanByNameReturnsProductAndAmpersandReturnsFactory, SpringCoreBeansFactoryBeanEdgeCasesLabTest#factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName, SpringCoreBeansFactoryBeanDeepDiveLabTest#singletonFactoryBeanProduct_isCached_byTheContainer
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractBeanFactory#getObjectForBeanInstance, AbstractBeanFactory#getTypeForFactoryBean(...), FactoryBeanRegistrySupport#getObjectFromFactoryBean(...), AbstractBeanFactory#doGetBean, FactoryBeanRegistrySupport#getObjectFromFactoryBean ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractBeanFactory#getObjectForBeanInstance），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansFactoryBeanDeepDiveLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
  - FactoryBean 主题建议强化“产品/工厂”边界的可验证路径：提示读者用 `&` 前缀、按类型发现与缓存命中点去自证判断。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-circular-dependencies.md

- 主题：09. 循环依赖：现象、原因与规避（constructor vs setter）
- 文内结构线索：导读 / 机制主线：为什么 constructor 死、setter 有时能活？ / 1. 将现象固化为断言（避免主观推断） / 1.2 为什么读者看完仍不懂“为什么要三级缓存”？（桥接：2-level vs 3-level） / 2. 三层缓存的真实语义：final / early / factory / 3. 关键窗口期：early exposure 发生在 `doCreateBean` 的哪一步？ / 4. 断点闭环：从 `getSingleton` 看清“到底救没救” / 排障配方：如何定位“环路边”并选择打断手段 ...
- 文内已出现的入口用例：SpringCoreBeansContainerLabTest#circularDependencyWithConstructorsFailsFast, SpringCoreBeansContainerLabTest, SpringCoreBeansCircularDependencyBoundaryLabTest, SpringCoreBeansEarlyReferenceLabTest, SpringCoreBeansContainerLabTest#circularDependencyWithSettersMaySucceedViaEarlySingletonExposure, SpringCoreBeansCircularDependencyBoundaryLabTest#constructorCycleCanBeBrokenViaLazyInjectionPointProxy ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, ConstructorResolver#autowireConstructor, AbstractAutowireCapableBeanFactory#populateBean, AbstractBeanFactory#checkDependencies, AbstractAutowireCapableBeanFactory#getEarlyBeanReference, DefaultListableBeanFactory#setAllowCircularReferences(boolean) ...
- 跨模块互链：../../../spring-core-aop/docs/part-01-proxy-fundamentals/01-aop-proxy-mental-model.md
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 对跨模块链接补“跳转目的”：在链接附近用 1–2 句说明为什么此处需要 AOP/TX 视角，以及跳过去应验证的关键点（例如代理创建点/自调用行为/拦截器链顺序）。
  - 在“机制主线：为什么 constructor 死、setter 有时能活？”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → ConstructorResolver#autowireConstructor），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansCircularDependencyBoundaryLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“4. 断点闭环：从 `getSingleton` 看清“到底救没救””附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障配方：如何定位“环路边”并选择打断手段”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。


## spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig

### spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/01-debugging-and-observability.md

- 主题：第 19 章：11. 调试与自检：如何“观察到”容器正在做什么
- 文内结构线索：导读 / 机制主线 / 0. 观测对象总览：读者通常只是在观察 5 类对象 / 1. 最简单也最有效：查容器里到底有哪些 Bean / 2. 进一步：看 BeanDefinition（定义层） / 3. 固定观察点：候选集合 vs 最终注入（以及容器记录的依赖边） / 4. Spring Boot 的“条件报告”：把自动装配的生效/失效原因打印出来 / 5. 日志：输出容器行为以便观察 ...
- 文内已出现的入口用例：SpringCoreBeansAutoConfigurationLabTest, SpringCoreBeansAutoConfigurationOrderingLabTest, SpringCoreBeansAutowireCandidateSelectionLabTest, SpringCoreBeansContainerLabTest, SpringCoreBeansMergedBeanDefinitionLabTest, SpringCoreBeansBeanCreationTraceLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, AbstractBeanFactory#getMergedLocalBeanDefinition(beanName), DefaultListableBeanFactory#findAutowireCandidates(...), DefaultListableBeanFactory#getBeanNamesForType(...) ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansAutoConfigurationLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“0. 观测对象总览：读者通常只是在观察 5 类对象”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。

### spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/02-auto-config-ordering.md

- 主题：09. Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？
- 文内结构线索：导读 / 机制主线：顺序不定义，就会“看起来像偶发” / 1. 现象：跨 Auto-Config 的 `@ConditionalOnBean` 可能因为顺序不确定而失败 / 2. 修复思路：让顺序从“偶然”变成“确定” / 可复现闭环（基于 `SpringCoreBeansAutoConfigurationOrderingLabTest`） / 3. 断点闭环：把“顺序”落到可观察证据 / 4. 常见误区（工程里最容易误诊的点） / 源码调用链（方法级）：从“导入”到“条件评估” ...
- 文内已出现的入口用例：SpringCoreBeansAutoConfigurationOrderingLabTest, SpringCoreBeansAutoConfigurationBackoffTimingLabTest, SpringCoreBeansAutoConfigurationOrderingLabTest#conditionalOnBean_canFailAcrossAutoConfigurations_whenOrderingIsNotDefined, SpringCoreBeansAutoConfigurationOrderingLabTest#autoConfigurationAfter_canMakeCrossAutoConfigConditionsDeterministic_evenIfImportOrderIsReversed
- 文内已出现的源码入口/锚点：AutoConfigurationImportSelector#selectImports, ConditionEvaluator#shouldSkip, ConfigurationClassPostProcessor#processConfigBeanDefinitions, SpringCoreBeansAutoConfigurationOrderingLabTest#conditionalOnBean_canFailAcrossAutoConfigurations_whenOrderingIsNotDefined
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线：顺序不定义，就会“看起来像偶发””附近把关键入口串成更清晰的主线（例如：AutoConfigurationImportSelector#selectImports → ConditionEvaluator#shouldSkip），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansAutoConfigurationOrderingLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“3. 断点闭环：把“顺序”落到可观察证据”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“4. 常见误区（工程里最容易误诊的点）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/03-spring-boot-auto-configuration.md

- 主题：第 21 章：10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）
- 文内结构线索：导读 / 机制主线 / 1. 先说结论：Boot 做了什么？ / 2. 自动装配的入口：`@SpringBootApplication` / `@EnableAutoConfiguration` / 3. 自动配置类从哪里来？（类清单的来源） / 4. 为什么自动配置不是“全都生效”？——条件（Conditions） / 5. 如何“覆盖”自动配置？ / 可复现闭环（基于 `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`） ...
- 文内已出现的入口用例：SpringCoreBeansAutoConfigurationBackoffTimingLabTest, SpringCoreBeansAutoConfigurationImportOrderingLabTest, SpringCoreBeansAutoConfigurationLabTest, SpringCoreBeansConditionEvaluationReportLabTest, SpringCoreBeansAutoConfigurationOrderingLabTest, SpringCoreBeansAutoConfigurationOverrideMatrixLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, DefaultListableBeanFactory#registerBeanDefinition, DefaultListableBeanFactory#doResolveDependency, AbstractApplicationContext#refresh ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“2. 自动装配的入口：`@SpringBootApplication` / `@EnableAutoConfiguration`”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“6. 如何“观察到”自动装配做了什么？”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。


## spring-core-modules/spring-core-beans/docs/part-03-container-internals

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/01-container-bootstrap-and-infrastructure.md

- 主题：第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？
- 文内结构线索：导读 / 机制主线 / 1. 现象：同样是 Spring 容器，不同启动方式结果不一样 / 补充：如何识别“基础设施 Bean”（`ROLE_INFRASTRUCTURE`）以及它对排障的意义 / 可复现闭环（基于 `SpringCoreBeansBootstrapInternalsLabTest`） / 2. `@Bean` 为什么能“变成 BeanDefinition”？ / 排障分流：这是定义层问题还是实例层问题？ / 源码最短路径（call chain） ...
- 文内已出现的入口用例：SpringCoreBeansBootstrapInternalsLabTest, SpringCoreBeansResourceInjectionLabTest, SpringCoreBeansRegistryPostProcessorLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, DefaultListableBeanFactory#doResolveDependency, AnnotationConfigUtils#registerAnnotationConfigProcessors, GenericApplicationContext#refresh
  -> AbstractApplicationContext#refresh
       -> invokeBeanFactoryPostProcessors(beanFactory)
            -> PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors
                 -> (如果存在) ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry
                      -> 解析 @Configuration/@Bean/@Import/@ComponentScan 并注册更多 BeanDefinition

       -> registerBeanPostProcessors(beanFactory)
            -> PostProcessorRegistrationDelegate#registerBeanPostProcessors
                 -> (实例化并注册) AutowiredAnnotationBeanPostProcessor
                 -> (实例化并注册) CommonAnnotationBeanPostProcessor

       -> finishBeanFactoryInitialization(beanFactory)
            -> DefaultListableBeanFactory#preInstantiateSingletons
                 -> AbstractAutowireCapableBeanFactory#doCreateBean
                      -> populateBean
                           -> AutowiredAnnotationBeanPostProcessor#postProcessProperties (@Autowired/@Value)
                           -> CommonAnnotationBeanPostProcessor#postProcessProperties (@Resource)
                      -> initializeBean
                           -> InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization (@PostConstruct)
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansBootstrapInternalsLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“固定观察点（watch list）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“补充：如何识别“基础设施 Bean”（`ROLE_INFRASTRUCTURE`）以及它对排障的意义”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/02-bdrpp-definition-registration.md

- 主题：13. BeanDefinitionRegistryPostProcessor：在“注册阶段”动态加定义
- 文内结构线索：导读 / 机制主线 / 1. 一句话结论：先有“定义”，后有“实例” / 2. 现象：未显式注册 bean，但它依然出现了 / 3. 顺序：BDRPP 先于普通 BFPP / 可复现闭环（基于 `SpringCoreBeansRegistryPostProcessorLabTest`） / 排障分流：这是定义层问题还是实例层问题？ / 源码最短路径（call chain） ...
- 文内已出现的入口用例：SpringCoreBeansRegistryPostProcessorLabTest, SpringCoreBeansRegistryPostProcessorLabTest.beanDefinitionRegistryPostProcessor_canRegisterNewBeanDefinitions(), SpringCoreBeansRegistryPostProcessorLabTest.bdrppRunsBeforeRegularBeanFactoryPostProcessor(), SpringCoreBeansRegistryPostProcessorLabTest#getBeanDuringPostProcessing_instantiatesTooEarly_andSkipsLaterBeanPostProcessors
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors, DefaultListableBeanFactory#registerBeanDefinition, DefaultListableBeanFactory#preInstantiateSingletons, PostProcessorRegistrationDelegate#registerBeanPostProcessors, AbstractBeanFactory#doGetBean ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansRegistryPostProcessorLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“固定观察点（watch list）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障分流：这是定义层问题还是实例层问题？”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
  - PostProcessor 主题建议把“顺序/时机误判”压缩到一条时间线：过早 getBean / programmatic 注册 / internal BPP 的差异如何在同一主线上被观察到。

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/03-post-processor-ordering.md

- 主题：14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序
- 文内结构线索：导读 / 机制主线 / 1. 规则总览（记住这三层就够） / 1.1 源码解析：真正参与排序的“不是接口名”，而是 comparator 的比较规则 / 2. BFPP 的顺序：先改谁的定义？ / 3. BPP 的顺序：谁先“动手”改实例？ / 4. 常见误解 / 可复现闭环（基于 `SpringCoreAopMultiProxyStackingLabTest`） ...
- 文内已出现的入口用例：SpringCoreBeansPostProcessorOrderingLabTest, SpringCoreBeansProgrammaticBeanPostProcessorLabTest, SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticBppExecutionOrder_isRegistrationOrder_notOrderedInterface
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, PostProcessorRegistrationDelegate#sortPostProcessors, PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors, Ordered#getOrder()
- 跨模块互链：../../../spring-core-aop/docs/part-01-proxy-fundamentals/06-debugging.md, ../../../spring-core-aop/docs/part-03-proxy-stacking/01-multi-proxy-stacking.md
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 对跨模块链接补“跳转目的”：在链接附近用 1–2 句说明为什么此处需要 AOP/TX 视角，以及跳过去应验证的关键点（例如代理创建点/自调用行为/拦截器链顺序）。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → PostProcessorRegistrationDelegate#sortPostProcessors），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreAopMultiProxyStackingLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“固定观察点（watch list）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障分流：这是定义层问题还是实例层问题？”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/04-pre-instantiation-short-circuit.md

- 主题：15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行
- 文内结构线索：导读 / 机制主线 / 1. 现象：构造器抛异常会让 refresh 直接失败 / 2. 现象：短路后，构造器不再执行 / 3. 这个机制有什么现实意义？ / 可复现闭环（基于 `SpringCoreBeansPreInstantiationLabTest`） / 排障分流：这是定义层问题还是实例层问题？ / 4. 源码调用链（方法级）：短路发生在哪个分支？ ...
- 文内已出现的入口用例：SpringCoreBeansPreInstantiationLabTest, SpringCoreBeansPreInstantiationLabTest.withoutBeforeInstantiationShortCircuit_refreshFailsAndConstructorWasCalled(), SpringCoreBeansPreInstantiationLabTest.postProcessBeforeInstantiation_canShortCircuitDefaultInstantiationPath()
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation, AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation, AbstractAutowireCapableBeanFactory#doCreateBean, AbstractAutowireCapableBeanFactory#createBean(beanName, mbd, args), AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation(beanName, mbd) ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansPreInstantiationLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“排障分流：这是定义层问题还是实例层问题？”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/05-early-reference-and-circular.md

- 主题：16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？
- 文内结构线索：导读 / 一页式最短证据链（10 分钟）：观察到 factory 层价值 + early 形态决策 / 机制主线：early reference 的“时机”与“形态” / 1. 先运行实验：让问题变成“可见”的 / 2. 需要同时记住的三件事（建议整体掌握） / 3. 源码最短路径：把三件事串成一条证据链 / 4. 断点闭环（推荐照着做一次） / 可复现闭环（基于 `SpringCoreBeansEarlyReferenceLabTest`） ...
- 文内已出现的入口用例：SpringCoreBeansEarlyReferenceLabTest, SpringCoreBeansRawInjectionDespiteWrappingLabTest, SpringCoreBeansCircularDependencyBoundaryLabTest, SpringCoreBeansContainerLabTest, SpringCoreBeansEarlyReferenceLabTest#getEarlyBeanReference_canProvideEarlyProxyDuringCircularDependencyResolution, SpringCoreBeansEarlyReferenceLabTest#injectingConcreteTypeFailsWhenFinalBeanIsJdkProxy_duringCircularDependency ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, DefaultSingletonBeanRegistry#getSingleton, AbstractAutowireCapableBeanFactory#getEarlyBeanReference, AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization
- 跨模块互链：../../../spring-core-aop/docs/part-01-proxy-fundamentals/01-aop-proxy-mental-model.md
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 对跨模块链接补“跳转目的”：在链接附近用 1–2 句说明为什么此处需要 AOP/TX 视角，以及跳过去应验证的关键点（例如代理创建点/自调用行为/拦截器链顺序）。
  - 在“机制主线：early reference 的“时机”与“形态””附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → DefaultSingletonBeanRegistry#getSingleton），并在关键分支处点明触发条件与结果形态。
  - 在“1. 先运行实验：让问题变成“可见”的”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“一页式最短证据链（10 分钟）：观察到 factory 层价值 + early 形态决策”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“一页式最短证据链（10 分钟）：观察到 factory 层价值 + early 形态决策”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
  - 循环依赖主题建议补“工程对照”：区分“缓存语义”与“改变时机（@Lazy/ObjectProvider）”，并提示读者如何从现象判断自己属于哪一种。

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/06-lifecycle-callback-order.md

- 主题：17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）
- 文内结构线索：导读 / 机制主线 / 1. 一个可断言的顺序（比看日志更可靠） / 2. prototype 为什么默认不走销毁回调？ / 可复现闭环（基于 `SpringCoreBeansBootstrapInternalsLabTest`） / 排障分流：这是定义层问题还是实例层问题？ / 3. 源码调用链（方法级）：初始化与销毁发生在哪里？ / 4. 排障决策表（生命周期：从“没执行”到“证据链”） ...
- 文内已出现的入口用例：SpringCoreBeansLifecycleCallbackOrderLabTest, SpringCoreBeansLifecycleCallbackOrderLabTest.singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization(), SpringCoreBeansLifecycleCallbackOrderLabTest.prototypeBeans_areNotDestroyedByContainerByDefault(), SpringCoreBeansBootstrapInternalsLabTest, SpringCoreBeansLifecycleCallbackOrderLabTest.prototypeBeans_areNotDestroyedByContainerByDefault
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractAutowireCapableBeanFactory#doCreateBean, AbstractAutowireCapableBeanFactory#populateBean, AbstractAutowireCapableBeanFactory#initializeBean, AbstractApplicationContext#doClose, DefaultSingletonBeanRegistry#destroySingletons ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractAutowireCapableBeanFactory#doCreateBean），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansBootstrapInternalsLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“排障分流：这是定义层问题还是实例层问题？”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/07-refresh-to-bean-creation-mainline.md

- 主题：18. 从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）
- 文内结构线索：导读 / 机制主线 / 0. 先把“主线地图”记住：容器做两件事 / 1. 第一幕：`refresh()` 的骨架（容器主线） / 2. 第二幕：`invokeBeanFactoryPostProcessors()` —— 图为什么会“越长越大” / 3. 第三幕：`registerBeanPostProcessors()` —— 容易误判为“初始化”，但其本质是“装配规则” / 4. 第四幕：`finishBeanFactoryInitialization()` —— 图开始变成对象 / 5. 第五幕：一次 `getBean()` 的内核（`doGetBean`） ...
- 文内已出现的入口用例：SpringCoreBeansBootstrapInternalsLabTest, SpringCoreBeansRegistryPostProcessorLabTest, SpringCoreBeansPostProcessorOrderingLabTest, SpringCoreBeansPreInstantiationLabTest, SpringCoreBeansBeanCreationTraceLabTest, SpringCoreBeansEarlyReferenceLabTest ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractApplicationContext#refresh, AbstractBeanFactory#doGetBean, AbstractAutowireCapableBeanFactory#doCreateBean, AbstractApplicationContext#finishBeanFactoryInitialization, org.springframework.context.support.AbstractApplicationContext#refresh
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansBeanCreationTraceLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“7. 把“关键分支”变成调试能力：建议的断点与观察变量”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障分流：现象 → 阶段 → 关键方法 → 必看变量 → 对应 LabTest”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。


## spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/01-lazy-semantics.md

- 主题：第 23 章：18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）
- 文内结构线索：导读 / 机制主线 / 1. lazy-init bean：refresh 阶段不创建 / 2. 关键反直觉点：lazy-init 也挡不住“被别人依赖” / 3. `@Lazy` 放在注入点：注入一个 proxy，而不是直接注入目标对象 / 补充：注入点 `@Lazy` 的内部实现（不是 lazy-init，而是“延迟解析代理”） / 可复现闭环（基于 `SpringCoreBeansLazyLabTest`） / 4. 代理类型边界：接口注入点 vs 类注入点（必须会排障） ...
- 文内已出现的入口用例：SpringCoreBeansLazyLabTest, SpringCoreBeansLazyLabTest#lazyInjectionPoint_canDeferCreationOfLazyBeanUntilFirstUse, SpringCoreBeansLazyLabTest#lazyInjectionPoint_onConcreteClass_usesClassBasedProxy_andDefersCreationUntilFirstUse
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, DefaultListableBeanFactory#preInstantiateSingletons, DefaultListableBeanFactory#doResolveDependency(...), AbstractBeanFactory#doGetBean ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansLazyLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“4. 代理类型边界：接口注入点 vs 类注入点（必须会排障）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/02-depends-on.md

- 主题：19. dependsOn：强制初始化顺序（即使没有显式依赖）
- 文内结构线索：导读 / 机制主线：它解决的是“顺序”，不是“注入” / 1. 方法级入口：dependsOn 在哪一步生效？ / 2. 写法入口：`@DependsOn` / `BeanDefinition#setDependsOn(...)` / XML `depends-on` / 3. 容器内部结构：两张依赖图怎么读？ / 4. 销毁顺序：为什么关闭时顺序“反过来”？ / dependsOn vs SmartLifecycle phase：什么时候用哪一个？ / 父子容器边界（层级 context 下的依赖解析） ...
- 文内已出现的入口用例：SpringCoreBeansDependsOnLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractBeanFactory#doGetBean, AbstractApplicationContext#refresh, DefaultListableBeanFactory#preInstantiateSingletons, DefaultSingletonBeanRegistry#getDependentBeans(beanName), DefaultSingletonBeanRegistry#getDependenciesForBean(beanName) ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线：它解决的是“顺序”，不是“注入””附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractBeanFactory#doGetBean），并在关键分支处点明触发条件与结果形态。
  - 在“1. 方法级入口：dependsOn 在哪一步生效？”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“8. 断点闭环（建议照做一次）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“7. 排障决策表（初始化/关闭/异常消息 → 证据链）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/03-resolvable-dependency.md

- 主题：20. registerResolvableDependency：能注入，但它不是 Bean
- 文内结构线索：导读 / 机制主线：它是“可解析依赖”，不是“可获取 Bean” / 1. 方法级入口：注入是怎么进入 `doResolveDependency` 的？ / 2. 机制：`resolvableDependencies` 到底是什么？ / 3. 容器默认会注册哪些 ResolvableDependency？（以及怎么确认） / 4. 高级用法：用 `ObjectFactory` 做“按需提供” / 5. 它和 `*Aware` 是什么关系？ / 可复现闭环（基于 `SpringCoreBeansResolvableDependencyLabTest`） ...
- 文内已出现的入口用例：SpringCoreBeansResolvableDependencyLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, DefaultListableBeanFactory#resolvableDependencies, DefaultListableBeanFactory#doResolveDependency, DefaultListableBeanFactory#resolveDependency, ConstructorResolver#autowireConstructor, DefaultListableBeanFactory#registerResolvableDependency(Class<?> dependencyType, Object autowiredValue) ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线：它是“可解析依赖”，不是“可获取 Bean””附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → DefaultListableBeanFactory#resolvableDependencies），并在关键分支处点明触发条件与结果形态。
  - 在“1. 方法级入口：注入是怎么进入 `doResolveDependency` 的？”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“7. 断点闭环（建议照做一次）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“6. 排障决策表（能注入/不能 getBean/命中不了 → 证据链）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/04-context-hierarchy.md

- 主题：21. 父子 ApplicationContext：可见性与覆盖边界
- 文内结构线索：导读 / 机制主线 / 1. 现象：child 能看到 parent，parent 看不到 child / 2. 覆盖（override）是“按名字”的，并且只在 child 生效 / 可复现闭环（基于 `SpringCoreBeansContextHierarchyLabTest`） / 排障分流：这是定义层问题还是实例层问题？ / 4. 面试常问（父子 ApplicationContext） / 源码与断点 ...
- 文内已出现的入口用例：SpringCoreBeansContextHierarchyLabTest, SpringCoreBeansContextHierarchyLabTest.childContext_canSeeParentBeans_butParentCannotSeeChildBeans()
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractBeanFactory#doGetBean, AbstractApplicationContext#setParent, AbstractBeanFactory#containsLocalBean, DefaultListableBeanFactory#containsBeanDefinition, AbstractApplicationContext#getParent ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractBeanFactory#doGetBean），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansContextHierarchyLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障分流：这是定义层问题还是实例层问题？”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/05-bean-names-and-aliases.md

- 主题：22. Bean 名称与 alias：同一个实例，多一个名字
- 文内结构线索：导读 / 机制主线 / 1. 现象：两个名字获取到的是同一个对象 / 2. alias 在容器里的定位 / 排障分流：这是定义层问题还是实例层问题？ / 可复现闭环（基于 `SpringCoreBeansBeanNameAliasLabTest`） / 4. 面试常问（beanName 与 alias） / 源码与断点 ...
- 文内已出现的入口用例：SpringCoreBeansBeanNameAliasLabTest, SpringCoreBeansBeanNameAliasLabTest.aliasResolvesToSameSingletonInstanceAsCanonicalName()
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractBeanFactory#transformedBeanName, AbstractBeanFactory#doGetBean, DefaultSingletonBeanRegistry#getSingleton, DefaultListableBeanFactory#registerBeanDefinition, DefaultListableBeanFactory#transformedBeanName ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractBeanFactory#transformedBeanName），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansBeanNameAliasLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障分流：这是定义层问题还是实例层问题？”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md

- 主题：23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义
- 文内结构线索：导读 / 机制主线 / 1. 最重要的规则：`&` 前缀 / 2. product 也参与“按类型查找” / 3. isSingleton 的语义：容器是否缓存“product” / FactoryBean 与代理/循环依赖的交叉 / 可复现闭环（基于 `SpringCoreBeansContainerLabTest`） / 排障分流：这是定义层问题还是实例层问题？ ...
- 文内已出现的入口用例：SpringCoreBeansContainerLabTest, SpringCoreBeansFactoryBeanDeepDiveLabTest, SpringCoreBeansFactoryBeanEdgeCasesLabTest, SpringCoreBeansContainerLabTest.factoryBeanByNameReturnsProductAndAmpersandReturnsFactory(), SpringCoreBeansFactoryBeanDeepDiveLabTest.factoryBeanProductParticipatesInTypeMatching_andIsRetrievedByProductType(), SpringCoreBeansFactoryBeanDeepDiveLabTest.singletonFactoryBeanProduct_isCached_byTheContainer() ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractBeanFactory#getObjectForBeanInstance, FactoryBeanRegistrySupport#getObjectFromFactoryBean, FactoryBeanRegistrySupport#getCachedObjectForFactoryBean, AbstractBeanFactory#doGetBean, AbstractBeanFactory#isTypeMatch ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractBeanFactory#getObjectForBeanInstance），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于 `SpringCoreBeansContainerLabTest`）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障分流：这是定义层问题还是实例层问题？”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
  - FactoryBean 主题建议强化“产品/工厂”边界的可验证路径：提示读者用 `&` 前缀、按类型发现与缓存命中点去自证判断。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/07-bean-definition-overriding.md

- 主题：24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？
- 文内结构线索：导读 / 机制主线 / 1. allowBeanDefinitionOverriding=true：最后一个 wins / 2. allowBeanDefinitionOverriding=false：同名注册 fail-fast / 3. 覆盖语义的来源：Spring vs Boot 的开关路径 / 4. 定义层覆盖 vs 实例缓存：覆盖不会回滚已创建单例 / 5. 为什么这个点重要？ / 6. 排障分流：这是定义层问题还是实例层问题？ ...
- 文内已出现的入口用例：SpringCoreBeansBeanDefinitionOverridingLabTest, SpringCoreBeansBeanDefinitionOriginLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(...), DefaultListableBeanFactory#isAllowBeanDefinitionOverriding(), DefaultListableBeanFactory#registerBeanDefinition, DefaultListableBeanFactory#getBeanDefinition(beanName), DefaultSingletonBeanRegistry#singletonObjects ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(...)），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（用本仓库 Lab/Test 运行一次）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“6. 排障分流：这是定义层问题还是实例层问题？”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/08-programmatic-bpp-registration.md

- 主题：25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱
- 文内结构线索：导读 / 机制主线：两条注册路径 + 一个“不可逆”事实 / 1. 现象 1：手工添加的 BPP 会比“作为 bean 自动发现”的 BPP 更早执行 / 2. 现象 2：programmatic BPP 的执行顺序 = 注册顺序（不是 Ordered 顺序） / 3. 两条注册路径对照：读者到底走的是哪条？ / 4. 排障分流：顺序问题 vs 时机问题（先分清楚再下手） / 5. Debug 断点闭环（用一次就够） / 可复现闭环（基于本章 Lab） ...
- 文内已出现的入口用例：SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticallyAddedBpp_runsBeforeBeanDefinedBpp_evenIfBeanDefinedIsPriorityOrdered, SpringCoreBeansProgrammaticBeanPostProcessorLabTest, SpringCoreBeansProgrammaticRegistrationLabTest, SpringCoreBeansRegistryPostProcessorLabTest, SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticBppExecutionOrder_isRegistrationOrder_notOrderedInterface, SpringCoreBeansRegistryPostProcessorLabTest#getBeanDuringPostProcessing_instantiatesTooEarly_andSkipsLaterBeanPostProcessors
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, DefaultListableBeanFactory#addBeanPostProcessor, PostProcessorRegistrationDelegate#registerBeanPostProcessors, SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticallyAddedBpp_runsBeforeBeanDefinedBpp_evenIfBeanDefinedIsPriorityOrdered, SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticBppExecutionOrder_isRegistrationOrder_notOrderedInterface
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线：两条注册路径 + 一个“不可逆”事实”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → DefaultListableBeanFactory#addBeanPostProcessor），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（基于本章 Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“5. Debug 断点闭环（用一次就够）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“4. 排障分流：顺序问题 vs 时机问题（先分清楚再下手）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
  - PostProcessor 主题建议把“顺序/时机误判”压缩到一条时间线：过早 getBean / programmatic 注册 / internal BPP 的差异如何在同一主线上被观察到。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/09-smart-initializing-singleton.md

- 主题：26. SmartInitializingSingleton：所有单例都创建完之后再做事
- 文内结构线索：导读 / 机制主线 / 1. 现象：回调发生在“非 lazy 单例创建完成之后” / 2. 机制：它是 preInstantiateSingletons 的“收尾回调” / 回调来源分型：SmartInitializingSingleton 在生命周期里处于哪一层？ / 回调与代理交织：回调发生在 proxy 还是 target 上？ / 排障分流：这是定义层问题还是实例层问题？ / 4. 面试常问（SmartInitializingSingleton） ...
- 文内已出现的入口用例：SpringCoreBeansSmartInitializingSingletonLabTest, SpringCoreBeansSmartInitializingSingletonLabTest#afterSingletonsInstantiated_runsAfterNonLazySingletons_andBeforeLazyBeans, SpringCoreBeansSmartInitializingSingletonLabTest.afterSingletonsInstantiated_runsAfterNonLazySingletons_andBeforeLazyBeans()
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractApplicationContext#finishBeanFactoryInitialization, DefaultListableBeanFactory#preInstantiateSingletons, DefaultSingletonBeanRegistry#getSingleton, AbstractBeanFactory#doGetBean, AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractApplicationContext#finishBeanFactoryInitialization），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障分流：这是定义层问题还是实例层问题？”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md

- 主题：27. SmartLifecycle：start/stop 时机与 phase 顺序
- 文内结构线索：导读 / 机制主线 / 回调来源分型：它和其他回调有什么层级差异？ / 回调与代理交织：start/stop 执行在 proxy 还是 target 上？ / 1. 现象：start 按 phase 升序，stop 反向 / 2. 机制：LifecycleProcessor 统一管理 / 排障分流：这是定义层问题还是实例层问题？ / 4. 面试常问（SmartLifecycle / phase） ...
- 文内已出现的入口用例：SpringCoreBeansSmartLifecycleLabTest, SpringCoreBeansSmartLifecycleLabTest#smartLifecycleDoesNotAutoStart_whenIsAutoStartupIsFalse, SpringCoreBeansSmartLifecycleLabTest#containerStopsSmartLifecycle_viaStopCallbackMethod_notStopMethod, SpringCoreBeansSmartLifecycleLabTest#smartLifecycleStartsInPhaseOrder_andStopsInReverseOrder, SpringCoreBeansSmartLifecycleLabTest.smartLifecycleStartsInPhaseOrder_andStopsInReverseOrder()
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractApplicationContext#finishRefresh, LifecycleProcessor#onRefresh, DefaultLifecycleProcessor#startBeans, DefaultLifecycleProcessor#stopBeans, SpringCoreBeansSmartLifecycleLabTest#smartLifecycleDoesNotAutoStart_whenIsAutoStartupIsFalse
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractApplicationContext#finishRefresh），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障分流：这是定义层问题还是实例层问题？”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/11-custom-scope-and-scoped-proxy.md

- 主题：28. 自定义 Scope + scoped proxy：thread scope 的真实语义
- 文内结构线索：导读 / 机制主线 / 1. 注册自定义 scope（thread） / 2. 同类现象：prototype 注入 singleton 也会“冻结” / 3. 解法 1：ObjectProvider（推荐，机制最直观） / 4. 解法 2：scoped proxy（更“无感”，但引入代理语义） / 5. 销毁语义：prototype 不会自动销毁，自定义 scope 必须显式回收 / 排障分流：这是定义层问题还是实例层问题？ ...
- 文内已出现的入口用例：SpringCoreBeansCustomScopeLabTest#threadScope_createsOneInstancePerThread_whenAccessedDirectly, SpringCoreBeansCustomScopeLabTest, SpringCoreBeansCustomScopeLabTest#prototypeInjectedIntoSingleton_isResolvedOnce_butObjectProviderCanObtainFreshPrototypeEachCall, SpringCoreBeansCustomScopeLabTest#objectProvider_honorsThreadScope_whenUsedInsideSingleton, SpringCoreBeansCustomScopeLabTest#scopedProxy_honorsThreadScope_whenInjectedIntoSingleton, SpringCoreBeansCustomScopeLabTest.threadScope_createsOneInstancePerThread_whenAccessedDirectly() ...
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractBeanFactory#doGetBean, AbstractBeanFactory#registerScope, DefaultListableBeanFactory#registerScope, Scope#get, SpringCoreBeansCustomScopeLabTest#threadScope_createsOneInstancePerThread_whenAccessedDirectly ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractBeanFactory#doGetBean），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障分流：这是定义层问题还是实例层问题？”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
  - 代理主题建议补“Beans 侧换壳点”与“AOP 侧调用链”的最短互链：让读者能同时解释“对象何时被替换”与“为何调用时经过拦截器”。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/12-factorybean-edge-cases.md

- 主题：29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效
- 文内结构线索：导读 / 机制主线 / 0. 与代理/循环依赖的交叉边界（只要记住一条） / 1. 现象：getBeanNamesForType(..., allowEagerInit=false) 找不到 unknownValue / 2. 但读者仍然可以按名字获取到它 / 排障分流：这是定义层问题还是实例层问题？ / 4. 面试常问（FactoryBean 边界） / 源码与断点 ...
- 文内已出现的入口用例：SpringCoreBeansFactoryBeanEdgeCasesLabTest, SpringCoreBeansFactoryBeanEdgeCasesLabTest#factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName, SpringCoreBeansContainerLabTest#factoryBeanByNameReturnsProductAndAmpersandReturnsFactory, SpringCoreBeansFactoryBeanDeepDiveLabTest#singletonFactoryBeanProduct_isCached_byTheContainer, SpringCoreBeansFactoryBeanDeepDiveLabTest#nonSingletonFactoryBeanProduct_isNotCached_byTheContainer, SpringCoreBeansFactoryBeanEdgeCasesLabTest.factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName()
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, DefaultListableBeanFactory#getBeanNamesForType, FactoryBeanRegistrySupport#getTypeForFactoryBean, DefaultListableBeanFactory#doGetBeanNamesForType, AbstractBeanFactory#getType, AbstractBeanFactory#isTypeMatch ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → DefaultListableBeanFactory#getBeanNamesForType），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障分流：这是定义层问题还是实例层问题？”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
  - FactoryBean 主题建议强化“产品/工厂”边界的可验证路径：提示读者用 `&` 前缀、按类型发现与缓存命中点去自证判断。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md

- 主题：30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）
- 文内结构线索：导读 / 机制主线 / 1. 现象：field injection 在构造器里拿不到依赖 / 2. 现象：constructor injection 在构造器里就能获取到依赖 / 2.1 DependencyDescriptor 深入分析：解析“注入点语义”的核心对象 / 2.2 依赖解析分支树（简化版） / 2.3 关键变量解释（调试时只看这几项） / 3. `postProcessProperties(...)` 在哪里起作用？ ...
- 文内已出现的入口用例：SpringCoreBeansInjectionPhaseLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractAutowireCapableBeanFactory#autowireConstructor, AbstractAutowireCapableBeanFactory#populateBean, DependencyDescriptor#required, DependencyDescriptor#annotations, DependencyDescriptor#resolvableType ...
- 跨模块互链：../../../spring-core-aop/docs/part-01-proxy-fundamentals/01-aop-proxy-mental-model.md, ../../../spring-core-tx/docs/part-01-transaction-basics/02-transactional-proxy.md
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 对跨模块链接补“跳转目的”：在链接附近用 1–2 句说明为什么此处需要 AOP/TX 视角，以及跳过去应验证的关键点（例如代理创建点/自调用行为/拦截器链顺序）。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractAutowireCapableBeanFactory#autowireConstructor），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“2.3 关键变量解释（调试时只看这几项）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“排障分流：这是定义层问题还是实例层问题？”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md

- 主题：31. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）
- 文内结构线索：导读 / 为什么最终暴露对象会变化？（统一解释：缓存解决“时机”，BPP 决定“形态”） / 机制主线：容器允许“换对象” / 1. 方法级主线：代理替换发生在 initializeBean 的哪一步？ / 2. proxy 的两种形态与类型边界（必须会排障） / 3. self-invocation：为什么“看起来像配置问题”，本质是调用路径问题？ / 4. 必须知道的“三个替换点”（pre / early / after-init） / 5. 排障决策表（代理/增强：从“没生效”到“证据链”） ...
- 文内已出现的入口用例：SpringCoreBeansBeanCreationTraceLabTest, SpringCoreBeansProxyingPhaseLabTest, SpringCoreBeansEarlyReferenceLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization, AbstractAutowireCapableBeanFactory#doCreateBean, AbstractAutowireCapableBeanFactory#initializeBean, AbstractAutowireCapableBeanFactory#getEarlyBeanReference, InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation ...
- 跨模块互链：../../../spring-core-aop/docs/part-01-proxy-fundamentals/01-aop-proxy-mental-model.md, ../../../spring-core-aop/docs/part-02-autoproxy-and-pointcuts/01-autoproxy-creator-mainline.md
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 对跨模块链接补“跳转目的”：在链接附近用 1–2 句说明为什么此处需要 AOP/TX 视角，以及跳过去应验证的关键点（例如代理创建点/自调用行为/拦截器链顺序）。
  - 在“机制主线：容器允许“换对象””附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization），并在关键分支处点明触发条件与结果形态。
  - 在“6. 断点闭环（建议照做一次）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“2. proxy 的两种形态与类型边界（必须会排障）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
  - 代理主题建议补“Beans 侧换壳点”与“AOP 侧调用链”的最短互链：让读者能同时解释“对象何时被替换”与“为何调用时经过拦截器”。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/15-resource-injection-name-first.md

- 主题：32. `@Resource` 注入：为什么它更像“按名称找 Bean”？
- 文内结构线索：导读 / 机制主线：`@Resource` 的三个关键事实 / 机制系统阐述：条件 → 分支 → 结果 / DependencyDescriptor 深入分析：`@Resource` 的注入点语义从哪来？ / 依赖解析分支树（`@Resource` 专用简化版） / 1. 先运行实验：没有处理器时，`@Resource` 会“完全失效” / 2. 再运行实验：装上处理器后，`@Resource` 默认按字段名注入（name-first） / 3. 源码最短路径：是谁在什么时候把字段赋值的？ ...
- 文内已出现的入口用例：SpringCoreBeansResourceInjectionLabTest#withoutAnnotationConfigProcessors_resourceIsIgnored, SpringCoreBeansResourceInjectionLabTest#registerAnnotationConfigProcessors_enablesResourceAndResolvesByNameFirst, SpringCoreBeansResourceInjectionLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractAutowireCapableBeanFactory#populateBean, AbstractAutowireCapableBeanFactory#doCreateBean, DefaultListableBeanFactory#doResolveDependency, PostProcessorRegistrationDelegate#registerBeanPostProcessors, CommonAnnotationBeanPostProcessor#autowireResource ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线：`@Resource` 的三个关键事实”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractAutowireCapableBeanFactory#populateBean），并在关键分支处点明触发条件与结果形态。
  - 在“1. 先运行实验：没有处理器时，`@Resource` 会“完全失效””附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“4. Debug 断点闭环（推荐照做一次）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“5. 排障分流：三类问题，三条路”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md

- 主题：33. 候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界
- 文内结构线索：导读 / 机制主线：先问“注入的是一个，还是一组？” / 1. 方法级入口：注入是怎么进入 `doResolveDependency` 的？ / 2. 单依赖注入：胜者是怎么选出来的？ / 3. 集合注入：`@Order` 到底管什么？ / 4. 排障决策表（候选选择/排序：从异常到证据链） / 5. 断点闭环（建议照做一次） / 6. 面试常问（标准答案 + 方法级证据链） ...
- 文内已出现的入口用例：SpringCoreBeansAutowireCandidateSelectionLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, DefaultListableBeanFactory#doResolveDependency, DefaultListableBeanFactory#findAutowireCandidates, DefaultListableBeanFactory#determineAutowireCandidate, AutowiredAnnotationBeanPostProcessor#postProcessProperties, QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线：先问“注入的是一个，还是一组？””附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → DefaultListableBeanFactory#doResolveDependency），并在关键分支处点明触发条件与结果形态。
  - 在“1. 方法级入口：注入是怎么进入 `doResolveDependency` 的？”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“5. 断点闭环（建议照做一次）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“4. 排障决策表（候选选择/排序：从异常到证据链）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md

- 主题：34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast
- 文内结构线索：导读 / 机制主线：`@Value` 严不严格，取决于 resolver / 1. 先把链路拆开：`@Value` 不是“直接读 Environment” / 2. 默认行为（non-strict）：缺失占位符可能原样保留 / 3. strict fail-fast：注册 `PropertySourcesPlaceholderConfigurer`（BFPP） / 4. 默认值（强烈推荐）：`${key:default}` 让“缺失配置”可控 / 5. Debug 断点闭环：把 strict/non-strict 变成可见证据 / 6. 排障分流：先确定问题停留在“解析/求值/转换”的哪一步 ...
- 文内已出现的入口用例：SpringCoreBeansValuePlaceholderResolutionLabTest, SpringCoreBeansValuePlaceholderResolutionLabTest#defaultEmbeddedValueResolver_resolvesExistingProperty_butLeavesMissingPlaceholderUnresolved, SpringCoreBeansValuePlaceholderResolutionLabTest#propertySourcesPlaceholderConfigurer_canMakeMissingPlaceholderFailFast, SpringCoreBeansValuePlaceholderResolutionLabTest#propertySourcesPlaceholderConfigurer_strictMode_allowsMissingPlaceholderWhenDefaultValueIsProvided
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractBeanFactory#resolveEmbeddedValue, AbstractApplicationContext#prepareBeanFactory, Environment#resolvePlaceholders, PropertySourcesPlaceholderConfigurer#postProcessBeanFactory, BeanFactory#resolveEmbeddedValue ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线：`@Value` 严不严格，取决于 resolver”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractBeanFactory#resolveEmbeddedValue），并在关键分支处点明触发条件与结果形态。
  - 在“5. Debug 断点闭环：把 strict/non-strict 变成可见证据”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“6. 排障分流：先确定问题停留在“解析/求值/转换”的哪一步”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/18-merged-bean-definition.md

- 主题：35. BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？
- 文内结构线索：导读 / 机制主线 / 2. merged 到底“合并”了什么？ / 3. merged 发生在时间线哪里？ / 4. 为什么 merged 和“注入/生命周期元数据”强相关？ / 源码最短路径（call chain） / 固定观察点（watch list） / 反例（counterexample） ...
- 文内已出现的入口用例：SpringCoreBeansMergedBeanDefinitionLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractBeanFactory#getMergedLocalBeanDefinition, DefaultListableBeanFactory#getMergedBeanDefinition, AbstractBeanFactory#getMergedLocalBeanDefinition(beanName)
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractBeanFactory#getMergedLocalBeanDefinition），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“固定观察点（watch list）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md

- 主题：36. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界
- 文内结构线索：导读 / 机制主线：两条链路 + 一个决策点 / 1. 两条必须区分的链路：property values vs `@Value` / 2. 一个核心决策点：`TypeConverterDelegate#convertIfNecessary` / 3. 最小可运行实验（让“转换发生在哪”可断言） / 4. Debug 断点闭环：把“转换”从黑盒变成白盒 / 5. 排障分流：读者到底该看哪一章/哪条链？ / 6. `ConversionService` vs `PropertyEditor`：需要知道的边界 ...
- 文内已出现的入口用例：SpringCoreBeansTypeConversionLabTest, SpringCoreBeansBeansSupportUtilitiesLabTest, SpringCoreBeansTypeConversionLabTest#stringPropertyValue_canBeConvertedToIntDuringPopulateBean
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, AbstractAutowireCapableBeanFactory#populateBean, AbstractBeanFactory#resolveEmbeddedValue, AbstractAutowireCapableBeanFactory#applyPropertyValues, TypeConverterDelegate#convertIfNecessary, BeanDefinition#getPropertyValues() ...
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线：两条链路 + 一个决策点”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → AbstractAutowireCapableBeanFactory#populateBean），并在关键分支处点明触发条件与结果形态。
  - 在“3. 最小可运行实验（让“转换发生在哪”可断言）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“4. Debug 断点闭环：把“转换”从黑盒变成白盒”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“机制主线：两条链路 + 一个决策点”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-generic-type-matching-pitfalls.md

- 主题：37. 泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失
- 文内结构线索：导读 / 机制主线 / 0. 先建立一个“排障口径”：候选类型信息的三大来源 / 源码与断点 / 最小可运行实验（Lab） / 常见误区与边界 / 1. 为什么 Spring 要关心泛型？ / 2. 最小可复现：泛型信息一旦丢失，按 ResolvableType 就会失配 ...
- 文内已出现的入口用例：SpringCoreBeansGenericTypeMatchingPitfallsLabTest, SpringCoreBeansGenericTypeMatchingPitfallsLabTest#genericTypeMatching_canFailWhenTypeInfoIsLost, SpringCoreBeansGenericTypeMatchingPitfallsLabTest#genericTypeMatching_canWorkWhenCandidateKeepsGenericSignature_likeConcreteClassInstance, SpringCoreBeansGenericTypeMatchingPitfallsLabTest#genericTypeMatching_canBeRestoredByProvidingTargetTypeMetadata_evenIfRuntimeInstanceIsAProxy
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, BeanDefinition#getResolvableType, FactoryBean#getObjectType, GenericTypeAwareAutowireCandidateResolver#checkGenericTypeMatch, FactoryBean#getObjectType()
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → BeanDefinition#getResolvableType），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“0. 先建立一个“排障口径”：候选类型信息的三大来源”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-environment-and-propertysource.md

- 主题：38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线
- 文内结构线索：导读 / 机制主线 / 1. 是什么：Environment 抽象解决的是什么问题？ / 2. PropertySource 抽象：属性到底来自哪里？ / 3. `@PropertySource`：它是怎么进入 Environment 的？ / 3.1 PropertySources 的“时序边界”：什么时候加，什么时候才会生效？ / 4. 占位符解析：`@Value("${...}")` 与 Environment 的连接点 / 5. 使用方式：最小可用手段（按“排障优先级”排序） ...
- 文内已出现的入口用例：SpringCoreBeansEnvironmentPropertySourceLabTest, SpringCoreBeansProfileRegistrationLabTest, SpringCoreBeansValuePlaceholderResolutionLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, PropertySourcesPropertyResolver#getProperty, AbstractBeanFactory#resolveEmbeddedValue, Environment#getProperty(...), ConfigurationClassPostProcessor#processConfigBeanDefinitions, PropertySourceProcessor#processPropertySource
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → PropertySourcesPropertyResolver#getProperty），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“5. 使用方式：最小可用手段（按“排障优先级”排序）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
  - Environment/PropertySource 主题建议强化“顺序 + 时机”两条主因：给一个覆盖/不生效的对照场景，并指向解析入口与 propertySources 顺序的观察方式。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-beanfactory-api-deep-dive.md

- 主题：39. BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界
- 文内结构线索：导读 / 机制主线 / 1. 是什么：BeanFactory 在 Spring 体系里的位置 / 2. BeanFactory 接口族谱（在源码里看到的都从这里来） / 3. 最小容器边界：哪些能力来自 BeanFactory，哪些必须由 ApplicationContext 承接？ / 3.1 容器外对象三段能力：autowire / initialize / destroy / 4. 使用方式：在真实项目里会如何接触 BeanFactory？ / 源码与断点 ...
- 文内已出现的入口用例：SpringCoreBeansBeanFactoryApiLabTest, SpringCoreBeansBeanFactoryVsApplicationContextLabTest, SpringCoreBeansBootstrapInternalsLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, PostProcessorRegistrationDelegate#registerBeanPostProcessors, DefaultListableBeanFactory#doResolveDependency, AbstractBeanFactory#doGetBean
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → PostProcessorRegistrationDelegate#registerBeanPostProcessors），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。


## spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/01-aot-and-native-overview.md

- 主题：第 24 章：40. AOT / Native 总览：为什么“JVM 可运行”不等于“Native 可运行”
- 文内结构线索：导读 / 机制主线 / 1. 结论先行：AOT/Native 改变了什么？ / 2. 关键概念：RuntimeHints（AOT 的“契约”） / 3. 在真实项目里会遇到的典型现象（症状表） / 3.1 工程化策略：把 native 风险前置为 JVM 单测断言 / 可复现闭环（把 AOT/RuntimeHints 变成“能断言的事实”） / 源码与断点 ...
- 文内已出现的入口用例：SpringCoreBeansAotFactoriesLabTest, SpringCoreBeansAotRuntimeHintsLabTest, SpringCoreBeansRuntimeHintsBoundaryLabTest
- 文内已出现的源码入口/锚点：ApplicationContext#refresh, org.springframework.context.support.AbstractApplicationContext#refresh, org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean, RuntimeHintsRegistrar#registerHints, AotServices#factories, AotServices.Loader#load
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“可复现闭环（把 AOT/RuntimeHints 变成“能断言的事实”）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/02-runtimehints-basics.md

- 主题：41. RuntimeHints 入门：把构建期契约完成验证
- 文内结构线索：导读 / 机制主线：把“运行期能力需求”前置成“构建期契约” / 1. RuntimeHints 是什么？（需要记住的最小集合） / 2. 方法级入口：RuntimeHints 是怎么被注册/收集的？ / 3. 最小实践：用单测把契约“钉死” / 4. Debug / 断点建议：怎么把它从“黑箱”变成“可观察”？ / 5. 排障决策表（Native 异常 → 该补哪类 hints） / 6. 面试常问（标准答案 + 方法级证据链） ...
- 文内已出现的入口用例：SpringCoreBeansAotRuntimeHintsLabTest
- 文内已出现的源码入口/锚点：Class#getDeclaredMethods, Constructor#newInstance, ClassLoader#getResource, RuntimeHintsRegistrar#registerHints(RuntimeHints hints, ClassLoader classLoader)
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线：把“运行期能力需求”前置成“构建期契约””附近把关键入口串成更清晰的主线（例如：Class#getDeclaredMethods → Constructor#newInstance），并在关键分支处点明触发条件与结果形态。
  - 在“2. 方法级入口：RuntimeHints 是怎么被注册/收集的？”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“4. Debug / 断点建议：怎么把它从“黑箱”变成“可观察”？”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“5. 排障决策表（Native 异常 → 该补哪类 hints）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/03-xml-bean-definition-reader.md

- 主题：42. XML → BeanDefinitionReader：定义层解析与错误分型
- 文内结构线索：导读 / 机制主线 / 1. 结论先行：XML 的价值不在“写法”，而在“链路” / 源码与断点 / 最小可运行实验（Lab） / 2. 复现入口（可运行） / 3. 源码 / 断点建议（把“看 XML”变成“走链路”） / 常见误区与边界 ...
- 文内已出现的入口用例：SpringCoreBeansXmlBeanDefinitionReaderLabTest
- 文内已出现的源码入口/锚点：DefaultListableBeanFactory#registerBeanDefinition, AbstractApplicationContext#refresh, PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors, XmlBeanDefinitionReader#loadBeanDefinitions, DefaultBeanDefinitionDocumentReader#registerBeanDefinitions, BeanDefinitionParserDelegate#parseBeanDefinitionElement
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：DefaultListableBeanFactory#registerBeanDefinition → AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/04-autowirecapablebeanfactory-external-objects.md

- 主题：43. 容器外对象注入：AutowireCapableBeanFactory
- 文内结构线索：导读 / 机制主线 / 集成案例（真实项目高频）：第三方回调对象如何“补齐注入” / 1. 结论先行：注入 ≠ 生命周期托管 ≠ 代理替换 / 源码与断点 / 最小可运行实验（Lab） / 2. 复现入口（可运行） / 3. 源码 / 断点建议（把“容器外对象”放回统一生命周期主线） ...
- 文内已出现的入口用例：SpringCoreBeansAutowireCapableBeanFactoryLabTest
- 文内已出现的源码入口/锚点：AbstractAutowireCapableBeanFactory#populateBean, AbstractAutowireCapableBeanFactory#initializeBean, AutowireCapableBeanFactory#initializeBean, AutowireCapableBeanFactory#autowireBean, AutowireCapableBeanFactory#destroyBean
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：AbstractAutowireCapableBeanFactory#populateBean → AbstractAutowireCapableBeanFactory#initializeBean），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/05-spel-and-value-expression.md

- 主题：44. SpEL 与 `@Value("#{...}")`：表达式解析链路
- 文内结构线索：导读 / 机制主线 / 1. 先运行 Lab：把“链路拆分”固定成断言 / 2. 源码最短路径（call chain）：从 @Value 到最终注入 / 3. 三连排障（强烈推荐把这张表背下来） / 4. 断点闭环（把“解析/求值/转换”三段分别观察到） / 常见误区与边界 / 源码调用链（方法级）：`@Value` 的“三连”在哪里发生 ...
- 文内已出现的入口用例：SpringCoreBeansSpelValueLabTest, SpringCoreBeansValuePlaceholderResolutionLabTest, SpringCoreBeansSpelValueLabTest#valueWithSpel_canReferenceBeanAndResultIsConvertedToTargetType, SpringCoreBeansSpelValueLabTest#spelCanComposeWithPlaceholderResolution_placeholdersResolveFirst_thenExpressionIsEvaluated, SpringCoreBeansSpelValueLabTest#spelEvaluationMaySucceedButTypeConversionMayFail_whenInjectingIntoPrimitiveType
- 文内已出现的源码入口/锚点：AbstractBeanFactory#resolveEmbeddedValue, BeanFactory#resolveEmbeddedValue, SpringCoreBeansSpelValueLabTest#valueWithSpel_canReferenceBeanAndResultIsConvertedToTargetType, SpringCoreBeansSpelValueLabTest#spelCanComposeWithPlaceholderResolution_placeholdersResolveFirst_thenExpressionIsEvaluated, SpringCoreBeansSpelValueLabTest#spelEvaluationMaySucceedButTypeConversionMayFail_whenInjectingIntoPrimitiveType
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：AbstractBeanFactory#resolveEmbeddedValue → BeanFactory#resolveEmbeddedValue），并在关键分支处点明触发条件与结果形态。
  - 在“4. 断点闭环（把“解析/求值/转换”三段分别观察到）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“3. 三连排障（强烈推荐把这张表背下来）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/06-custom-qualifier-meta-annotation.md

- 主题：45. 自定义 Qualifier：meta-annotation 与候选收敛
- 文内结构线索：导读 / 机制主线 / 1. 结论先行：自定义 Qualifier 的本质 / DependencyDescriptor 深入分析：注入点语义决定“Qualifier 是否生效” / 依赖解析分支树（简化版） / 关键变量（断点里只看这些） / 源码与断点 / 最小可运行实验（Lab） ...
- 文内已出现的入口用例：SpringCoreBeansCustomQualifierLabTest
- 文内已出现的源码入口/锚点：DefaultListableBeanFactory#findAutowireCandidates, DefaultListableBeanFactory#determineAutowireCandidate, QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：DefaultListableBeanFactory#findAutowireCandidates → DefaultListableBeanFactory#determineAutowireCandidate），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“关键变量（断点里只看这些）”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/07-xml-namespace-extension.md

- 主题：46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers
- 文内结构线索：导读 / 机制主线 / 1. 是什么：namespace 扩展解决的是什么问题？ / 2. 使用方式：最小可用写法（需要的最小 4 件套） / 3. 原理：把自定义元素放回容器定义层主线 / 错误分型（快速判断） / 源码与断点 / 最小可运行实验（Lab） ...
- 文内已出现的入口用例：SpringCoreBeansXmlNamespaceExtensionLabTest
- 文内已出现的源码入口/锚点：DefaultListableBeanFactory#registerBeanDefinition, BeanDefinitionParserDelegate#parseCustomElement, XmlBeanDefinitionReader#doLoadBeanDefinitions, DefaultBeanDefinitionDocumentReader#parseBeanDefinitions, DefaultNamespaceHandlerResolver#resolve
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：DefaultListableBeanFactory#registerBeanDefinition → BeanDefinitionParserDelegate#parseCustomElement），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/08-beandefinitionreader-other-inputs-properties-groovy.md

- 主题：47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy
- 文内结构线索：导读 / 机制主线 / 1. 是什么：为什么要有 BeanDefinitionReader 家族？ / 2. 使用方式：两种典型 reader 的最小闭环 / 3. 原理：Reader 把“输入”落到定义层主线的哪个位置？ / 源码与断点 / 最小可运行实验（Lab） / 0. 复现入口（可运行） ...
- 文内已出现的入口用例：SpringCoreBeansPropertiesBeanDefinitionReaderLabTest#propertiesBeanDefinitionReader_registersBeanDefinitions_fromPropertiesFile, SpringCoreBeansGroovyBeanDefinitionReaderLabTest#groovyBeanDefinitionReader_registersBeanDefinitions_fromGroovyScript, SpringCoreBeansGroovyBeanDefinitionReaderLabTest, SpringCoreBeansPropertiesBeanDefinitionReaderLabTest
- 文内已出现的源码入口/锚点：DefaultListableBeanFactory#registerBeanDefinition, SpringCoreBeansPropertiesBeanDefinitionReaderLabTest#propertiesBeanDefinitionReader_registersBeanDefinitions_fromPropertiesFile, SpringCoreBeansGroovyBeanDefinitionReaderLabTest#groovyBeanDefinitionReader_registersBeanDefinitions_fromGroovyScript, AbstractBeanDefinitionReader#loadBeanDefinitions, PropertiesBeanDefinitionReader#loadBeanDefinitions
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：DefaultListableBeanFactory#registerBeanDefinition → SpringCoreBeansPropertiesBeanDefinitionReaderLabTest#propertiesBeanDefinitionReader_registersBeanDefinitions_fromPropertiesFile），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/09-method-injection-replaced-method.md

- 主题：48. 方法注入（Method Injection）：replaced-method / MethodReplacer
- 文内结构线索：导读 / 机制主线 / 1. 是什么：它解决什么问题？不解决什么问题？ / 2. 使用方式：最小可用写法（XML） / 2.1 与 `@Lookup` 的差异与选型 / 2.2 AOT/Native 风险与替代 / 3. 原理：把现象放回容器主线（它发生在哪个阶段？） / 源码与断点 ...
- 文内已出现的入口用例：SpringCoreBeansReplacedMethodLabTest#replacedMethod_overridesTargetMethodViaCglibSubclassing_andIsVisibleInBeanDefinitionMethodOverrides, SpringCoreBeansReplacedMethodLabTest
- 文内已出现的源码入口/锚点：AbstractAutowireCapableBeanFactory#instantiateWithMethodInjection, SpringCoreBeansReplacedMethodLabTest#replacedMethod_overridesTargetMethodViaCglibSubclassing_andIsVisibleInBeanDefinitionMethodOverrides, AbstractBeanDefinition#getMethodOverrides
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：AbstractAutowireCapableBeanFactory#instantiateWithMethodInjection → SpringCoreBeansReplacedMethodLabTest#replacedMethod_overridesTargetMethodViaCglibSubclassing_andIsVisibleInBeanDefinitionMethodOverrides），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/10-built-in-factorybeans-gallery.md

- 主题：49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀
- 文内结构线索：导读 / 机制主线 / 1. 是什么：内置 FactoryBean 解决的是什么问题？ / 2. 使用方式：两类最常见的内置 FactoryBean（最小可用心智） / 3. 原理：把 `&beanName` 与 product 缓存放回容器主线 / 3.1 FactoryBean 与代理/循环依赖的交叉边界 / 源码与断点 / 最小可运行实验（Lab） ...
- 文内已出现的入口用例：SpringCoreBeansBuiltInFactoryBeansLabTest, SpringCoreBeansServiceLoaderFactoryBeansLabTest, SpringCoreBeansBuiltInFactoryBeansLabTest#builtInFactoryBeans_methodInvoking_and_serviceLocator_and_factoryDereference, SpringCoreBeansServiceLoaderFactoryBeansLabTest#serviceListFactoryBean_loadsProviders_fromMetaInfServices, SpringCoreBeansServiceLoaderFactoryBeansLabTest#serviceLoaderFactoryBean_exposesRawServiceLoader
- 文内已出现的源码入口/锚点：AbstractBeanFactory#getObjectForBeanInstance, AbstractAutowireCapableBeanFactory#getEarlyBeanReference, AbstractBeanFactory#doGetBean, FactoryBeanRegistrySupport#getObjectFromFactoryBean, BeanFactory#getBean(...), FactoryBean#isSingleton ...
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：AbstractBeanFactory#getObjectForBeanInstance → AbstractAutowireCapableBeanFactory#getEarlyBeanReference），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“常见误区与边界”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
  - FactoryBean 主题建议强化“产品/工厂”边界的可验证路径：提示读者用 `&` 前缀、按类型发现与缓存命中点去自证判断。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/11-property-editor-and-value-resolution.md

- 主题：50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象
- 文内结构线索：导读 / 机制主线 / 0. `${...}` vs `#{...}` 的职责边界（先分清再排障） / 1. 是什么：需要分清 2 个“发生位置不同”的问题 / 3. 原理：把现象放回容器主线（定义层 → 实例层） / 源码与断点 / 最小可运行实验（Lab） / 0. 复现入口（可运行） ...
- 文内已出现的入口用例：SpringCoreBeansBeanDefinitionValueResolutionLabTest, SpringCoreBeansPropertyEditorLabTest, SpringCoreBeansPropertyEditorLabTest#HostAndPortEditor, SpringCoreBeansPropertyEditorLabTest#HostAndPortRegistrar, SpringCoreBeansPropertyEditorLabTest#withCustomPropertyEditor_stringToCustomType_shouldSucceed, SpringCoreBeansBeanDefinitionValueResolutionLabTest#registerDemoBean
- 文内已出现的源码入口/锚点：BeanDefinitionValueResolver#resolveValueIfNecessary, CustomEditorConfigurer#postProcessBeanFactory, PropertyEditorRegistrySupport#registerCustomEditor, BeanDefinitionValueResolver#resolveReference
- 注意：该文档存在占位/待补痕迹（建议在执行时优先清理）
- 注意：该文档包含 `AE-DEEPENING` 块（如需降模板化，可按章改写）
- 继续深化策略：
  - 清理占位/未完/待补痕迹：用本章主题下可验证的解释替代（不要保留“以后再写”）。
  - 将 `AE-DEEPENING` 提示从“维度清单”改为更贴合本章的“继续深挖路线”：只保留读者最常遇到的 2–3 个卡点，并说明各自如何用现有用例/断点验证。
  - 在“机制主线”附近把关键入口串成更清晰的主线（例如：BeanDefinitionValueResolver#resolveValueIfNecessary → CustomEditorConfigurer#postProcessBeanFactory），并在关键分支处点明触发条件与结果形态。
  - 在“最小可运行实验（Lab）”附近把用例与论点绑定：明确跑完哪个测试方法后，应去哪个入口方法断点验证哪一个结论，避免“跑了但不知道证明了什么”。
  - 在“源码与断点”附近把断点建议写得更可操作：哪些断点用于确认哪个分支/状态变化，避免只列方法名。
  - 在“0. `${...}` vs `#{...}` 的职责边界（先分清再排障）”附近把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
