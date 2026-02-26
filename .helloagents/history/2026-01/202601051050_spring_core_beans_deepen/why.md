# Change Proposal: spring-core-beans 深挖升级（结构重构 + 知识点补齐 + 可复现实验）

## Requirement Background

当前 `spring-core-beans` 已具备基础“书籍式”分 Part 的文档与配套 Labs/Exercises，但整体仍存在以下不足，导致读者难以仅凭本模块获得对 Spring Bean 的**全面、透彻、可落到源码与断点**的理解：

1. **深度不足**：部分章节停留在概念解释，缺少“时间线（refresh）/关键数据结构（BeanDefinition、单例缓存）/关键参与者（BFPP/BPP/BDRPP）/断点入口”的系统映射。
2. **覆盖不全**：已有 Labs 覆盖面较广，但仍存在读者高频困惑点未被专章系统化（例如：泛型匹配坑、类型转换链路、`@Lookup` 方法注入、`BeanFactory` vs `ApplicationContext` 等）。
3. **结构与导航仍可增强**：目录与章节承接在细节上存在可读性与一致性问题（例如 TOC 排版异常、章节间跳转与索引能力不足），不利于“顺读成体系/跳读可定位”。
4. **“可复现闭环”不够强**：并非每个核心机制都能做到“文档 → 对应 Lab → 断点观察点 → 结论复述”的闭环体验。

本变更希望把 `spring-core-beans` 升级为：读者只要顺读并跑完 Labs，就能把 Spring Bean 从**定义层→实例层→代理层**以及从**refresh 时间线→扩展点→边界条件**完整打通。

## Change Content

1. **结构重构（Book Style 强化）**
   - 修复/重写 `docs/README.md` 的目录与映射，保证顺读路径清晰、链接可用、章节定位准确。
   - 为章节补齐统一的“上一章｜目录｜下一章”导航与索引策略（在不大规模重命名的前提下，优先通过 TOC 重排与新增章节编号扩展）。

2. **知识点补齐（更深入、更全面）**
   - 基于读者高频问题与源码关键路径补齐缺失专题，形成“Bean 全景知识图谱”。
   - 每个专题都要求：概念 → 关键类/方法 → 断点入口 → 可复现实验 → 常见坑与边界。

3. **可复现实验体系强化（Labs/Exercises）**
   - 对齐“每个关键机制至少 1 个可运行的最小实验（JUnit）”的目标。
   - 增强实验的“降噪条件断点模板/观察点清单/预期结论”，让读者能稳定复现与复述。

4. **排障能力建设（从异常到定位）**
   - 增强“异常 → 断点入口 → 关键对象观察点 → 根因定位 → 修复策略”的文档闭环。
   - 用本仓库现有 Labs 作为复现载体，补齐与文档的双向链接。

## Impact Scope

- **Modules:**
  - `spring-core-beans`（主要变更范围）
  - `helloagents/`（知识库同步与变更记录）
- **Files:**
  - `docs/beans/spring-core-beans/**`（新增/重写/扩展章节、索引、附录）
  - `spring-core-beans/src/test/**`（新增/增强 Labs/Exercises 与测试支撑）
  - `scripts/`（可选：如需新增文档一致性/链接校验脚本）
  - `helloagents/wiki/**`、`helloagents/CHANGELOG.md`、`helloagents/history/**`（同步记录）
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: R1 Docs Navigation（书籍式结构与导航）
**Module:** spring-core-beans
在不牺牲可维护性的前提下，使读者可“顺读成体系、跳读可定位”。

#### Scenario: S1.1 从目录顺读
读者从 `docs/README.md` 进入：
- 目录结构清晰，无排版异常
- 章节链接可用，章节之间承接稳定（上一章/下一章可跳转）

#### Scenario: S1.2 从问题跳读
读者遇到问题（例如循环依赖、注入歧义、代理替换）：
- 能在 1–2 次跳转内定位到对应章节与对应 Lab

### Requirement: R2 Bean Knowledge Map（Bean 全景知识图谱）
**Module:** spring-core-beans
将 Spring Bean 的核心概念落到可追踪的时间线与数据结构上。

#### Scenario: S2.1 三层模型落地
读者能复述并验证：
- `BeanDefinition`（定义层） ≠ Bean instance（实例层） ≠ proxy（代理层）
- 关键结论可在断点中观察到（例如 `doCreateBean`、`postProcessAfterInitialization`）

### Requirement: R3 Labs/Exercises Close the Loop（可复现闭环）
**Module:** spring-core-beans
每个关键机制都有“文档→实验→断点→结论”的闭环。

#### Scenario: S3.1 最小实验可跑
读者运行单个 `@Test` 方法：
- 能稳定复现该机制的关键行为
- 文档提供条件断点与观察点清单，避免噪声淹没

### Requirement: R4 Missing Topics Coverage（补齐缺失专题）
**Module:** spring-core-beans
新增/增强章节覆盖读者高频困惑点与源码关键路径专题。

#### Scenario: S4.1 常见“坑点”可系统理解
读者能通过专章理解并验证（示例）：
- 泛型注入匹配与坑（`List<Foo<T>>`、`Map<String, Foo>`、bridge method 等）
- 类型转换（`BeanWrapper`/`ConversionService`）与属性绑定差异
- `@Lookup` 方法注入的机制与适用场景
- `BeanFactory` vs `ApplicationContext` 的能力边界与使用选择

### Requirement: R5 Troubleshooting Playbook（排障手册）
**Module:** spring-core-beans
从典型异常入手建立排障路径，形成可复用的“定位套路”。

#### Scenario: S5.1 从异常到断点入口
当出现 `UnsatisfiedDependencyException` / `NoSuchBeanDefinitionException` 等：
- 文档给出推荐断点入口（如 `doResolveDependency` / `createBean`）
- 给出观察点（候选列表、限定符、primary、缓存状态）与定位步骤

## Risk Assessment

- **Risk:** 文档重排/新增导致链接断裂与目录失真  
  **Mitigation:** 优先“新增编号章节 + TOC 重排”而非大规模重命名；提供自检清单/必要时加入链接校验。
- **Risk:** 新增/增强 Labs 使测试耗时增长、学习路径变重  
  **Mitigation:** 坚持“最小启动器（JUnit）+ 精确到方法运行”；将扩展实验归档到 Exercises/Appendix。
- **Risk:** 深挖内容过于“源码党”导致读者门槛上升  
  **Mitigation:** 每章先给结论与直觉，再给源码导航；提供分层阅读提示（必读/选读/进阶）。

