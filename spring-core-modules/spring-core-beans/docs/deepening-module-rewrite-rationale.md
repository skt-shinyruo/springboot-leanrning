# 模块级重写理由：把文档、Lab 与测试支撑层绑成证据链

## 定位：为什么允许模块级重写

`spring-core-beans` 的目标不是堆积 Spring Beans 知识点，而是让读者能把一个现象追到可运行证据：章节解释机制，Lab 固定事实，断点与测试支撑工具暴露关键变量。只要重写能让这条链路更短、更稳定，就应优先重写。

本轮重写采用三层结构：

1. **入口层（README / Guide / Appendix）**：负责把问题送到正确章节、Lab 和断点。
2. **证据层（`*LabTest` / Matrix / Pack）**：负责把章节结论跑成断言。
3. **观察层（`testsupport`）**：负责把 BeanDefinition、依赖边、注入点等内部对象稳定输出，避免每章重复写 dump 逻辑。

## 重写原则

- **README 是唯一顺序来源**：`docs/` 目录保持扁平，正文页不再维护上一章/下一章，避免导航漂移。
- **正文必须绑定 Lab**：每篇正文都应能回答“对应现象如何运行、在哪下断点、看什么变量”。
- **测试套件表达学习路线**：Book Matrix、Branch Matrix、Breakpoint Pack 不是随机聚合，而是面向最小闭环、关键分支和排障入口。
- **支撑工具只做可观察性**：`testsupport` 不承载业务结论，只把内部结构变成稳定文本，便于断言和文档复用。
- **重写必须有回归契约**：文档链接、章节入口标记、测试类引用不能只靠人工记忆维护。

## 高收益改写点

| 改写点 | 好处 | 验证方式 |
| --- | --- | --- |
| README 从链接清单改成“入口 + 路线 + 症状导航 + 顺序目录” | 新读者能在 1 分钟内找到章节、Lab 和断点入口 | `SpringCoreBeansDocumentationContractTest` 校验 README 链接与目录覆盖 |
| Guide/Appendix 从重复说明改成工具页 | 避免每篇正文重复解释主线，把工具页职责固定为导航、排障、自检 | 本地 Markdown 链接校验与章节入口标记校验 |
| Lab 套件按使用场景命名 | `Book Matrix` 负责最小学习闭环，`Branch Matrix` 负责关键分支，`Breakpoint Pack` 负责排障断点 | Maven 可直接运行对应 suite |
| `testsupport` 聚焦 Dumper | 章节不需要重复写低层反射/BeanFactory dump 代码，输出也更稳定 | Dumper 自测 + 章节 Lab 引用 |
| 新增模块契约测试 | 后续任意重写都能 fail-fast 地发现链接断裂、章节入口缺失、文档引用了不存在的 Test，或 testsupport 输出漂移 | `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansModuleContractLabTest test` |

## 后续重写验收口径

一次模块级重写至少要说明：

1. **减少了什么认知成本**：例如少一次跳转、少一个重复概念、少一个不确定入口。
2. **增强了什么证据链**：例如新增断言、补齐 Lab 引用、把关键变量稳定输出。
3. **降低了什么维护风险**：例如加入契约测试、删除重复导航、统一 suite 入口。
4. **如何回归验证**：至少给出具体 Maven 命令或文档契约命令。

## 维护入口

- 模块入口：[../README.md](../README.md)
- 模块契约测试：`SpringCoreBeansModuleContractLabTest`
- 文档契约测试：`SpringCoreBeansDocumentationContractTest`
- 最小学习闭环：`SpringCoreBeansLabTest`
- Book Matrix：`SpringCoreBeansBookMatrixLabTest`
- 断点包：`SpringCoreBeansBreakpointPackLabTest`
