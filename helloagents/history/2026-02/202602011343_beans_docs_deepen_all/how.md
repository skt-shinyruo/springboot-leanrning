# Technical Design: spring-core-beans docs 逐章继续深化（按章差异化 + 跨模块互链）

## Technical Solution

### Core Technologies
- 文档：Markdown（现有 `spring-core-modules/spring-core-beans/docs/**` 体系）
- 校验工具：
  - `rg` 做全量扫描（缺口定位、占位清理、互链盘点）
  - `python3 - <<'PY' ... PY` 方式做“相对链接目标存在性”与“引用的 Lab/Test 存在性”自检（不新增脚本、不引入新依赖）
- 回归测试：
  - `mvn -pl spring-core-modules/spring-core-beans test`

### Implementation Key Points

#### 1) 逐章阅读 → 逐章策略清单（决定“怎么补强”）
对 `spring-core-modules/spring-core-beans/docs/**` 的每一篇文档进行阅读，生成“逐章继续深化策略清单”：

- 输出位置：`helloagents/plan/202602011343_beans_docs_deepen_all/audit/chapter-strategies.md`
- 写法原则：只写与该章内容相关的补强策略；不引入统一小标题/固定骨架；不做“全章重写”式的无差别改动
- 关注点：该章当前已经有的入口（源码锚点/推荐用例/互链）与读者最可能卡住的缺口（在哪里补一句解释就能降低成本，在哪里需要补一个可跑入口才能闭环）

该策略清单在执行阶段充当“施工图”：修改时以每章策略为准，不强行套统一模板。

#### 2) 执行阶段：按章差异化落地（不套模板）
执行时的核心原则是：**每章按自身主题补强**，而不是“把所有章节补成同一种形态”。

典型的补强动作包括（不要求每章都包含，按需要选择）：
- 把文内已经出现的源码入口串成更清晰的主线（读者能知道从哪进、在哪个分支停、结论是什么）
- 把文内已经出现的 Lab/Test 与正文关键论点绑定（跑完后知道去哪里断点验证）
- 把“容易误判”的点收敛成可操作的排错路径（让读者能把主观判断变成可验证步骤）
- 对涉及 proxy/事务/自调用的章节补齐最短跨模块跳转建议（为什么要跳、跳过去验证什么）

#### 3) 跨模块互链策略（Beans → AOP）
本次范围以 Beans docs 为主：仅在 Beans 章节中补齐对 AOP 章节的跳转建议与说明，不扩散修改到 AOP docs（除非发现断链或明显误导）。

对于跨模块互链，要求写清楚两件事：
1. **为什么要跳**：当前章的机制在 Beans 视角只能解释到哪里，哪个问题需要 AOP 视角补齐
2. **跳过去验证什么**：给出最短断点/证据链入口（例如：proxy 创建点、self-invocation 行为、advisor/interceptor 执行顺序）

#### 4) 批次化执行（控制风险）
按目录分批（便于 review 与回归），并以策略清单为准滚动推进：
- deepening-strategies（优先把“怎么继续深化”写成可操作路线，而不是维度清单）
- Part-00 ~ Part-05（按章节顺序滚动）
- Appendix（pitfalls/self-check/debugger pack 等需要与正文一致）

每个批次建议至少做一次“链接自检 + 引用自检”；关键批次（或合批结束）再跑模块测试。

## Security and Performance

- **Security:** 仅文档修改，不新增密钥/token/内网地址/个人信息；示例命令与日志片段不包含敏感参数。
- **Performance:** 文档改动不引入运行时开销；回归以模块测试为主，避免全仓耗时验证。

## Testing and Deployment

- **Testing:** `mvn -pl spring-core-modules/spring-core-beans test`
- **Docs self-check:**
  - 术语与占位清理扫描：`rg -n \"TODO|FIXME|未完\" spring-core-modules/spring-core-beans/docs`
  - 相对链接目标存在性检查（beans docs 全量，检查文件是否存在）
  - Lab/Test 引用存在性检查（beans docs 全量，校验引用到的测试类/路径真实存在）
- **Deployment:** 无发布动作；执行完毕后仅需迁移方案包到 `helloagents/history/YYYY-MM/` 并更新索引
