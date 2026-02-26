# Change Proposal: 全模块更深一层对标（V2 / Batch 01）

> Batch 01 目标模块：`springboot-security` / `springboot-data-jpa` / `spring-core-events` / `springboot-web-client`

## Requirement Background

在上一轮对标（例如 `202601091802_modules_depth_align_to_beans`）中，多个模块已经补齐：

- Guide 机制主线（时间线/关键参与者/关键分支/推荐断点/可跑入口）
- 每章至少 1 个可断言坑点/边界（绑定默认 Lab/Test 证据链）
- 少量关键分支的默认 Lab 覆盖缺口

但要把学习深度继续推到接近 `spring-core-beans` 的“源码级可解释性”，仍存在三类常见缺口：

1. **断点锚点仍偏“方向性”**：很多 Guide 虽然给了断点建议，但还缺少“到 class/method 级别”的锚点与 call chain sketch，导致读者下断点时容易迷路。
2. **默认 Lab 的覆盖仍偏“主线”**：很多结论能跑，但关键分支的“对照实验”不足（尤其是选择/匹配/顺序/回退等分支），不利于形成稳定的排障能力。
3. **章节坑点需要“第二层对照”**：每章 1 个坑点是底线；更深一层需要再补 1 个“可断言对照坑点/边界”，形成更稳的知识网（同章内对比更清晰）。

- 每模块新增 ≥1 个默认 Lab（默认参与回归、可断言、尽量不 flaky）
- Guide 内每个关键分支都补齐“源码级断点锚点 + call chain sketch”
- 每章新增 ≥1 个可断言坑点/边界（对照实验），绑定默认 Lab/Test 证据链
- 每模块新增/补齐 Debug Playbook：从“现象 → 分支 → 断点 → 修复”走完闭环

## Change Content

1. **Guide 深化（源码级）**：把关键分支落到 class/method 的断点锚点，并补齐 call chain sketch（建议以“测试断言 → 调用链 → 断点入口”反推）。
2. **默认 Lab 增量**：每个目标模块新增 ≥1 个默认 Lab，用于覆盖一个“更深分支”的稳定断言闭环。
3. **章节对照坑点**：每个章节新增 ≥1 条可断言坑点/边界（对照实验），并绑定到默认 Lab/Test 入口与断点锚点。
4. **Debug Playbook**：在 Guide 中补齐“排障分流地图”，让读者从现象快速定位到机制分支与断点。

## Impact Scope

- **Modules:**
  - `springboot-security`
  - `springboot-data-jpa`
  - `spring-core-events`
  - `springboot-web-client`
- **Files:**
  - `*/docs/part-00-guide/00-deep-dive-guide.md`（Guide 源码级深化 + Debug Playbook）
  - `*/docs/part-01-*/**.md`（每章新增对照坑点/边界）
  - `*/docs/appendix/99-self-check.md`（补齐对照坑点/自测强化）
  - `*/src/test/java/**`（新增默认 Lab，作为证据链 SSOT）
  - `helloagents/wiki/modules/*.md`（模块规格同步）
  - `helloagents/CHANGELOG.md`（变更记录）
- **APIs:** 无新增对外 API（教育工程内部使用）
- **Data:** 仅测试/示例数据；无生产数据变更

## Core Scenarios

### Requirement: Guide Key Branch Breakpoint Anchors
**Module:** Batch 01 target modules
把 Guide 的关键分支从“建议断点”升级为“断点锚点（class/method）+ call chain sketch（可复述）”。

#### Scenario: Each Key Branch Has Breakpoint Anchors
- 每个关键分支都能给出 class/method 级别断点锚点（含框架类与仓库内示例代码）
- 每个关键分支都能给出一段可复述的调用链草图（call chain sketch）
- Guide 中能从“现象/断言”一跳回到对应断点

### Requirement: Default Lab Coverage Expansion
**Module:** Batch 01 target modules
每个目标模块新增 ≥1 个默认 Lab，覆盖一个“更深分支”的稳定断言闭环。

#### Scenario: Each Module Adds One Default Lab
- 新增 `*LabTest` 默认参与回归
- 断言尽量基于稳定可观察信号（状态码/异常类型/SQL 计数/调用序列/线程名等）
- 尽量不引入 flaky 因素（避免 sleep、依赖真实网络/真实时间）

### Requirement: Chapter Pitfall Contrast Expansion
**Module:** Batch 01 target modules
每个章节新增 ≥1 个“对照坑点/边界”，形成更深一层的对比学习闭环。

#### Scenario: Each Chapter Adds One More Verifiable Pitfall
- 坑点必须包含：Symptom / Root Cause / Verification / Breakpoints / Fix
- Verification 必须绑定默认 `*LabTest#method` 或现有默认 Lab/Test

### Requirement: Debug Playbook
**Module:** Batch 01 target modules
每个模块提供可执行的排障分流地图（现象→分支→断点→修复建议）。

#### Scenario: From Symptom To Fix With Breakpoints
- 给出 5–8 条“高频现象”分流路径
- 每条路径都能落到 1–3 个断点锚点 + 1 个默认可跑入口

### Requirement: Repo Gates Stay Green
**Module:** Batch 01 target modules

#### Scenario: Repo Gates Stay Green
- `mvn -pl <module> test`（批次内模块）通过
- `mvn test` 全绿

## Risk Assessment

- **Risk:** 新增/深化的断点锚点引用框架内部类，容易与版本变化产生偏差。  
  **Mitigation:** 以“关键路径最稳定的入口类”为锚点（例如 FilterChainProxy / SimpleApplicationEventMulticaster / SimpleJpaRepository），并在 Guide 中注明“这是入口锚点，细节可按 call chain 继续下钻”。
- **Risk:** 新增默认 Lab 涉及并发/异步/超时等因素导致 flaky。  
  **Mitigation:** 选择稳定信号（CountDownLatch/线程名/固定超时上限/纯内存 stub ExchangeFunction），避免 sleep 与真实网络依赖。
- **Risk:** 一次性覆盖面过大导致回归与排障成本上升。  
  **Mitigation:** 采用 Batch 机制：每批 3–4 个模块，批内闭环后立刻迁移方案包并留痕，再进入下一批。

