# 45. 自定义 Qualifier：meta-annotation 与候选收敛

## 导读

- 本章主题：**45. 自定义 Qualifier：meta-annotation 与候选收敛**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansCustomQualifierLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansCustomQualifierLabTest.java`

## 机制主线

当你进入真实项目，`@Qualifier("beanName")` 常常不够用：

- 你希望限定条件有“业务语义”（例如 `@Cn` / `@Internal` / `@ReadOnly`）
- 你希望团队统一约束（避免到处写字符串 beanName）

这就需要你理解：**Qualifier 的本质是“候选收敛规则”**，而不是“改名”。

---

## 1. 结论先行：自定义 Qualifier 的本质

自定义 Qualifier 的做法通常是：

1) 定义一个注解（例如 `@Cn`）
2) 用 `@Qualifier` 做 meta-annotation
3) 在候选 bean 上标注 `@Cn`（作为候选元数据）
4) 在注入点也标注 `@Cn`（作为收敛条件）

因此你应该能把它放回依赖解析主线：

- 候选收集：`findAutowireCandidates`
- 候选收敛：`determineAutowireCandidate`
- Qualifier 匹配：`QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`

对照阅读：

- [03. 依赖注入解析：候选收集→候选收敛→最终注入](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
- [33. 候选选择与优先级：@Primary/@Priority/@Order 的边界](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)

- 两个同类型候选（两个实现）
- 通过自定义 Qualifier 把候选收敛到 1 个

入口测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansCustomQualifierLabTest.java`
  - `customQualifierMetaAnnotation_canNarrowDownCandidates_forSingleInjection()`（meta-annotation 命中收敛）

- `DefaultListableBeanFactory#findAutowireCandidates`（候选集合）
- `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`（Qualifier 匹配过滤）

如果你要解释最终选中规则：

- `DefaultListableBeanFactory#determineAutowireCandidate`

- `DependencyDescriptor`：注入点的类型信息与注解（`@Qualifier/@Cn/...`）
- `candidates`：当前候选集合里有哪些 beanName（以及它们的定义来源）
- Qualifier 匹配细节：是 “meta-annotation 命中” 还是 “value/name 命中”

---

你应该能回答：

- 自定义 Qualifier（meta-annotation）如何参与候选收敛？它影响的是“候选收集”还是“候选收敛”阶段？
- 当候选有多个实现时，你如何用 2 个断点证明“哪些候选被过滤/为什么被过滤”？
- 为什么说它能把“字符串 Qualifier”提升为“带业务语义的类型约束”？（好处与边界是什么）

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansCustomQualifierLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 2. 复现入口（可运行）

本模块提供一个最小实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansCustomQualifierLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansCustomQualifierLabTest test
```

## 3. 源码 / 断点建议（把“为什么注入的是它”讲成可复述算法）

只需要 2 个断点，你就能在真实项目里解释“为什么注入的是它”：

建议观察点（你下断点时应该盯住这些变量）：

- 自定义 Qualifier 是如何参与候选收敛的？
- 你会在哪两个方法下断点证明“候选集合如何被过滤”？

## 常见坑与边界

- [03. 依赖注入解析：类型/名称/@Qualifier/@Primary](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
- [33. 候选选择与优先级：@Primary/@Priority/@Order 的边界](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)

## 4. 常见误区

1) **误区：自定义 Qualifier = 更好的 @Primary**
   - `@Primary` 是“默认胜出者”，自定义 Qualifier 是“按语义显式选择”，适用场景不同。
2) **误区：把 Qualifier 当作 beanName**
   - Qualifier 是过滤条件，beanName 只是可能参与收敛的一种信号。

## 一句话自检

- 你能解释清楚：自定义 Qualifier 解决的是“候选收敛”的哪一类问题吗？它和 `@Primary` 的边界是什么？
- 你能说出：候选集合是在依赖解析的哪个方法里被过滤/收敛的吗？（提示：`doResolveDependency` / candidate resolver）
- 你能给出：如何用一个最小 LabTest + 两个断点把“为什么注入的是它”讲成可复述算法？

## 小结与下一章

## 5. 小结

下一章开始进入“真实世界里经常遇到，但很多人没系统学过”的内容：

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansCustomQualifierLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansCustomQualifierLabTest.java`

上一章：[44. SpEL 与 `@Value("#{...}")`：表达式解析链路](44-spel-and-value-expression.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers](46-xml-namespace-extension.md)

<!-- BOOKIFY:END -->
