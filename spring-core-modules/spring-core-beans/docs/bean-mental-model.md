# Bean 对象模型：一个名字背后的多层对象

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 Spring Bean 的对象模型，而不是注解写法。
    - 重点区分 Java class、BeanDefinition、merged RootBeanDefinition、原始实例、early reference、最终暴露对象、代理对象和 FactoryBean 产品。
    - 读完后应能判断 `getBean()` 返回的到底是哪一层对象。

    观察对象：Bean 名称背后的定义、实例、代理和产品对象。
    主线位置：注册定义之后、单 Bean 创建和最终暴露之前。
    对照入口：`SpringCoreBeansContainerLabTest`、`SpringCoreBeansBeanGraphDebugLabTest`、`SpringCoreBeansLifecycleRawVsProxyLabTest`、`SpringCoreBeansFactoryBeanDeepDiveLabTest`。
<!-- CHAPTER-CARD:END -->

Spring Bean 不是 Java 类的同义词。Java 类只是容器可能用来创建对象的输入之一；Spring 真正管理的是一个名字下的一组语义：如何定义、何时创建、怎样注入、是否懒加载、是否参与候选选择、是否被代理、以什么 scope 复用、什么时候销毁。

因此同一个 bean name 在不同阶段可能对应不同对象或元数据。排障时如果把它们都叫“Bean”，很容易误判：类存在不代表有 BeanDefinition；有 BeanDefinition 不代表已经创建实例；构造器返回的原始对象也不一定是最终从 `getBean()` 取到的对象。

## 八个层次

| 层次 | 它是什么 | 典型观察点 |
| --- | --- | --- |
| Java class | 字节码类型，例如 `ExampleBean.class` | classpath、扫描候选、构造器和方法签名 |
| BeanDefinition | 注册表里的定义记录 | bean class、factory method、scope、lazy、role、source |
| merged `RootBeanDefinition` | 创建阶段使用的合并后运行时定义 | 父子定义、默认值、工厂方法、生命周期方法合并后的结果 |
| raw instance | 构造器、工厂方法或 supplier 刚拿到的原始对象 | 构造器已执行，属性注入和初始化可能尚未完成 |
| early reference | singleton 循环依赖中提前暴露的引用 | 可能是原始对象，也可能已被提前代理 |
| exposed object | singleton 缓存或 scope 对外暴露的最终对象 | `getBean()` 通常拿到这一层 |
| proxy object | 后处理器包装出来的代理 | 事务、AOP、缓存、异步等能力通常要求调用经过它 |
| FactoryBean product | `FactoryBean#getObject()` 生产的产品 | `getBean("name")` 默认返回产品，`getBean("&name")` 返回工厂本身 |

`SpringCoreBeansContainerLabTest` 的 `beanDefinitionIsNotTheBeanInstance` 直接固定了第一条边界：`BeanDefinition` 不是业务对象实例。`beanFactoryPostProcessorCanModifyBeanDefinitionBeforeInstantiation` 说明定义在实例化前仍可能被改写；`beanPostProcessorCanModifyBeanInstanceAfterInitialization` 则说明实例创建后还可能被处理。

## 从名字到对象的最短流转

一个普通 singleton 大致会经过下面的对象流转：

```text
bean name
-> BeanDefinition 注册
-> merged RootBeanDefinition
-> raw instance
-> 属性注入和 Aware 回调
-> 初始化回调
-> BeanPostProcessor after-init 包装
-> exposed object
-> getBean(name)
```

这条线故意省略了完整创建算法，因为本文关注的是对象身份。关键点是：注册表保存的是定义，创建阶段使用的是合并定义，生命周期回调先作用在原始实例上，最终暴露对象可能已经不是原始实例。

`SpringCoreBeansLifecycleRawVsProxyLabTest` 展示了这个差异：`@PostConstruct` 运行在 raw bean 上，after-init `BeanPostProcessor` 可以返回 JDK proxy，最终 `getBean(WorkService.class)` 得到的是代理对象。Lab 用 identity hash 证明 `postConstructIdentityHash` 和 `exposedIdentityHash` 不相等。

## 为什么 `getBean()` 结果可能不是构造出来的对象

至少有四类原因会让 `getBean()` 结果与“构造器创建的对象”不同。

第一，实例可能来自工厂方法而不是构造器。`@Bean` 方法、静态工厂方法、实例工厂方法、supplier 都可以成为原始实例来源。此时 BeanDefinition 记录的重点可能是 `factoryBeanName` 和 `factoryMethodName`，而不是直接的 bean class。

第二，`BeanPostProcessor` 可以替换暴露对象。典型 AOP 代理就是在初始化前后窗口介入，返回一个包装了 target 的 proxy。容器后续缓存和注入的通常是这个 exposed object。

第三，循环依赖可能使用 early reference。singleton 属性注入循环依赖中，A 创建出 raw instance 后还没完成初始化，容器可能先暴露一个 ObjectFactory。B 注入 A 时拿到的是 early reference；如果自动代理创建器参与，这个 early reference 可能已经是代理。

第四，名字可能指向 `FactoryBean` 产品。`SpringCoreBeansFactoryBeanDeepDiveLabTest` 证明 `getBean("valueFactory")` 返回 `Value` 产品，`getBean("&valueFactory")` 才返回 `ValueFactoryBean` 本身；`getType("valueFactory")` 也按产品类型参与匹配。

## FactoryBean 的双重身份

FactoryBean 最容易让“Bean 是对象实例”这个直觉失效。容器里有一个名为 `valueFactory` 的 BeanDefinition，它创建的是 `ValueFactoryBean` 工厂；但普通调用方通过 `getBean("valueFactory")` 取到的是 `getObject()` 生产的 `Value`。只有带 `&` 前缀时，名字才被解释为工厂本身。

产品是否缓存由 `FactoryBean#isSingleton()` 决定，不等同于工厂 Bean 自身的 scope。`SpringCoreBeansFactoryBeanDeepDiveLabTest` 中，工厂本身默认仍是 singleton；当 `isSingleton()` 为 false 时，每次取产品都可能得到新对象。

## 依赖图看的是最终选择

自动装配不是只看 class。`SpringCoreBeansBeanGraphDebugLabTest` 展示了候选先按类型发现，再经过 `@Primary` 等规则收敛为最终注入对象。容器记录的 dependency edge 是最终选择后的边，而不是所有候选。

所以排查“为什么注入了它”时，要同时观察候选列表、BeanDefinition 元数据和最终依赖边。只看某个类是否存在，无法解释 primary、qualifier、代理、FactoryBean 产品类型这些容器语义。

## 排障判断

遇到“拿到的对象不对”时，先问五个问题：

- 注册表里是否真的有对应 BeanDefinition，还是只有 class 在 classpath 上？
- 这个 BeanDefinition 是直接 class、工厂方法，还是 FactoryBean？
- 生命周期回调里看到的是 raw instance，还是最终 exposed object？
- 是否有 BeanPostProcessor 或自动代理创建器替换了对象？
- 如果按名称获取，是否误把 FactoryBean 产品和 `&` 工厂本身混在一起？

这些问题能把“Bean 是什么”拆回容器实际管理的层次，而不是停留在注解或类名层面。
