# 第 24 章：40. AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansAotFactoriesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[39. BeanFactory API 深挖：接口族谱与手动 bootstrap 的边界](../part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[41. RuntimeHints 入门：把构建期契约跑通](41-runtimehints-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**40. AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotFactoriesLabTest.java`

## 机制主线

这一章不是教你“如何构建 native image”，而是回答一个更关键的问题：

> **为什么同一套 Spring 应用，在 JVM 模式里能工作，但切到 AOT/Native 就可能失败？**

---

## 1. 结论先行：AOT/Native 改变了什么？

你可以把 JVM 与 Native 的差异理解为：

- **JVM（open-world）**：运行期反射/代理/资源扫描默认可用，许多“动态能力”在运行时临时决定  
- **Native（closed-world）**：运行期能力被收紧，**必须在构建期声明**（否则镜像里没有）

因此在 Spring 世界里，AOT/Native 的核心问题往往不是“业务逻辑”，而是：

- 你有没有把“运行期才知道的需求”前置成 **构建期契约**？
- 这些契约是否被 **Spring AOT 基础设施**发现与汇总？

### 机制讲透：条件 → 分支 → 结果

**条件**：运行期需要反射/代理/资源/序列化  
**分支**：是否在构建期注册 RuntimeHints  
**结果**：未注册 → Native 运行期失败；已注册 → 能在 JVM 单测中被断言验证  
**断点建议**：`RuntimeHintsRegistrar#registerHints`

---

## 2. 关键概念：RuntimeHints（AOT 的“契约”）

在 Spring 体系中，AOT/Native 的“显式声明”通常通过 **RuntimeHints** 来表达：

- “这个类型需要反射访问”
- “这个代理需要生成”
- “这个资源需要打包进镜像”

你暂时不需要记住所有 hints 的分类，只需要建立一个工程直觉：

> **AOT/Native 下失败的很多问题，本质都是 “hints 缺失”。**

你可以把 RuntimeHints 理解成“构建期白名单”：

- **反射**：哪些类/方法/构造器可以被反射访问  
- **代理**：哪些接口/类允许生成代理  
- **资源**：哪些文件/路径需要打包进镜像  
- **序列化**：哪些类型允许序列化/反序列化  

### 2.1 spring-beans 的 AOT 基础设施：`META-INF/spring/aot.factories` 与 `AotServices`

很多初学者在学习 AOT 时会有一个错觉：

> “AOT = 我自己写 RuntimeHintsRegistrar + 配点 hints 就完了”

但在 Spring Framework 里，AOT 不是“一个点”，而是一套 **基础设施**：

- **Spring 会在 classpath 里发布 AOT service 列表**：`META-INF/spring/aot.factories`
- **Spring 会用 `AotServices` 去发现并加载这些服务**
- `spring-beans` 自己就会提供一部分 AOT services（比如 BeanFactory 初始化阶段的 AOT processors）

这一点对“为什么会有这些接口/类”“它们何时参与容器主线”非常关键（这也正是 `org.springframework.beans.factory.aot.*` 这个包存在的原因）。

---

## 3. 你在真实项目里会遇到的典型现象（症状表）

下面这些“看起来像业务 bug”的问题，常见根因其实是 AOT/Native 约束：

- **反射失败**：`NoSuchMethod` / `IllegalAccess` / `ClassNotFound`  
- **代理失败**：AOP/事务拦截失效，代理类不可生成  
- **资源缺失**：配置文件/模板/静态资源在 native 中找不到  
- **运行期扫描失效**：JVM 下能扫描到，Native 下扫描不到  

这一章的目标是让你知道：这些问题都可以被归类到“契约缺失”，并能落到一个具体入口：

- **RuntimeHints**（声明反射/代理/资源等需求）

---

## 3.1 工程化策略：把 native 风险前置为 JVM 单测断言

本模块的 Lab 采用的策略是：**不构建 native image，也能验证“契约是否存在”**。

你可以在 JVM 单测里：

- 注册 hints（`RuntimeHintsRegistrar`）
- 用 `RuntimeHintsPredicates` 断言某个反射/资源/代理是否已被声明

这样做的好处是：

- 失败更早、更快（CI 即可发现）
- 不依赖 native 构建环境（成本低）

---

学习阶段你只需要能回答两件事：

1) hints 在哪里被“注册/汇总”？
2) 我怎么证明“现在 hints 有/没有”？

- `RuntimeHintsRegistrar#registerHints`（你定义的注册入口）

当你想把 AOT 放回 `spring-beans` 的真实基础设施时（而不是只停留在“我自己写 hints”）：

- `AotServices#factories`（定位 `META-INF/spring/aot.factories` 的加载入口）
- `AotServices.Loader#load`（观察：某个 service interface 最终加载到了哪些实现类）
- `BeanFactoryInitializationAotProcessor`（BeanFactory 初始化阶段的 AOT processor 入口）

---

---

如果你把这一章读成一句话，就是：

> **AOT/Native 的本质是“把运行期能力需求前置成构建期契约”，而 RuntimeHints 是这份契约的表达方式。**

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

学习阶段只要抓住一个主线：**AOT/Native 把很多“运行时的猜测与反射”前移到“构建期的显式声明”**。

- JVM：反射、动态代理、类路径扫描等“运行期能力”默认可用（成本是启动慢/内存大/可预知性较差）。
- Native（AOT）：运行期能力被收紧（换来启动快/内存小/可预知性强），你必须在构建期把需求“说清楚”。

1) 你有没有用到反射？（例如框架要反射调用构造器/方法/字段）
2) 你有没有用到动态代理？（JDK/CGLIB proxy）
3) 你有没有依赖运行期扫描/注册？（classpath 扫描、动态注册 bean 定义）
4) 这些需求能不能在构建期被推导/声明？

下一章会把它落成“可断言”的最小实验：[41. RuntimeHints 入门](41-runtimehints-basics.md)。

- 反射访问失败（NoSuchMethod/NoSuchField/IllegalAccess）
- 代理类生成/使用失败（尤其是动态代理/接口代理）
- 资源缺失（`ClassPathResource` 找不到、模板/配置加载失败）
- 运行期扫描失效（“JVM 下能发现、Native 下发现不了”）

## 4. 复现入口（可运行）

> 注意：本模块的 AOT Lab **不构建 native image**。
> 我们用 JVM 单测验证“构建期契约”的存在性（hints 是否注册），以保证可复现与低成本。

- 入口测试：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotFactoriesLabTest.java`
- 推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansAotRuntimeHintsLabTest,SpringCoreBeansAotFactoriesLabTest test
```

## 5. Debug / 断点建议（够用版）

本模块把断点聚焦到“hints 的注册”与“hints 的断言”上：

1) **误区：AOT/Native = 更快的 JVM**
   - 更准确的理解：AOT/Native = “约束更强的运行环境 + 构建期契约”。
2) **误区：只要加了 hints，就一定能跑**
   - hints 只解决“反射/代理/资源”等契约问题；业务逻辑、条件装配、初始化时机仍然需要正确性。

> **AOT/Native 的关键是把“运行期才能知道的事”变成“构建期必须说清楚的事”。**

下一章我们用一个最小可断言实验把 RuntimeHints 的注册与验证跑通：

- [41. RuntimeHints 入门：如何把“需求”变成可验证的契约](41-runtimehints-basics.md)

## 常见坑与边界

### 常见误区（把“JVM 能跑”误当成“Native 也能跑”）

1) **误区：AOT/Native 的问题都是“依赖/环境问题”**
   - 更常见的根因：缺失构建期契约（hints）导致运行期能力不可用（反射/代理/资源等）。
2) **误区：我只要把反射都改掉，就不需要 hints**
   - 实际上 Spring 的很多能力（包括框架自身）依赖“可发现/可反射/可生成”的元信息；hints 解决的是“告诉构建器保留/生成哪些能力”。
3) **误区：hints 能解决一切**
   - hints 只解决“反射/代理/资源”等契约问题；业务逻辑、条件装配、初始化时机仍然需要正确性。

## 排障决策表（AOT/Native：从“现象”到“hints 证据链”）

| 现象 | 最可能根因 | 证据（断点/断言） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| JVM 正常，Native 下反射失败 / `ClassNotFound` / `NoSuchMethod` | 缺失 reflection hints（构建期未声明） | `RuntimeHintsPredicates` 断言 hints 是否存在；查看 `RuntimeHintsRegistrar#registerHints` 是否执行 | 写/补 `RuntimeHintsRegistrar` 并确保被加载（factories/aot.factories） | `SpringCoreBeansAotRuntimeHintsLabTest` |
| 资源读取在 Native 下失败 | 缺失 resource hints | 同上，用 predicates 断言资源 hints | 注册资源 hints（pattern/文件） | `SpringCoreBeansAotRuntimeHintsLabTest`（按本仓库方式做可断言对照） |
| 代理/动态生成能力失效 | 缺失 proxy hints 或 AOT 生成契约 | predicates 断言代理 hints；定位 registrar 是否被加载 | 注册 proxy hints；必要时调整设计避免运行时动态行为 | 结合 `SpringCoreBeansAotRuntimeHintsLabTest` 的对照断言 |
| “我加了 registrar 但没生效” | registrar 没被 factories 发现/加载 | 断点 `AotServices.factories().load(...)`（或等价入口）；看注册器列表 | 确保 factories 文件/配置正确，包名/类名匹配 | `SpringCoreBeansAotFactoriesLabTest` |

## 面试常问（AOT / RuntimeHints：为什么“JVM 能跑 ≠ Native 能跑”）

### Q1：一句话解释 AOT/Native 和 JVM 的本质差异是什么？

- 标准答案（可复述）：
  - Native 环境下运行期信息不可得/受限，很多动态行为必须前移到构建期声明；RuntimeHints 是这份“构建期契约”的一部分。

### Q2：RuntimeHints 解决什么问题？解决不了什么问题？

- 标准答案（可复述）：
  - 解决反射/资源/代理等“运行期能力可用性”的契约问题；不解决业务逻辑正确性、条件装配语义、生命周期/时机等逻辑问题。

### Q3：你如何用证据链证明“没注册就不会命中”？

- 标准答案（可复述）：
  - 用对照测试：一个场景注册 hints、一个不注册；用 predicates/断言验证 hints 是否存在，并把 registrar 的加载入口写清楚（factories/aot.factories）。
- 最小复现：
  - `SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansAotFactoriesLabTest`

## 一句话自检

- 你能用一句话解释：为什么“JVM 能跑”不等于“Native 能跑”吗？（提示：运行期信息在 Native 下不可得）
- 你能说出：RuntimeHints 的作用域是什么、解决什么问题、解决不了什么问题吗？
- 如果你要把一个 AOT/Native 失败变成“可复现证据链”，你会优先写一个什么样的最小对照测试？

## 小结与下一章
<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑” —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
- 回到主线：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 7. 小结与下一章预告

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotFactoriesLabTest.java`

上一章：[39. BeanFactory API 深挖：接口族谱与手动 bootstrap 的边界](../part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[41. RuntimeHints 入门：把构建期契约跑通](41-runtimehints-basics.md)

<!-- BOOKIFY:END -->
