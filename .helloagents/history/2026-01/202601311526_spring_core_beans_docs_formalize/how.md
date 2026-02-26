# Technical Design: spring-core-beans 文档书面化改写（去口语化）

## Technical Solution

### Core Technologies
- Markdown（文档载体）
- ripgrep（快速定位口语化短语与第二人称表达）
- Maven（用于回归验证，确保仅发生预期的文档改动）

### Implementation Key Points
1. **范围界定（默认）**
   - 目标：`spring-core-modules/spring-core-beans/**/*.md`（排除 `target/`）
   - 不改动：代码块、命令、类名/方法名/注解名、文件路径与相对链接结构
   - 可选扩展：`src/test/**` 中用于练习说明的文本（字符串/注释）；**本次执行未启用该扩展**

2. **书面化改写要求（写作口径）**
   - 叙述视角：避免使用“你/我们/咱们”等对话式人称；改用“本文/本章/本节/读者/开发者”等书面表述。
   - 表达方式：避免使用“先…再…/别急/来看”等口语化引导；改用说明式表达（例如“可依次…”，“推荐…”，“本节说明…”）。
   - 词汇选择：将“讲透/搞明白/拿到/踩坑/坑位/背出来”等口语化表达调整为更中性、可检索的技术表述（例如“系统阐述/理解/获得/常见误区/掌握”）。
   - 句式控制：减少感叹句与反问句，优先使用陈述句；避免情绪化修饰词与口语助词。
   - 术语一致性：保留既有技术术语（例如 IoC、BeanDefinition、BPP/BFPP 等），避免在不同章节之间出现不一致翻译。

3. **执行策略**
   - 先改写“入口文档”：模块 `README.md` 与 `docs/README.md`，建立统一语体基线。
   - 再按目录分批改写：`docs/part-00-guide` → `docs/part-01-ioc-container` → `docs/part-02-boot-autoconfig` → `docs/part-03-container-internals` → `docs/part-04-wiring-and-boundaries` → `docs/part-05-aot-and-real-world` → `docs/deepening-strategies` 与 `docs/appendix`。
   - 每批次完成后执行一次“关键词扫描”，用于发现潜在口语化表述；最终改写以全文语境为准，避免机械替换。

4. **验收口径（Definition of Done）**
   - 目录范围内 Markdown 文档完成书面化改写。
   - 对关键词清单执行扫描，结果为 0（允许出现在代码块中的情形需人工确认并明确保留理由）。
   - 文档中的相对链接可用（抽检关键入口文档与跨章节导航）。

5. **逐句改写执行方式（人工语义改写）**
   - 以“逐篇阅读全文 + 逐句语义改写”为执行方式，避免基于固定替换规则的机械改写。
   - 对每一处口语化表达，结合上下文将其改写为书面表达；必要时对句式进行调整，以保证语义严谨与叙述连贯。

## Security and Performance
- **Security:** 文档改写不引入敏感信息；不新增密钥/令牌等内容；命令示例保持最小必要权限。
- **Performance:** 变更不影响运行时性能；仅增加文档维护工作量，已通过分批改写与扫描降低风险。

## Testing and Deployment
- **Testing:**
  - 快速一致性检查：对目标目录执行关键词扫描（用于发现潜在口语化表述，最终以语境人工改写为准）。
  - 回归验证：`mvn -pl :spring-core-beans test`（确保未引入非预期代码变更或构建异常）。
- **Deployment:** 无部署流程变更；文档提交后即可生效。
