# 48. 方法注入（Method Injection）：replaced-method / MethodReplacer

## 导读

- 本章主题：**48. 方法注入（Method Injection）：replaced-method / MethodReplacer**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansReplacedMethodLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansReplacedMethodLabTest.java`

## 机制主线

这一章解决一个“你可能没在新项目里写过，但在读源码/排障时经常看到”的问题：

> **Spring beans 里的 replaced-method 是什么？它跟 AOP 有什么关系？它是怎么做到“改写方法实现”的？**

先给结论：

- `replaced-method` 属于 Spring beans 的 **method injection** 能力
- 它发生在 **实例化阶段**，通过 **CGLIB 子类化** 生成一个“可替换方法的子类”
- 它不是 AOP（不是 advisor/interceptor 那套调用链），但同样依赖代理/字节码增强思想

---

入口测试：

对应 XML：

---

- 测试：`SpringCoreBeansReplacedMethodLabTest#replacedMethod_overridesTargetMethodViaCglibSubclassing_andIsVisibleInBeanDefinitionMethodOverrides`
- XML：`spring-core-modules/spring-core-beans/src/test/resources/part05_aot_and_real_world/xml/replaced-method.xml`

## 1. 是什么：它解决什么问题？不解决什么问题？

它解决的问题：

- 让“某个方法的实现”变成 **配置驱动/可替换**（而不是写死在 class 里）
- 让容器在实例化阶段生成一个“方法可被替换”的对象（适用于历史配置/框架扩展点）

它不解决的问题：

- 它不是 AOP：不提供 pointcut/advice 的条件匹配与链式拦截
- 它不是依赖注入：不是“选哪个 bean 注入”
- 它更像是：**容器在创建对象时，生成一个“方法可被替换”的子类**

---

## 2. 怎么用：最小可用写法（XML）

- 一个目标 bean（包含要被替换的方法）
- 一个 `MethodReplacer`（实现 `reimplement(...)`）
- 在 `<bean>` 内声明 `<replaced-method name="..." replacer="..."/>`

- `service.greet("Alice")` 返回的是 replacer 的结果，而不是原始实现
- 目标对象的 class 变成了 CGLIB enhanced class（可用 `Enhancer.isEnhanced` 观察）

---

## 3. 原理：把现象放回容器主线（它发生在哪个阶段？）

把它放回 beans 主线，你会更容易理解：

1) XML 被解析为 BeanDefinition（定义层）
2) BeanDefinition 内部会携带一个 `MethodOverrides`（记录 lookup/replaced 的方法覆盖信息）
3) 当 BeanFactory 创建实例时：
   - 如果发现 definition 存在 method overrides
   - 就会走到 **InstantiationStrategy 的 method injection 分支**
4) Spring 使用 CGLIB 生成子类，并在目标方法处委托给 `MethodReplacer`

所以它的本质是：**定义层的“方法覆盖元数据”影响了实例化策略**。

---

### 4.1 关键对象（你至少要认识名字）

- `AbstractBeanDefinition#getMethodOverrides`：定义层里存放 method override 元数据
- `MethodReplacer`：替换实现（你写的逻辑）
- `InstantiationStrategy`：
  - 当存在 method overrides 时，Spring 会切到 method injection 的 instantiate 分支

建议观察点：

- BeanDefinition 的 `methodOverrides` 是否为空（为什么会走到 method injection 分支）
- 生成后的 bean class 是否为 enhanced class（是否真的发生了子类化）
- replacer 是如何被注入/查找到的（它本身也是一个 bean）

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansReplacedMethodLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansReplacedMethodLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansReplacedMethodLabTest test
```

- `spring-core-modules/spring-core-beans/src/test/resources/part05_aot_and_real_world/xml/replaced-method.xml`

- 你想让某个 bean 的某个方法在运行时“由容器决定实现”，而不是固定写死在类里
- 或者你需要一种“配置驱动的可插拔方法实现”（历史上在 XML 配置时代更常见）

最小结构（本仓库已提供可运行版本）：

1) 一个目标 bean（包含要被替换的方法）
2) 一个 `MethodReplacer` bean（提供替换实现）
3) `<replaced-method name="..." replacer="..."/>`（把 method override 元数据写进 BeanDefinition）

你运行 Lab 后应该能断言：

- 目标方法的返回值来自 replacer（而不是原始实现）
- 目标对象的 runtime class 为 enhanced class（证明子类化发生）
- BeanDefinition 的 `methodOverrides` 非空（证明“定义层元数据”驱动了实例化策略分支）

## 4. 怎么实现的：关键类/关键方法/关键分支 + 断点入口

### 4.2 推荐断点（从“为什么会走到这里”到“替换发生点”）

1) `AbstractAutowireCapableBeanFactory#createBeanInstance`：实例化入口（选择 instantiation strategy）
2) `AbstractAutowireCapableBeanFactory#instantiateWithMethodInjection`：method injection 分支入口
3) `CglibSubclassingInstantiationStrategy#instantiateWithMethodInjection`：CGLIB 子类化实现点（“为什么必须是子类”）
4) `MethodReplacer#reimplement`：替换逻辑真正执行点（最终证据）

## 常见坑与边界

## 5. 常见边界与误区

1) **误区：这是 AOP**
   - 不是。它是“实例化策略”层面的子类化替换。
2) **边界：final class / final method**
   - CGLIB 子类化的天然限制：final 类/方法无法被覆盖。
3) **误区：这在现代项目里没意义**
   - 你可能不写，但你要能在排障时识别：某个对象为什么是 enhanced class、为什么方法行为“不像源码那样”。

## 小结与下一章

- `AbstractAutowireCapableBeanFactory#createBeanInstance`（实例化入口）
- `AbstractAutowireCapableBeanFactory#instantiateWithMethodInjection`（method injection 分支）
- `SimpleInstantiationStrategy#instantiateWithMethodInjection`（策略抽象入口）
- `CglibSubclassingInstantiationStrategy#instantiateWithMethodInjection`（CGLIB 子类化实现）
- `MethodReplacer#reimplement`（你的替换逻辑被调用的点）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansReplacedMethodLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansReplacedMethodLabTest.java`

上一章：[47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy](47-beandefinitionreader-other-inputs-properties-groovy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀](49-built-in-factorybeans-gallery.md)

<!-- BOOKIFY:END -->
