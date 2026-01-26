# 49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀

## 导读

- 本章主题：**49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBuiltInFactoryBeansLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansServiceLoaderFactoryBeansLabTest.java`

## 机制主线

这一章补齐一个“你不一定会手写，但排障/读源码时必遇到”的知识点：

> **Spring 自带那么多 `*FactoryBean` 到底是干嘛的？`&beanName` 为什么能拿到另一个对象？**

你需要先把一件事讲清楚：

- `FactoryBean` 不是“帮你 new 对象的工具类”，它是一个**容器级机制**：  
  容器把它当作“能生产 product 的 bean”，并且在 `getBean` 时做特殊分派。

本章用 3 类常见的内置 FactoryBean 做闭环：

- `MethodInvokingFactoryBean`：把“调用一个方法”变成一个 bean（product）
- `ServiceLocatorFactoryBean`：把 `BeanFactory#getBean(...)` 包装成一个“服务定位器代理”
- `ServiceLoader*FactoryBean`：把 Java SPI（`ServiceLoader`）的 provider 列表/loader 变成一个 bean（product）

---

入口测试：

- `SpringCoreBeansBuiltInFactoryBeansLabTest#builtInFactoryBeans_methodInvoking_and_serviceLocator_and_factoryDereference`（& 前缀 + product/factory + 缓存语义）
- `SpringCoreBeansServiceLoaderFactoryBeansLabTest#serviceListFactoryBean_loadsProviders_fromMetaInfServices`（SPI providers → List）
- `SpringCoreBeansServiceLoaderFactoryBeansLabTest#serviceLoaderFactoryBean_exposesRawServiceLoader`（SPI loader → ServiceLoader）

1) `getBean("uuidSingleton")` 多次返回同一个 `UUID`（product 被缓存）  
2) `getBean("uuidPrototype")` 多次返回不同 `UUID`（product 不缓存）  
3) `getBean("&uuidPrototype")` 返回的是 `MethodInvokingFactoryBean` 本体  
4) `ServiceLocator` 每次方法调用都会回到容器查找：prototype 每次都是新实例

---

## 1. 是什么：内置 FactoryBean 解决的是什么问题？

在 Spring 里你经常想做两件事：

1) **把“配置/元数据”变成一个对象**（product）  
2) 把“对象的创建逻辑”放到容器可管理的位置（可复用、可缓存、可替换）

这两件事用 `FactoryBean` 都能表达：

- FactoryBean 本体（factory）：一个普通 bean（也有自己的生命周期）
- FactoryBean 的 product：你真正想注入/使用的对象

所以 **FactoryBean 更像是“可插拔的对象生产协议”**，而不是语法糖。

---

## 2. 怎么用：两类最常见的内置 FactoryBean（最小可用心智）

### 2.1 `MethodInvokingFactoryBean`（把“调用方法”变成一个 bean）

你把它当作“把一次方法调用的结果注册为一个 bean”即可：

- 目标可以是 static method（例如 `UUID.randomUUID`）
- 也可以是目标对象的方法（targetObject + targetMethod）
- 关键点：它决定 **product 是否缓存**（`isSingleton`）

这类 FactoryBean 的典型场景：

- 遗留 XML 配置里把某些值/对象拼出来（不想写 Java 配置类）
- 或者你在排障时看见它，需要能判断“这个 bean 到底是值，还是值的工厂”

### 2.2 `ServiceLocatorFactoryBean`（把“按需查找”包装成代理）

它会生成一个实现你接口的代理。接口方法通常长这样：

- `T get(String beanName)`：参数作为 beanName
- 返回值 `T`：作为 getBean 的目标类型

这类机制常见于：

### 2.3 `ServiceLoader*FactoryBean`（把 Java SPI provider 变成 bean）

这组内置 FactoryBean 面向的是 Java 标准的 SPI 机制（`ServiceLoader`）：

- `ServiceLoaderFactoryBean`：product 是 `ServiceLoader<T>`（你自己决定如何迭代/选择）
- `ServiceListFactoryBean`：product 是 `List<T>`（直接给你 provider 列表）
- `ServiceFactoryBean`：product 是单个 `T`（通常用于“只希望有一个 provider”的场景）

它的价值不在于“更好用”，而在于“你在真实项目/源码里可能会碰到它”：

- 你想把“SPI provider 列表”交给 Spring 管理（生命周期/注入）
- 或者你在排障时看到 `ServiceListFactoryBean`，需要能判断“这是 FactoryBean 还是 product”

---

## 3. 原理：把 `&beanName` 与 product 缓存放回容器主线

你只要抓住这条主线，就能解释清楚大多数 “FactoryBean 相关的魔法”：

1) 容器先按 beanName 找到一个实例（可能是普通 bean，也可能是 FactoryBean）
2) 如果它是 FactoryBean：
   - `getBean("x")` 默认返回 **product**
   - `getBean("&x")` 返回 **factory 本体**
3) product 是否缓存，取决于：
   - FactoryBean 的 `isSingleton()`（它声明 product 是否单例）
   - 以及容器对 FactoryBean product 的缓存策略（FactoryBeanRegistry）

换句话说：

> `&` 不是语法糖，它是容器级分支选择；  
> `isSingleton` 不是 bean scope，它描述的是 product 的缓存语义。

---

### 4.1 `&beanName` 分支（你排障最常用的入口）

1) `AbstractBeanFactory#doGetBean`  
2) `AbstractBeanFactory#getObjectForBeanInstance`

观察点：

- `name` / `beanName` / `transformedBeanName`：`&` 前缀是怎么被剥离的
- `isFactoryDereference(name)`：本次到底要 factory 还是 product
- `beanInstance` 的真实类型：是 FactoryBean 还是普通 bean

### 4.2 product 缓存与 FactoryBeanRegistry

- `FactoryBeanRegistrySupport#getObjectFromFactoryBean`

观察点：

- `factory.isSingleton()`：声明 product 是否单例
- `factoryBeanObjectCache`：product 是否命中缓存

### 4.3 `MethodInvokingFactoryBean` 关键入口

- `MethodInvokingFactoryBean#afterPropertiesSet`（准备与首次 invoke）
- `MethodInvokingFactoryBean#getObject`（返回 product，可能每次 invoke）

观察点：

- `this.singleton`：你配置的缓存语义
- `this.cachedObject`（或类似字段）：是否缓存了结果

### 4.4 `ServiceLocatorFactoryBean` 关键入口

- `ServiceLocatorFactoryBean#afterPropertiesSet`（创建代理）
- `ServiceLocatorFactoryBean$ServiceLocatorInvocationHandler#invoke`（每次方法调用都会到这里）

观察点：

- `serviceLocatorInterface`：代理实现的接口
- `beanFactory`：最终回到哪个 BeanFactory 查找
- `args[0]`：是否被当作 beanName

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBuiltInFactoryBeansLabTest.java`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansServiceLoaderFactoryBeansLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBuiltInFactoryBeansLabTest,SpringCoreBeansServiceLoaderFactoryBeansLabTest test
```

你要观察的现象（Lab 里都有断言）：

- 需要“运行时决定拿哪个实现”（按名字/按策略）
- 需要“每次调用都重新拿一个 prototype”（典型：状态型对象、短生命周期对象）

## 4. 怎么实现的：关键类/方法 + 断点入口 + 观察点

推荐断点（按你要回答的问题分组）：

1) **`&beanName` / product vs factory 分支**
   - `AbstractBeanFactory#doGetBean`
   - `AbstractBeanFactory#getObjectForBeanInstance`
   - `BeanFactoryUtils#isFactoryDereference`
2) **FactoryBean product 缓存语义**
   - `FactoryBeanRegistrySupport#getObjectFromFactoryBean`
   - 观察：`factory.isSingleton()` / `factoryBeanObjectCache`
3) **MethodInvokingFactoryBean**
   - `MethodInvokingFactoryBean#afterPropertiesSet`
   - `MethodInvokingFactoryBean#getObject`
4) **ServiceLocatorFactoryBean**
   - `ServiceLocatorFactoryBean#afterPropertiesSet`
   - `ServiceLocatorFactoryBean$ServiceLocatorInvocationHandler#invoke`
5) **ServiceLoader*FactoryBean（SPI）**
   - `ServiceListFactoryBean#getObject` / `ServiceLoaderFactoryBean#getObject`（视具体类型略有差异）

1) **误区：`getBean("x")` 就是拿到名为 x 的 bean 本体**
   - 对 FactoryBean 来说，`getBean("x")` 默认拿到的是 product，不是 factory。
2) **误区：`MethodInvokingFactoryBean` 用来“生成随机值/时间戳”**
   - 默认 `singleton=true`，结果会被缓存；你以为每次都会变，其实不会。
3) **误区：ServiceLocator 只是“语法糖”**
   - 它改变了依赖关系表达方式：从注入时确定 → 运行时决定；排障更难，慎用。

## 常见坑与边界

但是注意：这是一种 **service locator 模式**，会把依赖关系从“注入点”挪到“调用点”，可读性更差，能不用就不用。

### 常见边界与误区（你为什么会在真实项目里踩）

- **FactoryBean 双重身份永远要先确认**：你拿到的是 product 还是 factory？（`&` 前缀）
- **默认 singleton 缓存会“冻结结果”**：例如 MethodInvokingFactoryBean，你以为每次调用都变，其实是同一个 product 被缓存。
- **ServiceLocator 的排障成本更高**：它把依赖关系从“注入时”推迟到“调用时”，定位问题必须回到调用点追踪。

## 一句话自检

- 你能解释清楚：FactoryBean 的 product/factory 分流规则吗？什么时候必须用 `&name`？
- 你能说出：MethodInvoking/ServiceLocator/ServiceLoader 这几类 FactoryBean 各自把“依赖关系”放在了哪里吗？
- 你遇到“拿到的对象类型不对/每次返回都一样/调用时才失败”时，第一反应会去哪个章节/哪个断点入口？

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBuiltInFactoryBeansLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansServiceLoaderFactoryBeansLabTest.java`

上一章：[48. 方法注入：replaced-method / MethodReplacer（实例化策略分支）](48-method-injection-replaced-method.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象](50-property-editor-and-value-resolution.md)

<!-- BOOKIFY:END -->
