# 37. 泛型匹配与注入坑：ResolvableType 与代理导致的类型信息丢失

## 导读

- 本章主题：**37. 泛型匹配与注入坑：ResolvableType 与代理导致的类型信息丢失**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`

## 机制主线

- （本章主线内容暂以契约骨架兜底；建议结合源码与测试用例补齐主线解释。）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

这一章解决一个“看起来很合理，但在真实项目里经常踩坑”的问题：

> 我明明在容器里有一个 `Handler<String>`，  
> 为什么按 `Handler<String>`（带泛型）去找/注入时，Spring 却说“没有候选”？  
> 但按原始类型 `Handler`（不带泛型）又能找到？

结论先行：

- Spring 在“按泛型匹配”时依赖 `ResolvableType`
- **如果候选 bean 在运行时丢失了泛型信息（常见原因：JDK dynamic proxy、手工注册 singleton 实例等）**，那么：
  - 按原始类型能匹配（`Handler`）
  - 按带泛型的 ResolvableType 可能匹配不到（`Handler<String>`）

---

## 1. 为什么 Spring 要关心泛型？

在大型系统中，“同一个原始类型”的实现可能很多；泛型经常被用作“更精确的语义”：

- `Handler<String>` vs `Handler<Long>`
- `Repository<User>` vs `Repository<Order>`

因此 Spring 在一部分场景中会尝试用 `ResolvableType` 做更精细的候选筛选。

---

## 2. 最小可复现：泛型信息一旦丢失，按 ResolvableType 就会失配

### 2.1 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`
- 推荐运行命令：
  - 类级：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansGenericTypeMatchingPitfallsLabTest test`
  - 方法级（更快）：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansGenericTypeMatchingPitfallsLabTest#genericTypeMatching_canFailWhenTypeInfoIsLost test`
- 你将断言/观察到：
  - 容器“看起来”注册了一个 `T` 的实现，但在按 `ResolvableType` 匹配时可能失配（尤其是代理/手工注册实例时）
  - 结论不是“泛型不能用”，而是“泛型匹配依赖可被保留的类型信息”，要知道何时会丢

对应实验（可运行 + 可断言）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`

建议直接跑：

```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansGenericTypeMatchingPitfallsLabTest test
```

这个 Lab 做了两件事（对照非常强）：

1) **用 class metadata 注册**（`RootBeanDefinition(StringHandler.class)`）  
   - BeanFactory 能通过 `StringHandler implements Handler<String>` 推断出泛型信息  
   - 因此 `getBeanNamesForType(Handler<String>)` 能找到它
2) **用运行时 JDK proxy 实例注册 singleton**  
   - 代理实现的是原始接口 `Handler`，运行时很难保留 `Handler<String>` 的泛型参数  
   - 因此按 `Handler` 能找到，但按 `Handler<String>` 会失配

你应该把这个现象记成一句话：

> **“泛型匹配依赖类型信息；一旦候选从‘类定义’退化为‘运行时代理实例’，泛型信息可能就不再可靠。”**

---

## 3. 真实项目里你会在哪些地方遇到它？

常见触发点：

1) **JDK 动态代理（接口代理）**  
   - 代理类只实现接口，泛型参数在运行时往往不可见/不可推断
2) **手工注册 singleton 实例**（`registerSingleton`）  
   - 容器拿到的是一个对象，不一定能反推出“它原本的泛型语义”
3) **FactoryBean 的类型预测**  
   - `FactoryBean#getObjectType()` 返回不准确/为 `null` 时，按类型发现会出现边界问题（见 [23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义](23-factorybean-deep-dive.md) 与 [29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效](29-factorybean-edge-cases.md)）

---

## 4. 怎么避免/修复（工程建议）

1) 如果你依赖“按泛型精确匹配”，尽量让候选以 **BeanDefinition + class metadata** 的形式进入容器  
   - 让容器在定义层就能看到目标类，从而更可靠地计算 ResolvableType
2) 对运行时代理场景，不要过度依赖“按泛型查找”  
   - 更稳妥的方式通常是：原始类型 + `@Qualifier`（或显式命名）来表达意图
3) 如果你必须让“运行时对象”参与泛型匹配  
   - 需要你提供额外的类型信息来源（例如通过工厂/提供者模式显式声明 object type），否则结果常常不可控

---

## 5. Debug / 断点建议（把“泛型匹配失效”定位到具体分支）

### 5.1 推荐断点（候选收敛与泛型匹配的关键点）

- 依赖解析主线（你需要看到候选如何收敛）：
  - `DefaultListableBeanFactory#doResolveDependency`
  - `DefaultListableBeanFactory#findAutowireCandidates`
- 泛型匹配关键点（你需要看到 ResolvableType 如何参与匹配）：
  - `GenericTypeAwareAutowireCandidateResolver#checkGenericTypeMatch`
  - `ResolvableType#forClass`

### 5.2 条件断点模板（降低噪声）

在 `DefaultListableBeanFactory#doResolveDependency` 上建议使用条件断点：

- 按依赖类型过滤（示例：你的泛型接口/父类）：
  - `descriptor.getDependencyType().getName().contains(\"YourService\")`
- 按 beanName 过滤（当你已知目标候选名称时）：
  - `\"yourBeanName\".equals(beanName)`

### 5.3 推荐观察点（watch list）

- `descriptor`：依赖描述（包含注入点、required/optional 等信息）
- `descriptor.resolvableType`：Spring 看到的“带泛型的目标类型”
- `candidateName` / `candidate`：候选集合与当前候选
- `beanFactory.getType(candidateName)`：容器推断的候选类型（可能因为代理/FactoryBean 变得不可靠）

## 源码锚点（建议从这里下断点）

- `ResolvableType`：泛型类型描述的核心抽象
- `DefaultListableBeanFactory#getBeanNamesForType(ResolvableType ...)`：按 ResolvableType 查找候选的入口之一

---

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`

上一章：[36. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界](36-type-conversion-and-beanwrapper.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线](38-environment-and-propertysource.md)

<!-- BOOKIFY:END -->
