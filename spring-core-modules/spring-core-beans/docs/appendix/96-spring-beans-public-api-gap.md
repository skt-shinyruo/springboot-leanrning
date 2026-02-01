# 96. spring-beans Public API Gap 清单（按包/机制域分批深化）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Public API Gap 清单（按包/机制域分批深化）
    - 使用方式：建议先用本章的“清单/索引/分流”把问题分型，再回到对应章节用断点与 Lab 把结论证明出来；团队内训/复盘时可直接按本章结构复用。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`
    - 推荐 Lab：`SpringCoreBeansBreakpointPackLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[95. spring-beans Public API Index（索引）](95-spring-beans-public-api-index.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[97. Explore/Debug 用例（可选启用，不影响默认回归）](97-explore-debug-tests.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**Public API Gap 清单（按包/机制域分批深化）**
- 阅读方式建议：这章不是“讲课”，而是一个可维护的“覆盖率看板”：哪些 spring-beans 的 Public API 已经有 Lab+Docs 闭环，哪些仍需要补齐。可以用它驱动后续的学习/补齐工作。

!!! summary "本章要点"

    - Gap 清单的用途：把“尚未掌握的内容”显式化，避免学习停留在舒适区。
    - 本仓库的标准不是“写了文档就算学完”，而是：**Doc + Lab + 断点入口 + 自检复述** 四件套闭环。
    - 当读者发现某个 API/机制不在主线章节里：先在 [95](95-spring-beans-public-api-index.md) 查索引定位，再回到本章看是否已覆盖。

!!! example "本章配套实验（先运行再读）"

    - Lab（作为“覆盖闭环”入口的总集合）：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansIocBranchMatrixLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest`

## 机制主线：为什么要维护 Gap？

在真实工程里，问题往往出在“读者没覆盖到的边界”：

- 读者知道 @Autowired，但读者不知道 `ResolvableDependency` 为什么能注入但不是 bean
- 读者知道 AOP，但读者不知道 early reference 与 final proxy 不一致会怎样 fail-fast
- 读者知道 `FactoryBean`，但读者不知道 `&` 前缀与缓存语义的边界

Gap 清单的目标是：**把这些“容易漏”的公共能力做成可审计的学习路线**。

---

## 1. 覆盖标准（本仓库的“教程级”验收口径）

一个 API/机制域被认为“已覆盖”，至少满足：

1) 文档：解释“解决什么问题 / 关键约束是什么 / 常见误区在哪里”
2) Lab：能够运行并复现核心现象（最好能断言，而不是只打印）
3) Debug：给出 2–5 个关键断点与 watch list（能观察到关键数据结构变化）
4) 自检：能用 2–3 句话复述（面试/复盘模板）

---

## 2. 当前清单（建议按需扩展）

> 说明：本清单不会试图枚举所有 API，而是按“机制域”列出最常被忽略、但一旦易错点代价很大的那批。

- IoC 主线（refresh → doCreateBean）：✅ 已覆盖（主线叙事 + Labs）
- 候选选择（Primary/Qualifier/by-name fallback/@Order vs @Priority）：✅ 已覆盖（Docs 14/33 + Labs）
- 循环依赖与 early reference：✅ 已覆盖（Docs 09/16 + Labs）
- `@Value` 占位符 / SpEL / 类型转换三连：✅ 已覆盖（Docs 34/44/36 + Labs）
- programmatic 注册与 BPP/BFPP 时机：✅ 已覆盖（Docs 25 + Labs）
- `FactoryBean` 深入分析与边界：✅ 已覆盖（Docs 08/23/29 + Labs）
- XML/Reader/Namespace 扩展：✅ 已覆盖（Part 05 + Labs）
- AOT/RuntimeHints：✅ 已覆盖（Part 05 + Labs）

若发现某个机制域仍存在实际缺口（例如“内容不够深入/无法建立断点闭环/缺少可复现入口”），可按下面步骤补齐：

1) 在 [95](95-spring-beans-public-api-index.md) 定位目标 API 包/类
2) 编写一条最小 Lab，将现象固化为断言
3) 在对应章节补齐断点闭环与常见误区

## 源码调用链（方法级）定位模板（Gap 场景）

当在某个 API/机制域上出现 Gap，最容易偏离主线的方式是“在源码细节中长时间徘徊”。更稳妥的方法是优先将 **最短调用链** 固定下来：

1) **选择入口（从 LabTest 进入）**：可运行入口通常比“从源码目录检索”更快收敛。
2) **确定 1 个关键方法**：通常是该机制域的“总入口”（例如 `doResolveDependency` / `doCreateBean` / `invokeBeanFactoryPostProcessors`）。
3) **确定 2 个关键分支/数据结构**：例如候选 Map、三层缓存、mergedBeanDefinition、embedded value 的解析前后值。
4) **将链路整理为 3 行**：入口 → 分支 → 结论（用于面试/复盘复述）。

无需把链路写成长篇大论；但必须能做到“方法级可指认”。

## 排障分流（Gap 视角：读者到底缺的是哪一段）

| 现象 | 更像缺口在哪 | 建议优先补充的材料 |
| --- | --- | --- |
| 读者知道结论，但无法指出“由哪个方法证明” | 调用链未固化 | 优先补充：章节中的“源码调用链（方法级）”与断点入口 |
| 能设置断点，但不清楚应观察哪些变量 | 可观测闭环不完整 | 优先补充：watch list（最小够用版）与条件断点模板 |
| 能解释主线，但在边界场景下无法继续推导 | 边界用例缺失 | 优先补充：一个最小 Lab，将边界固化为断言 |
| 能复现问题，但不清楚如何修复 | 诊断→修复路径缺失 | 优先补充：排障决策表（Symptoms→Evidence→Fix→Verify） |

---

## 自检要点
应能够回答：

1) “Gap 清单”解决的是什么学习问题？（提示：把未知变成可审计）
2) 一个机制要达到“教程级”至少满足哪 4 个验收口径？
3) 如何从 API 索引（95）→ Gap（96）→ 具体章节/Lab（正文）完成一次补齐？
<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansBreakpointPackLabTest`，再用 `SpringCoreBeansIocBranchMatrixLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：把该页从“信息堆”变成“可用入口”：每个条目尽量落到“去哪里验证/怎么验证”，避免只列名词。
    - 下一跳：若是从现象进入，优先回到 [知识地图](92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

上一章：[95. spring-beans Public API Index（索引）](95-spring-beans-public-api-index.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[97. Explore/Debug 用例（可选启用，不影响默认回归）](97-explore-debug-tests.md)

<!-- BOOKIFY:END -->
