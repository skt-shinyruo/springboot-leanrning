# XML namespace 扩展：NamespaceHandler / Parser / spring.handlers
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把输入层解析或 AOT 契约变成可验证结果；再回到正文用断点把关键分支（reader/hints/值解析）观察到并能解释。

    观察对象：46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers。
    主线位置：输入层（XML/Properties/Groovy）解析的落点仍是 BeanDefinition；AOT/Native 的关键是把反射/代理/资源等需求变成可测试的构建期契约（RuntimeHints）。

    对照入口：`SpringCoreBeansXmlNamespaceExtensionLabTest`。需要下探源码时，可以从 `BeanDefinitionParserDelegate#parseCustomElement` / `XmlBeanDefinitionReader#doLoadBeanDefinitions` / `DefaultBeanDefinitionDocumentReader#parseBeanDefinitions` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 问题：XML namespace 扩展

先运行 `SpringCoreBeansXmlNamespaceExtensionLabTest`，把核心现象固定为可复现事实；随后围绕入口方法、关键分支和可观察变量阅读正文。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（AOT，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/aot.html
- 官方文档对照（Resources，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/resources.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansXmlNamespaceExtensionLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlNamespaceExtensionLabTest.java`


## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

这一章解决“遗留项目/三方组件里常见，但新手几乎没系统学过”的问题：

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

### 机制边界：条件、分支与结果

**条件**：XML 元素属于自定义 namespace
**分支**：`parseCustomElement` → `NamespaceHandlerResolver` → `BeanDefinitionParser`
**结果**：生成并注册 `BeanDefinition`，进入统一创建主线
**断点入口**：`BeanDefinitionParserDelegate#parseCustomElement`

---

## 是什么：namespace 扩展解决的是什么问题？

在 beans XML 里，Spring 既支持“通用 `<bean>` 元素”，也支持“业务/模块化语义更强的自定义元素”：

- `<bean>`：表达力强，但在大型 XML 配置里可读性很差
- `<tx:annotation-driven>`：一行顶很多行（注册一组基础设施 bean）
- `<context:component-scan>`：把扫描/注册逻辑打包成一个元素

这类 `<tx:...>`/`<context:...>` 的本质是：

> **把一段“注册 BeanDefinition 的动作”封装成一个 XML 元素**
> 最终仍然回到 `BeanDefinitionRegistry`。

---

## 使用方式：最小可用写法（需要的最小 4 件套）

若要做一个最小 namespace 扩展，最少需要：

1. 一个 namespace URI（例如 `http://learning.springboot/schema/demo`）
2. 一个 `NamespaceHandler`（把不同元素名绑定到 parser）
3. 一个 `BeanDefinitionParser`（解析元素属性并注册 BeanDefinition）
4. 两个 classpath 映射文件：
   - `META-INF/spring.handlers`
  - `META-INF/spring.schemas`（可选但常用，避免 XSD 拉网）

本仓库的最小实现已经给出（直接对照即可）：

---

## 原理：把自定义元素放回容器定义层主线

只要记住一条主线就能解释清楚：

1. `XmlBeanDefinitionReader` 读取 XML → 得到 DOM Document
2. `DefaultBeanDefinitionDocumentReader` 遍历子节点
3. 遇到 `<bean>`：走默认解析分支（`parseBeanDefinitionElement`）
4. 遇到 `<demo:message>` 这类自定义元素：走 **custom element 分支**
5. custom element 分支做三件事：
   - 用 namespace URI 找到 `NamespaceHandler`
   - handler 再用元素名找到对应 parser
   - parser 把 XML 属性解析成 BeanDefinition 并注册到 registry

产物是什么？

- **定义层产物：** `BeanDefinition`（注册进 `BeanDefinitionRegistry`）
- **实例层产物：** 之后正常走统一主线实例化（构造/注入/初始化）

---

1. `XmlBeanDefinitionReader#doLoadBeanDefinitions`（读入 XML 的入口）
2. `DefaultBeanDefinitionDocumentReader#parseBeanDefinitions`（遍历/分派）
3. `BeanDefinitionParserDelegate#parseCustomElement`（进入自定义元素分支）
4. `DefaultNamespaceHandlerResolver#resolve`（spring.handlers 映射解析）
5. `NamespaceHandlerSupport#parse`（handler 分派到具体 parser）
6. 自己的 `BeanDefinitionParser#parse`（注册 BeanDefinition）
7. `DefaultListableBeanFactory#registerBeanDefinition`（最终入库）

---

## 错误分型（快速判断）

遇到 namespace 相关异常时，优先做三分法：

1. **资源错误**：`spring.schemas` 未命中、XSD 资源找不到
2. **解析错误**：XML 结构不合法/namespace 未识别（document 级失败）
3. **语义错误**：Parser 解析属性失败/抛异常（element 级失败）

对应入口：

- 资源：`DefaultNamespaceHandlerResolver#resolve` / `NamespaceHandlerResolver`
- 解析：`XmlBeanDefinitionReader#loadBeanDefinitions`
- 语义：自定义 `BeanDefinitionParser#parse`

---

---

## 实验：把现象固定成断言

本章可复核的实验入口：
- Lab：`SpringCoreBeansXmlNamespaceExtensionLabTest`
- 命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 从实验现象看边界

> **XML 中那些表面上像隐式行为的 `<context:...>` / `<tx:...>` 到底是如何变成 BeanDefinition 的？是否可以自定义扩展？出现问题时应如何设置断点定位？**

## 运行入口

入口测试（可先运行通再设置断点）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlNamespaceExtensionLabTest.java`

运行命令：

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

## 怎么实现的：关键类/方法 + 断点入口 + 观察点

### 4.1 断点入口（从“能解释链路”到“看细节”）

断点组合（从入口到闭环）：

### 4.2 观察点（设置断点时应该盯这些变量）

1. **误区：自定义 namespace = 自定义标签语法**
   - 真实本质：读者是在写“把 XML 转成 BeanDefinition 的解析器”。
2. **误区：XSD 一定会去网上下载**
   - Spring 通过 `spring.schemas` 把 URL 映射到 classpath，正常不需要网络。
3. **误区：观察到 `<tx:...>` 就以为是 transaction 模块的“运行时能力”**
   - `<tx:...>` 更多是“定义层注册基础设施 bean”，运行时能力通常由 BPP/代理实现。

## 边界：XML namespace 扩展
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


- namespace URI：是否与在 `spring.handlers` 的 key 一致（注意 `http\://` 的转义）
- element 的 localName：是否命中 handler 里注册的 parser key
- `parserContext.getRegistry()`：最终是否把 beanDefinition 注册进了正确 registry
- `BeanDefinition` 内容：
  - beanClassName / constructorArgs / propertyValues 是否符合预期
- XSD 解析：
  - `spring.schemas` 是否命中（避免网络访问）

### 误判点：现象要落回方法和变量

1. **误区：namespace 扩展是“XML 语法糖”**
   - 本质上在扩展的是“定义层输入”：把 element 解析成 BeanDefinition 并注册进 registry。
2. **误区：XSD/handler 的加载失败只会影响这一小块配置**
   - 实际上它会导致整个 XML 文档解析失败，使流程停留在定义阶段，应用甚至不会进入创建业务 bean 的阶段。
3. **误区：排障只看 XML 文本**
   - 更快的方式：从 `NamespaceHandlerResolver` 与 `BeanDefinitionParser` 设置断点，直接看“有没有命中 handler/parser、最终注册了什么定义”。

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

## 验收口径：XML namespace 扩展
- 需要解释清楚：XML namespace 扩展发生在定义阶段还是创建阶段吗？输出产物是什么？
- 需要说出：`spring.handlers` 与 `spring.schemas` 分别负责解决什么问题吗？（提示：handler 映射 vs XSD 映射）
- 遇到 `<xxx:...>` 不生效时，最短的断点链路应该从哪里进？

## 小结：XML namespace 扩展


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansXmlNamespaceExtensionLabTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlNamespaceExtensionLabTest.java`

<!-- BOOKIFY:END -->
