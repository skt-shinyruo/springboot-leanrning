# 41. RuntimeHints 入门：把构建期契约跑通

## 导读

- 本章主题：**RuntimeHints 入门：把构建期契约跑通**
- 目标只有一个：把 RuntimeHints 从“听说过”变成“能断言证明”。
  无需先会构建 native image，也能理解 RuntimeHints：因为它本质上是**可测试的契约数据结构**。

!!! summary "本章要点"

    - RuntimeHints 解决的是 “JVM 能跑 ≠ Native 能跑” 的核心矛盾：native image 默认对反射/动态代理/资源访问等能力是**受限**的。
    - RuntimeHints 的正确姿势是：**用 Registrar 注册 + 用单测断言**，把“需要哪些能力”变成可回归的构建期契约。
    - 需要记住的关键接口只有一个：`RuntimeHintsRegistrar#registerHints(RuntimeHints hints, ClassLoader classLoader)`。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAotRuntimeHintsLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java`

## 机制主线：把“运行期能力需求”前置成“构建期契约”

在 JVM 上写代码时，很多能力是“默认可用”的：

- 反射：`Class#getDeclaredMethods` / `Constructor#newInstance`
- 动态代理：JDK Proxy / CGLIB（JVM 里是运行期生成字节码）
- 资源读取：`ClassPathResource`、`ClassLoader#getResource`

但 native image 的世界里，这些能力往往需要“显式声明”。RuntimeHints 就是这个声明机制的统一入口：

> **我需要对哪些类型做反射？我需要哪些动态代理？我需要哪些 classpath 资源？**
> 这些信息必须在构建期提前收集并固化，才能让 native image 在运行期具备等价能力。

### 机制讲透：条件 → 分支 → 结果

**条件**：运行期需要反射/代理/资源访问  
**分支**：是否通过 `RuntimeHintsRegistrar` 注册能力  
**结果**：未注册 → native 运行期失败；已注册 → 可被单测断言保证  
**断点建议**：`RuntimeHintsRegistrar#registerHints`

## 1. RuntimeHints 是什么？（需要记住的最小集合）

RuntimeHints 不是“配置文件”，它更像是一棵“契约对象树”。最常见的几类：

- 反射 hints：哪些类型/成员允许反射访问（构造器/方法/字段）
- 资源 hints：哪些资源需要打进镜像（路径/通配符）
- 代理 hints：哪些接口需要创建 JDK 动态代理
- 序列化/JNI/其他：视具体框架与场景而定

无需背 API 全家桶，排障时只要能把“报错现象”映射到“该补哪类 hints”即可（见第 5 节决策表）。

## 2. 方法级入口：RuntimeHints 是怎么被注册/收集的？

### 2.1 最小入口：实现 `RuntimeHintsRegistrar`

读者写一个 registrar：

- 实现 `RuntimeHintsRegistrar#registerHints(...)`
- 在其中向 `RuntimeHints` 写入需要的契约

这一层的意义是：**可以在 JVM 单测里直接 new 一个 `RuntimeHints`，调用 registrar，再断言 hints 内容**。

### 2.2 AOT 收集视角（读者只需要知道“它会被收集”）

在 Spring Boot 3 / Spring Framework 6 的 AOT 流程里：

- AOT 引擎会扫描并执行 registrar，把 hints 汇总到一个 `RuntimeHints` 实例中
- 然后把这些 hints 转换为 native image 的配置输出（具体输出形态与工具链有关）

> 本仓库的目标是把“契约写对/断言对”训练扎实；native image 的完整打包链路见上一章与后续章节。

## 3. 最小实践：用单测把契约“钉死”

推荐读者形成固定套路（对应本章 Lab）：

1) 写 registrar：只做一件事 —— 注册需要的 hints
2) 写测试：断言 `RuntimeHints` 里确实包含了那条契约
3) 未来每次 refactor：只要测试还绿，就能证明 native image 相关契约没被破坏

这种方式比“到处贴 JSON 配置/靠打包失败再补”更工程化，也更适合团队内训与面试讲解。

## 4. Debug / 断点建议：怎么把它从“黑箱”变成“可观察”？

若只做 JVM 单测（推荐先做这个），断点收益最高的点通常是：

1) 相应的 `RuntimeHintsRegistrar#registerHints`：看读者到底注册了什么
2) `RuntimeHints` 的具体写入点（reflection/resources/proxies 子对象的 register 方法）

若要追 AOT 收集链路：

- 先把“能断言的契约”写出来，再去追“谁调用了我的 registrar”
- 否则读者很容易在 AOT 的大量框架代码里迷路（而且不同版本差异大）

## 5. 排障决策表（Native 报错 → 该补哪类 hints）

| 现象（native 运行期） | 最可能缺失 | 需要补的 hints 类型 | 排查/修复路径 |
| --- | --- | --- | --- |
| 反射创建失败（构造器/方法不可访问、反射调用报错） | 类型/成员未声明可反射 | Reflection hints | 在 registrar 注册该类型的反射访问；用 JVM 单测断言 |
| JDK 动态代理失败（接口代理不可用） | 代理接口未声明 | Proxy hints | 注册需要代理的接口集合；确认代理创建点对应的接口列表 |
| 资源读取不到（classpath 下文件/模板缺失） | 资源未被打包进镜像 | Resource hints | 注册资源路径/模式；用测试断言资源模式存在 |
| 序列化相关异常 | 序列化元数据缺失 | Serialization hints | 仅在确实需要时注册；尽量减少可序列化类型集合 |

> 提醒：不要“全量放开反射”。RuntimeHints 的价值之一就是把能力暴露面最小化（安全/体积/可维护性）。

## 6. 面试常问（标准答案 + 方法级证据链）

### Q1：RuntimeHints 是什么？为什么需要它？

- 标准答案：RuntimeHints 是 native image 的构建期契约，用于声明运行期需要的反射/代理/资源等能力；否则 native 环境下这些能力默认受限，JVM 能跑不代表 native 能跑。
- 方法级证据链：通过 `RuntimeHintsRegistrar#registerHints(RuntimeHints, ClassLoader)` 把契约写入 `RuntimeHints`；本章 Lab 用单测断言契约存在。

### Q2：为什么推荐用 Registrar + 单测，而不是直接写 JSON 配置？

- 标准答案：Registrar 可复用、可组合、可随代码 refactor；单测能回归验证契约不丢失；JSON 容易漂移且缺乏方法级证据链。
- 方法级证据链：测试直接构造 `RuntimeHints` 并调用 registrar，再断言 hints 内容。

### Q3：排 native 报错时，第一步怎么做？

- 标准答案：先把报错归类为“反射/代理/资源/序列化”之一，再补对应 hints；不要上来就全量放开反射。
- 方法级证据链：看报错触发点（反射/代理/资源读取）→ 定位缺失类别 → 回到 registrar 增量注册并用单测锁定。

## 自检要点
RuntimeHints = **构建期契约对象**；通过 `RuntimeHintsRegistrar#registerHints` 注册；用 JVM 单测断言契约，避免 native 打包阶段才“撞墙”。

## 小结与下一章

- 本章完成后：应能够把 RuntimeHints 当成“可测试的契约”来写，而不是当成“黑箱配置”来补。
- 下一章起，将把“定义层输入”的真实世界补齐：XML → BeanDefinitionReader → BeanDefinition（以及失败时的异常分型）。

### 对应 Lab/Test

- Lab：`SpringCoreBeansAotRuntimeHintsLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java`

上一章：[40. AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”](024-40-aot-and-native-overview.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[42. XML → BeanDefinitionReader：定义层解析与错误分型](42-xml-bean-definition-reader.md)
