# 怎么做（How）

## 总体策略

以 `springboot-web-mvc` 为模板，建立跨模块可复用的“三件套”：

1) **关键分支矩阵（Branch Matrix）**
- 形式：每个模块新增 `*BranchMatrixLabTest`（优先使用 JUnit Platform Suite 聚合现有 `*LabTest`）
- 作用：为“关键分支/边界条件/典型故障模式”提供单入口回归与调试入口

2) **断点地图（Breakpoint / Watchpoint Map）**
- 形式：每个模块新增 `docs/<topic>/<module>/part-00-guide/*-02-breakpoint-map.md`
- 内容：入口断点、关键分支断点、Watchpoint 建议、运行命令与 IDE 调试建议

3) **排障 Playbook**
- 形式：统一补齐各模块 `appendix/*-90-common-pitfalls.md` 的结构块（必要时在 `self-check` 引用）
- 内容：症状 → 复现入口（Book/Branch Matrix）→ 证据收集（日志/断点/指标）→ 决策表 → 修复建议

## 命名与放置规则

### 测试入口命名

- Boot 模块：`Boot<ModuleName>BranchMatrixLabTest`
- Spring Core 模块：`SpringCore<ModuleName>BranchMatrixLabTest`
- 特殊模块：
  - `spring-core-aop-weaving`：拆分为 LTW/CTW（或明确只提供其中一种入口），并在文档中标注运行方式差异

### Suite 聚合的可见性规则（关键）

默认策略：**Suite 类与被 `@SelectClasses` 的测试类处于同一 package**，避免 package-private 导致编译失败。

仅在确有必要跨包聚合时才考虑：
- 将被聚合测试类改为 `public`（需评估影响范围）
- 或拆分成多个 Branch Matrix（按 part/package 分组）

## 文档新增/改造规则

### 断点地图（Breakpoint Map）最小结构

- “如何运行”（给出最小命令）：优先指向 Book Matrix + Branch Matrix
- “入口断点”（从 `main` / `DispatcherServlet` / `@Transactional` / `@Cacheable` 等模块核心入口开始）
- “关键分支断点”（围绕模块的 3~8 个决策点）
- “Watchpoints”（可观察变量/集合大小/异常映射/线程名/TransactionStatus 等）
- “常见误区与快速自检”（链接到 common pitfalls / self-check）

### 关键分支矩阵（Branch Matrix）文档最小结构

- “矩阵的目的”：为什么这些分支值得覆盖
- “分支/边界条件清单”：用表格表达（条件 → 期望行为 → 复现入口 → 观察点）
- “推荐调试路线”：从哪个入口 test 开始、断点怎么下、证据如何收集

### 排障 Playbook（common pitfalls）统一结构块

- 症状（Symptoms）
- 最小复现（Repro）：Book Matrix / Branch Matrix 命令
- 证据收集（Evidence）：日志、断点、关键对象状态、必要时补充 actuator/metrics
- 分支决策（Decision）：按“如果…那么…”拆分
- 修复建议（Fix）：配置项、代码点位、测试验证命令

## 模块差异与特殊处理

- `spring-core-aop-weaving`：
  - LTW 需要 `-javaagent`，CTW 则要求不带 `-javaagent`；在 Maven surefire 多 execution 场景下，单一 `-Dtest=...` 可能跑到错误的 execution
  - 处理策略：在 LTW 测试中增加 “无 javaagent 则 assume/skip” 的保护，并在文档明确“如何正确运行 LTW/CTW”
- `spring-core-beans` / `spring-core-aop` / `spring-core-events`：
  - 测试分散在多个 part package；Branch Matrix 优先从最关键 part（通常 part01）做一个入口，后续再扩展到其它 part 的分支矩阵入口

## 验证策略

优先按“模块级别快速验证 → 全仓回归”：

- Spot-check：逐模块运行 `*BranchMatrixLabTest`（以及必要的 Book Matrix）
- 全仓：`mvn -q test`
- 文档：构建 docs-site。

## 交付与知识库同步

- 同步更新 `helloagents/wiki/modules/*.md`（新增 Branch Matrix / Breakpoint Map / Playbook 入口）
- 更新 `helloagents/CHANGELOG.md`（记录本轮新增能力）
- 方案包执行完成后迁移到 `helloagents/history/YYYY-MM/` 并更新 `helloagents/history/index.md`
