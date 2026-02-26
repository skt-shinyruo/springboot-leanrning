# Change Proposal: spring-core-beans 文档分批重构与测试闭环

## Requirement Background
spring-core-beans 模块已具备大量文档与测试资产，但目录顺序、模板契约与证据链密度不一致，读者难以沿“主线时间线 → 分支矩阵 → 断点 → 可跑实验”形成稳定闭环。本次以“分批重构”为策略，逐 Part 完成统一模板与可验证入口，降低一次性重排带来的链接漂移风险。

## Change Content
1. 重构 docs 目录与入口顺序，统一章节命名与阅读路径
2. 分批补齐主线时间线、调用链、关键分支矩阵、断点地图、排障与性能/并发专题
3. 对齐模块 README 与 docs/README 入口与导航索引
4. 补齐 Labs/Exercises，确保文档章节有可跑证据链

## Impact Scope
- **Modules:** spring-core-beans
- **Files:** spring-core-modules/spring-core-beans/README.md, spring-core-modules/spring-core-beans/docs/README.md, spring-core-modules/spring-core-beans/docs/part-*/**.md, spring-core-modules/spring-core-beans/docs/appendix/**.md, spring-core-modules/spring-core-beans/src/test/java/**
- **APIs:** 无
- **Data:** 无

## Core Scenarios

### Requirement: R1-章节结构与命名重构
**Module:** spring-core-beans/docs
对文档目录与章节命名进行重排与统一，保证入口一致、顺读清晰、重构可回溯。

#### Scenario: S1-稳定入口与目录一致
在完成重排后：
- README 与 docs/README 入口一致
- 章节顺序与主线时间线一致，链接不丢失

### Requirement: R2-机制主线与调用链补齐
**Module:** spring-core-beans/docs
每个关键机制都具备“调用链/主线时间线/机制图”的可追踪描述。

#### Scenario: S1-主线可追踪
读者可从“章节入口 → 调用链/时间线 → 对应测试”形成稳定闭环。

### Requirement: R3-关键分支矩阵与断点包
**Module:** spring-core-beans/docs
为关键分支提供矩阵化描述，并给出可定位的断点与观察点清单。

#### Scenario: S1-分支可定位
读者能在 1–2 次跳转内找到关键分支与推荐断点。

### Requirement: R4-排障与边界专题完善
**Module:** spring-core-beans/docs/appendix
补齐常见坑与边界条件的排障路径（现象→根因→验证）。

#### Scenario: S1-现象到验证
排障条目提供明确的可跑入口与观察点。

### Requirement: R5-性能与并发专题
**Module:** spring-core-beans/docs
新增或补齐性能/并发专题，覆盖单例缓存、并发 getBean、初始化成本与诊断路径。

#### Scenario: S1-瓶颈与验证入口
每个性能/并发结论都提供实验入口与可观察指标。

### Requirement: R6-可跑证据链与测试补齐
**Module:** spring-core-beans/src/test/java
补齐 Labs/Exercises，确保文档中的入口可直接运行。

#### Scenario: S1-文档到测试闭环
每章末尾能跳转到对应测试类/方法并可执行。

## Risk Assessment
- **Risk:** 章节重排导致链接断裂与导航漂移
- **Risk:** 变更范围大导致遗漏
  **Mitigation:** 每个 Part 独立收敛，文档与测试 1:1 映射验证
