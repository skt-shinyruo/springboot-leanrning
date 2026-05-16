# 深入阅读路线：先学什么、每阶段验收什么

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文给的是学习顺序和阶段目标，不重复讲机制正文。
    - 重点是把“先看什么、学到什么程度、拿什么验收”说清楚。
    - 模块级验收入口是 `SpringCoreBeansModuleContractLabTest`。

    观察对象：学习顺序、阶段目标和验证入口。
    主线位置：整个模块文档体系的学习路线。
    对照入口：`SpringCoreBeansModuleContractLabTest`。
<!-- CHAPTER-CARD:END -->

如果把 Spring Bean 文档一次性全读，通常会把“定义、注册、创建、注入、生命周期、Boot/AOT”揉成一团。更稳妥的方式是按阶段推进，每一阶段只回答当前层该回答的问题。

## 阶段 1：先确认文档边界

目标不是理解机制，而是知道问题该去哪里找。先看 [知识地图：Spring Bean 文档归属](appendix-knowledge-map.md)，再看 `SpringCoreBeansModuleContractLabTest` 负责的契约：文档目录、测试支撑和引用关系是否保持同步。

这一阶段你应该能回答：

- 某个问题属于注册、创建、注入、生命周期，还是 Boot/AOT。
- 应该先看主文档，还是先看附录或 guide。
- 哪个 Lab 是最短验证入口。

## 阶段 2：先学对象模型，再学注册

先看 `bean-mental-model.md`，再看 `bean-definition-registration.md` 和 `bean-definition-metadata-and-origin.md`。这个顺序的目的很简单：先知道容器托管的是什么，再知道这些东西怎么进容器、靠什么元数据做决策。

这一阶段完成后，你应该能回答：

- BeanDefinition 和最终对象不是同一个层次。
- 注册时点会影响后续创建和候选选择。
- metadata 不是注释，是容器决策输入。

## 阶段 3：再看 refresh 和单 Bean 主线

接下来进入 `refresh-mainline.md` 和 `bean-creation-mainline.md`。前者回答“容器什么时候准备好”，后者回答“单个 Bean 怎么出来并对外暴露”。

这一阶段完成后，你应该能回答：

- refresh 改变了哪些容器状态。
- `getBean()` 触发的创建链路有哪些阶段。
- 什么时候是 raw instance，什么时候是最终 exposed object。

## 阶段 4：最后按分支主题补齐

当主线已经能走通，再去看注入、生命周期、代理、Boot/AOT 和排障附录。这个顺序比“先看最难的”更有效，因为后面的很多现象都依赖前面的定义、注册和主线理解。

`SpringCoreBeansModuleContractLabTest` 适合作为每个大阶段的回归入口：只要它还绿，说明文档目录、引用和支撑测试没有漂移。阶段专题的正确性，再由各自的 Lab 套件补充验证。
