# 42. XML → BeanDefinitionReader：定义层解析与错误分型
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：42. XML → BeanDefinitionReader：定义层解析与错误分型
    - 怎么使用：建议先跑本章推荐 Lab，把输入层解析或 AOT 契约跑通；再回到正文用断点把关键分支（reader/hints/值解析）看见并能解释。
    - 原理：输入层（XML/Properties/Groovy）解析的落点仍是 BeanDefinition；AOT/Native 的关键是把反射/代理/资源等需求变成可测试的构建期契约（RuntimeHints）。
    - 源码入口：`XmlBeanDefinitionReader#loadBeanDefinitions` / `DefaultBeanDefinitionDocumentReader#registerBeanDefinitions` / `BeanDefinitionParserDelegate#parseBeanDefinitionElement`
    - 推荐 Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[41. RuntimeHints 入门：把构建期契约跑通](41-runtimehints-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[43. 容器外对象注入：AutowireCapableBeanFactory](43-autowirecapablebeanfactory-external-objects.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**42. XML → BeanDefinitionReader：定义层解析与错误分型**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，应能够用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见误区在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlBeanDefinitionReaderLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：“Resource→Reader→registerBeanDefinition”的最短证据链与关键入口。
    - B（边界反例）：反例：schema 不匹配/namespace 扩展缺失/属性类型转换失败的误判对照。
    - C（排障 SOP）：排障 SOP：把 BeanDefinitionStoreException 分型到解析/注册/转换哪个阶段。
    - D（断点观察）：断点：loadBeanDefinitions、doRegisterBeanDefinitions、注册入口。
    - E（面试复述）：面试追问：XML 解析与注解解析最终为什么都落到 BeanDefinition？如何证明。
<!-- AE-DEEPENING:END -->
## 机制主线

这一章解决一个“读者不一定天天写，但读者一定会遇到”的问题：

> **当读者看到 `BeanDefinitionStoreException`、`BeanDefinitionParsingException`、或者某个 bean “定义层”就读不进来时，应该从哪里下手？**

Spring 的 IoC 容器把所有配置来源（注解、`@Bean`、`@Import`、XML、程序化注册……）最终都归一到：

> **BeanDefinition（定义层）**

XML 只是其中一种输入形式。理解它的价值在于：它能让读者更清晰地区分“定义层失败”与“实例层失败”。

---

### 机制讲透：条件 → 分支 → 结果

**条件**：XML 能否被读取与正确解析  
**分支**：资源读取 → XML 解析 → BeanDefinition 注册  
**结果**：任一环节失败即“定义层失败”，成功后才进入实例化链路  
**断点建议**：`XmlBeanDefinitionReader#loadBeanDefinitions`

## 1. 结论先行：XML 的价值不在“写法”，而在“链路”

XML 这条链路的核心是：

- `XmlBeanDefinitionReader#loadBeanDefinitions`：把 XML 读成 BeanDefinition，并注册到 BeanFactory
- 注册成功之后，后续仍走统一主线：refresh → instantiate → populate → initialize

因此遇到 XML 相关异常时，第一件事就是分型：

- **定义层异常**：读/解析/注册阶段失败（refresh 前半段）
- **实例层异常**：创建/注入/初始化阶段失败（refresh 后半段或 getBean 时）

---

本模块提供最小 XML 示例：

- 正常 XML：成功注册 BeanDefinition，并能读取 definition 元信息
- 非法 XML：抛出 `BeanDefinitionStoreException`（用于读者建立“错误分型”直觉）

入口测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlBeanDefinitionReaderLabTest.java`
  - `xmlBeanDefinitionReader_loadsBeanDefinitions_andBeanDefinitionContainsConstructorArgValues()`（正常 XML：definition 可观察）
  - `invalidXml_throwsBeanDefinitionStoreException_asDefinitionPhaseErrorSignal()`（非法 XML：定义层失败信号）

把 XML 问题迅速收敛到 3 个层次（入口 → 解析 → 入库）：

1) 入口：`XmlBeanDefinitionReader#loadBeanDefinitions`（读资源 + 进入 XML 解析）
2) 解析：`DefaultBeanDefinitionDocumentReader#registerBeanDefinitions`（把 Document 变成一组 BeanDefinition）
   - 进一步深挖：`BeanDefinitionParserDelegate#parseBeanDefinitionElement`
3) 入库：`DefaultListableBeanFactory#registerBeanDefinition`（定义注册入口：冲突/覆盖/合法性检查）

当需要把错误放回 refresh 主线理解时：

- `AbstractApplicationContext#refresh`
- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`

- `Resource` / `resourceDescription`：到底读的是哪一个 XML（路径/类路径资源/文件资源）
- `beanName` / `beanClassName`：解析到的定义是否符合预期
- `BeanDefinition` 的关键信息：scope、propertyValues、constructorArgumentValues
- 异常分型：是“XML 语法/命名空间解析失败”，还是“注册阶段合法性检查失败”

---

这一章应当带走的能力是：

- 能把 XML 输入归一到 BeanDefinition 视角
- 能用 `BeanDefinitionStoreException` 快速判断“定义层失败”
- 能在 debugger 里用 2–3 个入口断点把问题收敛到：读资源 → 解析 Document → registerBeanDefinition

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 2. 复现入口（可运行）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlBeanDefinitionReaderLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansXmlBeanDefinitionReaderLabTest test
```

## 3. 源码 / 断点建议（把“看 XML”变成“走链路”）

建议观察点（下断点时优先盯这些变量）：

- `resource` / `resourceDescription`：到底读的是哪一个 XML（路径/类路径资源/文件资源）
- `document` / `root`：XML 是否被正确解析成 Document（命名空间/元素结构是否符合预期）
- `beanName` / `beanClassName`：解析出来的定义是什么（是否指向了读者期望的类型）
- `BeanDefinition` 关键信息：constructor args / property values / scope / lazy 等元数据是否符合预期
- 异常类型与 cause：是“解析失败”（document 层）还是“注册失败”（registry 层）

1) **误区：XML 问题只能靠“看 XML”解决**
   - 更有效：先分型（定义层 vs 实例层），再锁定断点入口。
2) **误区：XML = 过时，不用学**
   - 在真实项目里，遗留配置/三方组件/某些 starter 仍可能引入 XML 资源；排障时必须认识链路。

## 常见误区与边界

### 常见误区

1) **误区：把 XML 问题当成“业务逻辑问题”**
   - XML 读不进来时，容器甚至还没开始创建相应的业务 bean；优先用“定义层入口断点”确认读/解析/注册发生在何处失败。
2) **误区：看到 `BeanDefinitionStoreException` 就直接全局搜字符串**
   - 更快的方式：从 `XmlBeanDefinitionReader#loadBeanDefinitions` 进，先确认 resource 与 schema/namespace，再定位到具体 element 的 parse。
3) **误区：以为 XML 只会影响“创建对象”**
   - XML 的核心价值是让读者把“输入形态”统一回 BeanDefinition：读者看的其实是“定义元数据”，不是实例本身。

## 面试常问（XML：Reader 到底做了什么）

### Q1：XML 是如何变成 BeanDefinition 并注册进容器的？

- 标准答案（可复述）：
  - XML 通过 `XmlBeanDefinitionReader` 读取与解析，产出 `BeanDefinition`，最终注册到 `DefaultListableBeanFactory` 的 registry；它属于定义层输入，不是直接 new 对象。
- 证据链（方法级）：
  - `XmlBeanDefinitionReader#loadBeanDefinitions`
  - `DefaultListableBeanFactory#registerBeanDefinition`
- 最小复现：
  - `SpringCoreBeansXmlBeanDefinitionReaderLabTest`

### Q2：排 XML 解析问题时，如何快速判断“定义没注册”还是“创建失败”？

- 标准答案（可复述）：
  - 先在 `registerBeanDefinition` 证明定义是否写入 registry；如果没写入，优先排资源/解析/schema/namespace；如果写入了，再回到 `doCreateBean` 看实例化/注入/初始化链路。
- 断点建议：
  - 定义层：`XmlBeanDefinitionReader#loadBeanDefinitions` / `registerBeanDefinition`
  - 实例层：`AbstractAutowireCapableBeanFactory#doCreateBean`

## 自检要点
- 应能够解释清楚：XML 在 Spring 里最终会变成什么吗？（提示：BeanDefinition）
- 遇到 `BeanDefinitionStoreException` 时，第一步应该先分型到“定义阶段”还是“创建阶段”？为什么？
- 应能够说出：从哪条最短调用链进断点，能最快定位到“哪个资源/哪个 element 解析失败”吗？

## 小结与下一章

## 5. 小结与下一章预告

- XML 是一种输入形式，它最终会被归一为 BeanDefinition 并注册到 BeanFactory
- XML 相关异常排障优先做“定义层 vs 实例层”分型；定义层失败的典型信号是 `BeanDefinitionStoreException`

下一章将补齐另一个真实工程经常遇到的边界：**容器外对象**（不是 Spring 创建的）如何获得注入、初始化与销毁能力（AutowireCapableBeanFactory）。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlBeanDefinitionReaderLabTest.java`

上一章：[41. RuntimeHints 入门：把构建期契约跑通](41-runtimehints-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[43. 容器外对象注入：AutowireCapableBeanFactory](43-autowirecapablebeanfactory-external-objects.md)

<!-- BOOKIFY:END -->
