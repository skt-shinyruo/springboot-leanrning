# 45. 自定义 Qualifier：meta-annotation 与候选收敛
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：45. 自定义 Qualifier：meta-annotation 与候选收敛
    - 使用方式：可先运行本章推荐 Lab，把输入层解析或 AOT 契约完成验证；再回到正文用断点把关键分支（reader/hints/值解析）观察到并能解释。
    - 原理：输入层（XML/Properties/Groovy）解析的落点仍是 BeanDefinition；AOT/Native 的关键是把反射/代理/资源等需求变成可测试的构建期契约（RuntimeHints）。
    - 源码入口：`QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate` / `DefaultListableBeanFactory#findAutowireCandidates` / `DefaultListableBeanFactory#determineAutowireCandidate`
    - 推荐 Lab：`SpringCoreBeansCustomQualifierLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[44. SpEL 与 `@Value("#{...}")`：表达式解析链路](44-spel-and-value-expression.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers](46-xml-namespace-extension.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**45. 自定义 Qualifier：meta-annotation 与候选收敛**
- 阅读建议：建议先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

!!! summary "本章要点"

    - 读完本章，应能够用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见误区在哪里”。
    - 如果只看一眼：请先运行一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansCustomQualifierLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansCustomQualifierLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansCustomQualifierLabTest` 把现象跑出来；跑完后回到正文，把“现象 → 调用链/分支 → 结论”对齐到源码。
    - 第一断点：`QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“常见误区与边界”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

当读者进入真实项目，`@Qualifier("beanName")` 常常不够用：

- 读者希望限定条件有“业务语义”（例如 `@Cn` / `@Internal` / `@ReadOnly`）
- 读者希望团队统一约束（避免到处写字符串 beanName）

这就需要读者理解：**Qualifier 的本质是“候选收敛规则”**，而不是“改名”。

---

### 机制系统阐述：条件 → 分支 → 结果

**条件**：注入点与候选 bean 同时标注自定义 Qualifier  
**分支**：`QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate` 做匹配过滤  
**结果**：候选集合被缩小 → winner 选择更稳定  
**断点建议**：`QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`

## 1. 结论先行：自定义 Qualifier 的本质

自定义 Qualifier 的做法通常是：

1) 定义一个注解（例如 `@Cn`）
2) 用 `@Qualifier` 做 meta-annotation
3) 在候选 bean 上标注 `@Cn`（作为候选元数据）
4) 在注入点也标注 `@Cn`（作为收敛条件）

因此应能够把它放回依赖解析主线：

- 候选收集：`findAutowireCandidates`
- 候选收敛：`determineAutowireCandidate`
- Qualifier 匹配：`QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`

对照阅读：

- [03. 依赖注入解析：候选收集→候选收敛→最终注入](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
- [33. 候选选择与优先级：@Primary/@Priority/@Order 的边界](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)

## DependencyDescriptor 深入分析：注入点语义决定“Qualifier 是否生效”

`DependencyDescriptor` 是注入点语义的入口：

- `descriptor.getAnnotations()`：注入点是否存在自定义 Qualifier  
- `descriptor.getDependencyType()`：候选收集的类型基线  
- `descriptor.getDependencyName()`：by-name fallback 的隐式输入  

**结论**：Qualifier 不是“改 beanName”，而是“让 resolver 在候选收敛时多一个过滤条件”。

## 依赖解析分支树（简化版）

1) **快捷路径**：Optional/Provider/@Lazy/@Value  
2) **候选收集**：`findAutowireCandidates`  
3) **Qualifier 过滤**：`isAutowireCandidate`  
4) **winner 收敛**：Primary → by-name → Priority  
5) **失败**：无法唯一 → `NoUniqueBeanDefinitionException`

## 关键变量（断点里只看这些）

- `candidates`：候选集合（过滤前后差异）  
- `qualifiedName` / `value`：Qualifier 的匹配输入  
- `dependencyName`：by-name fallback 的关键输入  

- 两个同类型候选（两个实现）
- 通过自定义 Qualifier 把候选收敛到 1 个

入口测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansCustomQualifierLabTest.java`
  - `customQualifierMetaAnnotation_canNarrowDownCandidates_forSingleInjection()`（meta-annotation 命中收敛）

- `DefaultListableBeanFactory#findAutowireCandidates`（候选集合）
- `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`（Qualifier 匹配过滤）

若要解释最终选中规则：

- `DefaultListableBeanFactory#determineAutowireCandidate`

- `DependencyDescriptor`：注入点的类型信息与注解（`@Qualifier/@Cn/...`）
- `candidates`：当前候选集合里有哪些 beanName（以及它们的定义来源）
- Qualifier 匹配细节：是 “meta-annotation 命中” 还是 “value/name 命中”

---

应能够回答：

- 自定义 Qualifier（meta-annotation）如何参与候选收敛？它影响的是“候选收集”还是“候选收敛”阶段？
- 当候选有多个实现时，如何用 2 个断点证明“哪些候选被过滤/为什么被过滤”？
- 为什么说它能把“字符串 Qualifier”提升为“带业务语义的类型约束”？（好处与边界是什么）

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先运行它们）：
- Lab：`SpringCoreBeansCustomQualifierLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 复现/验证补充说明（来自原文迁移）

## 2. 复现入口（可运行）

本模块提供一个最小实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansCustomQualifierLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansCustomQualifierLabTest test
```

## 3. 源码 / 断点建议（把“为什么注入的是它”讲成可复述算法）

只需要 2 个断点，即可在真实项目里解释“为什么注入的是它”：

建议观察点（设置断点时应该盯住这些变量）：

- 自定义 Qualifier 是如何参与候选收敛的？
- 可以在哪两个方法设置断点证明“候选集合如何被过滤”？

## 常见误区与边界

- [03. 依赖注入解析：类型/名称/@Qualifier/@Primary](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
- [33. 候选选择与优先级：@Primary/@Priority/@Order 的边界](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)

## 4. 常见误区

1) **误区：自定义 Qualifier = 更好的 @Primary**
   - `@Primary` 是“默认胜出者”，自定义 Qualifier 是“按语义显式选择”，适用场景不同。
2) **误区：把 Qualifier 当作 beanName**
   - Qualifier 是过滤条件，beanName 只是可能参与收敛的一种信号。

## 排障决策表（Qualifier：为什么注入的不是所期望的那个）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| 注入到了“不是预期的实现” | 候选收敛规则没表达清楚（缺 Qualifier/Primary 冲突） | `DefaultListableBeanFactory#determineAutowireCandidate`；看最终 winner 如何选出 | 在注入点用 `@Qualifier`（或自定义 Qualifier）显式缩小候选；避免“靠默认” | `SpringCoreBeansCustomQualifierLabTest` |
| `NoUniqueBeanDefinitionException` | 单依赖没有唯一胜者 | `findAutowireCandidates` 看候选集合；`isAutowireCandidate` 看过滤是否生效 | 明确 `@Qualifier` / `@Primary`；必要时拆分类型或引入语义标签 | 同上 |
| 容易误以为 `@Primary` 会覆盖一切但实际被“压过” | 注入点带了更强限定（Qualifier） | `AutowireCandidateResolver#isAutowireCandidate` / `QualifierAnnotationAutowireCandidateResolver` | 认识强信号优先级：Qualifier > Primary（单依赖收敛） | 结合 [33](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md) |

## 面试常问（自定义 Qualifier / meta-annotation）

### Q1：自定义 Qualifier 解决的是什么问题？和 `@Primary` 的边界是什么？

- 标准答案（可复述）：
  - `@Primary` 是“默认胜者”；自定义 Qualifier 是“按语义显式选择”，用来缩小候选集合并表达业务意图，两者适用场景不同。
- 证据链（方法级）：
  - 候选收集：`DefaultListableBeanFactory#findAutowireCandidates`
  - 限定过滤：`AutowireCandidateResolver#isAutowireCandidate`（Qualifier 逻辑在 resolver 里）
  - winner 收敛：`DefaultListableBeanFactory#determineAutowireCandidate`
- 最小复现：
  - `SpringCoreBeansCustomQualifierLabTest`

### Q2：自定义 Qualifier（meta-annotation）是怎么参与候选过滤的？

- 标准答案（可复述）：
  - 通过候选解析器识别注入点上的 Qualifier 元注解，进而决定某个候选是否可注入；它不是“改 beanName”，而是“改候选集合”。
- 证据链（方法级）：
  - `QualifierAnnotationAutowireCandidateResolver`（或同类 resolver）的 `isAutowireCandidate` 分支

## 自检要点
- 应能够解释清楚：自定义 Qualifier 解决的是“候选收敛”的哪一类问题吗？它和 `@Primary` 的边界是什么？
- 应能够说出：候选集合是在依赖解析的哪个方法里被过滤/收敛的吗？（提示：`doResolveDependency` / candidate resolver）
- 应能够给出：如何用一个最小 LabTest + 两个断点把“为什么注入的是它”讲成可复述算法？

## 小结与下一章

## 5. 小结

下一章开始进入“真实世界里经常遇到，但很多人没系统学过”的内容：

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansCustomQualifierLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansCustomQualifierLabTest.java`

上一章：[44. SpEL 与 `@Value("#{...}")`：表达式解析链路](44-spel-and-value-expression.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers](46-xml-namespace-extension.md)

<!-- BOOKIFY:END -->
