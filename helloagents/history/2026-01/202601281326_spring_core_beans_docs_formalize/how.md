# Technical Design: spring-core-beans docs 书面化改写（去口语化）

## Technical Solution

### Core Technologies

- Markdown 文本批处理（跳过 fenced code block）
- 基于关键短语的定向改写（避免“机械替换导致语义错误”）

### Implementation Key Points

1. **改写目标定义（可执行口径）：**
   - 去除第二人称叙述（“你/你会/你能/你必须/如果你…”），改为“陈述式/规范式/被动式/读者视角（第三人称）”。
   - 去除俚语与口语化词汇（如“踩坑/翻车/别慌/很香/一把梭”等），改为“易错点/高频错误/注意事项”等书面词汇。
   - 保留技术名词、类名/方法名、路径、命令与代码片段，不做语义改写。

2. **改写范围控制：**
   - 仅修改 `spring-core-modules/spring-core-beans/docs/**/*.md`。
   - 不新增“章节桥接/格式性段落”；不调整目录结构与文件命名。

3. **Markdown 安全边界：**
   - 对 fenced code block（```...```）内内容不做替换，避免破坏代码示例与命令。
   - 仅对正文段落、列表项与标题进行措辞改写。

4. **验证策略：**
   - 关键短语扫描：改写后对 docs 目录进行 `rg` 扫描，确保主要口语标记显著减少（如“你/如果你/踩坑/翻车/一句话自检”等）。
   - 抽查回读：对“调用链/决策表/面试答案/排障清单”类章节抽查 5–10 个片段，确保结论仍准确、方法名未被误改。

## Security and Performance

- **Security:** 文档改写不引入外部链接依赖，不新增敏感信息；避免示例出现危险命令（DROP/TRUNCATE/rm -rf 等）。
- **Performance:** 仅文本改写，对运行时无影响。

## Testing and Deployment

- **Testing:** 不涉及代码行为变更；可选执行 `mvn -pl :spring-core-beans test` 作为回归验证（非阻塞）。
- **Deployment:** 无。

