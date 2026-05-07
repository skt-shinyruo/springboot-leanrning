# spring-beans Public API Gap 清单（按包/机制域分批深化）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先用本章的“清单/索引/分流”把问题分型，再回到对应章节用断点与 Lab 把结论证明出来；团队内训/复盘时可直接按本章结构复用。

    观察对象：Public API Gap 清单（按包/机制域分批深化）。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansBreakpointPackLabTest`。需要下探源码时，可以从 `org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` 这些入口切入。

<!-- CHAPTER-CARD:END -->

## 读法：把 Gap 当作覆盖看板

本页不是新的主线章节，而是把已读过的机制拿回来验证、排障和自检。读法如下：

1. 先运行 Book Matrix、Branch Matrix 或本页列出的最小 Lab，把现象固定成可重复结果。
2. 再按现象、题目或坑点定位对应章节、断点和关键变量。
3. 最后用对应实验/测试收敛答案；如果答案仍然只停留在概念层面，再回到正文补齐机制。

## 问题：spring-beans Public API Gap 清单（按包/机制域分批深化）

本章不是“讲课”，而是一个可维护的覆盖看板：哪些 spring-beans Public API 已经形成 Lab + Docs 闭环，哪些仍需要补齐。后续学习或补文档时，可以直接用它确定优先级。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab（作为“覆盖闭环”入口的总集合）：`SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansIocBranchMatrixLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest`

## 机制主线：为什么要维护 Gap？

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

在真实工程里，问题往往出在“读者没覆盖到的边界”：

- 读者知道 @Autowired，但不知道 `ResolvableDependency` 为什么能注入但不是 bean
- 读者知道 AOP，但不知道 early reference 与 final proxy 不一致会怎样 fail-fast
- 读者知道 `FactoryBean`，但不知道 `&` 前缀与缓存语义的边界

Gap 清单的目标是：**把这些“容易漏”的公共能力做成可审计的学习路线**。

---

## 覆盖标准（本仓库的“教程级”验收口径）

一个 API/机制域被认为“已覆盖”，至少满足：

1. 文档：解释“解决什么问题 / 关键约束是什么 / 常见误区在哪里”
2. Lab：能够运行并复现核心现象（最好能断言，而不是只打印）
3. Debug：给出 2–5 个关键断点与观察清单（能观察到关键数据结构变化）
4. 自检：能用 2–3 句话复述（面试/复盘模板）

---

## 当前清单（按需扩展）

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

1. 在 [95](appendix-spring-beans-public-api-index.md) 定位目标 API 包/类
2. 编写一条最小 Lab，将现象固化为断言
3. 在对应章节补齐断点闭环与常见误区

---

## 缺口清单（可对照最小 Labs）

> 本节刻意只挑“读者很容易卡住，但又很少有人把它做成最小实验”的点。
> 目标不是增加名词，而是把“结论”固化为可回归入口（docs + labs + 断点观察点）。

| 缺口点（机制域的细粒度短板） | 最短证据链入口（方法级） | 最小可跑入口（实验/测试） | 文档入口（Docs） | 状态 |
| --- | --- | --- | --- | --- |
| 程序化依赖解析：把 `resolveDependency(...)` 当成可编程 probe（无需真的把字段注入进对象） | `DefaultListableBeanFactory#resolveDependency` / `#doResolveDependency` | `SpringCoreBeansProgrammaticResolveDependencyLabTest` | `dependency-injection-resolution.md` / `wiring-autowire-candidate-selection-primary-priority-order.md` | ✅ 已补齐 |
| BeanDefinition 元数据 flags：primary/autowireCandidate/qualifiers 对候选收敛的影响（不仅是注解） | `determineAutowireCandidate` / `isAutowireCandidate` | `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest` | `wiring-autowire-candidate-selection-primary-priority-order.md` | ✅ 已补齐 |
| 基础设施 Bean 的 role：`ROLE_INFRASTRUCTURE` 如何帮助排障（把“注解能力”识别为基础设施处理器） | `AnnotationConfigUtils#registerAnnotationConfigProcessors` | `SpringCoreBeansInfrastructureBeanRoleLabTest` | `container-bootstrap-and-infrastructure.md` | ✅ 已补齐 |

## 源码调用链（方法级）定位模板（Gap 场景）

当在某个 API/机制域上出现 Gap，最容易偏离主线的方式是“在源码细节中长时间徘徊”。更稳妥的方法是优先将 **最短调用链** 固定下来：

1. **选择入口（从 LabTest 进入）**：可运行入口通常比“从源码目录检索”更快收敛。
2. **确定 1 个关键方法**：通常是该机制域的“总入口”（例如 `doResolveDependency` / `doCreateBean` / `invokeBeanFactoryPostProcessors`）。
3. **确定 2 个关键分支/数据结构**：例如候选 Map、三层缓存、mergedBeanDefinition、embedded value 的解析前后值。
4. **将链路整理为 3 行**：入口 → 分支 → 结论（用于面试/复盘复述）。

无需把链路写成长篇大论；但必须能做到“方法级可指认”。

## 排障分流（Gap 视角：读者到底缺的是哪一段）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


| 现象 | 更接近缺口在哪 | 先补充的材料 |
| --- | --- | --- |
| 读者知道结论，但无法指出“由哪个方法证明” | 调用链未固化 | 优先补充：章节中的“源码调用链（方法级）”与断点入口 |
| 能设置断点，但不清楚应观察哪些变量 | 可观测闭环不完整 | 优先补充：观察清单（最小够用版）与条件断点模板 |
| 能解释主线，但在边界场景下无法继续推导 | 边界用例缺失 | 优先补充：一个最小 Lab，将边界固化为断言 |
| 能复现问题，但不清楚如何修复 | 诊断→修复路径缺失 | 优先补充：排障决策表（Symptoms→Evidence→Fix→Verify） |

---

## 验收口径：spring-beans Public API Gap 清单（按包/机制域分批深化）
需要能回答：

1. “Gap 清单”解决的是什么学习问题？（提示：把未知变成可审计）
2. 一个机制要达到“教程级”至少满足哪 4 个验收口径？
3. 如何从 API 索引（95）→ Gap（96）→ 具体章节/Lab（正文）完成一次补齐？


## 小结：spring-beans Public API Gap 清单（按包/机制域分批深化）

`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
