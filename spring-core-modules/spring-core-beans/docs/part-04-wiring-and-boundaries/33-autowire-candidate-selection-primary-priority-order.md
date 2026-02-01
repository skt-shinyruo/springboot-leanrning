# 33. 候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`AutowiredAnnotationBeanPostProcessor#postProcessProperties` / `DefaultListableBeanFactory#doResolveDependency` / `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`
    - 推荐 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[32. `@Resource` 注入：为什么它更像“按名称找 Bean”？](32-resource-injection-name-first.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast](34-value-placeholder-resolution-strict-vs-non-strict.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界**
- 这章解决一个高频误判：把“集合排序”当成“单依赖选择”。
  许多 NoUnique/注入错对象问题，本质是候选收敛规则没理清。

!!! summary "本章要点"

    - **单依赖注入**必须选出唯一胜者；选不出就应该 fail-fast（`NoUniqueBeanDefinitionException`）。
    - **集合注入**注入的是“全部候选”，这时才谈排序；`@Order` 主要影响集合排序，不负责单依赖选胜者。
    - `@Qualifier` 是最强的“收敛信号”；`@Primary` 是默认胜者；`@Priority` 常作为 tie-break；by-name fallback 是隐式规则（不推荐依赖）。

!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`

## 机制主线：先问“注入的是一个，还是一组？”

读者排障时第一问永远是：

| 场景 | 注入点长什么样 | 若希望要的结果 | 主要规则 |
| --- | --- | --- | --- |
| 单依赖注入 | `T` / `private final T t` | 必须唯一胜者 | 候选收集 → 候选收敛（Primary/Qualifier/name/Priority…） |
| 集合注入 | `List<T>` / `Map<String,T>` / `ObjectProvider<T>` | 注入全部候选并尽量稳定排序 | 收集全部候选 → 排序（Order/Ordered/Priority） |

> 记住一句话：**选择（single）≠ 排序（collection）**。

## 1. 方法级入口：注入是怎么进入 `doResolveDependency` 的？

绝大多数按类型注入，最终都会汇入同一条链路：

1) `AutowiredAnnotationBeanPostProcessor#postProcessProperties`（属性填充阶段的注入触发点）
2) `DefaultListableBeanFactory#doResolveDependency`（依赖解析总入口）
3) `findAutowireCandidates`（按类型收集候选：`Map<String,Object>`）
4) `determineAutowireCandidate`（从候选里挑胜者：Primary/Qualifier/name/Priority…）

`@Qualifier` 的过滤与匹配常见落点：

- `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`

### DependencyDescriptor 深入分析：注入点语义决定“走哪条分支”

`doResolveDependency` 的每一次决策，都基于 `DependencyDescriptor`：

- `descriptor.getDependencyType()`：单依赖 vs 集合注入的第一道分叉
- `descriptor.getDependencyName()`：by-name fallback 的来源（字段/参数名）
- `descriptor.getAnnotations()`：`@Qualifier/@Lazy/@Value` 的入口
- `descriptor.getResolvableType()`：泛型匹配与候选过滤

**结论**：注入点语义不清楚，候选收敛就会变成“运气”。

### 依赖解析分支树（简化版）

1) **快捷路径**：`@Value` / `ObjectProvider` / `@Lazy`  
2) **候选收集**：`findAutowireCandidates`  
3) **候选收敛**：Qualifier → Primary → by-name → Priority  
4) **集合排序**（仅集合注入）：`AnnotationAwareOrderComparator#sort`  
5) **失败**：仍无法唯一 → `NoUniqueBeanDefinitionException`

## 2. 单依赖注入：胜者是怎么选出来的？

把规则压缩成应能够复述的版本（学习阶段不用背全分支）：

1) **Qualifier（最强）**：注入点显式指定 ⇒ 先过滤/匹配
2) **Primary（默认胜者）**：多个候选时优先选 primary
3) **by-name fallback（隐式，别依赖）**：依赖名/参数名与 beanName 匹配时可能收敛
4) **Priority（tie-break）**：在没有更强信号时打破平局（数值越小优先级越高）
5) 仍无法唯一 ⇒ fail-fast（NoUnique）

> 注意：`@Order` 不在这条链路里解决“唯一胜者”问题。

## 3. 集合注入：`@Order` 到底管什么？

当读者注入 `List<T>` 或使用 `ObjectProvider<T>.orderedStream()` 时：

- 容器会收集全部候选
- 然后按排序规则排序

排序入口常见锚点：

- `AnnotationAwareOrderComparator#sort`

因此可以观察到：

- `@Order(0)` 的候选排在 `@Order(1)` 前（数字越小越靠前）
- 但这不会让单依赖注入“自动挑一个”

## 4. 排障决策表（候选选择/排序：从异常到证据链）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| `NoUniqueBeanDefinitionException` | 单依赖注入候选太多且无法收敛 | `doResolveDependency` 看 `matchingBeans`；`determineAutowireCandidate` 走到 fail-fast | 用 `@Qualifier/@Primary/@Priority` 收敛；或让多余候选 back-off | `SpringCoreBeansAutowireCandidateSelectionLabTest` |
| 读者加了 `@Order` 但仍 NoUnique | 概念误用：`@Order` 只管集合排序 | `determineAutowireCandidate` 分支里看不到 `@Order` 决策 | 回到单依赖规则：Qualifier/Primary/Priority | 同上（order 不解决 single） |
| 注入到了“不是预期的那个” | by-name fallback 或 Primary/Priority 规则与读者预期不同 | 看 `dependencyName` 与 beanName 是否匹配；看 primaryCandidate | 显式 `@Qualifier`；减少隐式 by-name 依赖 | `SpringCoreBeansAutowireCandidateSelectionLabTest`（by-name 用例） |
| 集合顺序不稳定/不符合预期 | 没有明确 order 信息；或排序入口没走 orderedStream | 看是否走 `AnnotationAwareOrderComparator#sort`；List/Map 注入路径 | 给候选加 `@Order`/实现 `Ordered`；使用 `orderedStream()` | `SpringCoreBeansAutowireCandidateSelectionLabTest`（集合排序用例） |

## 5. 断点闭环（建议照做一次）

### 5.1 推荐断点（按收益排序）

1) `DefaultListableBeanFactory#doResolveDependency`（依赖解析总入口）
2) `DefaultListableBeanFactory#findAutowireCandidates`（候选集合在哪里收集）
3) `DefaultListableBeanFactory#determineAutowireCandidate`（胜者在哪里确定）
4) `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`（Qualifier 如何过滤/匹配）
5) `AnnotationAwareOrderComparator#sort`（集合排序入口）

### 5.2 固定观察点（watch list）

- `descriptor.getDependencyType()`（注入点要什么类型）
- `dependencyName`（by-name fallback 的关键输入）
- `matchingBeans.keySet()`（候选集合）
- `autowiredBeanName` / 最终 winner（到底选了谁）

## 6. 面试常问（标准答案 + 方法级证据链）

### Q1：`@Order` 能不能解决 `NoUniqueBeanDefinitionException`？为什么？

- 标准答案（可复述）：
  - 不能。`@Order` 主要影响集合注入排序，不参与单依赖注入的胜者选择；单依赖必须靠 `@Qualifier/@Primary/@Priority` 等规则收敛。
- 证据链（方法级）：
  - 单依赖主线：`doResolveDependency` → `determineAutowireCandidate`
  - 集合排序入口：`AnnotationAwareOrderComparator#sort`

### Q2：`@Primary` 和 `@Qualifier` 谁更强？为什么？

- 标准答案（可复述）：
  - `@Qualifier` 更强，它是注入点的显式约束；`@Primary` 是候选侧的默认胜者。显式约束应当压过默认规则。
- 证据链（方法级）：
  - `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`
  - `determinePrimaryCandidate`

### Q3：`@Priority` 的角色是什么？它和 `@Order` 有什么关系？

- 标准答案（可复述）：
  - `@Priority` 常在没有更强信号时作为单依赖 tie-break，也会影响集合排序；`@Order` 更偏集合排序信号，不负责单依赖选胜者。

## 自检要点
应能够用 3 句回答：

1) 单依赖注入与集合注入的根本差异是什么？
2) `@Order/@Priority/@Primary/@Qualifier` 分别解决什么问题？
3) 如何用断点证明“by-name fallback 真的发生了”？（提示：dependencyName 与 beanName）
<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansAutowireCandidateSelectionLabTest` 把现象跑出来；跑完后回到正文，把“现象 → 调用链/分支 → 结论”对齐到源码。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“4. 排障决策表（候选选择/排序：从异常到证据链）”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`

上一章：[32. `@Resource` 注入：为什么它更像“按名称找 Bean”？](32-resource-injection-name-first.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast](34-value-placeholder-resolution-strict-vs-non-strict.md)

<!-- BOOKIFY:END -->
