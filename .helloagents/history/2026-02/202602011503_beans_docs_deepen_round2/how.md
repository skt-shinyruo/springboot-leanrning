# Technical Design: spring-core-beans docs 继续深化（Round 2：从入口页/工具页开始）

## Technical Solution

### Core Technologies
- 文档：Markdown（现有 `spring-core-modules/spring-core-beans/docs/**`）
- 校验工具：
  - `rg`：定位入口页/工具页的缺口点、重复点与潜在断链
  - `python3`：做相对链接目标存在性检查、引用测试文件存在性检查（不引入新依赖，不新增脚本文件）
- 回归测试：
  - `mvn -pl spring-core-modules/spring-core-beans test`

### Implementation Key Points

> 具体落地策略（按文件）见：
> - `helloagents/history/2026-02/202602011503_beans_docs_deepen_round2/audit/entrypoints-round2.md`

#### 1) 建议从哪里开始（Round 2 的施工顺序）

Round 2 的目标是“让读者更快进入下一轮”，因此施工顺序建议按“入口 → 工具 → 承接章节”：

1. `spring-core-modules/spring-core-beans/docs/README.md`（Docs TOC / 症状驱动导航）
2. `spring-core-modules/spring-core-beans/docs/deepening-strategies/README.md`（策略入口：告诉读者如何使用策略）
3. `spring-core-modules/spring-core-beans/docs/appendix/03-knowledge-map.md`（从现象直达章节/断点/Lab）
4. `spring-core-modules/spring-core-beans/docs/part-00-guide/07-breakpoint-map.md`（断点地图：可复用观察点）
5. `spring-core-modules/spring-core-beans/docs/appendix/05-production-troubleshooting-checklist.md`（生产排障清单：最短诊断路径）

> 如执行过程中发现“某条路径需要正文承接（否则读者跳过去看不懂）”，再按需选择 0–3 个承接章节做最小补强；避免扩大范围导致再次全章模板化。

#### 2) 入口页策略：用“选择题”替代“说明书”

入口页要解决的问题不是“信息是否完整”，而是“读者下一步是否明确”：

- 以读者目标分流：卡在现象/想系统深挖/想做排障训练
- 每条分流都给出“下一步动作”：跑哪个 Lab/Test、在哪个断点看什么、如何自证结论
- 控制为最短路径：避免堆链接列表

#### 3) 工具页策略：把“索引”做成“可复用闭环”

工具页的增益来自“把主观判断变成可验证步骤”，因此优先补：

- 最短诊断路径（3–5 步）
- 第一个断点/观察点（读者停下后看什么变量/对象能判断分支）
- 与章节正文、Lab/Test 的回链（确保读者能从工具页走回证明链）

#### 4) 质量门禁：批次化修改 + 自检 + 回归

- 每改完一批（入口页/工具页之一），做一次：
  - 相对链接目标存在性检查
  - 引用测试文件存在性检查
- 合批完成后跑一次模块测试：
  - `mvn -pl spring-core-modules/spring-core-beans test`

#### 5) 关键文件的“具体写法”建议（避免抽象化与模板化）

本轮的中心思想是：入口页/工具页不负责讲机制细节，只负责把读者送到“可以证明结论”的下一步。

1) `docs/README.md`（入口页）
   - 目标：让读者看到入口页后，能立刻选中 1 条最短路线，并知道下一步要跑什么/看什么。
   - 写法：用 2–3 条目标分流（现象驱动/断点驱动/生产排障驱动）给出“下一步动作”；避免把 README 扩写成另一份知识地图。

2) `deepening-strategies/README.md`（策略入口）
   - 目标：让策略页回答“如何继续加深”，而不是“策略有哪些维度”。
   - 写法：明确两种进入方式：
     - 从现象进入：先跳到知识地图定位章节与入口用例，再回到章节正文的 `AE-DEEPENING` 做加深。
     - 从断点进入：先在断点地图选断点组确定阶段，再回到章节把观察收敛为结论/反例/排错路径。

3) `appendix/03-knowledge-map.md`（知识地图）
   - 目标：从现象直达“章节 + 断点组 + 推荐 Lab/Test”，减少读者自己拼图的次数。
   - 写法：高频行优先升级：
     - 给断点入口增加回链（能跳到断点地图的对应组），必要时配合断点地图补稳定锚点。
     - 推荐 Lab/Test 只升级最常用现象（3–5 行），避免表格过度膨胀。

4) `part-00-guide/07-breakpoint-map.md`（断点地图）
   - 目标：让断点地图更像“可复用观察点套件”，能从症状快速选断点组。
   - 写法：新增一个极短的“从症状选择断点组”的分流（覆盖 5 类高频即可），并回链到知识地图。

5) `appendix/05-production-troubleshooting-checklist.md`（生产排障清单）
   - 目标：把条目进一步压缩成可复用的最短诊断路径（3–5 步），并为每一步提供章节/Lab/断点组的回链。
   - 写法：优先挑 3 类高频事故写“最短路径”，不要追求覆盖所有条目都升级。

## Security and Performance

- **Security:** 文档改动不引入敏感信息（密钥/token/内网地址/个人信息）；示例命令不包含敏感参数。
- **Performance:** 仅文档与知识库改动；不引入运行时开销。

## Testing and Deployment

- **Testing:** `mvn -pl spring-core-modules/spring-core-beans test`
- **Docs self-check:**
  - 断链扫描与引用扫描：使用 `python3` 在本地执行（不新增脚本文件）
- **Deployment:** 无发布动作；执行完成后迁移方案包到 `helloagents/history/YYYY-MM/` 并更新 `helloagents/history/index.md`
