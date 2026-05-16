# Properties 与 Groovy 读取器：外部定义输入的两种风格

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文比较 `PropertiesBeanDefinitionReader` 和 `GroovyBeanDefinitionReader` 的输入风格。
    - 重点放在表达力、注册时机、资源加载方式和维护成本，而不是语法大全。
    - 读完后应该知道它们和 XML、注解一样，最后都只是把定义落到注册表里。

    观察对象：外部定义输入、资源加载和注册结果。
    主线位置：BeanDefinition 注册阶段。
    对照入口：`SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`、`SpringCoreBeansGroovyBeanDefinitionReaderLabTest`。
<!-- CHAPTER-CARD:END -->

Properties 和 Groovy 都属于“外部定义输入”。它们不是新的容器，不是新的 bean 模型，只是把 `BeanDefinition` 写在了不同的语法里。

两者的共同点很简单：

- 先由 reader 读取资源。
- 再把内容转成 `BeanDefinition`。
- 最后注册进 `BeanDefinitionRegistry`。

差别在于表达能力和维护代价。

## 表达能力

| 形式 | 表达力 | 适合什么 | 代价 |
| --- | --- | --- | --- |
| Properties | 低到中 | 大量机械式定义、键值映射、简单批量注册 | 语义较弱，复杂逻辑不自然 |
| Groovy | 高 | 条件分支、循环、复用、根据运行逻辑拼定义 | 逻辑更隐蔽，阅读门槛更高 |
| XML | 中 | 结构化定义、声明式配置、schema 校验 | 语法偏重，样板较多 |
| 注解/Java Config | 高 | 与代码同居，类型安全更强 | 配置和业务逻辑更容易混在一起 |

这不是“谁更先进”，而是“谁更适合你的定义来源”。

## 注册时机是一样的

`SpringCoreBeansPropertiesBeanDefinitionReaderLabTest` 和 `SpringCoreBeansGroovyBeanDefinitionReaderLabTest` 都说明了同一件事：reader 只是前置输入通道，真正把 bean 放进容器的仍然是注册表。

- Properties reader 先从 `beans.properties` 读出定义，再交给 `DefaultListableBeanFactory`。
- Groovy reader 先执行脚本，再把定义写进 `GenericApplicationContext`。

无论语法多花，落地结果仍然是 `BeanDefinition`。

## 资源加载方式

这两种 reader 都依赖资源路径。区别只是资源内容不同：

- Properties 更像结构化映射表。
- Groovy 更像可以执行的定义脚本。

所以它们都继承了资源加载的优缺点：classpath 读取简单，但逻辑分散在外部文件里；定位问题时要同时看代码和资源内容。

## 维护成本

Properties 的成本低在于规则简单，适合批量生成定义，也适合把“名字到定义”的机械映射收进一个文件里。

Groovy 的成本高在于它太像代码了。它允许你写条件、循环、计算和复用，这很强，但也意味着定义逻辑不再一眼可见。对于长期维护，Groovy 适合需要动态组织定义的场景，不适合把简单配置写得过于聪明。

## 本模块的观察入口

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPropertiesBeanDefinitionReaderLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansGroovyBeanDefinitionReaderLabTest test
```

- `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`：看 Properties 输入如何注册成 bean。
- `SpringCoreBeansGroovyBeanDefinitionReaderLabTest`：看 Groovy 脚本如何注册成 bean。
