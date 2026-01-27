# 09. Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？

## 导读

- 本章主题：**Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？**
- 阅读方式建议：先跑本章 Lab，看清楚“同一份条件、不同顺序，结果不同”的反直觉现象；再用断点把它放回 Boot 的 auto-config 导入与排序链路里理解。

!!! summary "本章要点"

    - 你在写 `@ConditionalOnBean` 时，隐含假设是“依赖的 bean 会在我之前注册/创建”。跨 auto-config 时，这个假设可能不成立：**顺序未定义就会不稳定**。
    - 解决思路不是“调整 import 列表顺序”，而是让依赖关系显式化：例如用 `@AutoConfiguration(after=...)` 把顺序从“偶然”变成“确定”。
    - 排障时优先问：问题发生在“定义是否注册”还是“实例是否创建”？大多数 auto-config 顺序问题本质是 **定义层顺序**。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOrderingLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationBackoffTimingLabTest.java`

## 机制主线：顺序不定义，就会“看起来像偶发”

在 Spring Boot 里，auto-configuration 的本质是：

- Boot 在 refresh 之前批量导入一堆配置类
- 这些配置类里会声明 bean（通过 `@Bean`、`@Import`、Registrar 等）
- 许多 bean 的注册/创建由条件控制（`@ConditionalOn*`）

因此如果两个 auto-config 之间存在“隐性依赖”，但顺序没声明，结果就可能随导入顺序/排序算法变化而变化。

这类问题在工程里最常见的表现就是：

- 本地可以
- CI 不行
- 升级 Boot 后偶发

---

## 1. 现象：跨 Auto-Config 的 `@ConditionalOnBean` 可能因为顺序不确定而失败

对应实验：

- `SpringCoreBeansAutoConfigurationOrderingLabTest#conditionalOnBean_canFailAcrossAutoConfigurations_whenOrderingIsNotDefined`

你会观察到：

- Dependent auto-config 想依赖 Marker auto-config 提供的 bean
- 但如果顺序未定义，Dependent 的条件可能在 Marker 之前评估 → 条件不成立 → bean 不注册

这就是“偶发”的根源：**条件评估是有时机的**，你不能假设“总会按你期望的顺序来”。

---

## 2. 修复思路：让顺序从“偶然”变成“确定”

对应实验：

- `SpringCoreBeansAutoConfigurationOrderingLabTest#autoConfigurationAfter_canMakeCrossAutoConfigConditionsDeterministic_evenIfImportOrderIsReversed`

核心做法：

- 在 Dependent auto-config 上显式声明：`@AutoConfiguration(after = MarkerAutoConfiguration.class)`

你应该得到的稳定结论是：

- 即使导入列表顺序反过来，结果仍然确定（因为排序规则不再依赖“列表偶然顺序”）

> 这也是工程里更健康的做法：显式表达依赖关系，而不是靠 import 列表“排队”。

---

## 3. 断点闭环：把“顺序”落到可观察证据

### 3.1 推荐断点（按收益排序）

1) `AutoConfigurationImportSelector#selectImports`（auto-config 导入入口）
2) `AutoConfigurationSorter` 相关方法（排序算法入口，具体方法名以版本为准）
3) `ConfigurationClassPostProcessor#processConfigBeanDefinitions`（把导入结果转换成 BeanDefinition 的主入口）
4) `ConditionEvaluator#shouldSkip`（条件评估点：为什么这个配置/bean 被跳过）

### 3.2 固定观察点（watch list）

- “最终导入的 auto-config 列表”（排序后的）
- `ConditionEvaluationReport`（如果你在 Boot 环境里排障，这个报告能直接告诉你为什么匹配/不匹配）
- 目标 bean 的 `BeanDefinition` 是否存在（定义层） vs 实例是否创建（实例层）

---

## 4. 常见坑（工程里最容易误诊的点）

1) **误区：靠调整 import 顺序修复**
   - import 顺序只是“当前偶然可用”，不是稳定契约；升级/依赖变化后容易再次翻车。
2) **误区：把问题当成“bean 创建失败”**
   - 很多 auto-config 问题是“根本没注册定义”（定义层就被跳过了）。
3) **误区：只看异常，不看 Condition 证据**
   - 在 Boot 环境里，优先用 ConditionEvaluationReport 定位“为什么没匹配”，再去下断点。

## 源码调用链（方法级）：从“导入”到“条件评估”

把 auto-config 顺序问题说清楚，你至少要能把下面这条最短调用链串起来：

1) 导入入口：`AutoConfigurationImportSelector#selectImports`（得到候选 auto-config 列表）
2) 排序入口：`AutoConfigurationSorter`（把隐式/显式顺序规则应用到列表上）
3) 定义层落地：`ConfigurationClassPostProcessor#processConfigBeanDefinitions`（把配置类解析成 BeanDefinition）
4) 条件评估：`ConditionEvaluator#shouldSkip`（决定某个配置类/某个 `@Bean` 是否被跳过）

排障时不要先找“bean 创建失败”，先证明“定义到底有没有注册进容器”。

## 面试常问（Auto-Config 顺序与确定性）

### Q1：为什么跨 auto-config 的 `@ConditionalOnBean` 会“偶发不匹配”？

- 标准答案（可复述）：
  - 因为顺序未定义时，条件评估可能发生在依赖 bean 注册之前；这属于定义层时机/顺序问题，不是实例层创建失败。
- 证据链（方法级）：
  - `AutoConfigurationImportSelector#selectImports` → `AutoConfigurationSorter` → `ConditionEvaluator#shouldSkip`
- 最小复现：
  - `SpringCoreBeansAutoConfigurationOrderingLabTest#conditionalOnBean_canFailAcrossAutoConfigurations_whenOrderingIsNotDefined`

### Q2：`@AutoConfiguration(after=...)` 解决的是什么问题？

- 标准答案（可复述）：
  - 把“隐式依赖”变成“显式排序规则”，让排序算法有稳定依据，从而使条件评估与定义注册顺序确定化。
- 最小复现：
  - `SpringCoreBeansAutoConfigurationOrderingLabTest#autoConfigurationAfter_canMakeCrossAutoConfigConditionsDeterministic_evenIfImportOrderIsReversed`

---

## 一句话自检

你应该能用 3 句答题：

1) 为什么跨 auto-config 的 `@ConditionalOnBean` 会出现“偶发不匹配”？（提示：顺序未定义 + 条件评估有时机）
2) `@AutoConfiguration(after=...)` 解决的是什么问题？（提示：把隐式依赖变成显式排序规则）
3) 你会用哪 2 个断点把“排序→条件评估→定义是否注册”走成证据链？

<!-- BOOKIFY:START -->

上一章：[09. 循环依赖：现象、原因与规避（constructor vs setter）](../part-01-ioc-container/09-circular-dependencies.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）](021-10-spring-boot-auto-configuration.md)

<!-- BOOKIFY:END -->
