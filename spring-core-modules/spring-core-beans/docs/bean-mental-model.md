    # Bean 心智模型：定义、实例、缓存与最终暴露对象
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：Bean、BeanDefinition、单例缓存、最终暴露对象分别是什么关系？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：定义元数据、运行时实例、singleton 缓存、暴露给调用方的对象之间的边界。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansBeanGraphDebugLabTest`。
    <!-- CHAPTER-CARD:END -->

## 先分清四个对象

读容器源码时，先不要把“Bean”当成一个单一对象。这里至少有四层：

| 对象 | 由谁管理 | 主要回答什么 |
| --- | --- | --- |
| `BeanDefinition` | `DefaultListableBeanFactory` | 这个 bean 应该怎样创建、来自哪里、有哪些 scope / lazy / factory method 等元数据？ |
| 原始实例 | 创建流程中的构造、工厂方法或实例工厂 | 当前已经 new 出来的 Java 对象是什么？ |
| singleton 缓存 | `DefaultSingletonBeanRegistry` | 当前哪些单例运行时对象可以被重复暴露？ |
| 最终暴露对象 | `getBean(...)` 返回给调用方的对象 | 调用者最终拿到的是原始对象、代理对象，还是 `FactoryBean` 的产品？ |

这四层不一定指向同一个对象。`BeanDefinition` 是定义；singleton 缓存是运行时实例的暴露通道；最终暴露对象还可能被 `BeanPostProcessor`、AOP 代理或 `FactoryBean` 语义改变。

## BeanDefinition 不是 Bean

`BeanDefinition` 是放在 `DefaultListableBeanFactory` 里的元数据，不是业务对象本身。它描述 bean class、scope、依赖、工厂方法、来源资源等信息，容器后续根据这些信息创建实例。

`SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance()` 用 `context.getBeanFactory().getBeanDefinition("exampleBean")` 取到定义，再用 `context.getBean(ExampleBean.class)` 取到实例。断言里 `BeanDefinition` 不是 `ExampleBean`，而 `ExampleBean` 实例真实存在。这个 Lab 适合放断点看 `DefaultListableBeanFactory#getBeanDefinition`：它只是从定义表取元数据，不会把定义对象“变成”业务实例。

如果想看定义如何进入容器，跳到 [bean-definition-registration.md](bean-definition-registration.md)。如果想看定义如何被消费并创建对象，跳到 [bean-creation-mainline.md](bean-creation-mainline.md)。

## 单例缓存缓存的是什么

singleton 缓存不登记“有哪些 BeanDefinition”。它服务的是运行时暴露：一个 singleton 创建出来以后，后续同名查找可以复用同一个运行时对象；创建过程中遇到允许解决的循环依赖时，也可能临时暴露 early reference。

这部分在 `DefaultSingletonBeanRegistry` 里，而不是 `DefaultListableBeanFactory` 的定义注册表里。源码阅读时，把这两件事分开：

- `DefaultListableBeanFactory` 保存和查询 `BeanDefinition`。
- `DefaultSingletonBeanRegistry` 保存已经创建或正在创建过程里可暴露的 singleton 对象。

所以 `DefaultSingletonBeanRegistry#getSingleton` 是运行时实例查找入口，不是定义查找入口。它回答“这个名字当前有没有可返回的 singleton 对象”，而不是“这个名字有没有定义”。

## FactoryBean 会让名字和对象分叉

`FactoryBean` 会让同一个 bean name 出现两个观察口径：

- `getBean("sequence")` 返回 `FactoryBean` 生产出来的产品。
- `getBean("&sequence")` 返回 `FactoryBean` 对象本身。

`SpringCoreBeansContainerLabTest#factoryBeanByNameReturnsProductAndAmpersandReturnsFactory()` 里的 `sequence` 是 `SequenceFactoryBean`。Lab 里连续两次 `getBean("sequence", Long.class)` 得到 `1L` 和 `2L`，说明默认名字走的是产品对象；再调用 `getBean("&sequence")`，断言返回的是 `SequenceFactoryBean`，并且直接调用工厂的 `getObject()` 得到 `3L`。

源码上，普通 `getBean` 先进入 `AbstractBeanFactory#doGetBean`，拿到实例后会判断是否需要从 `FactoryBean` 取产品；产品获取和缓存语义继续看 `FactoryBeanRegistrySupport#getObjectFromFactoryBean`。更完整的 `FactoryBean` 语义放在 [factorybean.md](factorybean.md)。

## early reference 不是最终对象的同义词

early reference 是 Spring 为部分 setter / field 循环依赖保留的运行时暴露手段，不是“提前创建好的最终 Bean”的简称。`SpringCoreBeansContainerLabTest#circularDependencyWithSettersMaySucceedViaEarlySingletonExposure()` 里，`SetterA` 和 `SetterB` 通过 setter 互相注入，最终能拿到同一组对象；同一个测试类里的构造器循环依赖则会失败。

这个差异来自创建阶段是否有机会先实例化对象，再填充属性。setter / field 场景可能先暴露 early reference，让另一个 bean 完成注入；构造器场景需要在构造参数阶段就拿到对方，没有这个窗口。

如果 bean 后续会被代理，early reference 还可能和代理提前暴露发生交互：调用方看到的 early reference 可能已经是代理，也可能需要和最终 exposed object 做一致性校验。这里先建立边界，三级缓存、代理早期暴露和失败分支放到 [early-reference-and-three-level-cache.md](early-reference-and-three-level-cache.md)。

## 读源码时看哪些入口

建议按这条线读，不要从缓存类和定义类之间来回跳：

1. `DefaultListableBeanFactory#getBeanDefinition`：先确认定义查询只取元数据。
2. `AbstractBeanFactory#doGetBean`：看 `getBean(...)` 如何从名字、类型、scope、`FactoryBean` 语义进入创建或复用流程。
3. `DefaultSingletonBeanRegistry#getSingleton`：看 singleton 实例什么时候从缓存返回，什么时候进入创建回调，什么时候允许 early reference。
4. `FactoryBeanRegistrySupport#getObjectFromFactoryBean`：看普通名字为什么返回产品，`&` 前缀为什么绕开产品语义。

再往下的构造、属性填充、初始化、后处理器替换对象，放到 [bean-creation-mainline.md](bean-creation-mainline.md) 继续读。

## 用本模块怎么验证

最短命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test
```

重点看三个 Lab：

- `beanDefinitionIsNotTheBeanInstance()`：证明定义对象和运行时实例不是一回事。
- `factoryBeanByNameReturnsProductAndAmpersandReturnsFactory()`：证明普通名字拿产品，`&` 名字拿工厂。
- `circularDependencyWithSettersMaySucceedViaEarlySingletonExposure()`：证明 setter 循环依赖可以通过 early singleton exposure 成功。

需要把对象图打印出来时，再结合 `SpringCoreBeansBeanGraphDebugLabTest` 观察实例之间的引用关系。

## 相邻主题

- [bean-definition-registration.md](bean-definition-registration.md)：定义怎样进入容器。
- [bean-creation-mainline.md](bean-creation-mainline.md)：定义怎样被消费并创建对象。
- [factorybean.md](factorybean.md)：`FactoryBean` 的产品、工厂对象和类型预测语义。
- [early-reference-and-three-level-cache.md](early-reference-and-three-level-cache.md)：early reference、三级缓存和代理一致性。
- [appendix-knowledge-map.md](appendix-knowledge-map.md)：回到知识点地图。
