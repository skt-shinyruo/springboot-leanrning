# 46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers

## 导读

- 本章主题：**46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansXmlNamespaceExtensionLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlNamespaceExtensionLabTest.java`

## 机制主线

这一章解决“遗留项目/三方组件里非常常见，但新手几乎没系统学过”的问题：

结论先讲清楚：XML namespace 扩展不是魔法，它本质是一个“插件系统”：

- `spring.handlers`：把 **namespace URI → NamespaceHandler 类** 映射起来
- `spring.schemas`：把 **XSD URL → classpath 里的 xsd 文件** 映射起来（避免网络拉取）
- NamespaceHandler + Parser：把 **自定义 XML 元素 → BeanDefinition 注册** 落到容器定义层

---

配套资源（你可以用来定位/排障）：

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

## 2. 怎么用：最小可用写法（你需要的最小 4 件套）

如果你要做一个最小 namespace 扩展，最少需要：

1) 一个 namespace URI（例如 `http://learning.springboot/schema/demo`）
2) 一个 `NamespaceHandler`（把不同元素名绑定到 parser）
3) 一个 `BeanDefinitionParser`（解析元素属性并注册 BeanDefinition）
4) 两个 classpath 映射文件：
   - `META-INF/spring.handlers`
   - `META-INF/spring.schemas`（可选但强烈建议，避免 XSD 拉网）

本仓库的最小实现已经给出（直接对照即可）：

---

## 3. 原理：把自定义元素放回容器定义层主线

你只要记住一条主线就能解释清楚：

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
6) 你自己的 `BeanDefinitionParser#parse`（注册 BeanDefinition）
7) `DefaultListableBeanFactory#registerBeanDefinition`（最终入库）

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

> **XML 里那些看起来像魔法的 `<context:...>` / `<tx:...>` 到底是怎么变成 BeanDefinition 的？我自己能不能做一个？出问题怎么断点排？**

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

### 4.2 观察点（你下断点时应该盯这些变量）

1) **误区：自定义 namespace = 自定义标签语法**
   - 真实本质：你是在写“把 XML 转成 BeanDefinition 的解析器”。
2) **误区：XSD 一定会去网上下载**
   - Spring 通过 `spring.schemas` 把 URL 映射到 classpath，正常不需要网络。
3) **误区：看见 `<tx:...>` 就以为是 transaction 模块的“运行时能力”**
   - `<tx:...>` 更多是“定义层注册基础设施 bean”，运行时能力通常由 BPP/代理实现。

## 常见坑与边界

- namespace URI：是否与你在 `spring.handlers` 的 key 一致（注意 `http\://` 的转义）
- element 的 localName：是否命中 handler 里注册的 parser key
- `parserContext.getRegistry()`：最终是否把 beanDefinition 注册进了正确 registry
- `BeanDefinition` 内容：
  - beanClassName / constructorArgs / propertyValues 是否符合预期
- XSD 解析：
  - `spring.schemas` 是否命中（避免网络访问）

### 常见误区（以及为什么你在真实项目里会踩）

1) **误区：namespace 扩展是“XML 语法糖”**
   - 本质上你在扩展的是“定义层输入”：把 element 解析成 BeanDefinition 并注册进 registry。
2) **误区：XSD/handler 的加载失败只会影响这一小块配置**
   - 实际上它会让整个 XML 文档解析失败，直接卡在定义阶段，应用甚至不会进入创建业务 bean 的阶段。
3) **误区：排障只看 XML 文本**
   - 更快的方式：从 `NamespaceHandlerResolver` 与 `BeanDefinitionParser` 下断点，直接看“有没有命中 handler/parser、最终注册了什么定义”。 

## 一句话自检

- 你能解释清楚：XML namespace 扩展发生在定义阶段还是创建阶段吗？输出产物是什么？
- 你能说出：`spring.handlers` 与 `spring.schemas` 分别负责解决什么问题吗？（提示：handler 映射 vs XSD 映射）
- 你遇到 `<xxx:...>` 不生效时，最短的断点链路应该从哪里进？

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansXmlNamespaceExtensionLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlNamespaceExtensionLabTest.java`

上一章：[45. 自定义 Qualifier：meta-annotation 与候选收敛](45-custom-qualifier-meta-annotation.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy](47-beandefinitionreader-other-inputs-properties-groovy.md)

<!-- BOOKIFY:END -->
