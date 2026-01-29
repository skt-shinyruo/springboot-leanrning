# Technical Design: beans + aop 基础知识可解释性升级（Why Index + 最短证据链）

## Technical Solution

### Core Technologies

- 文档载体：Markdown（模块内 `*/docs/**`）
- 可验证入口：JUnit5 + Maven Surefire（`mvn -pl :spring-core-beans test` / `mvn -pl :spring-core-aop test`）
- 断点/证据链：Spring Framework 6.x（Spring Boot 3.x）

### Implementation Key Points

#### 1) 信息架构升级：引入模块级 Why Index（基础问题索引）

问题不是“内容不存在”，而是“读者无法在正确时机找到/拼出论证链”。因此需要新增一个**答案先行**的入口页，作为模块内基础问题的 SSOT：

- 每个条目固定结构：
  1) 一句话结论（Answer）
  2) 为什么重要（Why it matters）
  3) 最短证据链（Proof in 10 minutes）
     - 推荐 Lab/Test（可运行命令）
     - 关键断点（3 个以内稳定锚点）
     - watch list（3–5 个关键变量/集合）
  4) 常见误区对照（Misconceptions）
  5) 下一步章节导航（Next reading）
- Why Index 必须被显式链接到：
  - 模块 docs/README（目录页）
  - 模块 README（仓库入口）
  - 与该问题最相关的章节顶部（防止读者在正文中迷路）

#### 2) 跨模块前置依赖回链：Beans ↔ AOP Proxy

本项目的真实困难点在于：三级缓存/early reference 这类问题，必须先有“最终暴露对象可能被包装成 proxy/wrapper”的前置心智模型；而这个心智模型又依赖 AOP/代理机制的理解。

因此需要把跨模块依赖“写死”为显式导航链：

- Beans 模块（循环依赖/early reference/代理替换）：
  - 在 `09/16/31` 章节开头补充“前置：Proxy 心智模型/Call Path”的跳转链接；
  - 用一句话把问题归位：三级缓存解决的是“窗口期提前交付引用”，`getEarlyBeanReference` 解决的是“early 是否等于 final（proxy/wrapper）”。
- AOP 模块（代理心智模型/AutoProxyCreator）：
  - 在 `代理心智模型` 与 `AutoProxyCreator 主线` 章节补充“前置：Bean 创建阶段与 proxy 替换发生点”的跳转链接；
  - 明确 AOP 的代理替换依赖 Beans 主线：BPP 在 bean 创建过程中可能返回替身对象（proxy）。

#### 3) 最短闭环（10/30/3）落地为“可执行清单”

为了避免“写了很多，但读者不知道怎么用”，需要把最短闭环固化成清单与命令：

- 10 分钟：一个命令跑通 + 一个断点看见关键分支
- 30 分钟：3–5 个断点观察点把数据结构变化看见
- 3 分钟：给出可复述模板（结论 → 证据链 → 反例/边界）

该清单应在：
- Beans：`docs/README.md` 与 `part-00-guide/012-01-quickstart-30min.md` 强化入口
- AOP：`docs/README.md` 与 `part-00-guide/029-00-deep-dive-guide.md` 强化入口

#### 4) 命名与可检索性：让“读者的搜索词”命中

真实读者通常会搜索：
- “三级缓存 / three level cache”
- “earlySingletonObjects / singletonFactories”
- “getEarlyBeanReference”
- “raw vs wrapped / allowRawInjectionDespiteWrapping”
- “AOP proxy / call path / self invocation”

实现阶段应确保：
- Why Index 的标题与小节包含这些关键词（中英都出现一次）
- 模块 docs/README 在“症状驱动导航/快速定位”里能命中这些词

## Architecture Decision ADR

### ADR-001: 引入模块级 Why Index 作为基础问题 SSOT

**Context:**  
基础问题在章节内分散存在，但读者难以在正确时机检索到答案；跨模块前置依赖（Beans ↔ AOP Proxy）未被显式表达，导致“必须拼图才能理解”。

**Decision:**  
为 `spring-core-beans` 与 `spring-core-aop` 引入模块级 Why Index（基础问题索引页），并在 docs/README、模块 README、关键章节顶部强制挂载入口；同时补齐跨模块互链，形成稳定学习路径。

**Rationale:**  
- 提升可检索性：让问题先命中索引页，而不是让读者在长文里盲找  
- 提升可验证性：每个问题都绑定 Lab/断点闭环  
- 提升一致性：跨模块前置关系显式化，降低认知断层

**Alternatives:**  
- 仅在现有章节中追加解释 → 拒绝原因：仍然分散，读者需要拼图  
- 单独写“三级缓存专章” → 拒绝原因：只能解决一个问题，不能提升整体“基础问题可解释性”能力

**Impact:**  
- 文档结构更“问题驱动”，降低读者入门成本  
- 需要维护跨模块链接与入口一致性（可用脚本/自检步骤缓解）

## Security and Performance

- **Security:** 本变更为文档与测试入口增强，不涉及敏感信息与权限变更；仍需避免在文档中鼓励读者执行危险命令（如 destructive 操作）。
- **Performance:** 新增/调整内容主要为文档；若新增 Lab，应控制为最小上下文加载，避免显著增加默认回归耗时。

## Testing and Deployment

- 文档一致性自检：
  - 关键链接不应断链（实现阶段执行一次全局 `rg` 检查 + 按需补最小脚本）
- 功能回归：
  - `mvn -pl :spring-core-beans test`
  - `mvn -pl :spring-core-aop test`

