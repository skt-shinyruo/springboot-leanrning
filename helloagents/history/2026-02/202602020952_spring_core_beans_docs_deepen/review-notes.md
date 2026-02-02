# spring-core-beans：逐章评审笔记（按章内容定制，不套固定模板）

> 说明：这是“先评审后落盘”的阶段产物。每一节都是针对该章现有内容给出的具体补充/完善/深入点（偏机制 + 源码入口 + 排障）。

## 版本语境（用于官方文档对照）

- Spring Boot：`3.5.9`
- Spring Framework：`6.2.x`（本仓库索引基线：`6.2.15`）

---

## `spring-core-modules/spring-core-beans/README.md`

- 章节标题：spring-core-beans

- 把“本模块基线版本（Boot/Framework）”放到更显眼的位置：读者遇到版本差异时能第一时间对齐语境。
- 将“从现象进入”的路径再缩短：给出 2–3 条最常见现象的第一跳（对应章节 + 第一断点），避免读者在目录里来回跳。
- 把“跨模块跳转”写成“跳过去验证什么”：例如 Beans → AOP/Tx/Validation/Boot（明确验证点，不堆链接）。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/README.md`

- 章节标题：spring-core-beans 文档导航（Docs TOC）

- 把“本模块基线版本（Boot/Framework）”放到更显眼的位置：读者遇到版本差异时能第一时间对齐语境。
- 将“从现象进入”的路径再缩短：给出 2–3 条最常见现象的第一跳（对应章节 + 第一断点），避免读者在目录里来回跳。
- 把“跨模块跳转”写成“跳过去验证什么”：例如 Beans → AOP/Tx/Validation/Boot（明确验证点，不堆链接）。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/appendix/025-90-common-pitfalls.md`

- 章节标题：第 25 章：90. 常见误区清单（建议反复对照）
- 卡片“知识点”：常见误区清单（建议反复对照）
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 附录/索引型章节建议强化“怎么用”：从异常栈/类名进入→在索引定位→跳到章节+Lab+断点验证，而不是把它当成背诵材料。
- 如果是快照类文件（例如 API index/gap），建议在开头更明显地写清“这是快照/不自动更新/如何重新生成（如果未来需要）”。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/appendix/026-99-self-check.md`

- 章节标题：第 26 章：99. 自测题：是否能够真的理解了？
- 卡片“知识点”：自测题：是否能够真的理解了？
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/appendix/91-glossary.md`

- 章节标题：91. 术语表（Glossary）
- 卡片“知识点”：91. 术语表（Glossary）
- 卡片“源码入口”：`@Value("#{...}")` / `DefaultListableBeanFactory#registerBeanDefinition` / `DefaultSingletonBeanRegistry#getSingleton`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 附录/索引型章节建议强化“怎么用”：从异常栈/类名进入→在索引定位→跳到章节+Lab+断点验证，而不是把它当成背诵材料。
- 如果是快照类文件（例如 API index/gap），建议在开头更明显地写清“这是快照/不自动更新/如何重新生成（如果未来需要）”。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/appendix/92-knowledge-map.md`

- 章节标题：92. 知识地图（Knowledge Map）：从现象直达章节/断点/Lab
- 卡片“知识点”：知识地图（从现象直达章节/断点/Lab）
- 卡片“源码入口”：`DefaultSingletonBeanRegistry#getSingleton` / `CommonAnnotationBeanPostProcessor#postProcessProperties` / `AbstractBeanFactory#resolveEmbeddedValue`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 附录/索引型章节建议强化“怎么用”：从异常栈/类名进入→在索引定位→跳到章节+Lab+断点验证，而不是把它当成背诵材料。
- 如果是快照类文件（例如 API index/gap），建议在开头更明显地写清“这是快照/不自动更新/如何重新生成（如果未来需要）”。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/appendix/93-interview-playbook.md`

- 章节标题：93. 面试复述模板（Interview Playbook）：用“证据链”回答 Spring IoC
- 卡片“知识点”：面试复述模板：用“证据链”回答 Spring IoC
- 卡片“源码入口”：`ApplicationContext#refresh` / `AbstractApplicationContext#refresh` / `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 附录/索引型章节建议强化“怎么用”：从异常栈/类名进入→在索引定位→跳到章节+Lab+断点验证，而不是把它当成背诵材料。
- 如果是快照类文件（例如 API index/gap），建议在开头更明显地写清“这是快照/不自动更新/如何重新生成（如果未来需要）”。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/appendix/94-production-troubleshooting-checklist.md`

- 章节标题：94. 生产排障清单（Troubleshooting Checklist）：从症状到证据链
- 卡片“知识点”：生产排障清单：从症状到证据链
- 卡片“源码入口”：`DefaultListableBeanFactory#registerBeanDefinition` / `DefaultListableBeanFactory#doResolveDependency` / `DefaultSingletonBeanRegistry#getSingleton`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 附录/索引型章节建议强化“怎么用”：从异常栈/类名进入→在索引定位→跳到章节+Lab+断点验证，而不是把它当成背诵材料。
- 如果是快照类文件（例如 API index/gap），建议在开头更明显地写清“这是快照/不自动更新/如何重新生成（如果未来需要）”。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/appendix/95-spring-beans-public-api-index.md`

- 章节标题：95. spring-beans Public API 索引（Spring Framework 6.2.15）
- 卡片“知识点”：spring-beans Public API Index（索引）
- 卡片“源码入口”：`org.springframework.beans.factory.BeanFactory` / `org.springframework.beans.factory.support.DefaultListableBeanFactory#getBean`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 附录/索引型章节建议强化“怎么用”：从异常栈/类名进入→在索引定位→跳到章节+Lab+断点验证，而不是把它当成背诵材料。
- 如果是快照类文件（例如 API index/gap），建议在开头更明显地写清“这是快照/不自动更新/如何重新生成（如果未来需要）”。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/appendix/96-spring-beans-public-api-gap.md`

- 章节标题：96. spring-beans Public API Gap 清单（按包/机制域分批深化）
- 卡片“知识点”：Public API Gap 清单（按包/机制域分批深化）
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 附录/索引型章节建议强化“怎么用”：从异常栈/类名进入→在索引定位→跳到章节+Lab+断点验证，而不是把它当成背诵材料。
- 如果是快照类文件（例如 API index/gap），建议在开头更明显地写清“这是快照/不自动更新/如何重新生成（如果未来需要）”。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/appendix/97-explore-debug-tests.md`

- 章节标题：97. Explore/Debug 用例（可选启用，不影响默认回归）
- 卡片“知识点”：Explore/Debug 用例：如何开启、看什么、怎么把观察结果“用回主线”
- 卡片“源码入口”：`DefaultSingletonBeanRegistry#getSingleton` / `DefaultSingletonBeanRegistry#addSingleton` / `DefaultSingletonBeanRegistry#addSingletonFactory`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/appendix/98-debugger-pack.md`

- 章节标题：98. Debugger Pack（断点包总入口）
- 卡片“知识点”：Debugger Pack（断点包总入口）
- 卡片“源码入口”：`AbstractApplicationContext#refresh` / `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` / `PostProcessorRegistrationDelegate#registerBeanPostProcessors`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/appendix/99-team-training-kit.md`

- 章节标题：99. 团队内训讲义（Training Kit）：可直接用于授课的课时脚本
- 卡片“知识点”：团队内训讲义（可直接用于授课的课时脚本）
- 卡片“源码入口”：`DefaultListableBeanFactory#doResolveDependency` / `DefaultListableBeanFactory#determineAutowireCandidate` / `AbstractAutowireCapableBeanFactory#populateBean`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/deepening-strategies/README.md`

- 章节标题：spring-core-beans：内容级再加深策略（按章节）

- 这类文件本质是“行动指南”：建议把每条建议都落到“关键分支 + 固定 watch list + 1 个边界反例（可复现）”，减少只写“跑哪个 Lab”。
- 对跨模块跳转（Beans→AOP/TX/Validation/Boot）建议补“为什么要跳、跳过去看哪条证据链”，避免变成链接集合。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/deepening-strategies/appendix.md`

- 章节标题：逐章内容级再加深建议（appendix 工具章节）

- 这类文件本质是“行动指南”：建议把每条建议都落到“关键分支 + 固定 watch list + 1 个边界反例（可复现）”，减少只写“跑哪个 Lab”。
- 对跨模块跳转（Beans→AOP/TX/Validation/Boot）建议补“为什么要跳、跳过去看哪条证据链”，避免变成链接集合。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/deepening-strategies/docs-root.md`

- 章节标题：逐章内容级再加深建议（Docs TOC / 目录页）

- 这类文件本质是“行动指南”：建议把每条建议都落到“关键分支 + 固定 watch list + 1 个边界反例（可复现）”，减少只写“跑哪个 Lab”。
- 对跨模块跳转（Beans→AOP/TX/Validation/Boot）建议补“为什么要跳、跳过去看哪条证据链”，避免变成链接集合。

## `spring-core-modules/spring-core-beans/docs/deepening-strategies/module-readme.md`

- 章节标题：逐章内容级再加深建议（模块 README）

- 这类文件本质是“行动指南”：建议把每条建议都落到“关键分支 + 固定 watch list + 1 个边界反例（可复现）”，减少只写“跑哪个 Lab”。
- 对跨模块跳转（Beans→AOP/TX/Validation/Boot）建议补“为什么要跳、跳过去看哪条证据链”，避免变成链接集合。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-00-guide.md`

- 章节标题：逐章内容级再加深建议（part-00-guide 指南）

- 这类文件本质是“行动指南”：建议把每条建议都落到“关键分支 + 固定 watch list + 1 个边界反例（可复现）”，减少只写“跑哪个 Lab”。
- 对跨模块跳转（Beans→AOP/TX/Validation/Boot）建议补“为什么要跳、跳过去看哪条证据链”，避免变成链接集合。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-01-ioc-container.md`

- 章节标题：逐章内容级再加深建议（part-01-ioc-container）

- 这类文件本质是“行动指南”：建议把每条建议都落到“关键分支 + 固定 watch list + 1 个边界反例（可复现）”，减少只写“跑哪个 Lab”。
- 对跨模块跳转（Beans→AOP/TX/Validation/Boot）建议补“为什么要跳、跳过去看哪条证据链”，避免变成链接集合。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-02-boot-autoconfig.md`

- 章节标题：逐章内容级再加深建议（part-02-boot-autoconfig）

- 这类文件本质是“行动指南”：建议把每条建议都落到“关键分支 + 固定 watch list + 1 个边界反例（可复现）”，减少只写“跑哪个 Lab”。
- 对跨模块跳转（Beans→AOP/TX/Validation/Boot）建议补“为什么要跳、跳过去看哪条证据链”，避免变成链接集合。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-03-container-internals.md`

- 章节标题：逐章内容级再加深建议（part-03-container-internals）

- 这类文件本质是“行动指南”：建议把每条建议都落到“关键分支 + 固定 watch list + 1 个边界反例（可复现）”，减少只写“跑哪个 Lab”。
- 对跨模块跳转（Beans→AOP/TX/Validation/Boot）建议补“为什么要跳、跳过去看哪条证据链”，避免变成链接集合。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-04-wiring-and-boundaries.md`

- 章节标题：逐章内容级再加深建议（part-04-wiring-and-boundaries）

- 这类文件本质是“行动指南”：建议把每条建议都落到“关键分支 + 固定 watch list + 1 个边界反例（可复现）”，减少只写“跑哪个 Lab”。
- 对跨模块跳转（Beans→AOP/TX/Validation/Boot）建议补“为什么要跳、跳过去看哪条证据链”，避免变成链接集合。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-05-aot-and-real-world.md`

- 章节标题：逐章内容级再加深建议（part-05-aot-and-real-world）

- 这类文件本质是“行动指南”：建议把每条建议都落到“关键分支 + 固定 watch list + 1 个边界反例（可复现）”，减少只写“跑哪个 Lab”。
- 对跨模块跳转（Beans→AOP/TX/Validation/Boot）建议补“为什么要跳、跳过去看哪条证据链”，避免变成链接集合。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/aot.html
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/

## `spring-core-modules/spring-core-beans/docs/part-00-guide/009-00-why-index.md`

- 章节标题：第 09 章：00. 基础问题索引（Why Index）：把高频“为什么”做成可验证闭环
- 卡片“知识点”：009-00-why-index
- 卡片“源码入口”：`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton` / `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#addSingletonFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#getEarlyBeanReference`

- 建议补一段“从异常/现象到第一断点”的最短分流（两三条就够），让读者能把问题先分到定义层/实例层/最终暴露对象。
- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-00-guide/010-03-mainline-timeline.md`

- 章节标题：第 10 章：主线时间线：IoC 容器从 refresh 到创建 Bean
- 卡片“知识点”：主线时间线：IoC 容器从 refresh 到创建 Bean
- 卡片“源码入口”：`AbstractApplicationContext#refresh` / `AbstractApplicationContext#prepareBeanFactory` / `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-00-guide/011-00-deep-dive-guide.md`

- 章节标题：第 11 章：00. 深入分析指南：将“Bean 三层模型”落实到源码与断点
- 卡片“知识点”：深入分析指南：将“Bean 三层模型”落实到源码与断点
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-00-guide/011-04-branch-decision-matrix.md`

- 章节标题：第 11 章：关键分支矩阵（Branch Decision Matrix）
- 卡片“知识点”：关键分支矩阵（Branch Decision Matrix）
- 卡片“源码入口”：`DefaultListableBeanFactory#doResolveDependency` / `CommonAnnotationBeanPostProcessor#postProcessProperties` / `AbstractBeanFactory#resolveEmbeddedValue`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-00-guide/012-01-quickstart-30min.md`

- 章节标题：第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）
- 卡片“知识点”：30 分钟快速闭环：先快后深（3 个最小实验入口）
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-00-guide/013-01-applicationcontext-refresh-call-chain.md`

- 章节标题：第 13 章：01：`refresh()` 调用链（容器从“定义”到“实例”的主线）
- 卡片“知识点”：01：`refresh()` 调用链（容器从“定义”到“实例”的主线）
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.context.support.PostProcessorRegistrationDelegate` / `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-00-guide/013-02-breakpoint-map.md`

- 章节标题：第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）
- 卡片“知识点”：断点地图（容器主线：可复用断点/观察点清单）
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`

- 章节标题：第 14 章：03. 依赖注入解析：类型/名称/@Qualifier/@Primary
- 卡片“知识点”：依赖注入解析：类型/名称/@Qualifier/@Primary
- 卡片“源码入口”：`org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` / `#findAutowireCandidates` / `#determineAutowireCandidate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 依赖解析建议突出 DependencyDescriptor 的“需求表达”：required / annotations / resolvableType / dependencyName 这几个字段如何直接影响候选收集与收敛。
- 建议补“早返回通道”对照：resolvableDependencies / @Value(suggestedValue) / 注入点 @Lazy / Optional|ObjectProvider|Stream（它们为什么不会走“选唯一候选”）。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/java.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

## `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md`

- 章节标题：第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）
- 卡片“知识点”：Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把 prototype “像单例”的误判直接落到 doGetBean 的调用时机：注入点只获取一次 vs 运行时按需获取。
- 如果涉及 scoped proxy，建议明确“双定义”的可观测信号：beanName 对应代理，scopedTarget.* 对应真实目标；并补一个“如何在断点里一眼识别”的提示。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html

## `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md`

- 章节标题：第 16 章：05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）
- 卡片“知识点”：Bean 生命周期骨架（instantiate→populate→initialize→destroy）；初始化回调链（Aware / BPP / `@PostConstruct` / `afterPropertiesSet` / `initMethod` / after-init proxy）；销毁链路（DestructionAwareBPP / `@PreDestroy` / `DisposableBean` / `destroyMethod`）；Scope 语义（prototype 默认不自动销毁）；容器级生命周期钩子（`SmartInitializingSingleton` / `SmartLifecycle` / refresh 事件）。
- 卡片“源码入口”：`AbstractAutowireCapableBeanFactory#doCreateBean` / `#populateBean` / `#initializeBean` / `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization` / `DefaultSingletonBeanRegistry#destroySingletons` / `DisposableBeanAdapter#destroy`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 生命周期章节适合把回调顺序落到方法级证据链：invokeAwareMethods → applyBPPBeforeInit → invokeInitMethods → applyBPPAfterInit → registerDisposableBean，并提示“在哪一步可能被替换成 proxy”。
- 建议补一个“为什么回调没执行”的排障分流：是否 lazy、是否 prototype、是否 pre-instantiation short-circuit、是否容器外对象（AutowireCapableBeanFactory）。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md`

- 章节标题：第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）
- 卡片“知识点”：容器扩展点：BFPP vs BPP（以及它们能/不能做什么）
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议用“能改什么 + 发生在哪一步”解释 BFPP/BDRPP/BPP：把它们放回 refresh 的时间线，读者会更快建立阶段意识。
- 顺序语义（PriorityOrdered/Ordered/others）建议补一个真实案例：顺序变化如何导致占位符解析、代理增强或 beanDefinition 修改的结果不同。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/java.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

## `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/018-07-configuration-enhancement.md`

- 章节标题：第 18 章：07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）
- 卡片“知识点”：`@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- @Configuration 相关章节建议拆清两条路径：方法内直接调用 vs 参数注入（容器解析），并用对照实验说明 proxyBeanMethods=true/false 下的差异点。
- 建议补“常见误判”：关闭 proxyBeanMethods 不等于破坏 singleton，真正的分叉在“是否经过容器解析/注入链路”。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/java.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

## `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`

- 章节标题：02. Bean 注册入口：扫描、@Bean、@Import、registrar（已合并）
- 卡片“知识点”：Bean 注册入口：扫描、@Bean、@Import、registrar
- 卡片“源码入口”：`DefaultListableBeanFactory#registerBeanDefinition` / `DefaultSingletonBeanRegistry#registerSingleton` / `ClassPathBeanDefinitionScanner#doScan`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 注册入口建议拆成“扫描 / @Bean / @Import(Selector/Registrar) / 编程式注册”四条线，并明确各自落点：最终都落到 registry 的 registerBeanDefinition，但上游路径完全不同。
- 建议补“最短差分法”：同一个场景分别用扫描与 @Bean 注册，导出 beanName 列表做 diff，再回到 ConfigurationClassPostProcessor 与扫描器入口解释差异。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/java.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

## `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md`

- 章节标题：第 20 章：01. Bean 运行机制：从 BeanDefinition 到最终暴露对象
- 卡片“知识点”：Bean 运行机制：从 BeanDefinition 到最终暴露对象
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-factorybean.md`

- 章节标题：08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）
- 卡片“知识点”：08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）
- 卡片“源码入口”：`FactoryBean#getObject()` / `AbstractBeanFactory#getObjectForBeanInstance` / `FactoryBean#isSingleton()`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- FactoryBean 建议补“类型匹配坑”：getObjectType() 返回 null/误报如何影响候选收集（尤其是泛型 + FactoryBean 叠加）。
- 建议补缓存语义的断点闭环：FactoryBean 本身缓存 vs product 缓存不是一回事；用 getObjectForBeanInstance 与相关缓存变量把差异讲清。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`

- 章节标题：09. 循环依赖：现象、原因与规避（constructor vs setter）
- 卡片“知识点”：循环依赖：现象、原因与规避（constructor vs setter）
- 卡片“源码入口”：`ConstructorResolver#autowireConstructor` / `AbstractAutowireCapableBeanFactory#populateBean` / `SpringCoreBeansContainerLabTest#circularDependencyWithConstructorsFailsFast`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 循环依赖建议把“三层缓存”从字段名升级为语义：final/early/factory 三类对象各解决什么问题；并明确“救得了/救不了”的条件。
- raw vs wrapped 相关建议把触发条件写清：哪一步把 exposedObject 变了（BPP），以及 allowRawInjectionDespiteWrapping 两个开关分别在保护什么风险。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md`

- 章节标题：第 19 章：11. 调试与自检：如何“观察到”容器正在做什么
- 卡片“知识点”：调试与自检：如何“观察到”容器正在做什么
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/020-09-auto-config-ordering.md`

- 章节标题：09. Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？
- 卡片“知识点”：Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？
- 卡片“源码入口”：`AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `ConfigurationClassPostProcessor#processConfigBeanDefinitions`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- Boot 自动装配章节建议给出一条最短调用链：导入候选 → 配置类解析 → 条件评估 → 注册 BeanDefinition，并给出 ConditionEvaluationReport 的可观测证据。
- 顺序相关建议把维度拆开：Import 顺序 / processor 顺序 / bean 初始化顺序，避免把“没生效”都归因到 @Order。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md`

- 章节标题：第 21 章：10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）
- 卡片“知识点”：Spring Boot 自动装配如何影响 Bean（Auto-configuration）
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- Boot 自动装配章节建议给出一条最短调用链：导入候选 → 配置类解析 → 条件评估 → 注册 BeanDefinition，并给出 ConditionEvaluationReport 的可观测证据。
- 顺序相关建议把维度拆开：Import 顺序 / processor 顺序 / bean 初始化顺序，避免把“没生效”都归因到 @Order。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md`

- 章节标题：第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？
- 卡片“知识点”：容器启动与基础设施处理器：为什么注解能工作？
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-03-container-internals/13-bdrpp-definition-registration.md`

- 章节标题：13. BeanDefinitionRegistryPostProcessor：在“注册阶段”动态加定义
- 卡片“知识点”：13. BeanDefinitionRegistryPostProcessor：在“注册阶段”动态加定义
- 卡片“源码入口”：`PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` / `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry` / `DefaultListableBeanFactory#registerBeanDefinition`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议用“能改什么 + 发生在哪一步”解释 BFPP/BDRPP/BPP：把它们放回 refresh 的时间线，读者会更快建立阶段意识。
- 顺序语义（PriorityOrdered/Ordered/others）建议补一个真实案例：顺序变化如何导致占位符解析、代理增强或 beanDefinition 修改的结果不同。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/java.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

## `spring-core-modules/spring-core-beans/docs/part-03-container-internals/14-post-processor-ordering.md`

- 章节标题：14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序
- 卡片“知识点”：14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序
- 卡片“源码入口”：`Ordered#getOrder()` / `PostProcessorRegistrationDelegate#sortPostProcessors` / `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-03-container-internals/15-pre-instantiation-short-circuit.md`

- 章节标题：15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行
- 卡片“知识点”：15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行
- 卡片“源码入口”：`InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation` / `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md`

- 章节标题：16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？
- 卡片“知识点”：early reference 与循环依赖：getEarlyBeanReference 到底解决什么？
- 卡片“源码入口”：`DefaultSingletonBeanRegistry#getSingleton` / `AbstractAutowireCapableBeanFactory#getEarlyBeanReference` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 循环依赖建议把“三层缓存”从字段名升级为语义：final/early/factory 三类对象各解决什么问题；并明确“救得了/救不了”的条件。
- raw vs wrapped 相关建议把触发条件写清：哪一步把 exposedObject 变了（BPP），以及 allowRawInjectionDespiteWrapping 两个开关分别在保护什么风险。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-03-container-internals/17-lifecycle-callback-order.md`

- 章节标题：17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）
- 卡片“知识点”：17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）
- 卡片“源码入口”：`BeanPostProcessor#postProcessBeforeInitialization` / `InitializingBean#afterPropertiesSet` / `BeanPostProcessor#postProcessAfterInitialization`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把 prototype “像单例”的误判直接落到 doGetBean 的调用时机：注入点只获取一次 vs 运行时按需获取。
- 如果涉及 scoped proxy，建议明确“双定义”的可观测信号：beanName 对应代理，scopedTarget.* 对应真实目标；并补一个“如何在断点里一眼识别”的提示。
- 生命周期章节适合把回调顺序落到方法级证据链：invokeAwareMethods → applyBPPBeforeInit → invokeInitMethods → applyBPPAfterInit → registerDisposableBean，并提示“在哪一步可能被替换成 proxy”。
- 建议补一个“为什么回调没执行”的排障分流：是否 lazy、是否 prototype、是否 pre-instantiation short-circuit、是否容器外对象（AutowireCapableBeanFactory）。
- 建议用“能改什么 + 发生在哪一步”解释 BFPP/BDRPP/BPP：把它们放回 refresh 的时间线，读者会更快建立阶段意识。
- 顺序语义（PriorityOrdered/Ordered/others）建议补一个真实案例：顺序变化如何导致占位符解析、代理增强或 beanDefinition 修改的结果不同。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/java.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

## `spring-core-modules/spring-core-beans/docs/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`

- 章节标题：18. 从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）
- 卡片“知识点”：把 `ApplicationContext#refresh` 的“定义阶段”与“创建阶段”连成一条可设置断点的主线
- 卡片“源码入口”：`ApplicationContext#refresh` / `AbstractApplicationContext#refresh` / `AbstractBeanFactory#doGetBean`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/023-18-lazy-semantics.md`

- 章节标题：第 23 章：18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）
- 卡片“知识点”：Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/19-depends-on.md`

- 章节标题：19. dependsOn：强制初始化顺序（即使没有显式依赖）
- 卡片“知识点”：dependsOn：强制初始化顺序（即使没有显式依赖）
- 卡片“源码入口”：`AbstractBeanFactory#doGetBean` / `AbstractApplicationContext#refresh` / `DefaultListableBeanFactory#preInstantiateSingletons`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-resolvable-dependency.md`

- 章节标题：20. registerResolvableDependency：能注入，但它不是 Bean
- 卡片“知识点”：registerResolvableDependency：能注入，但它不是 Bean
- 卡片“源码入口”：`DefaultListableBeanFactory#resolvableDependencies` / `DefaultListableBeanFactory#doResolveDependency` / `DefaultListableBeanFactory#resolveDependency`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 依赖解析建议突出 DependencyDescriptor 的“需求表达”：required / annotations / resolvableType / dependencyName 这几个字段如何直接影响候选收集与收敛。
- 建议补“早返回通道”对照：resolvableDependencies / @Value(suggestedValue) / 注入点 @Lazy / Optional|ObjectProvider|Stream（它们为什么不会走“选唯一候选”）。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/java.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-context-hierarchy.md`

- 章节标题：21. 父子 ApplicationContext：可见性与覆盖边界
- 卡片“知识点”：21. 父子 ApplicationContext：可见性与覆盖边界
- 卡片“源码入口”：`AbstractBeanFactory#doGetBean` / `AbstractApplicationContext#setParent` / `AbstractBeanFactory#containsLocalBean`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-bean-names-and-aliases.md`

- 章节标题：22. Bean 名称与 alias：同一个实例，多一个名字
- 卡片“知识点”：22. Bean 名称与 alias：同一个实例，多一个名字
- 卡片“源码入口”：`SimpleAliasRegistry#canonicalName` / `SimpleAliasRegistry#registerAlias` / `AbstractBeanFactory#transformedBeanName`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/23-factorybean-deep-dive.md`

- 章节标题：23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义
- 卡片“知识点”：23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义
- 卡片“源码入口”：`FactoryBean#isSingleton()` / `AbstractBeanFactory#getObjectForBeanInstance` / `FactoryBean#getObject()`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- FactoryBean 建议补“类型匹配坑”：getObjectType() 返回 null/误报如何影响候选收集（尤其是泛型 + FactoryBean 叠加）。
- 建议补缓存语义的断点闭环：FactoryBean 本身缓存 vs product 缓存不是一回事；用 getObjectForBeanInstance 与相关缓存变量把差异讲清。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/24-bean-definition-overriding.md`

- 章节标题：24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？
- 卡片“知识点”：24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？
- 卡片“源码入口”：`DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(...)` / `DefaultListableBeanFactory#isAllowBeanDefinitionOverriding()` / `DefaultListableBeanFactory#registerBeanDefinition`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`

- 章节标题：25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱
- 卡片“知识点”：手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱
- 卡片“源码入口”：`DefaultListableBeanFactory#addBeanPostProcessor` / `PostProcessorRegistrationDelegate#registerBeanPostProcessors` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticallyAddedBpp_runsBeforeBeanDefinedBpp_evenIfBeanDefinedIsPriorityOrdered`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议用“能改什么 + 发生在哪一步”解释 BFPP/BDRPP/BPP：把它们放回 refresh 的时间线，读者会更快建立阶段意识。
- 顺序语义（PriorityOrdered/Ordered/others）建议补一个真实案例：顺序变化如何导致占位符解析、代理增强或 beanDefinition 修改的结果不同。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/java.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/26-smart-initializing-singleton.md`

- 章节标题：26. SmartInitializingSingleton：所有单例都创建完之后再做事
- 卡片“知识点”：26. SmartInitializingSingleton：所有单例都创建完之后再做事
- 卡片“源码入口”：`SmartInitializingSingleton#afterSingletonsInstantiated` / `AbstractApplicationContext#finishBeanFactoryInitialization` / `DefaultListableBeanFactory#preInstantiateSingletons`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md`

- 章节标题：27. SmartLifecycle：start/stop 时机与 phase 顺序
- 卡片“知识点”：27. SmartLifecycle：start/stop 时机与 phase 顺序
- 卡片“源码入口”：`LifecycleProcessor#onRefresh` / `DefaultLifecycleProcessor#startBeans` / `DefaultLifecycleProcessor#stopBeans`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 生命周期章节适合把回调顺序落到方法级证据链：invokeAwareMethods → applyBPPBeforeInit → invokeInitMethods → applyBPPAfterInit → registerDisposableBean，并提示“在哪一步可能被替换成 proxy”。
- 建议补一个“为什么回调没执行”的排障分流：是否 lazy、是否 prototype、是否 pre-instantiation short-circuit、是否容器外对象（AutowireCapableBeanFactory）。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md`

- 章节标题：28. 自定义 Scope + scoped proxy：thread scope 的真实语义
- 卡片“知识点”：28. 自定义 Scope + scoped proxy：thread scope 的真实语义
- 卡片“源码入口”：`AbstractBeanFactory#doGetBean` / `Scope#get` / `SpringCoreBeansCustomScopeLabTest#threadScope_createsOneInstancePerThread_whenAccessedDirectly`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把 prototype “像单例”的误判直接落到 doGetBean 的调用时机：注入点只获取一次 vs 运行时按需获取。
- 如果涉及 scoped proxy，建议明确“双定义”的可观测信号：beanName 对应代理，scopedTarget.* 对应真实目标；并补一个“如何在断点里一眼识别”的提示。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/29-factorybean-edge-cases.md`

- 章节标题：29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效
- 卡片“知识点”：29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效
- 卡片“源码入口”：`FactoryBean#getObjectType()` / `FactoryBean#getObjectType()==null` / `DefaultListableBeanFactory#getBeanNamesForType`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- FactoryBean 建议补“类型匹配坑”：getObjectType() 返回 null/误报如何影响候选收集（尤其是泛型 + FactoryBean 叠加）。
- 建议补缓存语义的断点闭环：FactoryBean 本身缓存 vs product 缓存不是一回事；用 getObjectForBeanInstance 与相关缓存变量把差异讲清。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md`

- 章节标题：30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）
- 卡片“知识点”：30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）
- 卡片“源码入口”：`DependencyDescriptor#required` / `DependencyDescriptor#annotations` / `DependencyDescriptor#resolvableType`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`

- 章节标题：31. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）
- 卡片“知识点”：代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）
- 卡片“源码入口”：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议用“能改什么 + 发生在哪一步”解释 BFPP/BDRPP/BPP：把它们放回 refresh 的时间线，读者会更快建立阶段意识。
- 顺序语义（PriorityOrdered/Ordered/others）建议补一个真实案例：顺序变化如何导致占位符解析、代理增强或 beanDefinition 修改的结果不同。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/java.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/32-resource-injection-name-first.md`

- 章节标题：32. `@Resource` 注入：为什么它更像“按名称找 Bean”？
- 卡片“知识点”：`@Resource` 注入：为什么它更像“按名称找 Bean”？
- 卡片“源码入口”：`CommonAnnotationBeanPostProcessor#autowireResource` / `SpringCoreBeansResourceInjectionLabTest#withoutAnnotationConfigProcessors_resourceIsIgnored` / `SpringCoreBeansResourceInjectionLabTest#registerAnnotationConfigProcessors_enablesResourceAndResolvesByNameFirst`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md`

- 章节标题：33. 候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界
- 卡片“知识点”：候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界
- 卡片“源码入口”：`AutowiredAnnotationBeanPostProcessor#postProcessProperties` / `DefaultListableBeanFactory#doResolveDependency` / `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 依赖解析建议突出 DependencyDescriptor 的“需求表达”：required / annotations / resolvableType / dependencyName 这几个字段如何直接影响候选收集与收敛。
- 建议补“早返回通道”对照：resolvableDependencies / @Value(suggestedValue) / 注入点 @Lazy / Optional|ObjectProvider|Stream（它们为什么不会走“选唯一候选”）。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/java.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`

- 章节标题：34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast
- 卡片“知识点”：`@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast
- 卡片“源码入口”：`Environment#resolvePlaceholders` / `AbstractBeanFactory#resolveEmbeddedValue` / `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- @Value/占位符/SpEL 章节建议把两条路径分开：${...}（placeholder）与 #{...}（SpEL），并给出各自第一断点入口。
- 建议补 strict vs non-strict 的工程建议：缺 key 时默认行为与 fail-fast 的取舍，尤其在生产环境的风险。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/expressions.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/35-merged-bean-definition.md`

- 章节标题：35. BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？
- 卡片“知识点”：35. BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？
- 卡片“源码入口”：`AbstractBeanFactory#getMergedLocalBeanDefinition` / `DefaultListableBeanFactory#getMergedBeanDefinition` / `AbstractBeanFactory#getMergedLocalBeanDefinition(beanName)`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`

- 章节标题：36. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界
- 卡片“知识点”：类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界
- 卡片“源码入口”：`TypeConverterDelegate#convertIfNecessary` / `BeanDefinition#getPropertyValues()` / `AbstractAutowireCapableBeanFactory#populateBean`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 类型转换章节建议强调边界：BeanWrapper/PropertyEditor/ConversionService 解决的是“对象属性访问与转换”，不要与 Boot Binder 的绑定语义混在一起。
- 建议补“转换失败怎么定位”：conversionService 是否存在、propertyEditor 何时生效、以及集合/枚举/日期常见失败栈的第一入口。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md`

- 章节标题：37. 泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失
- 卡片“知识点”：37. 泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失
- 卡片“源码入口”：`BeanDefinition#getResolvableType` / `FactoryBean#getObjectType` / `GenericTypeAwareAutowireCandidateResolver#checkGenericTypeMatch`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 泛型匹配章节建议补“为什么泛型信息会丢”：JDK/CGLIB 代理、FactoryBean、bridge method 各自会如何影响 ResolvableType。
- 建议给读者一个断点观察套路：descriptor.resolvableType vs candidateType/targetType 的对照，避免只靠猜。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/38-environment-and-propertysource.md`

- 章节标题：38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线
- 卡片“知识点”：38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线
- 卡片“源码入口”：`PropertySourcesPropertyResolver#getProperty` / `Environment#getProperty(...)` / `ConfigurationClassPostProcessor#processConfigBeanDefinitions`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- Environment/PropertySource 章节建议补“与生命周期的交叉点”：placeholder 解析常在 BFPP 阶段完成，@Value 注入发生在依赖解析阶段，错时序会导致误判。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md`

- 章节标题：39. BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界
- 卡片“知识点”：39. BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界
- 卡片“源码入口”：`PostProcessorRegistrationDelegate#registerBeanPostProcessors` / `DefaultListableBeanFactory#doResolveDependency` / `AbstractBeanFactory#doGetBean`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

## `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/024-40-aot-and-native-overview.md`

- 章节标题：第 24 章：40. AOT / Native 总览：为什么“JVM 可运行”不等于“Native 可运行”
- 卡片“知识点”：AOT / Native 总览：为什么“JVM 可运行”不等于“Native 可运行”
- 卡片“源码入口”：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- AOT 章节建议更强调工程动机与排错：反射/资源/代理在构建期需要声明，读者遇到错误时如何判断是“缺 hints”还是“机制不适配”。
- RuntimeHints 相关建议补一个最小闭环：在哪声明、如何验证、如何定位缺失（构建期/运行期报错的差异）。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/aot.html
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/

## `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/41-runtimehints-basics.md`

- 章节标题：41. RuntimeHints 入门：把构建期契约完成验证
- 卡片“知识点”：RuntimeHints 入门：把构建期契约完成验证
- 卡片“源码入口”：`Class#getDeclaredMethods` / `Constructor#newInstance` / `ClassLoader#getResource`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- AOT 章节建议更强调工程动机与排错：反射/资源/代理在构建期需要声明，读者遇到错误时如何判断是“缺 hints”还是“机制不适配”。
- RuntimeHints 相关建议补一个最小闭环：在哪声明、如何验证、如何定位缺失（构建期/运行期报错的差异）。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/aot.html
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/

## `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/42-xml-bean-definition-reader.md`

- 章节标题：42. XML → BeanDefinitionReader：定义层解析与错误分型
- 卡片“知识点”：42. XML → BeanDefinitionReader：定义层解析与错误分型
- 卡片“源码入口”：`XmlBeanDefinitionReader#loadBeanDefinitions` / `DefaultBeanDefinitionDocumentReader#registerBeanDefinitions` / `BeanDefinitionParserDelegate#parseBeanDefinitionElement`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- AOT 章节建议更强调工程动机与排错：反射/资源/代理在构建期需要声明，读者遇到错误时如何判断是“缺 hints”还是“机制不适配”。
- RuntimeHints 相关建议补一个最小闭环：在哪声明、如何验证、如何定位缺失（构建期/运行期报错的差异）。
- XML/Reader/Namespace 章节建议把“输入→定义层落地”讲成可执行链路：Reader → Registry → BeanDefinition，并补“错误分型”（解析失败/注册失败/创建失败）。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/resources.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/aot.html
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/

## `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md`

- 章节标题：43. 容器外对象注入：AutowireCapableBeanFactory
- 卡片“知识点”：43. 容器外对象注入：AutowireCapableBeanFactory
- 卡片“源码入口”：`AutowireCapableBeanFactory#initializeBean` / `AutowireCapableBeanFactory#autowireBean` / `AutowireCapableBeanFactory#destroyBean`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- AOT 章节建议更强调工程动机与排错：反射/资源/代理在构建期需要声明，读者遇到错误时如何判断是“缺 hints”还是“机制不适配”。
- RuntimeHints 相关建议补一个最小闭环：在哪声明、如何验证、如何定位缺失（构建期/运行期报错的差异）。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/aot.html
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/

## `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/44-spel-and-value-expression.md`

- 章节标题：44. SpEL 与 `@Value("#{...}")`：表达式解析链路
- 卡片“知识点”：44. SpEL 与 `@Value("#{...}")`：表达式解析链路
- 卡片“源码入口”：`@Value("#{...}")` / `BeanFactory#resolveEmbeddedValue` / `AbstractBeanFactory#resolveEmbeddedValue`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- AOT 章节建议更强调工程动机与排错：反射/资源/代理在构建期需要声明，读者遇到错误时如何判断是“缺 hints”还是“机制不适配”。
- RuntimeHints 相关建议补一个最小闭环：在哪声明、如何验证、如何定位缺失（构建期/运行期报错的差异）。
- @Value/占位符/SpEL 章节建议把两条路径分开：${...}（placeholder）与 #{...}（SpEL），并给出各自第一断点入口。
- 建议补 strict vs non-strict 的工程建议：缺 key 时默认行为与 fail-fast 的取舍，尤其在生产环境的风险。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/expressions.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/aot.html
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/

## `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md`

- 章节标题：45. 自定义 Qualifier：meta-annotation 与候选收敛
- 卡片“知识点”：45. 自定义 Qualifier：meta-annotation 与候选收敛
- 卡片“源码入口”：`QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate` / `DefaultListableBeanFactory#findAutowireCandidates` / `DefaultListableBeanFactory#determineAutowireCandidate`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- 依赖解析建议突出 DependencyDescriptor 的“需求表达”：required / annotations / resolvableType / dependencyName 这几个字段如何直接影响候选收集与收敛。
- 建议补“早返回通道”对照：resolvableDependencies / @Value(suggestedValue) / 注入点 @Lazy / Optional|ObjectProvider|Stream（它们为什么不会走“选唯一候选”）。
- AOT 章节建议更强调工程动机与排错：反射/资源/代理在构建期需要声明，读者遇到错误时如何判断是“缺 hints”还是“机制不适配”。
- RuntimeHints 相关建议补一个最小闭环：在哪声明、如何验证、如何定位缺失（构建期/运行期报错的差异）。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/java.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

## `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/46-xml-namespace-extension.md`

- 章节标题：46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers
- 卡片“知识点”：46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers
- 卡片“源码入口”：`BeanDefinitionParserDelegate#parseCustomElement` / `XmlBeanDefinitionReader#doLoadBeanDefinitions` / `DefaultBeanDefinitionDocumentReader#parseBeanDefinitions`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- AOT 章节建议更强调工程动机与排错：反射/资源/代理在构建期需要声明，读者遇到错误时如何判断是“缺 hints”还是“机制不适配”。
- RuntimeHints 相关建议补一个最小闭环：在哪声明、如何验证、如何定位缺失（构建期/运行期报错的差异）。
- XML/Reader/Namespace 章节建议把“输入→定义层落地”讲成可执行链路：Reader → Registry → BeanDefinition，并补“错误分型”（解析失败/注册失败/创建失败）。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/resources.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/aot.html
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/

## `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/47-beandefinitionreader-other-inputs-properties-groovy.md`

- 章节标题：47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy
- 卡片“知识点”：47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy
- 卡片“源码入口”：`SpringCoreBeansPropertiesBeanDefinitionReaderLabTest#propertiesBeanDefinitionReader_registersBeanDefinitions_fromPropertiesFile` / `SpringCoreBeansGroovyBeanDefinitionReaderLabTest#groovyBeanDefinitionReader_registersBeanDefinitions_fromGroovyScript` / `AbstractBeanDefinitionReader#loadBeanDefinitions`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- AOT 章节建议更强调工程动机与排错：反射/资源/代理在构建期需要声明，读者遇到错误时如何判断是“缺 hints”还是“机制不适配”。
- RuntimeHints 相关建议补一个最小闭环：在哪声明、如何验证、如何定位缺失（构建期/运行期报错的差异）。
- XML/Reader/Namespace 章节建议把“输入→定义层落地”讲成可执行链路：Reader → Registry → BeanDefinition，并补“错误分型”（解析失败/注册失败/创建失败）。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/resources.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/aot.html
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/

## `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/48-method-injection-replaced-method.md`

- 章节标题：48. 方法注入（Method Injection）：replaced-method / MethodReplacer
- 卡片“知识点”：48. 方法注入（Method Injection）：replaced-method / MethodReplacer
- 卡片“源码入口”：`SpringCoreBeansReplacedMethodLabTest#replacedMethod_overridesTargetMethodViaCglibSubclassing_andIsVisibleInBeanDefinitionMethodOverrides` / `AbstractAutowireCapableBeanFactory#instantiateWithMethodInjection` / `AbstractBeanDefinition#getMethodOverrides`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- AOT 章节建议更强调工程动机与排错：反射/资源/代理在构建期需要声明，读者遇到错误时如何判断是“缺 hints”还是“机制不适配”。
- RuntimeHints 相关建议补一个最小闭环：在哪声明、如何验证、如何定位缺失（构建期/运行期报错的差异）。
- 建议把本章的“关键分支”再写得更像排障分流：触发条件是什么、会走到哪个方法、结果形态是什么。
- 建议补 1 个边界反例（可复现）：让读者知道“看起来能用”的做法在什么条件下会翻车。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/aot.html
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/

## `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/49-built-in-factorybeans-gallery.md`

- 章节标题：49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀
- 卡片“知识点”：49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀
- 卡片“源码入口”：`BeanFactory#getBean(...)` / `FactoryBean#isSingleton` / `AbstractBeanFactory#getObjectForBeanInstance`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- FactoryBean 建议补“类型匹配坑”：getObjectType() 返回 null/误报如何影响候选收集（尤其是泛型 + FactoryBean 叠加）。
- 建议补缓存语义的断点闭环：FactoryBean 本身缓存 vs product 缓存不是一回事；用 getObjectForBeanInstance 与相关缓存变量把差异讲清。
- AOT 章节建议更强调工程动机与排错：反射/资源/代理在构建期需要声明，读者遇到错误时如何判断是“缺 hints”还是“机制不适配”。
- RuntimeHints 相关建议补一个最小闭环：在哪声明、如何验证、如何定位缺失（构建期/运行期报错的差异）。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/aot.html
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/

## `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/50-property-editor-and-value-resolution.md`

- 章节标题：50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象
- 卡片“知识点”：50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象
- 卡片“源码入口”：`BeanDefinitionValueResolver#resolveValueIfNecessary` / `CustomEditorConfigurer#postProcessBeanFactory` / `PropertyEditorRegistrySupport#registerCustomEditor`

- 你已经在卡片里给了源码入口；建议在正文里明确“为什么是这些入口”（各自对应哪条关键分支/关键数据结构），避免读者只会打断点不会收敛结论。
- AOT 章节建议更强调工程动机与排错：反射/资源/代理在构建期需要声明，读者遇到错误时如何判断是“缺 hints”还是“机制不适配”。
- RuntimeHints 相关建议补一个最小闭环：在哪声明、如何验证、如何定位缺失（构建期/运行期报错的差异）。
- 类型转换章节建议强调边界：BeanWrapper/PropertyEditor/ConversionService 解决的是“对象属性访问与转换”，不要与 Boot Binder 的绑定语义混在一起。
- 建议补“转换失败怎么定位”：conversionService 是否存在、propertyEditor 何时生效、以及集合/枚举/日期常见失败栈的第一入口。

（官方对照/延伸阅读建议）
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（适用版本：6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/aot.html
- Spring Boot Reference（适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/
