# 46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers
    - 怎么使用：建议先跑本章推荐 Lab，把输入层解析或 AOT 契约跑通；再回到正文用断点把关键分支（reader/hints/值解析）看见并能解释。
    - 原理：输入层（XML/Properties/Groovy）解析的落点仍是 BeanDefinition；AOT/Native 的关键是把反射/代理/资源等需求变成可测试的构建期契约（RuntimeHints）。
    - 源码入口：`BeanDefinitionParserDelegate#parseCustomElement` / `XmlBeanDefinitionReader#doLoadBeanDefinitions` / `DefaultBeanDefinitionDocumentReader#parseBeanDefinitions`
    - 推荐 Lab：`SpringCoreBeansXmlNamespaceExtensionLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[45. 自定义 Qualifier：meta-annotation 与候选收敛](45-custom-qualifier-meta-annotation.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy](47-beandefinitionreader-other-inputs-properties-groovy.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，应能够用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见误区在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansXmlNamespaceExtensionLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlNamespaceExtensionLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：“namespace resolution→handler→parser→BeanDefinition”的证据链。
    - B（边界反例）：反例：spring.handlers 缺失、schemaLocation 错误、parser 抛错的分型。
    - C（排障 SOP）：排障 SOP：namespace 解析失败如何定位到 handler 加载/资源缺失/解析异常。
    - D（断点观察）：断点：NamespaceHandlerResolver、handler mapping 加载点、parse 入口。
    - E（面试复述）：面试追问：XML 扩展机制与注解扩展机制（processor）有何异同？
<!-- AE-DEEPENING:END -->
## 机制主线

这一章解决“遗留项目/三方组件里非常常见，但新手几乎没系统学过”的问题：

结论先讲清楚：XML namespace 扩展不是隐式行为，它本质是一个“插件系统”：

- `spring.handlers`：把 **namespace URI → NamespaceHandler 类** 映射起来
- `spring.schemas`：把 **XSD URL → classpath 里的 xsd 文件** 映射起来（避免网络拉取）
- NamespaceHandler + Parser：把 **自定义 XML 元素 → BeanDefinition 注册** 落到容器定义层

---

配套资源（可以用来定位/排障）：

- `META-INF/spring.handlers`（namespace URI → Handler）
- `META-INF/spring.schemas`（XSD URL → 本地资源）
- 自定义 XSD 文件（classpath 内）
- 对应的 `NamespaceHandler` / `BeanDefinitionParser` 实现类

### 机制讲透：条件 → 分支 → 结果

**条件**：XML 元素属于自定义 namespace  
**分支**：`parseCustomElement` → `NamespaceHandlerResolver` → `BeanDefinitionParser`  
**结果**：生成并注册 `BeanDefinition`，进入统一创建主线  
**断点建议**：`BeanDefinitionParserDelegate#parseCustomElement`

---

## 1. 是什么：namespace 扩展解决的是什么问题？

在 beans XML 里，Spring 既支持“通用 `<bean>` 元素”，也支持“业务/模块化语义更强的自定义元素”：

- `<bean>`：表达力强，但在大型 XML 配置里可读性很差
- `<tx:annotation-driven>`：一行顶很多行（注册一组基础设施 bean）
- `<context:component-scan>`：把扫描/注册逻辑打包成一个元素

这类 `<tx:...>`/`<context:...>` 的本质是：

> **把一段“注册 BeanDefinition 的动作”封装成一个 XML 元素**
> 最终仍然回到 `BeanDefinitionRegistry`。

---

## 2. 怎么用：最小可用写法（需要的最小 4 件套）

若要做一个最小 namespace 扩展，最少需要：

1) 一个 namespace URI（例如 `http://learning.springboot/schema/demo`）
2) 一个 `NamespaceHandler`（把不同元素名绑定到 parser）
3) 一个 `BeanDefinitionParser`（解析元素属性并注册 BeanDefinition）
4) 两个 classpath 映射文件：
   - `META-INF/spring.handlers`
   - `META-INF/spring.schemas`（可选但强烈建议，避免 XSD 拉网）

本仓库的最小实现已经给出（直接对照即可）：

---

## 3. 原理：把自定义元素放回容器定义层主线

读者只要记住一条主线就能解释清楚：

1) `XmlBeanDefinitionReader` 读取 XML → 得到 DOM Document
2) `DefaultBeanDefinitionDocumentReader` 遍历子节点
3) 遇到 `<bean>`：走默认解析分支（`parseBeanDefinitionElement`）
4) 遇到 `<demo:message>` 这类自定义元素：走 **custom element 分支**
5) custom element 分支做三件事：
   - 用 namespace URI 找到 `NamespaceHandler`
   - handler 再用元素名找到对应 parser
   - parser 把 XML 属性解析成 BeanDefinition 并注册到 registry

产物是什么？

- **定义层产物：** `BeanDefinition`（注册进 `BeanDefinitionRegistry`）
- **实例层产物：** 之后正常走统一主线实例化（构造/注入/初始化）

---

1) `XmlBeanDefinitionReader#doLoadBeanDefinitions`（读入 XML 的入口）
2) `DefaultBeanDefinitionDocumentReader#parseBeanDefinitions`（遍历/分派）
3) `BeanDefinitionParserDelegate#parseCustomElement`（进入自定义元素分支）
4) `DefaultNamespaceHandlerResolver#resolve`（spring.handlers 映射解析）
5) `NamespaceHandlerSupport#parse`（handler 分派到具体 parser）
6) 读者自己的 `BeanDefinitionParser#parse`（注册 BeanDefinition）
7) `DefaultListableBeanFactory#registerBeanDefinition`（最终入库）

---

## 错误分型（快速判断）

遇到 namespace 相关异常时，优先做三分法：

1) **资源错误**：`spring.schemas` 未命中、XSD 资源找不到  
2) **解析错误**：XML 结构不合法/namespace 未识别（document 级失败）  
3) **语义错误**：Parser 解析属性失败/抛异常（element 级失败）  

对应入口：

- 资源：`DefaultNamespaceHandlerResolver#resolve` / `NamespaceHandlerResolver`  
- 解析：`XmlBeanDefinitionReader#loadBeanDefinitions`  
- 语义：自定义 `BeanDefinitionParser#parse`  

---

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansXmlNamespaceExtensionLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

> **XML 里那些看起来像隐式行为的 `<context:...>` / `<tx:...>` 到底是怎么变成 BeanDefinition 的？我自己能不能做一个？出问题怎么断点排？**

## 0. 复现入口（可运行）

入口测试（建议先跑通再下断点）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlNamespaceExtensionLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansXmlNamespaceExtensionLabTest test
```

- `spring-core-modules/spring-core-beans/src/test/resources/META-INF/spring.handlers`
- `spring-core-modules/spring-core-beans/src/test/resources/META-INF/spring.schemas`
- `spring-core-modules/spring-core-beans/src/test/resources/part05_aot_and_real_world/xml/demo-namespace.xml`
- `spring-core-modules/spring-core-beans/src/test/resources/META-INF/spring/demo.xsd`

- Handler：`.../xmlns/DemoNamespaceHandler`
- Parser：`.../xmlns/DemoMessageBeanDefinitionParser`
- XML：`src/test/resources/.../demo-namespace.xml`
- 映射：`src/test/resources/META-INF/spring.handlers` / `spring.schemas`

## 4. 怎么实现的：关键类/方法 + 断点入口 + 观察点

### 4.1 断点入口（建议从“能解释链路”到“看细节”）

最推荐的断点组合（从入口到闭环）：

### 4.2 观察点（设置断点时应该盯这些变量）

1) **误区：自定义 namespace = 自定义标签语法**
   - 真实本质：读者是在写“把 XML 转成 BeanDefinition 的解析器”。
2) **误区：XSD 一定会去网上下载**
   - Spring 通过 `spring.schemas` 把 URL 映射到 classpath，正常不需要网络。
3) **误区：看见 `<tx:...>` 就以为是 transaction 模块的“运行时能力”**
   - `<tx:...>` 更多是“定义层注册基础设施 bean”，运行时能力通常由 BPP/代理实现。

## 常见误区与边界

- namespace URI：是否与在 `spring.handlers` 的 key 一致（注意 `http\://` 的转义）
- element 的 localName：是否命中 handler 里注册的 parser key
- `parserContext.getRegistry()`：最终是否把 beanDefinition 注册进了正确 registry
- `BeanDefinition` 内容：
  - beanClassName / constructorArgs / propertyValues 是否符合预期
- XSD 解析：
  - `spring.schemas` 是否命中（避免网络访问）

### 常见误区（以及为什么在真实项目里会遇到）

1) **误区：namespace 扩展是“XML 语法糖”**
   - 本质上在扩展的是“定义层输入”：把 element 解析成 BeanDefinition 并注册进 registry。
2) **误区：XSD/handler 的加载失败只会影响这一小块配置**
   - 实际上它会让整个 XML 文档解析失败，直接卡在定义阶段，应用甚至不会进入创建业务 bean 的阶段。
3) **误区：排障只看 XML 文本**
   - 更快的方式：从 `NamespaceHandlerResolver` 与 `BeanDefinitionParser` 下断点，直接看“有没有命中 handler/parser、最终注册了什么定义”。

## 面试常问（XML namespace 扩展：扩展的不是对象，是“定义层翻译器”）

### Q1：自定义 XML namespace 扩展到底扩展的是什么？（Handler/Parser 的职责）

- 标准答案（可复述）：
  - 扩展的是“定义层解析器”：`NamespaceHandler` 负责把 namespace 下的元素映射到具体 `BeanDefinitionParser`；Parser 负责把元素翻译成 BeanDefinition 并注册到 registry。
- 证据链（方法级）：
  - `NamespaceHandlerResolver` / `NamespaceHandler#init`
  - `BeanDefinitionParser#parse`
  - 最终落点：`DefaultListableBeanFactory#registerBeanDefinition`
- 最小复现：
  - `SpringCoreBeansXmlNamespaceExtensionLabTest`

### Q2：为什么说 namespace 扩展属于“定义层输入”，不是“实例层扩展点”？

- 标准答案（可复述）：
  - 因为它的产物仍然是 BeanDefinition；实例怎么创建/怎么注入/怎么增强依旧走容器主线（`doCreateBean`、BPP 链等），namespace 只是换了一种输入语法。

## 自检要点
- 应能够解释清楚：XML namespace 扩展发生在定义阶段还是创建阶段吗？输出产物是什么？
- 应能够说出：`spring.handlers` 与 `spring.schemas` 分别负责解决什么问题吗？（提示：handler 映射 vs XSD 映射）
- 遇到 `<xxx:...>` 不生效时，最短的断点链路应该从哪里进？

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansXmlNamespaceExtensionLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlNamespaceExtensionLabTest.java`

上一章：[45. 自定义 Qualifier：meta-annotation 与候选收敛](45-custom-qualifier-meta-annotation.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy](47-beandefinitionreader-other-inputs-properties-groovy.md)

<!-- BOOKIFY:END -->
