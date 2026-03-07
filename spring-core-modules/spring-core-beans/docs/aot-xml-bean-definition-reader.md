# XML → BeanDefinitionReader：定义层解析与错误分型
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    - 使用方式：可先运行本章推荐 Lab，把输入层解析或 AOT 契约完成验证；再回到正文用断点把关键分支（reader/hints/值解析）观察到并能解释。

    本章围绕42. XML → BeanDefinitionReader：定义层解析与错误分型展开，主线可以概括为：输入层（XML/Properties/Groovy）解析的落点仍是 BeanDefinition；AOT/Native 的关键是把反射/代理/资源等需求变成可测试的构建期契约（RuntimeHints）。

    对照入口：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`。需要下探源码时，可以从 `XmlBeanDefinitionReader#loadBeanDefinitions` / `DefaultBeanDefinitionDocumentReader#registerBeanDefinitions` / `BeanDefinitionParserDelegate#parseBeanDefinitionElement` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 导读

本章围绕「42. XML → BeanDefinitionReader：定义层解析与错误分型」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreBeansXmlBeanDefinitionReaderLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（AOT，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/aot.html
- 官方文档对照（Resources，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/resources.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlBeanDefinitionReaderLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    建议 先跑 `SpringCoreBeansXmlBeanDefinitionReaderLabTest` 把现象跑出来；跑完后回到正文，把“现象 → 调用链/分支 → 结论”对齐到源码。
    - 第一断点：`XmlBeanDefinitionReader#loadBeanDefinitions`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“常见误区与边界”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](appendix-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](guide-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

这一章解决一个“读者不一定天天写，但读者一定会遇到”的问题：

> **当读者看到 `BeanDefinitionStoreException`、`BeanDefinitionParsingException`、或者某个 bean “定义层”就读不进来时，应该从哪里下手？**

Spring 的 IoC 容器把所有配置来源（注解、`@Bean`、`@Import`、XML、程序化注册……）最终都归一到：

> **BeanDefinition（定义层）**

XML 只是其中一种输入形式。理解它的价值在于：它能让读者更清晰地区分“定义层失败”与“实例层失败”。

---

### 机制系统阐述：条件 → 分支 → 结果

**条件**：XML 能否被读取与正确解析
**分支**：资源读取 → XML 解析 → BeanDefinition 注册
**结果**：任一环节失败即“定义层失败”，成功后才进入实例化链路
**断点建议**：`XmlBeanDefinitionReader#loadBeanDefinitions`

## 结论先行：XML 的价值不在“写法”，而在“链路”

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
   - 进一步深入分析：`BeanDefinitionParserDelegate#parseBeanDefinitionElement`
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

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（优先运行它们）：
- Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 验证补充（从实验现象出发）

## 复现入口（可运行）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlBeanDefinitionReaderLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansXmlBeanDefinitionReaderLabTest test
```

## 源码 / 断点建议（把“看 XML”变成“走链路”）

建议观察点（设置断点时优先盯这些变量）：

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
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


### 常见误区

1) **误区：把 XML 问题当成“业务逻辑问题”**
   - XML 读不进来时，容器甚至还没开始创建相应的业务 bean；优先用“定义层入口断点”确认读/解析/注册发生在何处失败。
2) **误区：看到 `BeanDefinitionStoreException` 就直接全局搜字符串**
   - 更快的方式：从 `XmlBeanDefinitionReader#loadBeanDefinitions` 进，先确认 resource 与 schema/namespace，再定位到具体 element 的 parse。
3) **误区：以为 XML 只会影响“创建对象”**
   - XML 的核心价值是让读者把“输入形态”统一回 BeanDefinition：读者看到的是“定义元数据”，不是实例本身。

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
  - 定义层：`XmlBeanDefinitionReader#loadBeanDefinitions` / `registerBeanDefinition`
  - 实例层：`AbstractAutowireCapableBeanFactory#doCreateBean`

## 自检要点
- 应能够解释清楚：XML 在 Spring 里最终会变成什么吗？（提示：BeanDefinition）
- 遇到 `BeanDefinitionStoreException` 时，第一步应该先分型到“定义阶段”还是“创建阶段”？为什么？
- 应能够说出：从哪条最短调用链进断点，能最快定位到“哪个资源/哪个 element 解析失败”吗？

## 小结

- XML 是一种输入形式，它最终会被归一为 BeanDefinition 并注册到 BeanFactory
- XML 相关异常排障优先做“定义层 vs 实例层”分型；定义层失败的典型信号是 `BeanDefinitionStoreException`


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlBeanDefinitionReaderLabTest.java`

<!-- BOOKIFY:END -->
