# 09. Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？
    - 使用方式：可先运行本章推荐 Lab/Exercise，再结合条件评估报告（ConditionEvaluationReport）把“为什么装配/为什么 back-off/为什么顺序影响结果”用证据链讲清楚。
    - 原理：Boot 的自动配置本质是“导入 + 条件评估 + 定义注册”，最终仍落到 BeanDefinition 与 refresh 主线（定义层→实例层→最终暴露对象）。
    - 源码入口：`AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `ConfigurationClassPostProcessor#processConfigBeanDefinitions`
    - 推荐 Lab：`SpringCoreBeansAutoConfigurationOrderingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[09. 循环依赖：现象、原因与规避（constructor vs setter）](../part-01-ioc-container/09-circular-dependencies.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）](021-10-spring-boot-auto-configuration.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？**
- 阅读方式建议：先运行本章 Lab，看清楚“同一份条件、不同顺序，结果不同”的反直觉现象；再用断点把它放回 Boot 的 auto-config 导入与排序链路里理解。

!!! summary "本章要点"

    - 在写 `@ConditionalOnBean` 时，隐含假设是“依赖的 bean 会在其之前注册/创建”。跨 auto-config 时，这个假设可能不成立：**顺序未定义就会不稳定**。
    - 解决思路不是“调整 import 列表顺序”，而是让依赖关系显式化：例如用 `@AutoConfiguration(after=...)` 把顺序从“偶然”变成“确定”。
    - 排障时优先问：问题发生在“定义是否注册”还是“实例是否创建”？大多数 auto-config 顺序问题本质是 **定义层顺序**。

!!! example "本章配套实验（先运行再读）"

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

### 角色分工速记（先抓住 4 个入口）

- 导入入口：`AutoConfigurationImportSelector#selectImports`  
- 排序入口：`AutoConfigurationImportSorter`  
- 条件评估：`ConditionEvaluator#shouldSkip`  
- 定义注册：`ConfigurationClassPostProcessor#processConfigBeanDefinitions`

### 机制系统阐述：条件 → 分支 → 结果（顺序问题版）

**条件**：依赖 Auto-Config 的 bean 是否已在定义层注册  
**分支**：排序后的导入列表 → 条件评估 → 注册/跳过  
**结果**：顺序不定义时，条件评估“偶发失败”  
**断点建议**：`AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip`

这类问题在工程里最常见的表现就是：

- 本地可以
- CI 不行
- 升级 Boot 后偶发

---

## 1. 现象：跨 Auto-Config 的 `@ConditionalOnBean` 可能因为顺序不确定而失败

对应实验：

- `SpringCoreBeansAutoConfigurationOrderingLabTest#conditionalOnBean_canFailAcrossAutoConfigurations_whenOrderingIsNotDefined`

可以观察到：

- Dependent auto-config 想依赖 Marker auto-config 提供的 bean
- 但如果顺序未定义，Dependent 的条件可能在 Marker 之前评估 → 条件不成立 → bean 不注册

这就是“偶发”的根源：**条件评估是有时机的**，读者不能假设“总会按读者期望的顺序来”。

---

## 2. 修复思路：让顺序从“偶然”变成“确定”

对应实验：

- `SpringCoreBeansAutoConfigurationOrderingLabTest#autoConfigurationAfter_canMakeCrossAutoConfigConditionsDeterministic_evenIfImportOrderIsReversed`

核心做法：

- 在 Dependent auto-config 上显式声明：`@AutoConfiguration(after = MarkerAutoConfiguration.class)`

应当得到的稳定结论是：

- 即使导入列表顺序反过来，结果仍然确定（因为排序规则不再依赖“列表偶然顺序”）

> 这也是工程里更健康的做法：显式表达依赖关系，而不是靠 import 列表“排队”。

---

## 可复现闭环（基于 `SpringCoreBeansAutoConfigurationOrderingLabTest`）

运行完成这些用例，应能够复述 3 条结论：

1) **顺序未定义时，条件评估可能失败**  
   - 断点：`ConditionEvaluator#shouldSkip`  
   - 断言：依赖 bean 的定义未注册 → 条件失败
2) **`@AutoConfiguration(after=...)` 能稳定顺序**  
   - 断点：`AutoConfigurationImportSorter`  
   - 断言：排序后依赖关系确定，条件稳定通过
3) **问题属于定义层时机**  
   - 断点：`processConfigBeanDefinitions`  
   - 断言：定义是否注册，比实例创建更关键

## 3. 断点闭环：把“顺序”落到可观察证据

### 3.1 推荐断点（按收益排序）

1) `AutoConfigurationImportSelector#selectImports`（auto-config 导入入口）
2) `AutoConfigurationSorter` 相关方法（排序算法入口，具体方法名以版本为准）
3) `ConfigurationClassPostProcessor#processConfigBeanDefinitions`（把导入结果转换成 BeanDefinition 的主入口）
4) `ConditionEvaluator#shouldSkip`（条件评估点：为什么这个配置/bean 被跳过）

### 3.2 固定观察点（watch list）

- “最终导入的 auto-config 列表”（排序后的）
- `ConditionEvaluationReport`（若在 Boot 环境里排障，这个报告能直接告诉读者为什么匹配/不匹配）
- 目标 bean 的 `BeanDefinition` 是否存在（定义层） vs 实例是否创建（实例层）

---

## 4. 常见误区（工程里最容易误诊的点）

1) **误区：靠调整 import 顺序修复**
   - import 顺序只是“当前偶然可用”，不是稳定契约；升级/依赖变化后容易再次出错。
2) **误区：把问题当成“bean 创建失败”**
   - 很多 auto-config 问题是“根本没注册定义”（定义层就被跳过了）。
3) **误区：只看异常，不看 Condition 证据**
   - 在 Boot 环境里，优先用 ConditionEvaluationReport 定位“为什么没匹配”，再去设置断点。

## 源码调用链（方法级）：从“导入”到“条件评估”

把 auto-config 顺序问题说清楚，至少应能够把下面这条最短调用链串起来：

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

## 自检要点
应能够用 3 句答题：

1) 为什么跨 auto-config 的 `@ConditionalOnBean` 会出现“偶发不匹配”？（提示：顺序未定义 + 条件评估有时机）
2) `@AutoConfiguration(after=...)` 解决的是什么问题？（提示：把隐式依赖变成显式排序规则）
3) 可以用哪 2 个断点把“排序→条件评估→定义是否注册”走成证据链？
<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansAutoConfigurationOrderingLabTest`，再用 `SpringCoreBeansAutoConfigurationBackoffTimingLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“4. 常见误区（工程里最容易误诊的点）”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

上一章：[09. 循环依赖：现象、原因与规避（constructor vs setter）](../part-01-ioc-container/09-circular-dependencies.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）](021-10-spring-boot-auto-configuration.md)

<!-- BOOKIFY:END -->
