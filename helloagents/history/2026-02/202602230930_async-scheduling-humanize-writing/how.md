# Technical Design: spring-boot-async-scheduling “人写化”改造（docs + DemoRunner）

## Technical Solution

### Writing Strategy（核心写作策略）

目标不是把内容“写漂亮”，而是让读者产生一种感受：

> 这像一个熟悉 Spring 的工程师/作者写的：有场景、有判断、有取舍、有证据入口，但不把读者当成被训练的对象。

具体策略：

1. **正文先讲明白，再给验证入口**
   - 正文：用连续段落讲“为什么”，把分支与边界解释清楚
   - 章末：统一用“进一步验证/自己跑一遍”的方式给出 `LabTest#method` 指针
2. **减少模板句与驱动式口吻**
   - 弱化“建议/你应该/先跑再读/你应该观察到什么”的重复句式
   - 用更自然的叙述：先描述现象 → 再解释机制 → 再给判断方法
3. **列表用于“枚举”，不用列表替代论证**
   - 需要推理的段落尽量用自然段
   - 只有在“列举候选/列举坑位/列举观察点”时才使用列表
4. **术语降噪**
   - 控制抽象词密度（闭环/手册级/证据链等），尽量用更具体的词：现象、边界、判断、复现入口
5. **结构性标记保留，但不让它决定写作**
   - 保留 `CHAPTER-CARD/GLOBAL-BOOK-NAV/BOOKIFY`（站点/导航需要）
   - 但正文不再围绕模板块组织，而围绕读者理解路径组织

### Docs Editing Approach（改写落地方式）

按“从入口到正文，再到附录”的顺序推进，保证读者体验逐步变好：

1. `docs/README.md` / `117-03-mainline-timeline.md` / `118-00-deep-dive-guide.md`：先把阅读路线写成作者导言
2. 主线章节（119/120/121/122/123/126/127/128）：逐章改写正文叙事，把验证入口下移
3. Appendix（124/125）：把 Pitfalls 写成排障短文，把 Self-check 写成习题册式自检
4. 最后回扫矩阵/断点地图（118-02/118-04）：保留功能，但改写为“读者真的会用”的文本

### DemoRunner Output Strategy（可运行示例输出改造）

目标：`mvn -pl :spring-boot-async-scheduling spring-boot:run` 的输出读起来像作者讲解，而不是机器 dump。

改造方式：

- 输出分节：
  - Executor/线程名（帮助读者建立最稳定的观测点）
  - 事务边界（调用方 txActive vs 异步线程 txActive）
  - SecurityContext（默认/Delegating* 对比）
  - Boot `spring.task.*`（属性读取 + 实际线程名验证）
- 每节最多 6–10 行，不做长篇输出；保留关键值（线程名/active/null）即可
- 保留一次性退出：DemoRunner 最后主动 close context，避免非 web 应用常驻

## Security and Performance

- **Security:** DemoRunner/文档不输出任何真实凭证；安全上下文使用测试 token；避免打印 token/password。
- **Performance:** 文档改写不影响性能；DemoRunner 仅做极少量异步调用与状态输出。

## Testing and Deployment

- **Tests:** `mvn -q -pl :spring-boot-async-scheduling test`（至少连续 3 次）
- **Run demo:** `mvn -pl :spring-boot-async-scheduling spring-boot:run`（确认输出可读、无长时间阻塞）
