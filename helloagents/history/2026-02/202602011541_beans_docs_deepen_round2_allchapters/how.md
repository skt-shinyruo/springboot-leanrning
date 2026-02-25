# Technical Design: spring-core-beans docs Round 2（全量逐章差异化继续深化）

## Technical Solution

### Core Technologies
- 文档：Markdown（现有 `spring-core-modules/spring-core-beans/docs/**`）
- 校验与回归：
  - 相对链接目标存在性检查（beans docs 全量）
  - 引用的测试类/文件路径存在性检查（beans docs 全量）
  - `mvn -pl spring-core-modules/spring-core-beans test`

### Implementation Key Points

#### 1) Round 2 的“施工图”怎么用（避免重新变成模板）

本轮的核心交付是逐章施工图：

- `helloagents/history/2026-02/202602011541_beans_docs_deepen_round2_allchapters/audit/chapter-strategies.md`

执行阶段对每一章的处理方式建议是“先读现状，再选最值钱的一刀”：

1. 先通读该章当前内容（特别是卡片/导读/断点建议/配套实验段落）
2. 对照施工图，挑 1–3 个最能降低读者成本的补强点（不追求面面俱到）
3. 把补强点落到“可验证动作”上（跑哪个用例、在哪个断点看什么、如何排除误判）
4. 章节末尾补一条“最短下一跳”（仅在确有必要时；写清跳转目的与验证入口）

> 本轮不追求统一写法：同样是“补闭环”，有的章适合用对照实验，有的章适合用最短排错路径，有的章只需要更清晰的承接与跳转目的。

#### 2) 施工顺序建议（不是硬规则）

如果希望先做“牵一发而动全身”的部分，建议优先顺序为：

1) 导航/工具页（让读者能更快进入下一轮）
- `docs/README.md`
- `appendix/03-knowledge-map.md`
- `part-00-guide/07-breakpoint-map.md`
- `appendix/05-production-troubleshooting-checklist.md`

2) 主线核心章节（读者最常复盘的骨架）
- refresh 主线与 doCreateBean 相关章节（Guide + Internals）
- 依赖解析/生命周期/BPP 相关章节（IoC container）

3) 边界与真实工程误区（最容易误诊、最需要对照）
- wiring & boundaries（候选选择/代理替换/占位符/类型转换/泛型匹配等）

4) AOT 与真实世界扩展（按需加深，避免抢主线篇幅）

> 若你的目标是“按目录顺读继续加深”，可以直接按 Docs TOC 顺序逐章落地；施工图按章节编号已覆盖全量。

#### 3) 质量门禁（执行阶段的最小闭环）

- 每完成一批章节（例如 5–10 篇），做一次：
  - 相对链接目标存在性检查
  - 引用的测试类/文件路径存在性检查
- 全量完成后做一次：
  - `mvn -pl spring-core-modules/spring-core-beans test`

#### 4) 文风与用词（针对你的反馈做硬约束）

- 避免“口号式抽象词”充当解释（例如把“理解了”写成“能复现/能验证/能排错”）。
- 避免为了统一而统一：不强制每章出现相同的小标题或同样的段落结构。
- 避免堆链接列表：跨章节跳转必须写清“为什么跳、去哪里看、如何验证”。

## Security and Performance

- **Security:** 文档示例不包含密钥/token/内网地址/个人信息；命令行示例不带敏感参数。
- **Performance:** 仅文档与知识库变更，不引入运行时开销。

## Testing and Deployment

- **Testing:** `mvn -pl spring-core-modules/spring-core-beans test`
- **Docs self-check:** 相对链接与引用路径全量检查（执行阶段跑）
- **Deployment:** 无发布动作；执行完成后迁移方案包到 `helloagents/history/YYYY-MM/` 并更新 `helloagents/history/index.md`

## 如需继续深化下一轮，建议从

如果 Round 2 执行完成后仍要继续加深（Round 3），优先从“高复用中枢页 + 高误判章节”开始，收益最大：

1) 中枢页（全局跳转与排错入口）：
- `appendix/03-knowledge-map.md`
- `part-00-guide/07-breakpoint-map.md`
- `appendix/05-production-troubleshooting-checklist.md`

2) 高误判章节（容易把问题看错层/看错时机）：
- 依赖解析/候选选择相关章节（NoSuch/NoUnique/Primary/Priority/Order）
- early reference / proxy 替换相关章节（循环依赖与代理不生效）
- `@Value`/占位符/SpEL/类型转换相关章节（最容易混成一个问题）

Round 3 的增益点不在“补更多信息”，而在“把路径再压短”：让读者从现象到结论的动作更少、证据更直接。
