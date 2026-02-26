# Technical Design: spring-core-beans 文档逐章深度完善（机制 / 源码 / 排障）

## Technical Solution

### Core Technologies

- 文档格式：Markdown（MkDocs Material 渲染，支持 admonition）
- 文档站点：`docs-site/`（MkDocs + `mkdocs-literate-nav` 读取 `docs/SUMMARY.md`）
- 依赖版本（用于“适用版本”标注）：
  - Spring Boot：`3.5.9`
  - Spring Framework：`6.2.x`（本仓库文档索引基线：`6.2.15`）

### Implementation Key Points

本次改造不是“给每章套同一模板”，而是按章节主题与现有内容的强弱，分别补齐最关键的拼图。总体执行策略分三层：

#### 1) 章节内容层（正文）

目标：把章节从“解释”提升到“可证明/可排障”。

- 将关键结论落到“方法级证据链”（入口方法 → 关键分支 → 数据结构/关键变量 → 结果形态）。
- 为章节补充更贴合主题的排障分流（异常/现象 → 第一入口方法 → 第一观察点 → 下一跳章节）。
- 对容易误判的边界补“反例/对照实验”提示（优先绑定现有 Lab/Test）。

#### 2) 继续加深层（deepening-strategies + AE-DEEPENING）

目标：把“下一步动作”写清楚且差异化。

- 以 `spring-core-modules/spring-core-beans/docs/deepening-strategies/` 为“继续加深建议”的 SSOT（按 Part 与章节组织）。
- 将各章正文中的 `AE-DEEPENING` 提示块对齐到对应策略文件，补齐：
  - 关键分支（If/Then）
  - 固定 watch list（3–5 个决定性变量）
  - 至少 1 个可复现的边界反例

#### 3) 官方文档对齐层（权威对照）

目标：让读者能“以官方 reference 为准”快速核对概念定义与边界差异。

- 每章新增或补强 `官方文档与延伸阅读（建议）` 区块：
  - Spring Framework Reference：尽量链接到具体页面（例如 beans / scopes / factory extension / expressions / aot）
  - Spring Boot Reference：对涉及 auto-config/条件评估/报告的章节补链接
- 版本标注采用“文字标注 + 稳定 URL”策略：
  - URL 尽量使用 `.../reference/...` 的稳定入口
  - 版本以文字标注（例如“适用版本：Spring Framework 6.2.x（本仓库基线 6.2.15）”），避免版本路径调整导致死链

## Draft Deliverables（交付方式 B：先看建议再落盘）

你明确希望“不要固定模板/不要标准化填空”，因此本次不再提供自动化的“章节结构/元信息”清单文件。

交付方式 B 将改为：

- 直接逐章阅读正文，给出**该章专属**的“补充/完善/深入点”（以机制 + 源码入口 + 排障路径为主）
- 你确认写作风格后，立即落盘到对应章节文档（不再额外维护一份模板化草案文件）

> 如你仍希望先看“建议”，我会以“按章节逐个输出的评审笔记”形式给出（每章内容差异化，不固定栏目），而不是生成结构化清单。

## Security and Performance

本次为文档改造，不涉及运行期性能，但需要做“文档安全性”与“工程可用性”检查：

- **安全性：**
  - 避免在文档中给出危险命令（例如破坏性 `rm -rf`）或默认建议降低安全配置
  - 对安全相关结论（例如循环依赖开关、raw injection）强调工程风险与推荐优先级（重构 > 折中开关）
- **工程可用性：**
  - 站点能构建（MkDocs build）
  - 相对链接有效（尤其是跨章节跳转）

## Testing and Verification Strategy

- 文档链接与站点校验：`mkdocs build -f docs-site/mkdocs.yml`（需要安装 `docs-site/requirements.txt`）
- 语义回归（可选但推荐）：`mvn -pl :spring-core-beans test`（确保文档提到的入口依然存在）
