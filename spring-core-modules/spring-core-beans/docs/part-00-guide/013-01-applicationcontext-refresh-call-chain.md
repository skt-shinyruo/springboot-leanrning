# 第 13 章：01：`refresh()` 调用链（容器从“定义”到“实例”的主线）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：`refresh()` 调用链（容器从“定义”到“实例”的主线）
    - 怎么使用：建议先跑本章推荐 Lab，把“容器阶段”固化为可观察现象，再按本文的调用链从 `refresh()` 走到 `doCreateBean()`；最后回到断点地图把断点收敛成“可复用清单”。
    - 原理：`AbstractApplicationContext#refresh` 按阶段推进：准备环境 → 生成 BeanFactory → 注册/执行 BFPP/BDRPP（改定义）→ 注册 BPP（改实例/可换成 proxy）→ 单例预实例化 → 容器就绪。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.context.support.PostProcessorRegistrationDelegate` / `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`
    - 推荐 Lab：`SpringCoreBeansContainerLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）](012-01-quickstart-30min.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 13 章：02：断点地图（容器主线可复用断点/观察点清单）](013-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01：`refresh()` 调用链（容器从“定义”到“实例”的主线）**
- 目标：把“我知道 refresh 很重要”升级为“我能解释 refresh 的阶段，并能把断点打在正确的阶段入口”。
- 基线版本：Spring Framework `6.2.15`（本仓库由 Spring Boot `3.5.9` 管理依赖版本）。本章提到的方法名以该版本为准。

!!! summary "本章要点"

    - 深挖 Beans 的第一要务不是背类名，而是建立**阶段感**：你要知道“我现在在看定义层（BeanDefinition）还是实例层（bean instance/proxy）”。
    - `refresh()` 里最值得先钉死的 3 个节点：**BFPP（改定义）**、**BPP（改实例/可能换代理）**、**单例预实例化（批量创建）**。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansContainerLabTest`

## 主线伪代码（把 refresh 当成“时间线”读）

你不需要一次性背完所有步骤，但你必须能把“关键阶段”按顺序复述出来（这样断点才打得准）。

> 提示：本仓库的断点地图会把下面的关键节点转成“可复用断点清单”，见：[02：断点地图](013-02-breakpoint-map.md)。

`AbstractApplicationContext#refresh`（省略不影响理解的细节）：

1. `prepareRefresh()`：准备环境（例如记录启动时间、初始化 property sources）
2. `obtainFreshBeanFactory()`：创建/刷新 `BeanFactory`（把“定义层容器”准备出来）
3. `prepareBeanFactory(beanFactory)`：注册基础设施（classloader、environment、resolvable dependencies）
4. `postProcessBeanFactory(beanFactory)`：留给子类扩展（Web 容器等）
5. **`invokeBeanFactoryPostProcessors(beanFactory)`：执行 BFPP/BDRPP（改定义）**
6. **`registerBeanPostProcessors(beanFactory)`：注册 BPP（改实例/可能换成 proxy）**
7. `initMessageSource()` / `initApplicationEventMulticaster()`：初始化基础设施组件
8. `onRefresh()`：留给子类扩展（Web 容器）
9. `registerListeners()`：注册 `ApplicationListener`
10. **`finishBeanFactoryInitialization(beanFactory)`：单例预实例化（会触发大量 bean 创建）**
11. `finishRefresh()`：容器就绪（发布事件等）

你可以把 5/6/10 当成“容器三段论”：

- 第 5 段（改定义）：`BeanDefinition` 图被加工/补齐
- 第 6 段（改实例）：`bean instance` 可能被增强/替换成 proxy
- 第 10 段（批量创建）：非 lazy 单例会在这里被创建出来（因此很多问题会在这里爆）

## 把调用链落到“你能下断点的锚点”

### 锚点 1：BFPP/BDRPP（改定义）——“注解为什么能生效？”

当你在真实项目里看到这些症状：

- `@Bean/@ComponentScan/@Import` 像没生效（BeanDefinition 不存在）
- `@ConfigurationProperties` 等基础设施没装上

优先把断点打在：

- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
- `ConfigurationClassPostProcessor#processConfigBeanDefinitions`

因为你卡的往往是“定义层没有长出来/没加工完”，而不是“实例创建失败”。

### 锚点 2：BPP（改实例/换代理）——“为什么最终暴露的是 proxy？”

当你看到：

- 注入进来的 bean 不是你以为的类型（`AopUtils.isAopProxy(bean)` 为 true）
- AOP/Tx/Cache/Security “不生效”或“自调用绕过”

优先把断点打在：

- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

你要观察的是：**`bean` → `result` 的第一次替换点**（什么时候从原对象变成 proxy）。

### 锚点 3：单例预实例化（批量创建）——“为什么启动就爆？”

当你看到：

- 应用启动阶段就报错（而不是第一次 `getBean` 才报错）
- 循环依赖/类型转换/依赖注入歧义在启动期爆炸

优先把断点打在：

- `DefaultListableBeanFactory#preInstantiateSingletons`
- `AbstractBeanFactory#doGetBean`
- `AbstractAutowireCapableBeanFactory#doCreateBean`

因为“启动期爆”几乎都意味着：**它是非 lazy 单例，且被预实例化触发了**。

## 一句话自检（你应该能回答）

1. BFPP 与 BPP 的差别是什么？它们分别“改什么”，分别在 refresh 的哪段执行？
2. 为什么 `@Autowired/@PostConstruct` 在你自己 new 出来的 `DefaultListableBeanFactory` 里可能不生效？
3. 为什么有些问题“启动就爆”，有些问题“第一次调用才爆”？你会把断点打在哪里区分两者？

## 小结与下一章

- 本章把 `refresh()` 变成可复述的时间线与可下断点的锚点；下一章把锚点收敛成一份可复用断点清单。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest`
- Lab：`SpringCoreBeansBootstrapInternalsLabTest`
- Lab：`SpringCoreBeansPostProcessorOrderingLabTest`
- Lab：`SpringCoreBeansBeanCreationTraceLabTest`

上一章：[part-00-guide/01-quickstart-30min.md](012-01-quickstart-30min.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](013-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
