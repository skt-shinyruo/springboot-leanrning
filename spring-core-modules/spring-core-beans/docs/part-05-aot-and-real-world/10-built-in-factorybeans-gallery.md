# 10. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀
    - 使用方式：可先运行本章推荐 Lab，把输入层解析或 AOT 契约完成验证；再回到正文用断点把关键分支（reader/hints/值解析）观察到并能解释。
    - 原理：输入层（XML/Properties/Groovy）解析的落点仍是 BeanDefinition；AOT/Native 的关键是把反射/代理/资源等需求变成可测试的构建期契约（RuntimeHints）。
    - 源码入口：`BeanFactory#getBean(...)` / `FactoryBean#isSingleton` / `AbstractBeanFactory#getObjectForBeanInstance`
    - 推荐 Lab：`SpringCoreBeansBuiltInFactoryBeansLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[09. 方法注入（Method Injection）：replaced-method / MethodReplacer](09-method-injection-replaced-method.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[11. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象](11-property-editor-and-value-resolution.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**10. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀**
- 阅读建议：建议先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（AOT，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/aot.html
- 官方文档对照（Spring Boot Reference，适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/


!!! summary "本章要点"

    - 读完本章，应能够用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见误区在哪里”。
    - 速读路径：请先运行一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBuiltInFactoryBeansLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansServiceLoaderFactoryBeansLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansBuiltInFactoryBeansLabTest`，再用 `SpringCoreBeansServiceLoaderFactoryBeansLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`AbstractBeanFactory#getObjectForBeanInstance`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“常见误区与边界”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/03-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/07-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

这一章补齐一个“读者不一定会手写，但排障/读源码时必遇到”的知识点：

> **Spring 自带那么多 `*FactoryBean` 到底是干嘛的？`&beanName` 为什么能获取到另一个对象？**

需要先把一件事讲清楚：

- `FactoryBean` 不是“帮读者 new 对象的工具类”，它是一个**容器级机制**：
  容器把它当作“能生产 product 的 bean”，并且在 `getBean` 时做特殊分派。

本章用 3 类常见的内置 FactoryBean 做闭环：

- `MethodInvokingFactoryBean`：把“调用一个方法”变成一个 bean（product）
- `ServiceLocatorFactoryBean`：把 `BeanFactory#getBean(...)` 包装成一个“服务定位器代理”
- `ServiceLoader*FactoryBean`：把 Java SPI（`ServiceLoader`）的 provider 列表/loader 变成一个 bean（product）

---

### 机制系统阐述：条件 → 分支 → 结果

**条件**：`getBean` 命中的是 FactoryBean
**分支**：默认返回 product；`&` 前缀返回 factory 本体
**结果**：product 的缓存语义取决于 `FactoryBean#isSingleton`
**断点建议**：`AbstractBeanFactory#getObjectForBeanInstance`

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

在 Spring 里读者经常想做两件事：

1) **把“配置/元数据”变成一个对象**（product）
2) 把“对象的创建逻辑”放到容器可管理的位置（可复用、可缓存、可替换）

这两件事用 `FactoryBean` 都能表达：

- FactoryBean 本体（factory）：一个普通 bean（也有自己的生命周期）
- FactoryBean 的 product：读者真正想注入/使用的对象

所以 **FactoryBean 更接近是“可插拔的对象生产协议”**，而不是语法糖。

---

## 2. 使用方式：两类最常见的内置 FactoryBean（最小可用心智）

### 2.1 `MethodInvokingFactoryBean`（把“调用方法”变成一个 bean）

读者把它当作“把一次方法调用的结果注册为一个 bean”即可：

- 目标可以是 static method（例如 `UUID.randomUUID`）
- 也可以是目标对象的方法（targetObject + targetMethod）
- 关键点：它决定 **product 是否缓存**（`isSingleton`）

这类 FactoryBean 的典型场景：

- 遗留 XML 配置里把某些值/对象拼出来（不想写 Java 配置类）
- 或者在排障时观察到它，需要能判断“这个 bean 到底是值，还是值的工厂”

### 2.2 `ServiceLocatorFactoryBean`（把“按需查找”包装成代理）

它会生成一个实现读者接口的代理。接口方法通常长这样：

- `T get(String beanName)`：参数作为 beanName
- 返回值 `T`：作为 getBean 的目标类型

这类机制常见于：

### 2.3 `ServiceLoader*FactoryBean`（把 Java SPI provider 变成 bean）

这组内置 FactoryBean 面向的是 Java 标准的 SPI 机制（`ServiceLoader`）：

- `ServiceLoaderFactoryBean`：product 是 `ServiceLoader<T>`（读者自己决定如何迭代/选择）
- `ServiceListFactoryBean`：product 是 `List<T>`（直接给读者 provider 列表）
- `ServiceFactoryBean`：product 是单个 `T`（通常用于“只希望有一个 provider”的场景）

它的价值不在于“更好用”，而在于“在真实项目/源码里可能会碰到它”：

- 若希望把“SPI provider 列表”交给 Spring 管理（生命周期/注入）
- 或者在排障时看到 `ServiceListFactoryBean`，需要能判断“这是 FactoryBean 还是 product”

---

## 3. 原理：把 `&beanName` 与 product 缓存放回容器主线

读者只要抓住这条主线，就能解释清楚大多数 “FactoryBean 相关的隐式行为”：

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

## 3.1 FactoryBean 与代理/循环依赖的交叉边界

当 FactoryBean 的 product 参与 AOP 或循环依赖时，需要额外注意：

- product 可能被 BPP 替换为 proxy（最终暴露对象不一定是原始 product）
- 如果发生 early reference，**early 形态与最终形态**可能不一致

排障要点：

- 断点 `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`：观察 early 形态
- 断点 `applyBeanPostProcessorsAfterInitialization`：观察最终替换

### 4.1 `&beanName` 分支（读者排障最常用的入口）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


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

- `this.singleton`：读者配置的缓存语义
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

- 本章已在正文中引用以下 LabTest（优先运行它们）：
- Lab：`SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 验证补充（从实验现象出发）

## 0. 复现入口（可运行）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBuiltInFactoryBeansLabTest.java`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansServiceLoaderFactoryBeansLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBuiltInFactoryBeansLabTest,SpringCoreBeansServiceLoaderFactoryBeansLabTest test
```

需要观察的现象（Lab 里都有断言）：

- 需要“运行时决定拿哪个实现”（按名字/按策略）
- 需要“每次调用都重新拿一个 prototype”（典型：状态型对象、短生命周期对象）

## 4. 怎么实现的：关键类/方法 + 断点入口 + 观察点

推荐断点（按需要回答的问题分组）：

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

1) **误区：`getBean("x")` 就是获取到名为 x 的 bean 本体**
   - 对 FactoryBean 来说，`getBean("x")` 默认获取到的是 product，不是 factory。
2) **误区：`MethodInvokingFactoryBean` 用来“生成随机值/时间戳”**
   - 默认 `singleton=true`，结果会被缓存；容易误以为每次都会变化，但实际上不会。
3) **误区：ServiceLocator 只是“语法糖”**
   - 它改变了依赖关系表达方式：从注入时确定 → 运行时决定；排障更难，慎用。

## 常见误区与边界

需要注意：这是一种 **service locator 模式**，会把依赖关系从“注入点”迁移到“调用点”，可读性更差，应谨慎采用。

### 常见边界与误区（读者为什么会在真实项目里遇到）

- **FactoryBean 的双重身份应首先明确**：读者获取到的是 product 还是 factory？（`&` 前缀）
- **默认 singleton 缓存会“冻结结果”**：例如 MethodInvokingFactoryBean，容易误以为每次调用都会变化，但实际上是同一个 product 被缓存。
- **ServiceLocator 的排障成本更高**：它把依赖关系从“注入时”推迟到“调用时”，定位问题必须回到调用点追踪。

## 面试常问（内置 FactoryBean：识别模式比背清单更重要）

### Q1：看到 `XXXFactoryBean`，如何快速判断“容器对外暴露的到底是谁”？

- 标准答案（可复述）：
  - 默认 `getBean("name")` 返回的是 product；`getBean("&name")` 才是 FactoryBean 本体。应先明确“名称语义”，再讨论类型匹配与缓存语义。
- 证据链（方法级）：
  - `AbstractBeanFactory#getObjectForBeanInstance`
  - `FactoryBeanRegistrySupport#getObjectFromFactoryBean`
- 最小复现：
  - `SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest`

### Q2：FactoryBean 最易误用的两个边界是什么？

- 标准答案（可复述）：
  - `getObjectType()` 不准确/返回 null 会影响 type-based 发现与条件装配；`isSingleton()` 决定的是 product 缓存语义，不是工厂本体是否单例。

## 自检要点
- 应能够解释清楚：FactoryBean 的 product/factory 分流规则吗？什么时候必须用 `&name`？
- 应能够说出：MethodInvoking/ServiceLocator/ServiceLoader 这几类 FactoryBean 各自把“依赖关系”放在了哪里吗？
- 遇到“获取到的对象类型不对/每次返回都一样/调用时才失败”时，第一反应会去哪个章节/哪个断点入口？

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBuiltInFactoryBeansLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansServiceLoaderFactoryBeansLabTest.java`

上一章：[48. 方法注入：replaced-method / MethodReplacer（实例化策略分支）](09-method-injection-replaced-method.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象](11-property-editor-and-value-resolution.md)

<!-- BOOKIFY:END -->
