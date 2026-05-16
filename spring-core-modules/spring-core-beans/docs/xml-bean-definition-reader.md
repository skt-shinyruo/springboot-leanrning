# XML BeanDefinition 读取：XML 如何落成定义

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 XML 是怎样被读成 `BeanDefinition` 的。
    - 重点是资源、读取器、解析委派、`<bean>` 元素、命名、属性/构造参数、父子定义和注册过程。
    - 读完后应该把 XML 看成一种 BeanDefinition 输入格式，而不是另一套容器模型。

    观察对象：XML 资源、解析器和注册表。
    主线位置：容器刷新前的定义加载阶段。
    对照入口：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`。
<!-- CHAPTER-CARD:END -->

XML 在 Spring 里从来不是“另一种容器”。它只是把定义写成了一个外部资源，然后由 `XmlBeanDefinitionReader` 读进 `BeanDefinitionRegistry`。

所以，XML 读入之后发生的事情和别的定义来源没有本质差异：容器最后看的仍然是 `BeanDefinition`。

## 输入的是资源，不是对象

`XmlBeanDefinitionReader` 先面对的是 `Resource`。这个资源可以来自 classpath、字节数组、文件系统，或者别的实现。读取器把资源解析成文档，再逐层把元素翻译成定义。

`SpringCoreBeansXmlBeanDefinitionReaderLabTest` 用 `ByteArrayResource` 说明了一个关键事实：即使 XML 是内存字符串，只要能被 reader 解析，它就会落成可注册的 `BeanDefinition`。

## 解析链路

概念上，XML 解析链路可以拆成三层：

1. `XmlBeanDefinitionReader` 负责入口和资源加载。
2. `DefaultBeanDefinitionDocumentReader` 负责文档级遍历。
3. `BeanDefinitionParserDelegate` 负责把 `<bean>`、`<property>`、`<constructor-arg>` 等元素翻译成定义内容。

这条链路的结果不是对象实例，而是一份已经结构化的 bean 定义。

## `<bean>` 最终变成什么

一个简单的 `<bean id="message" class="...">` 会变成一条注册记录，里面包含：

- bean 名称。
- bean 类名。
- 构造参数。
- 属性值。
- 资源描述。

在 `SpringCoreBeansXmlBeanDefinitionReaderLabTest` 里，`<constructor-arg value="from-xml"/>` 最终变成了 `TypedStringValue`，说明 XML 读入时并不是直接 new 对象，而是先把字符串参数翻译成定义层值对象。

这也是为什么 `BeanDefinitionStoreException` 能代表“定义读取失败”：它报的是解析或注册阶段的问题，不是业务 bean 的运行时异常。

## 命名是注册的一部分

XML 里的 `id`、`name` 和生成出来的 bean 名，都会进入注册表语义。这个名字决定：

- `getBean()` 用什么 key 查找。
- 别名是否生效。
- 依赖注入里按名称收敛时会不会命中。

XML 不是定义的“展示格式”，而是注册的直接输入，所以命名一旦写错，后续所有按名查询都会一起失效。

## 父子定义仍然是定义层问题

XML 里的父子定义不是“先创建父对象，再包一层子对象”。它们仍然只是 `BeanDefinition` 之间的继承关系。

父定义提供默认值，子定义覆盖局部属性；真正的运行时合并发生在创建阶段，容器会把这些信息合成 merged `RootBeanDefinition` 再进入实例化。

因此，XML 的父子语法本质上是在写定义的继承结构，而不是对象继承结构。

## 什么时候会失败

XML 读入失败常常在这几个地方出现：

- XML 语法本身不合法。
- schema 地址不对。
- namespace 未知。
- 元素无法映射到正确的定义内容。

`SpringCoreBeansXmlBeanDefinitionReaderLabTest` 里，非法 XML 会直接抛出 `BeanDefinitionStoreException`，这说明问题属于“定义输入”层，而不是 bean 实例化层。

## 本模块的观察入口

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansXmlBeanDefinitionReaderLabTest test
```

- `SpringCoreBeansXmlBeanDefinitionReaderLabTest`：看 XML 如何变成 `BeanDefinition`、构造参数如何落到定义里，以及错误如何以 `BeanDefinitionStoreException` 体现。

