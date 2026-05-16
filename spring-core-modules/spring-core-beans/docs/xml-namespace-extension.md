# XML namespace 扩展：自定义标签如何变成 BeanDefinition

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 Spring XML 自定义 namespace 的扩展链路。
    - 重点是 `spring.handlers`、`spring.schemas`、`NamespaceHandler`、`BeanDefinitionParser`、XSD，以及自定义标签到 `BeanDefinition` 的转换。
    - 读完后应该知道自定义 XML 标签不是“额外容器语法”，而只是定义输入的扩展。

    观察对象：namespace 解析、schema 映射和自定义标签注册。
    主线位置：XML 解析阶段。
    对照入口：`SpringCoreBeansXmlNamespaceExtensionLabTest`。
<!-- CHAPTER-CARD:END -->

Spring XML 的 namespace 扩展机制，本质上是在 XML 解析层插了一组映射和解析器。它不改变容器模型，只改变“这个标签怎么变成 `BeanDefinition`”。

## 三个映射文件

自定义 namespace 能工作，通常离不开这三部分：

1. `META-INF/spring.handlers`
2. `META-INF/spring.schemas`
3. 自定义的 `NamespaceHandler` / `BeanDefinitionParser`

其中：

- `spring.handlers` 负责把 namespace URI 映射到 handler 类。
- `spring.schemas` 负责把 schema 地址映射到本地 XSD 资源。
- handler 决定某个元素应该由哪个 parser 处理。

`SpringCoreBeansXmlNamespaceExtensionLabTest` 里，`DefaultNamespaceHandlerResolver` 能解析出 `DemoNamespaceHandler`，说明 `spring.handlers` 的映射已经生效。

## 从标签到定义的路径

一条典型路径是：

```text
XML element
-> namespace URI
-> spring.handlers 找到 NamespaceHandler
-> NamespaceHandler.init() 注册 BeanDefinitionParser
-> parser 读取元素属性
-> 生成 BeanDefinition
-> 注册进 BeanFactory
-> 容器后续正常创建 bean
```

这条路径里最重要的点是：最后还是回到 `BeanDefinition`。自定义标签只是帮助你把定义写得更像领域语言。

## XSD 的角色

`spring.schemas` 和 XSD 的作用，主要是让 XML 校验和开发工具知道这套标签长什么样。它让 IDE 能补全，让解析器能校验，也让 namespace 不至于依赖外网 schema。

但 XSD 本身不是运行时逻辑。真正把标签转成定义的，还是 `NamespaceHandler` 和 `BeanDefinitionParser`。

## 失败通常发生在哪

自定义 namespace 的常见失败点有：

- namespace URI 没有映射到 handler。
- handler 类没法加载。
- parser 没有在 `init()` 中注册。
- schema 没有正确映射。
- XML 里元素属性和 parser 预期不一致。

这些问题大多表现为“标签没被识别”或“BeanDefinition 没注册进去”，而不是对象实例化错误。

## 本模块的观察入口

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansXmlNamespaceExtensionLabTest test
```

- `SpringCoreBeansXmlNamespaceExtensionLabTest`：验证 `spring.handlers` 能解析出 handler，且自定义标签最终能注册成 `DemoMessage` 的 `BeanDefinition`。

