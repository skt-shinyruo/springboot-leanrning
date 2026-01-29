# Technical Design: spring-core-beans 逐章深度完善（Chapter-driven Deepening）

## Technical Solution

### Core Technologies
- **Docs:** Markdown（`spring-core-modules/spring-core-beans/docs/**.md`）
- **Verification:** Maven Surefire（模块测试/Lab）
- **Debugging:** IntelliJ IDEA Debugger（断点/观察点），以“方法级证据链 + watch list”固化机制

### Implementation Key Points

1. **逐章阅读 → 逐章补强（不套固定模板）**
   - 不预设“每章必须补哪些模块/固定字段”，而是按章节主题梳理：核心主线、关键分支、常见误区、边界条件、排障入口。
   - 对“概念易背但难证明”的点，优先补“方法级证据链 + 可运行验证”。

2. **章节间串联优先（把碎点连成主线）**
   - 把 Part 00 的“主线/断点地图”作为全书导航底座。
   - 对每章补充“上一章/下一章以外”的横向链接：例如把 `FactoryBean` 与 `type matching`、`merged bean definition`、`AOT hints` 串起来。

3. **验证优先（Lab/Test 与文档同步）**
   - 每个章节至少明确：推荐的 Lab/Test、预期现象、关键断点、观察变量。
   - 对边界条件（如 `@Lazy` 注入点代理、`getObjectType` 返回 `null`、early reference 与最终对象不一致）优先补可运行的最小复现。

4. **一致性与可维护性（但不强行统一内容）**
   - 对缺失 `CHAPTER-CARD` / `GLOBAL-BOOK-NAV` / `BOOKIFY` 的章节，按需要补齐导航锚点，减少读者迷路成本。
   - 对过长章节（如注册入口、refresh→doCreateBean 主线）优先做“索引/导航/分层”，避免只堆内容。

5. **逐章策略索引**
   - 每章的“补充/完善/深入策略”已拆分到本方案包的 `chapters/*.md`，便于按 Part 执行与回归。

## Security and Performance

- **Security:** 本次以文档与测试为主，无直接安全面；但涉及 SpEL / 表达式解析章节会补充“SpEL 注入风险与安全建议”的内容（属于文档层安全提示）。
- **Performance:** 仅在必要时新增少量测试；默认回归仍以 `mvn -pl :spring-core-beans test` 为基线，避免引入长耗时用例。

## Testing and Deployment

- **Testing:**
  - 运行模块全部测试：`mvn -pl :spring-core-beans test`
  - 章节/实验定向回归：`mvn -pl :spring-core-beans -Dtest=<TestClassName> test`
  - Explore/Debug（可选）：`mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeans*ExploreTest test`
- **Deployment:** 无（学习型模块文档与测试变更）

