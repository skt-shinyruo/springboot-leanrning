# Change Proposal: 进一步深化 Bean 注册入口（方法级调用链 + 排障决策表 + 面试标准答案）

## Requirement Background

`spring-core-beans` 的 `02-bean-registration` 已经补齐了“入口对照表/最短调用链/证据链/复述模板”，但对目标读者（源码进阶 / 面试 / 团队内训）仍存在关键缺口：

- **调用链不够细**：目前主要是“锚点级”链路（入口类 → 落点），缺少“能跟着断点走到方法级”的可追踪主线（refresh → BDRPP → 配置类解析 → 具体入口分支）。
- **排障不够像排障**：只有“常见坑列表”，缺少“现象 → 分层 → 最短验证 → 结论 → 修复策略”的决策表，无法直接用于生产/面试的快速定位。
- **面试输出不够标准**：有“答题结构模板”，但缺少可直接背诵/复述的“标准答案”（包含关键分界点与证据链）。

本变更目标：把 `02-bean-registration` 升级为“**方法级可追踪**、**可落地排障**、**可复述标准答案**”的交付物。

## Change Content

1. 新增“源码调用链到方法级”章节：
   - 以 Spring Framework 6.2.x 的 `refresh()` 时间线为主线
   - 按入口拆分：ComponentScan / @Bean / @Import（selector/registrar）/ programmatic（definition vs singleton）
   - 每条链路提供：入口 → 关键类 → 关键方法 → 最终落点（registerBeanDefinition/ registerSingleton）
2. 新增“排障决策表（注册相关）”：
   - 覆盖典型现象：扫描不生效、@Import 不生效、定义存在但实例不存在、实例存在但注入/代理/回调不生效、同名覆盖/冲突等
   - 每行给出：阶段归类（定义层/创建层/注入层）+ 最短断点 + watch list + 修复策略 + 推荐 Lab
3. 新增“面试标准答案（可复述）”：
   - 以“主线 → 分支 → 边界 → 证据链”的结构输出
   - 每题至少包含：一句话结论 + 关键方法级证据链 + 常见反例/坑
4. 保持章节导航与实验入口块不变，不引入新依赖、不改变现有测试行为。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`
- **Files:**
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/CHANGELOG.md`
  - `helloagents/history/index.md`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: R1-bean-registration-callchain-method-level
读者能从 `refresh()` 主线定位到“注册发生在哪里”，并能沿着入口分支把调用链跟到方法级（直到最终落点）。
- 预期结果：能回答“这个注册入口具体是在 refresh 的哪一步触发的？”
- 预期结果：能在调试器中用断点与变量证明“是谁把 BeanDefinition 注册进 registry 的”。

### Requirement: R2-bean-registration-troubleshooting-decision-table
读者能把“注册相关现象”快速分层（定义层/创建层/注入层），并按决策表定位到最短验证路径与修复策略。
- 预期结果：遇到“扫不到/导不进/定义有但没实例/实例有但不生效”，能用表格 2–3 步得到结论。

### Requirement: R3-bean-registration-interview-standard-answers
读者能用标准答案复述注册入口与边界，且每个答案都能落到“可证明的证据链”。
- 预期结果：能用 3–5 分钟解释 `registerBeanDefinition` vs `registerSingleton` 的根本差异与后果。

## Risk Assessment

- **Risk:** 方法级调用链过长导致读者负担增加  
  **Mitigation:** 用“主线骨架 + 分支最短链路 + 断点/变量”压缩信息；避免罗列所有内部细节。
- **Risk:** Spring 版本差异导致少数方法名微调  
  **Mitigation:** 以稳定锚点（`refresh`/`invokeBeanFactoryPostProcessors`/`ConfigurationClassPostProcessor`/`registerBeanDefinition`）为主，注明“6.2.x 为准”。

