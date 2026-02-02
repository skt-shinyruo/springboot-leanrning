# 44. SpEL 与 `@Value("#{...}")`：表达式解析链路
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：44. SpEL 与 `@Value("#{...}")`：表达式解析链路
    - 使用方式：可先运行本章推荐 Lab，把输入层解析或 AOT 契约完成验证；再回到正文用断点把关键分支（reader/hints/值解析）观察到并能解释。
    - 原理：输入层（XML/Properties/Groovy）解析的落点仍是 BeanDefinition；AOT/Native 的关键是把反射/代理/资源等需求变成可测试的构建期契约（RuntimeHints）。
    - 源码入口：`@Value("#{...}")` / `BeanFactory#resolveEmbeddedValue` / `AbstractBeanFactory#resolveEmbeddedValue`
    - 推荐 Lab：`SpringCoreBeansSpelValueLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[43. 容器外对象注入：AutowireCapableBeanFactory](43-autowirecapablebeanfactory-external-objects.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[45. 自定义 Qualifier：meta-annotation 与候选收敛](45-custom-qualifier-meta-annotation.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**44. SpEL 与 `@Value("#{...}")`：表达式解析链路**
- 阅读建议：建议先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（SpEL，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/expressions.html
- 官方文档对照（Spring Boot Reference，适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/


!!! summary "本章要点"

    - `${...}` 与 `#{...}` 并不是两套“注入系统”，它们都要先经过 `BeanFactory#resolveEmbeddedValue`（解析字符串），再进入**类型转换**，最后才注入到字段/参数。
    - 组合写法 `@Value("#{ ${demo.base:40} + 2 }")` 能成立，是因为通常先做 `${...}` 占位符解析，再对剩余字符串做 `#{...}` SpEL 求值。
    - 排障时不要混：值注入失败可以拆成三段（按最短断点链路）：
      1) 占位符解析（`${...}`）→ 2) SpEL 求值（`#{...}`）→ 3) 类型转换（注入点类型）
    - 最常见误判：把“类型转换失败（NumberFormatException 等）”误以为“SpEL 解析失败”；把“缺失占位符原样通过”误以为“配置没加载”。


!!! example "本章配套实验（先运行再读）"

    - Lab：
      - `SpringCoreBeansSpelValueLabTest`（SpEL 引用 bean / 组合占位符 / 类型转换失败形态）
      - `SpringCoreBeansValuePlaceholderResolutionLabTest`（strict vs non-strict，占位符默认值 `${k:default}`）
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansSpelValueLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansSpelValueLabTest`，再用 `SpringCoreBeansValuePlaceholderResolutionLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`AbstractBeanFactory#resolveEmbeddedValue`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“3. 三连排障（强烈推荐把这张表背下来）”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，SpEL 与 @Value 表达式）：https://docs.spring.io/spring-framework/reference/core/expressions.html

这一章解决两类“很常见但很难讲清”的问题：

1) **概念边界**：`${...}` 和 `#{...}` 到底分别属于哪条链路？
2) **排障边界**：值注入失败时，怎么把问题快速定位到“解析/求值/转换”的哪一步？

先给结论（读者背这 4 句就够排障）：

1) `${...}` 是 **占位符解析**（通常来自 Environment/PropertySources），本质是“把字符串里的 key 替换成值”。
2) `#{...}` 是 **SpEL 求值**（可以计算/引用 bean/调用方法），本质是“执行表达式并产生一个对象”。
3) 两者最终都要经过 **类型转换**（注入点类型决定转换规则），然后才能注入成功。
4) 组合写法 `#{ ${...} + 2 }` 能成立，通常意味着：**先解析 `${...}`，再对剩余字符串做 SpEL 求值**。

对照阅读（把“解析/求值/转换”三段分别看清）：

- 占位符 strict vs non-strict： [34](../part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md)
- 类型转换边界： [36](../part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md)

---

### 机制系统阐述：条件 → 分支 → 结果

**条件**：`@Value` 字符串包含 `${...}` 或 `#{...}`  
**分支**：`resolveEmbeddedValue` 先做占位符解析，再做 SpEL 求值  
**结果**：得到最终对象后再做类型转换并注入  
**断点建议**：`AbstractBeanFactory#resolveEmbeddedValue`

## 1. 先运行 Lab：把“链路拆分”固定成断言

建议按这个顺序运行（从正常路径到失败分流）：

1) 引用 bean + 类型转换：`SpringCoreBeansSpelValueLabTest#valueWithSpel_canReferenceBeanAndResultIsConvertedToTargetType`
2) 占位符 + SpEL 组合（解析顺序）：`SpringCoreBeansSpelValueLabTest#spelCanComposeWithPlaceholderResolution_placeholdersResolveFirst_thenExpressionIsEvaluated`
3) 求值成功但转换失败：`SpringCoreBeansSpelValueLabTest#spelEvaluationMaySucceedButTypeConversionMayFail_whenInjectingIntoPrimitiveType`

命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansSpelValueLabTest test
```

---

## 2. 源码最短路径（call chain）：从 @Value 到最终注入

可以把 `@Value` 注入链路压缩成这条“最短可跟栈”：

1) `AutowiredAnnotationBeanPostProcessor#postProcessProperties`：识别 `@Value` 注入点
2) `BeanFactory#resolveEmbeddedValue`（常见落点：`AbstractBeanFactory#resolveEmbeddedValue`）：解析字符串
   - 可能包含 `${...}` 占位符替换
   - 可能包含 `#{...}` 表达式求值（通过 `BeanExpressionResolver`）
3) `TypeConverter#convertIfNecessary`：把解析/求值结果转换成注入点目标类型
4) 注入到字段/参数（属性填充或构造注入）

读者只要把这条链路记住，就不会再把“值注入失败”当成黑箱。

---

## 3. 三连排障（强烈推荐把这张表背下来）
> 官方参考（Spring Framework 6.2.x，SpEL 与 @Value 表达式）：https://docs.spring.io/spring-framework/reference/core/expressions.html


| 现象 | 最可能根因 | 需要去哪看（断点/变量） |
| --- | --- | --- |
| 值是 `"${demo.missing}"` 原样字符串 | non-strict 占位符解析放行了缺失 key | `resolveEmbeddedValue` 输入/输出；对照 [34] |
| 直接启动失败：Could not resolve placeholder | strict fail-fast（更健康） | `PropertySourcesPlaceholderConfigurer` 是否启用；对照 [34] |
| 报 SpEL parse/eval 异常（表达式本身不合法/求值失败） | 表达式语法/上下文问题（bean 不可见等） | `StandardBeanExpressionResolver#evaluate`、表达式字符串 |
| 表达式返回了 `"42"` 但注入到 `int` 失败（NumberFormatException 等） | 类型转换失败（不是 SpEL 失败） | `convertIfNecessary` 的 source/targetType 与 root cause |

---

## 4. 断点闭环（把“解析/求值/转换”三段分别观察到）

### 4.1 推荐断点（按收益排序）

1) `AutowiredAnnotationBeanPostProcessor#postProcessProperties`（定位注入点与原始字符串）
2) `AbstractBeanFactory#resolveEmbeddedValue`（看 `${...}`/`#{...}` 处理前后字符串）
3) `StandardBeanExpressionResolver#evaluate`（只在 `#{...}` 场景触发）
4) `TypeConverter#convertIfNecessary`（看转换失败到底从哪来）

### 4.2 Watch List（最小够用版）

- 原始值：`@Value` 的字符串（包含 `${...}` 或 `#{...}`）
- 解析后字符串：`resolveEmbeddedValue` 的输出（是否还包含 `${`/`#{`）
- SpEL 求值结果：value 的 runtime type（String/Integer/对象）
- 目标类型：注入点类型（字段类型/参数类型）
- 异常 root cause：`NumberFormatException` / `SpelEvaluationException` / `IllegalArgumentException` 等

## 常见误区与边界

1) **把“类型转换失败”误以为 “SpEL 失败”**
   - 典型：`@Value("#{ 'not-a-number' }") private int n;`
   - 现象：root cause 可能是 `NumberFormatException`（转换失败），不是 `SpelParseException`
2) **把“缺失占位符原样通过”误以为 “配置没加载/没生效”**
   - 典型：non-strict 模式下 `${demo.missing}` 可能被原样注入成字符串
   - 解法：要么 strict fail-fast（`PropertySourcesPlaceholderConfigurer`），要么用默认值 `${k:default}`
3) **误区：`@Value` 直接读 Environment**
   - 正确模型：`@Value` → `resolveEmbeddedValue` →（占位符/SpEL）→ 类型转换 → 注入
4) **误区：SpEL 只能返回字符串**
   - SpEL 可以返回任意对象（bean 引用结果、计算结果）；是否需要转换取决于注入点类型

## 源码调用链（方法级）：`@Value` 的“三连”在哪里发生

本章的核心是把 `@Value` 的三步拆开看清楚（占位符 → SpEL → 类型转换），最短调用链如下：

1) 注入点入口：`AutowiredAnnotationBeanPostProcessor#postProcessProperties`（获取到原始字符串）
2) 解析入口：`AbstractBeanFactory#resolveEmbeddedValue`（处理 `${...}`）
3) 表达式求值：`StandardBeanExpressionResolver#evaluate`（只在 `#{...}` 场景触发）
4) 类型转换：`TypeConverterDelegate#convertIfNecessary`（把求值结果/字符串转成注入点类型）

在断点里把“字符串解析前后值 / SpEL 求值结果类型 / requiredType”三件事看清楚，就能快速定位是第几步出问题。

## 面试常问（SpEL / @Value：区分链路比背语法更重要）

### Q1：`${...}` 与 `#{...}` 的本质差异是什么？

- 标准答案（可复述）：
  - `${...}` 是占位符解析（来自 PropertySource/Environment）；`#{...}` 是 SpEL 表达式求值（可以做计算/引用 bean）。两者都可能在 `resolveEmbeddedValue` 之后进入类型转换。
- 证据链（方法级）：
  - `${...}`：`resolveEmbeddedValue`
  - `#{...}`：`StandardBeanExpressionResolver#evaluate`
- 最小复现：
  - `SpringCoreBeansSpelValueLabTest`

### Q2：值注入失败时，如何快速判断是“解析/求值/转换”哪一步？

- 标准答案（可复述）：
  - 应先检查 `resolveEmbeddedValue` 的输出（`${...}` 是否仍存在）；再检查 `evaluate` 是否抛出 `SpelEvaluationException`；最后检查 `convertIfNecessary` 是否抛出 `TypeMismatch/NumberFormat` 等转换异常。
- 最小复现：
  - `SpringCoreBeansSpelValueLabTest`（配合本章断点/Watch List）

## 自检要点
- 应能够解释清楚：`${...}` 与 `#{...}` 分别属于哪条链路吗？（占位符解析 vs 表达式求值）
- 遇到值注入失败时，能否按“三连”收敛：解析（placeholder）→ 计算（SpEL）→ 转换（TypeConverter）？
- 应能够说出：最短断点链路该打在哪 3 个方法上，把上面三步分别观察到吗？

## 小结与下一章

这一章的目标不是“会写 SpEL”，而是：当 `@Value` 出问题时，应能够 **在 1 分钟内定位是解析/求值/转换的哪一步**。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansSpelValueLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansSpelValueLabTest.java`

上一章：[43. 容器外对象注入：AutowireCapableBeanFactory](43-autowirecapablebeanfactory-external-objects.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[45. 自定义 Qualifier：meta-annotation 与候选收敛](45-custom-qualifier-meta-annotation.md)

<!-- BOOKIFY:END -->
