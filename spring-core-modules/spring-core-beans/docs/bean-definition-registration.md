    # BeanDefinition 注册：谁把定义放进容器
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：一个 BeanDefinition 是如何被注册进容器的？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanDefinitionRegistrationDiffLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：扫描、配置类解析和普通定义注册如何把 BeanDefinition 放入 registry。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansBeanDefinitionRegistrationDiffLabTest` / `SpringCoreBeansComponentScanLabTest`。
    <!-- CHAPTER-CARD:END -->

## 注册解决的是“容器知道什么”

BeanDefinition 注册发生在创建实例之前，解决的是：容器的定义表里有哪些 bean name，每个名字对应什么元数据。后续 `getBean(...)`、依赖解析、单例预实例化都要先有这份定义视图，才能知道该创建哪个类、调用哪个工厂方法、是否 lazy、来源资源是什么。

在 `DefaultListableBeanFactory` 里，普通定义最终会进入 bean definition map。这个动作的公共口径是 `BeanDefinitionRegistry#registerBeanDefinition`，实际落点通常是 `DefaultListableBeanFactory#registerBeanDefinition`。不同入口都可能调用它，但放进去的 `BeanDefinition` 内容不一样。

## 四种入口放进去的定义不一样

`SpringCoreBeansBeanDefinitionRegistrationDiffLabTest#beanDefinitionMetadata_differsAcrossRegistrationMechanisms()` 把几种入口放在同一个容器里，再用 `BeanDefinitionOriginDumper.dump(...)` 对比输出。关键差异不是“能不能 getBean”，而是定义元数据的形状不同。

| 入口 | Lab 中的 bean name | 放入 registry 的特征 |
| --- | --- | --- |
| component scan | `scanComponent` | `ClassPathBeanDefinitionScanner#doScan` 扫描 stereotype，定义里能看到被扫描类的 `beanClassName`，`factoryMethodName` 为空。 |
| `@Bean` factory method | `beanMethodBean` | `ConfigurationClassPostProcessor#processConfigBeanDefinitions` 解析配置类后注册，定义会带上 `factoryMethodName: beanMethodBean`。 |
| `ImportBeanDefinitionRegistrar` | `registrarBean` | registrar 拿到 `BeanDefinitionRegistry` 后主动注册，Lab 里写入了 `resourceDescription: lab:registrar-definition` 和 `source`。 |
| programmatic `RootBeanDefinition` | `programmaticBean` | 测试直接 new `RootBeanDefinition` 并调用 `context.registerBeanDefinition(...)`，因此 `lazyInit`、`resourceDescription`、`source` 都由调用方决定。 |

`SpringCoreBeansComponentScanLabTest#componentScan_registersStereotypes_andRespectsExplicitBeanName()` 另一个角度证明扫描入口会把 `@Component` / `@Service` 这类 stereotype 注册成容器可见的 bean name；`componentScan_excludeFilters_canPreventRegistrationEvenIfAnnotated()` 则说明被注解标记过的类也可能被过滤器挡在 registry 之外。

`ImportBeanDefinitionRegistrar` 的设计和边界继续看 [import-selector-and-registrar.md](import-selector-and-registrar.md)。手写 `RootBeanDefinition` 和实例注册 API 的区别继续看 [programmatic-registration.md](programmatic-registration.md)。

## registerSingleton 的边界

`registerSingleton` 容易和 BeanDefinition 注册混在一起，但它走的是另一条路：把一个已经存在的 singleton 实例放进 `DefaultSingletonBeanRegistry`，不是向 `DefaultListableBeanFactory` 的定义表登记一条普通 `BeanDefinition`。

Lab 里先执行：

```java
context.getBeanFactory().registerSingleton("singletonBean", new SingletonBean());
```

再用 `BeanDefinitionOriginDumper.dump(beanFactory, "singletonBean")` 观察。断言明确要求输出包含：

```text
- beanDefinition: (none)
- hint:
```

这说明 `singletonBean` 可以作为 singleton 被容器按名字找到，但没有正常的定义元数据可供 `getBeanDefinition(...)`、来源排查或创建流程消费。源码入口对应 `DefaultSingletonBeanRegistry#registerSingleton`。如果你需要让容器负责构造、依赖注入、初始化、销毁方法和候选元数据，应该注册 `BeanDefinition`；如果只是把外部已有对象挂进容器，才考虑 `registerSingleton`。

## refresh 前后的分界线

这里的关键分界线不是“API 是否还能调用”，而是“能不能参与本轮 `refresh()` 的定义加工和实例创建节奏”。

`SpringCoreBeansBeanDefinitionRegistrationDiffLabTest` 在 `refresh()` 之前完成三件事：`context.register(...)` 放入配置类，`context.registerBeanDefinition(...)` 放入手写定义，`registerSingleton(...)` 放入已有实例。随后 `refresh()` 触发配置类解析、scanner、registrar、BeanFactoryPostProcessor、单例预实例化等步骤。

因此，`refresh()` 前注册的普通 `BeanDefinition` 可以参与本轮配置类处理和后处理器修改；`refresh()` 后再追加定义虽然在底层 registry 仍可能成立，但已经错过本轮 `ConfigurationClassPostProcessor` 解析和 singleton 预实例化窗口，需要调用方自己理解触发时机。`registerSingleton` 更特殊：它绕过定义创建主线，无论放在什么时机，都不能补出一份正常 `BeanDefinition`。

## 源码阅读顺序

建议按入口到落点读：

1. `ClassPathBeanDefinitionScanner#doScan`：扫描候选类、生成 bean name、注册扫描定义。
2. `ConfigurationClassPostProcessor#processConfigBeanDefinitions`：解析 `@Configuration`、`@Bean`、`@Import`，把工厂方法和 registrar 结果转成定义。
3. `BeanDefinitionRegistry#registerBeanDefinition`：看所有“定义注册”入口共同依赖的 registry 契约。
4. `DefaultListableBeanFactory#registerBeanDefinition`：看定义表如何保存 bean name、如何处理已有定义、如何维护 names 列表。
5. `DefaultSingletonBeanRegistry#registerSingleton`：单独看实例注册，确认它没有进入 bean definition map。

定义上的 `source`、`resourceDescription`、`factoryMethodName`、`primary` 等字段怎样辅助排查，放到 [bean-definition-metadata-and-origin.md](bean-definition-metadata-and-origin.md)。名字、别名和注册名的定位规则放到 [bean-name-and-alias.md](bean-name-and-alias.md)。

## 用本模块怎么验证

最短命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanDefinitionRegistrationDiffLabTest,SpringCoreBeansComponentScanLabTest test
```

重点看：

- `beanDefinitionMetadata_differsAcrossRegistrationMechanisms()`：同一容器内对比 scan、`@Bean`、registrar、programmatic `RootBeanDefinition` 和 `registerSingleton`。
- `BeanDefinitionOriginDumper.dump(...)`：只读定义元数据，不触发 bean 实例化；如果 `DefaultListableBeanFactory` 里没有这个 name 的定义，就输出 `(none)` 和提示。
- `componentScan_registersStereotypes_andRespectsExplicitBeanName()`：证明扫描入口会把 stereotype 变成 bean name。
- `componentScan_excludeFilters_canPreventRegistrationEvenIfAnnotated()`：证明注册前的过滤会影响容器最终知道什么。

## 相邻主题

- [import-selector-and-registrar.md](import-selector-and-registrar.md)：`@Import`、selector 和 registrar 的注册窗口。
- [programmatic-registration.md](programmatic-registration.md)：编程式定义注册和实例注册 API。
- [bean-definition-metadata-and-origin.md](bean-definition-metadata-and-origin.md)：定义元数据与来源排查。
- [bean-name-and-alias.md](bean-name-and-alias.md)：bean name、alias 和定位规则。
- [appendix-knowledge-map.md](appendix-knowledge-map.md)：回到知识点地图。
