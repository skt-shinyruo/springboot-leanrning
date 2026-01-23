# 44. SpEL 与 `@Value("#{...}")`：表达式解析链路

## 导读

- 本章主题：**44. SpEL 与 `@Value("#{...}")`：表达式解析链路**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansSpelValueLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansSpelValueLabTest.java`

## 机制主线

1) 面试时答不清：`${...}` 与 `#{...}` 的链路差异是什么？  
2) 生产排障时误判：值注入失败到底是“占位符解析问题”、还是“表达式计算问题”、还是“类型转换问题”？

---

## 1. 结论先行：`${...}` 与 `#{...}` 分别做什么？

- `${...}`：占位符解析（通常来自 Environment / PropertySources）
- `#{...}`：SpEL 表达式解析（可以计算、可以引用 bean、可以调用方法）

但它们最终都会回到同一条主线：

> **解析出字符串/对象 → 再做类型转换 → 注入到目标类型**

对照阅读：

- [34. @Value 占位符解析：strict vs non-strict](../part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md)
- [36. 类型转换：BeanWrapper / ConversionService](../part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md)

---

1) SpEL 能引用容器里的 bean（`@beanName`）
2) SpEL 的结果会进入类型转换（例如返回 `"42"` 仍可注入到 `int`）

入口测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansSpelValueLabTest.java`
  - `valueWithSpel_canReferenceBeanAndResultIsConvertedToTargetType()`（覆盖：引用 bean + 进入类型转换）

1) `AutowiredAnnotationBeanPostProcessor#postProcessProperties`（识别 `@Value` 的入口）
2) `AbstractBeanFactory#resolveEmbeddedValue`（`${...}` / `#{...}` 的字符串解析入口）
3) 类型转换（取决于注入点）：
   - `BeanWrapperImpl#setPropertyValue`（属性填充）
   - 或 `TypeConverter#convertIfNecessary`（注入点转换）

当你需要进一步看 SpEL 的执行细节时，再扩展到表达式解析器内部：

- `StandardBeanExpressionResolver#evaluate`（把 `#{...}` 交给 SpEL 计算）
- `SpelExpression#getValue` / `ExpressionParser#parseExpression`（表达式解析与求值）

- 原始字符串：到底是 `"${...}"` 还是 `"#\{...\}"`（两条链路的入口信号不同）
- `BeanExpressionContext`：SpEL 能不能访问到你期望的 bean（例如 `@beanName`）
- 最终值类型：SpEL 计算结果是 `String` 还是 `Integer`/对象（决定后续转换是否必需）

---

你应该能回答：

- `${...}` 与 `#{...}` 的职责边界是什么？（占位符解析 vs 表达式求值）
- 当 `@Value("#{...}")` 注入失败/值不符合预期时，你如何把问题拆成“解析 vs 计算 vs 转换”三段定位？
- SpEL 引用 `@beanName` 的前提是什么？（注入点在哪个阶段解析、能访问哪些 bean）

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansSpelValueLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

这一章的目标是把它拆开成可验证链路。

## 2. 复现入口（可运行）

本模块提供一个最小 SpEL 实验，覆盖两个你最需要的能力：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansSpelValueLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansSpelValueLabTest test
```

## 3. 源码 / 断点建议（从“值注入失败”到“根因”）

把链路分成三段打断点，排障会非常快：

建议观察点（断点时优先盯这些变量）：

- `${...}` 与 `#{...}` 的职责边界是什么？
- 值注入失败时，你怎么用 3 个断点把问题收敛到“解析 vs 计算 vs 转换”？

## 常见坑与边界

很多人知道 `@Value("${...}")`，但对 `@Value("#{...}")`（SpEL）没有清晰边界，导致两类问题：

## 4. 常见误区

1) **误区：@Value 就是读 Environment**
   - `${...}` 的默认行为确实常来自 Environment resolver，但 `#{...}` 属于表达式系统。
2) **误区：SpEL 只会返回字符串**
   - SpEL 可以返回任何对象（包括 bean 引用结果）；最终是否需要转换取决于注入点类型。

## 小结与下一章

## 5. 小结与下一章预告

下一章我们补齐依赖注入体系的“自定义能力”：

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansSpelValueLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansSpelValueLabTest.java`

上一章：[43. 容器外对象注入：AutowireCapableBeanFactory](43-autowirecapablebeanfactory-external-objects.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[45. 自定义 Qualifier：meta-annotation 与候选收敛](45-custom-qualifier-meta-annotation.md)

<!-- BOOKIFY:END -->
