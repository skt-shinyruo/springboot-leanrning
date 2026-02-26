# Task List: spring-core-beans 文档书本化（Bookify）

Directory: `helloagents/plan/202601032012_spring-core-beans-bookify-docs/`

---

## 1. spring-core-beans（Docs IA + TOC）
- [√] 1.1 新增目录页 `docs/beans/spring-core-beans/README.md`：给出 Part 概览 + 主线阅读路径，verify why.md#目录页--part-结构像书一样从目录开始顺读
- [√] 1.2 设计 Part 目录结构与“章节映射表（旧→新）”，并在目录页固化，verify why.md#目录页--part-结构像书一样从目录开始顺读

## 2. spring-core-beans（章节迁移：按 Part 分批）
- [√] 2.1 迁移 Part 1（IoC Container 主线）章节到新目录（含重命名/重新编号），并修复本 Part 内互链，verify why.md#章节导航与链接正确
- [√] 2.2 迁移 Part 2（Boot 自动装配与可观察性）章节到新目录，并修复互链，verify why.md#章节导航与链接正确
- [√] 2.3 迁移 Part 3（Container Internals 与扩展点）章节到新目录，并修复互链，verify why.md#章节导航与链接正确
- [√] 2.4 迁移 Part 4（装配语义与边界）章节到新目录，并修复互链，verify why.md#章节导航与链接正确
- [√] 2.5 迁移 Appendix（pitfalls/self-check）章节到新目录，并修复互链，verify why.md#章节导航与链接正确

## 3. spring-core-beans（章节模板 A–G 标准化）
- [√] 3.1 为所有章节补齐统一“顶部导航（上一章｜目录｜下一章）”，verify why.md#章节模板一致a–g
- [√] 3.2 为所有章节补齐 A/B：本章定位 + 核心结论（3–7 条），verify why.md#章节模板一致a–g
- [√] 3.3 为所有章节补齐 C/D：机制主线 + 源码解析（关键类/方法/分支 + 伪代码），verify why.md#章节模板一致a–g
- [√] 3.4 为所有章节补齐 E/F：最小可复现实验入口 + 常见坑/反例，verify why.md#章节模板一致a–g
- [√] 3.5 为所有章节补齐 G：本章小结 + 下一章预告（固定输出），verify why.md#章节模板一致a–g

## 4. spring-core-beans（全局链接修复与一致性审计）
- [√] 4.1 更新 `spring-core-beans/README.md` 的 docs 链接/阅读顺序索引，确保入口指向新目录，verify why.md#章节导航与链接正确
- [√] 4.2 全局扫描并修复 `docs/beans/spring-core-beans/**` 内部链接（含 cross-part 引用），并做一次“目录可达/上下章可达”审计，verify why.md#章节导航与链接正确

## 5. Security Check
- [√] 5.1 执行安全自检（G9）：确认 docs 不引入敏感信息、不粘贴大段第三方源码、链接不指向生产/内部地址

## 6. Knowledge Base Sync
- [√] 6.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：同步新的 docs 书本结构与主线阅读路径
- [√] 6.2 更新 `helloagents/CHANGELOG.md`：记录本次“docs 书本化”重大结构调整

## 7. Testing
- [√] 7.1 运行 `mvn -q -pl spring-core-beans test`，确保 Labs/Exercises 不受影响（文档引用入口仍存在）
