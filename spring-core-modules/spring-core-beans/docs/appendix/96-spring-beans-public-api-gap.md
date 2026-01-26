# 96. spring-beans Public API Gap 清单（按包/机制域分批深化）

## 导读

- 本章主题：**Public API Gap 清单（按包/机制域分批深化）**
- 阅读方式建议：这章不是“讲课”，而是一个可维护的“覆盖率看板”：哪些 spring-beans 的 Public API 已经有 Lab+Docs 闭环，哪些仍需要补齐。你可以用它驱动后续的学习/补齐工作。

!!! summary "本章要点"

    - Gap 清单的用途：把“我还没学透什么”显式化，避免学习停留在舒适区。
    - 本仓库的标准不是“写了文档就算学完”，而是：**Doc + Lab + 断点入口 + 自检复述** 四件套闭环。
    - 当你发现某个 API/机制不在主线章节里：先在 [95](95-spring-beans-public-api-index.md) 查索引定位，再回到本章看是否已覆盖。

!!! example "本章配套实验（先跑再读）"

    - Lab（作为“覆盖闭环”入口的总集合）：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansIocBranchMatrixLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest`

## 机制主线：为什么要维护 Gap？

在真实工程里，问题往往出在“你没覆盖到的边界”：

- 你知道 @Autowired，但你不知道 `ResolvableDependency` 为什么能注入但不是 bean
- 你知道 AOP，但你不知道 early reference 与 final proxy 不一致会怎样 fail-fast
- 你知道 `FactoryBean`，但你不知道 `&` 前缀与缓存语义的边界

Gap 清单的目标是：**把这些“容易漏”的公共能力做成可审计的学习路线**。

---

## 1. 覆盖标准（本仓库的“教程级”验收口径）

一个 API/机制域被认为“已覆盖”，至少满足：

1) 文档：解释“解决什么问题 / 关键约束是什么 / 常见坑在哪里”  
2) Lab：能跑出核心现象（最好能断言，而不是只打印）  
3) Debug：给出 2–5 个关键断点与 watch list（能看见关键数据结构变化）  
4) 自检：能用 2–3 句话复述（面试/复盘模板）

---

## 2. 当前清单（建议按需扩展）

> 说明：本清单不会试图枚举所有 API，而是按“机制域”列出最常被忽略、但一旦踩坑代价很大的那批。

- IoC 主线（refresh → doCreateBean）：✅ 已覆盖（主线叙事 + Labs）
- 候选选择（Primary/Qualifier/by-name fallback/@Order vs @Priority）：✅ 已覆盖（Docs 14/33 + Labs）
- 循环依赖与 early reference：✅ 已覆盖（Docs 09/16 + Labs）
- `@Value` 占位符 / SpEL / 类型转换三连：✅ 已覆盖（Docs 34/44/36 + Labs）
- programmatic 注册与 BPP/BFPP 时机：✅ 已覆盖（Docs 25 + Labs）
- `FactoryBean` 深挖与边界：✅ 已覆盖（Docs 08/23/29 + Labs）
- XML/Reader/Namespace 扩展：✅ 已覆盖（Part 05 + Labs）
- AOT/RuntimeHints：✅ 已覆盖（Part 05 + Labs）

如果你发现某个机制域仍有真实缺口（“写了但不够深/无法断点/没有可复现入口”），建议直接按下面模板补齐：

1) 先在 [95](95-spring-beans-public-api-index.md) 定位 API 包/类
2) 写一条最小 Lab 把现象固化
3) 在对应章节补齐断点闭环与常见坑

---

## 一句话自检

你应该能回答：

1) “Gap 清单”解决的是什么学习问题？（提示：把未知变成可审计）  
2) 一个机制要达到“教程级”至少满足哪 4 个验收口径？  
3) 你如何从 API 索引（95）→ Gap（96）→ 具体章节/Lab（正文）完成一次补齐？

<!-- BOOKIFY:START -->

上一章：[95. spring-beans Public API Index（索引）](95-spring-beans-public-api-index.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[97. Explore/Debug 用例（可选启用，不影响默认回归）](97-explore-debug-tests.md)

<!-- BOOKIFY:END -->
