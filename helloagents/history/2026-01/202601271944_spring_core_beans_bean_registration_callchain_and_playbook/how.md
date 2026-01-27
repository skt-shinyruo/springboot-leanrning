# Implementation Plan: Bean 注册入口（方法级调用链 + 排障决策表 + 面试标准答案）

## Goals

- 让读者能从现象出发，沿着 `refresh()` 主线把注册链路追到“方法级可断点”。
- 让 `02-bean-registration` 具备“可直接用来排障/面试”的决策表与标准答案。

## Non-Goals（本次不做）

- 不新增/修改现有 Lab 的行为（除非发现文档与代码事实冲突）。
- 不在本章完整讲解注入解析/生命周期/后处理器/循环依赖，只在“边界提醒”处给出必要链接与断点入口。

## Plan

1. 章节结构扩展（不破坏现有阅读路径）
   - 在“入口对照表/证据链”之后新增“方法级调用链”章节
   - 在“断点闭环”之后新增“排障决策表”
   - 在“面试/内训复述模板”基础上新增“面试标准答案（可复述）”
2. 方法级调用链内容（以 Spring Framework 6.2.x 为准）
   - 主线骨架：`AbstractApplicationContext#refresh` → `invokeBeanFactoryPostProcessors` → `PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors`
   - 分支链路：
     - ComponentScan：配置类解析链路 → `ComponentScanAnnotationParser#parse` → `ClassPathBeanDefinitionScanner#doScan` → `registerBeanDefinition`
     - @Bean：`ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass` → `loadBeanDefinitionsForBeanMethod`
     - @Import：`ConfigurationClassParser#processImports` → selector / registrar 分支 → 最终落点
     - Programmatic：`registerBeanDefinition` vs `registerSingleton` 的后果与可观察点
   - 每条链路补齐断点建议与 watch list
3. 排障决策表（最常见注册类问题）
   - 以“现象”作为入口，强制先分层（定义层/创建层/注入层）
   - 每行提供：最短断点、关键变量、结论与修复策略、推荐 Lab（可复现）
4. 面试标准答案
   - 每题输出：一句话结论 + 证据链（关键方法）+ 反例/坑 + 加分项
5. 同步知识库（SSOT）
   - `helloagents/wiki/modules/spring-core-beans.md`：追加本次交付记录与入口链接
   - `helloagents/CHANGELOG.md`：Unreleased/Changed 增补本次深化条目
6. 验证
   - 跑最小回归：优先跑 `SpringCoreBeansImportLabTest` 或 `SpringCoreBeansProgrammaticRegistrationLabTest`

## Acceptance Criteria

- 文档内至少包含 4 条“方法级调用链”（scan/@Bean/@Import/Programmatic），且每条都有“最终落点 + 断点 + watch list”。
- 至少 10 行“排障决策表”，覆盖常见注册相关现象，并能映射到具体断点/变量。
- 至少 8 题“面试标准答案”，每题包含证据链与反例。

