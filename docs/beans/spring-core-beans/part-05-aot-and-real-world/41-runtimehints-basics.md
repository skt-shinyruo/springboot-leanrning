# 41. RuntimeHints 入门：把构建期契约跑通

## 导读

- 本章主题：**41. RuntimeHints 入门：把构建期契约跑通**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAotRuntimeHintsLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java`

## 机制主线

这一章的目标只有一个：把 RuntimeHints 从“听说过”变成“能断言证明”。

> **你不需要先会构建 native image，也能理解 RuntimeHints。**  
> 因为 RuntimeHints 本质上是“契约数据结构”：你能在 JVM 测试里构造它、注册它、断言它。

---

## 1. RuntimeHints 是什么？

你可以把 RuntimeHints 理解为：

**Spring 在 AOT/Native 场景里“提前声明你需要哪些运行期能力”的契约数据结构。**

Spring 的 hints 典型包括（先记住分类，不需要背 API）：

- Reflection hints：哪些类/构造器/方法/字段需要可反射访问
- Proxy hints：哪些接口/类需要生成代理
- Resource hints：哪些 classpath 资源需要被打包
- （常见延伸）Serialization hints / JNI hints 等：取决于你启用的子系统与运行时需求

---

## 2. 最小实践：RuntimeHintsRegistrar

学习阶段最推荐的入口是：

- `RuntimeHintsRegistrar`

它表达了一个非常工程化、可审计的契约：

> “我负责把某个模块/某个能力运行所需的 hints 注册进去。”

你不需要先会 native image，也能用 JVM 单测把这个契约跑通：

---

- **没有注册 hints**：断言 hints 不命中
- **注册了 hints**：断言 hints 命中

入口测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java`
  - `runtimeHints_areEmptyByDefault_untilExplicitlyRegistered()`（对照：默认不命中）
  - `runtimeHintsRegistrar_canRegisterReflectionHints_forAType()`（注册后命中）

- `RuntimeHintsRegistrar#registerHints`：观察你往 `RuntimeHints` 里写了什么

---

你应该能回答：

- 为什么 RuntimeHints 是 AOT/Native 的核心契约？
- 你如何用一个 JVM 单测证明 hints “有/没有”（并能定位是“没注册”还是“注册粒度不对”）？
- 当 hints “注册了但仍不生效”时，你会先看哪一个入口方法（提示：`RuntimeHintsRegistrar#registerHints`）

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansAotRuntimeHintsLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

- “在 AOT/Native 下仍然需要的运行期能力清单”
- 这些能力必须在构建期声明出来（否则 native image 里可能缺少元信息）

> “我负责把某个模块/某个能力运行所需的 hints 注册进去。”

## 3. 复现入口（可运行）

本模块提供了最小对照实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansAotRuntimeHintsLabTest test
```

## 4. Debug / 断点建议

建议先把断点打在你最容易控制的地方：

当你需要更“体系化”的视角（例如：Spring Boot 的 AOT 流程如何收集多个 hints 来源），再把断点扩展到 AOT 生成器与处理器链。

1) **误区：RuntimeHints 是运行时才生效**
   - 更准确：RuntimeHints 是构建期用于生成/裁剪元数据的输入，运行时只是享受结果。
2) **误区：只要有反射就一定需要 hints**
   - JVM 模式下不一定；AOT/Native 下通常必须显式声明（否则运行期信息不可得）。

下一章开始，我们把“真实世界里容易被忽略的机制”补齐成可运行实验：

## 常见坑与边界

## 5. 常见误区（面试/排障高频）

1) **误区：RuntimeHints 是“运行时开关/日志开关”**
   - 更准确：它是 **构建期契约输入**，决定 native image 里哪些元信息会被保留/生成。
2) **误区：hints 失效只能靠猜**
   - 本模块的 `SpringCoreBeansAotRuntimeHintsLabTest` 已经把“默认不命中 → 注册后命中”做成对照断言。
   - 排查顺序建议：先确认 `RuntimeHintsRegistrar#registerHints` 是否执行 → 再确认注册的 `TypeReference/MemberCategory` 是否覆盖你的真实访问方式。
3) **误区：只要注册一个 Type 就够**
   - 真正关键是“访问粒度”：构造器/方法/字段的 MemberCategory 是否足够。
   - 例如你只注册了“public constructors”，但运行期实际上需要反射调用方法/访问字段时，仍然会失败。

## 小结与下一章

## 6. 小结

你已经跑通了最小闭环（学习 AOT/Native 的正确起点）：

- RuntimeHints 默认为空（不注册就不命中）
- 通过 RuntimeHintsRegistrar 注册后，hints 可被 JVM 单测断言验证

下一章我们切到“定义层输入”的真实世界：XML → BeanDefinitionReader → BeanDefinition（以及失败时的异常分型）。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAotRuntimeHintsLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java`

上一章：[40. AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”](024-40-aot-and-native-overview.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[42. XML → BeanDefinitionReader：定义层解析与错误分型](42-xml-bean-definition-reader.md)

<!-- BOOKIFY:END -->
