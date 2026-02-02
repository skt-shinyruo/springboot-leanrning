# 47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy
    - 使用方式：可先运行本章推荐 Lab，把输入层解析或 AOT 契约完成验证；再回到正文用断点把关键分支（reader/hints/值解析）观察到并能解释。
    - 原理：输入层（XML/Properties/Groovy）解析的落点仍是 BeanDefinition；AOT/Native 的关键是把反射/代理/资源等需求变成可测试的构建期契约（RuntimeHints）。
    - 源码入口：`SpringCoreBeansPropertiesBeanDefinitionReaderLabTest#propertiesBeanDefinitionReader_registersBeanDefinitions_fromPropertiesFile` / `SpringCoreBeansGroovyBeanDefinitionReaderLabTest#groovyBeanDefinitionReader_registersBeanDefinitions_fromGroovyScript` / `AbstractBeanDefinitionReader#loadBeanDefinitions`
    - 推荐 Lab：`SpringCoreBeansGroovyBeanDefinitionReaderLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers](46-xml-namespace-extension.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[48. 方法注入：replaced-method / MethodReplacer（实例化策略分支）](48-method-injection-replaced-method.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy**
- 阅读建议：建议先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（AOT，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/aot.html
- 官方文档对照（Resources，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/resources.html


!!! summary "本章要点"

    - 读完本章，应能够用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见误区在哪里”。
    - 如果只看一眼：请先运行一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansGroovyBeanDefinitionReaderLabTest` / `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansPropertiesBeanDefinitionReaderLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansGroovyBeanDefinitionReaderLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest#propertiesBeanDefinitionReader_registersBeanDefinitions_fromPropertiesFile`，再用 `SpringCoreBeansGroovyBeanDefinitionReaderLabTest#groovyBeanDefinitionReader_registersBeanDefinitions_fromGroovyScript` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`AbstractBeanDefinitionReader#loadBeanDefinitions`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“常见误区与边界”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

这一章解决一个“源码视角必须掌握，但现代项目里容易被忽略”的问题：

> **Spring 是怎么把不同输入源（注解、XML、properties、groovy）统一成 BeanDefinition 的？BeanDefinitionReader 到底干了什么？**

核心结论：

- **BeanDefinitionReader 只负责一件事：把某种输入源解析成 BeanDefinition，并注册进 BeanDefinitionRegistry。**
- 它通常不负责“实例化 bean”，实例化仍然由 BeanFactory/容器主线完成。

---

入口测试：

- `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`
- `SpringCoreBeansGroovyBeanDefinitionReaderLabTest`

对应资源文件：

- `spring-core-modules/spring-core-beans/src/test/resources/part05_aot_and_real_world/reader/beans.properties`
- `spring-core-modules/spring-core-beans/src/test/resources/part05_aot_and_real_world/reader/beans.groovy`

---

- Properties：
  - 测试：`SpringCoreBeansPropertiesBeanDefinitionReaderLabTest#propertiesBeanDefinitionReader_registersBeanDefinitions_fromPropertiesFile`
  - 资源：`spring-core-modules/spring-core-beans/src/test/resources/part05_aot_and_real_world/reader/beans.properties`
- Groovy：
  - 测试：`SpringCoreBeansGroovyBeanDefinitionReaderLabTest#groovyBeanDefinitionReader_registersBeanDefinitions_fromGroovyScript`
  - 资源：`spring-core-modules/spring-core-beans/src/test/resources/part05_aot_and_real_world/reader/beans.groovy`

## 1. 是什么：为什么要有 BeanDefinitionReader 家族？

从 beans 体系角度看，Spring 的强大来自“输入多样但输出统一”：

- 输入可以是：注解、`@Bean`、`@Import`、XML、properties、groovy DSL、甚至程序化注册……
- 但最终都会归一为：**BeanDefinition（定义层）**

BeanDefinitionReader 的价值在于：

> 让“新的输入源”以插件形式接入容器定义层，而不用改容器核心。

---

### 机制系统阐述：条件 → 分支 → 结果

**条件**：选择不同输入源（properties / groovy / xml / 注解）  
**分支**：对应 Reader 解析 → `BeanDefinition` 注册  
**结果**：实例化统一走 BeanFactory 主线  
**断点建议**：`AbstractBeanDefinitionReader#loadBeanDefinitions`

## 2. 使用方式：两种典型 reader 的最小闭环

### 2.1 PropertiesBeanDefinitionReader（遗留/轻量输入）

适用场景：

- 遗留项目（非常早期的 Spring 配置风格）
- 教学/快速 demo：用最少语法表达“定义 → 注入”

本仓库的最小示例：

- reader：`PropertiesBeanDefinitionReader`
- registry：`DefaultListableBeanFactory`
- 输入：`beans.properties`

应当观察到：

- reader 先把 properties 解析成 BeanDefinition 并注册
- `getBean()` 时才会创建实例并完成属性填充

### 2.2 GroovyBeanDefinitionReader（DSL 输入）

适用场景：

- DSL 风格配置（历史上常见于 Spring 生态中的脚本化配置）
- 若希望把“定义层输入”做成更可读的脚本

本仓库的最小示例：

- reader：`GroovyBeanDefinitionReader`
- registry：`GenericApplicationContext`
- 输入：`beans.groovy`

依赖说明：

---

- `GroovyBeanDefinitionReader` 属于 Spring Beans 体系，但执行 groovy 脚本需要 Groovy 运行库。
- 本仓库已在 `spring-core-modules/spring-core-beans/pom.xml` 以 test scope 引入 `org.apache.groovy:groovy`，因此这章的 Lab 在测试环境可直接运行。

## 3. 原理：Reader 把“输入”落到定义层主线的哪个位置？

可以把 Reader 放回容器主线去理解：

1) 读者选择某个输入源（properties/groovy/xml/annotations）
2) 对应的 Reader 把它解析为 **BeanDefinition** 并注册进 Registry（定义层）
3) 之后读者 refresh context 或调用 getBean：
   - BeanFactory 根据定义创建实例（实例层）
   - 执行注入、回调、BPP 等（生命周期链路）

所以 Reader 解决的是“定义从哪里来”的问题，而不是“对象怎么创建/怎么被代理”的问题。

---

- `AbstractBeanDefinitionReader#loadBeanDefinitions`（reader 抽象入口）
- `DefaultListableBeanFactory#registerBeanDefinition`（定义入库统一入口）

- `PropertiesBeanDefinitionReader#loadBeanDefinitions`

- `GroovyBeanDefinitionReader#loadBeanDefinitions`

建议观察点：

- 注册了哪些 beanName（数量/名称是否符合预期）
- BeanDefinition 的 beanClassName / propertyValues / constructorArgs
- refresh/getBean 的时机：是否能够把“注册定义”和“创建实例”混为一谈

---

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先运行它们）：
- Lab：`SpringCoreBeansGroovyBeanDefinitionReaderLabTest` / `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansPropertiesBeanDefinitionReaderLabTest.java`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansGroovyBeanDefinitionReaderLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPropertiesBeanDefinitionReaderLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansGroovyBeanDefinitionReaderLabTest test
```

- `spring-core-modules/spring-core-beans/src/test/resources/part05_aot_and_real_world/reader/beans.properties`
- `spring-core-modules/spring-core-beans/src/test/resources/part05_aot_and_real_world/reader/beans.groovy`

- `GroovyBeanDefinitionReader` 位于 Spring beans 包中，但运行时需要 Groovy 运行库
- 本仓库已在 `spring-core-modules/spring-core-beans/pom.xml` 以 test scope 引入 `org.apache.groovy:groovy:4.0.21`，确保 Lab 可运行

## 4. 怎么实现的：断点入口与观察点（从 reader 到 registry）

建议断点（两条 reader 共通的收敛点）：

1) `AbstractBeanDefinitionReader#loadBeanDefinitions`：reader 家族统一入口（输入源 → BeanDefinition）
2) `DefaultListableBeanFactory#registerBeanDefinition`：定义入库统一入口（registry 层）

Properties reader 的典型断点：

- `PropertiesBeanDefinitionReader#loadBeanDefinitions`：properties 输入解析入口

Groovy reader 的典型断点：

- `GroovyBeanDefinitionReader#loadBeanDefinitions`：groovy script 解析入口

## 常见误区与边界

### 常见误区

1) **误区：Reader = 创建对象**
   - Reader 注册的是“配方”（BeanDefinition），对象创建发生在后续主线。
2) **误区：使用 Groovy/Properties，因此不属于 beans 体系**
   - 恰恰相反：这些机制说明 beans 体系的抽象能力（输入可扩展，输出统一）。

## 排障决策表（BeanDefinitionReader：资源/解析/注册分型）

| 现象 | 更像失败在哪一段 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| 资源不存在 / 路径错误 | 输入源（Resource） | 断点 `AbstractBeanDefinitionReader#loadBeanDefinitions`；看 `resourceDescription` | 修正路径/类路径；确认测试资源打包 | `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest` / `SpringCoreBeansGroovyBeanDefinitionReaderLabTest` |
| 脚本/属性解析异常 | reader 解析阶段 | `PropertiesBeanDefinitionReader#loadBeanDefinitions` / `GroovyBeanDefinitionReader#loadBeanDefinitions` | 修正格式；Groovy 确认运行库依赖存在 | 同上 |
| “看起来加载了，但容器里找不到 bean” | 注册阶段未落地 | 断点 `DefaultListableBeanFactory#registerBeanDefinition`；看是否真正写入 registry | 确认 beanName/定义是否冲突；排查覆盖策略 | 同上 |
| 误以为这是“创建失败” | 尚未进入创建阶段 | 先证明是否进入 `preInstantiateSingletons/doCreateBean` | 先把问题分型为“定义层 vs 实例层”再排查 | 结合 [18](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md) 主线 |

## 面试常问（Reader：输入可扩展，输出要统一）

### Q1：BeanDefinitionReader 做的是“注册配方”还是“创建对象”？为什么这个区分重要？

- 标准答案（可复述）：
  - Reader 负责把各种输入（XML/properties/groovy…）解析成 BeanDefinition 并注册到 registry；对象创建发生在后续 `getBean/preInstantiateSingletons/doCreateBean` 主线。区分清楚才能正确分型排障。
- 证据链（方法级）：
  - `AbstractBeanDefinitionReader#loadBeanDefinitions`
  - `DefaultListableBeanFactory#registerBeanDefinition`
- 最小复现：
  - `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest` / `SpringCoreBeansGroovyBeanDefinitionReaderLabTest`

## 自检要点
- 应能够解释清楚：BeanDefinitionReader 做的是“注册配方”还是“创建对象”吗？为什么这个区分重要？
- 应能够说出：Properties/Groovy 这类输入最终落到 Spring 的哪一种统一产物上吗？（提示：BeanDefinition）
- 遇到“Reader 加载失败/资源不存在/脚本解析失败”时，最短断点入口在哪？

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansGroovyBeanDefinitionReaderLabTest` / `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansPropertiesBeanDefinitionReaderLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansGroovyBeanDefinitionReaderLabTest.java`

上一章：[46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers](46-xml-namespace-extension.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[48. 方法注入：replaced-method / MethodReplacer（实例化策略分支）](48-method-injection-replaced-method.md)

<!-- BOOKIFY:END -->
