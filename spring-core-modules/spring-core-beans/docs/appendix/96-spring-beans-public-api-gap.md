# 96. spring-beans Public API Gap 清单（按包/机制域分批深化）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Public API Gap 清单（按包/机制域分批深化）
    - 怎么使用：建议先用本章的“清单/索引/分流”把问题分型，再回到对应章节用断点与 Lab 把结论证明出来；团队内训/复盘时可直接按本章结构复用。
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

    - Gap 清单的用途：把“我还没学透什么”显式化，避免学习停留在舒适区。
    - 本仓库的标准不是“写了文档就算学完”，而是：**Doc + Lab + 断点入口 + 自检复述** 四件套闭环。
    - 当读者发现某个 API/机制不在主线章节里：先在 [95](95-spring-beans-public-api-index.md) 查索引定位，再回到本章看是否已覆盖。

!!! example "本章配套实验（先跑再读）"

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
2) Lab：能跑出核心现象（最好能断言，而不是只打印）
3) Debug：给出 2–5 个关键断点与 watch list（能看见关键数据结构变化）
4) 自检：能用 2–3 句话复述（面试/复盘模板）

---

## 2. 当前清单（建议按需扩展）

> 说明：本清单不会试图枚举所有 API，而是按“机制域”列出最常被忽略、但一旦易错点代价很大的那批。

- IoC 主线（refresh → doCreateBean）：✅ 已覆盖（主线叙事 + Labs）
- 候选选择（Primary/Qualifier/by-name fallback/@Order vs @Priority）：✅ 已覆盖（Docs 14/33 + Labs）
- 循环依赖与 early reference：✅ 已覆盖（Docs 09/16 + Labs）
- `@Value` 占位符 / SpEL / 类型转换三连：✅ 已覆盖（Docs 34/44/36 + Labs）
- programmatic 注册与 BPP/BFPP 时机：✅ 已覆盖（Docs 25 + Labs）
- `FactoryBean` 深挖与边界：✅ 已覆盖（Docs 08/23/29 + Labs）
- XML/Reader/Namespace 扩展：✅ 已覆盖（Part 05 + Labs）
- AOT/RuntimeHints：✅ 已覆盖（Part 05 + Labs）

若发现某个机制域仍有真实缺口（“写了但不够深/无法断点/没有可复现入口”），建议直接按下面模板补齐：

1) 先在 [95](95-spring-beans-public-api-index.md) 定位 API 包/类
2) 写一条最小 Lab 把现象固化
3) 在对应章节补齐断点闭环与常见误区

## 源码调用链（方法级）定位模板（Gap 场景）

当在某个 API/机制域上出现 Gap，最容易走偏的方式是“看源码看到迷路”。更稳的套路是先把 **最短调用链** 固定下来：

1) **先选入口（从 LabTest 进）**：能跑起来的入口比“从源码目录翻”更快收敛。
2) **再锁 1 个关键方法**：通常是该机制域的“总入口”（例如 `doResolveDependency` / `doCreateBean` / `invokeBeanFactoryPostProcessors`）。
3) **再锁 2 个关键分支/数据结构**：比如候选 Map、三层缓存、mergedBeanDefinition、embedded value 的解析前后值。
4) **最后把链路写成 3 行**：入口 → 分支 → 结论（用于面试/复盘复述）。

无需把链路写成长篇大论；但必须能做到“方法级可指认”。

## 排障分流（Gap 视角：读者到底缺的是哪一段）

| 现象 | 更像缺口在哪 | 建议先补哪类材料 |
| --- | --- | --- |
| 读者知道结论，但一问“哪个方法证明”就卡住 | 调用链未固化 | 先补：章节里的“源码调用链（方法级）”与断点入口 |
| 可以下断点，但不知道看哪些变量 | 可观测闭环不完整 | 先补：watch list（最小够用版）与条件断点模板 |
| 应能够解释主线，但遇到边界就崩 | 边界用例缺失 | 先补：一个最小 Lab 把边界固化成断言 |
| 应能够复现，但不知道怎么修 | 诊断→修复路径缺失 | 先补：排障决策表（Symptoms→Evidence→Fix→Verify） |

---

## 自检要点
应能够回答：

1) “Gap 清单”解决的是什么学习问题？（提示：把未知变成可审计）
2) 一个机制要达到“教程级”至少满足哪 4 个验收口径？
3) 如何从 API 索引（95）→ Gap（96）→ 具体章节/Lab（正文）完成一次补齐？
<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：为每个 API 域补“对应章节与证据链入口”，帮助从 API 反向定位机制。
    - B（边界反例）：为 gap 项补“反例/边界触发条件”，明确为何它是 gap。
    - C（排障 SOP）：为 API 域补“常见排障场景入口”，让索引服务于排障而不是目录堆叠。
    - D（断点观察）：建议断点：哪个 API 域对应哪个关键断点入口。
    - E（面试复述）：面试题映射：某 API 域典型面试题与证明路径。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

上一章：[95. spring-beans Public API Index（索引）](95-spring-beans-public-api-index.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[97. Explore/Debug 用例（可选启用，不影响默认回归）](97-explore-debug-tests.md)

<!-- BOOKIFY:END -->
