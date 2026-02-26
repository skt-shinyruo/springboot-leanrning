# 变更提案：spring-core-beans 覆盖 spring-beans Public API + 关键内部算法（B 目标）

## 需求背景

当前 `spring-core-beans` 已经形成“书本化 docs + 可复现 Labs + 断点/观察点”的教学闭环，并覆盖了大量 IoC/装配/边界/真实世界主题。

但以 **Spring Framework `spring-beans` 模块**为范围（尤其是 `org.springframework.beans*` / `org.springframework.beans.factory*`），仍缺少一个“可审计、可检索”的 **Public API 全覆盖**与“可断点证明”的 **关键内部算法主线**：

- 很多 API 在正文里“被提到/被用到”，但缺少“系统化 API 图谱”和“覆盖状态审计”。
- 关键算法（如 `doGetBean/doCreateBean/doResolveDependency/值解析链路`）虽然在若干章节有解释，但缺少“按算法/分支组织的一次性对照索引”，导致学习者难以从 API → 主线 → 断点完成闭环。
- 需要在保持现状版本（Spring Framework 6.2.15 / Spring Boot 3.5.x）不升级的前提下，逐步补齐。

## 变更内容

1. 建立 `spring-beans` **Public API 清单（可生成）**与**映射表（可审计）**：
   - 以 Spring 6.2.15 的 `spring-beans-*-sources.jar` 为输入来源。
   - 输出到 `spring-core-beans` docs 的 Appendix（便于长期检索）。
2. 将 `spring-beans` 的 public 类型按“机制域（mechanism domains）”分组，形成**机制优先的教学主线**：
   - 每组都要能落到本项目的 `docs + Lab + 断点入口/观察点`。
3. 基于 API 清单做“覆盖差距（gap）审计”，并分批补齐：
   - 对于缺失/半缺失点：新增或增强章节（主线/边界/误区）。
   - 同步新增/增强 Labs：可复现、可断言、可下断点。
4. 交付两类测试入口（你要求 1+2 都要）：
   - **Core Labs（默认参与回归）**：覆盖主线机制与高频边界。
   - **Explore/Debug 用例（可选）**：用于深水断点观察/性能与缓存类行为验证，默认不影响 CI 稳定性（采用显式开关策略）。
5. 同步更新知识库与索引：
   - `docs/beans/spring-core-beans/README.md`、`docs/appendix/03-knowledge-map.md`
   - `helloagents/wiki/modules/spring-core-beans.md`
   - `helloagents/CHANGELOG.md`

## 影响范围

- **Modules:** `spring-core-beans`, `helloagents`（知识库/方案包）
- **Files:** docs/appendix 新增索引；若干章节增补；新增脚本与 Labs；知识库同步文件
- **APIs:** 无对外 API 改动（教学内容与测试增强）
- **Data:** 无

## 核心场景

### Requirement: spring-beans Public API 全覆盖（可检索、可审计）
**Module:** spring-core-beans
建立 public 类型清单与映射表，使学习者能从“某个 API 名称”直接定位到：
1) 它解决什么机制问题  
2) 对应章节（主线/边界/误区）  
3) 对应 Lab（可复现/可断言）  
4) 断点入口与观察点  

#### Scenario: 给定一个 public 类型名，能在 1 次跳转内定位章节与 Lab
- 期望：在 docs Appendix 的索引中找到类型 → 章节/实验入口

### Requirement: 关键内部算法主线可断点证明（源码级）
**Module:** spring-core-beans
把核心算法按“调用链 + 关键分支 + 观察点”组织起来，并提供最小实验入口，让读者能在 IDE 里复现并解释。

#### Scenario: 解释一次 bean 创建与依赖解析的关键分支
- 期望：能用断点证明 `doGetBean → doCreateBean → populateBean → initializeBean` 的阶段与分支
- 期望：能用断点证明 `doResolveDependency` 的候选收敛与优先级规则

### Requirement: 逐步补齐（可持续迭代、不爆炸）
**Module:** spring-core-beans

#### Scenario: 每一批次都能独立通过 tests + docs 链接检查
- 期望：每批次结束都能 `mvn -pl spring-core-beans test` 通过，并且 docs 0 断链

## 风险评估

- **风险：**范围过大导致“一次性补齐”失控、回归变慢、文档膨胀难维护  
  **缓解：**分批交付（每批 ≤ 3–5 章/≤ 3–5 Labs），先建立索引与 gap 审计，再按机制域逐组补齐。
- **风险：**Explore/Debug 用例影响 CI 稳定性  
  **缓解：**采用显式开关策略（默认不跑），并在 docs 写明“何时打开/如何运行/观察点”。

