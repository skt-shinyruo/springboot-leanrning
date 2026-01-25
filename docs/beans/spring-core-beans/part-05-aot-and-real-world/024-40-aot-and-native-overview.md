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
上一章：[第 23 章：18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）](../part-04-wiring-and-boundaries/023-18-lazy-semantics.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 25 章：90. 常见坑清单（建议反复对照）](../appendix/025-90-common-pitfalls.md)
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

因此在 Spring 世界里，AOT/Native 的核心问题往往不是“业务逻辑”，而是：

---

## 2. 关键概念：RuntimeHints（AOT 的“契约”）

在 Spring 体系中，AOT/Native 的“显式声明”通常通过 **RuntimeHints** 来表达：

- “这个类型需要反射访问”
- “这个代理需要生成”
- “这个资源需要打包进镜像”

你暂时不需要记住所有 hints 的分类，只需要建立一个工程直觉：

> **AOT/Native 下失败的很多问题，本质都是 “hints 缺失”。**

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

这一章的目标是让你知道：这些问题都可以被归类到“契约缺失”，并能落到一个具体入口：

- **RuntimeHints**（声明反射/代理/资源等需求）

---

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


## 6. 常见误区


## 小结与下一章
<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑” —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
- 回到主线：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 7. 小结与下一章预告

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotFactoriesLabTest.java`

上一章：[18](../part-04-wiring-and-boundaries/023-18-lazy-semantics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[013-02-breakpoint-map.md](../part-00-guide/013-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
