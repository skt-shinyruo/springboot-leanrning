# 30 分钟快速上手：先跑主线，再补关键分支

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文是一条路线图，不是机制教程。
    - 目标是在 30 分钟内跑通最短主线，再知道下一步该去看哪里。
    - 重点使用 `SpringCoreBeansLabTest` 和 `SpringCoreBeansMainlineCallChainLabTest` 作为入口。

    观察对象：最短主线、关键 Lab 和下一步阅读入口。
    主线位置：从容器现象回到主线调用链。
    对照入口：`SpringCoreBeansLabTest`、`SpringCoreBeansMainlineCallChainLabTest`。
<!-- CHAPTER-CARD:END -->

这不是一篇把机制讲完的文章，而是一条最短路线。目标只有一个：先让你在 30 分钟里看到 Bean 机制的主线，再决定是往注册、注入、生命周期，还是 Boot/AOT 方向继续深挖。

## 0-10 分钟：先跑最短可见结果

先跑 `SpringCoreBeansLabTest`：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test
```

这一步应该先让你看到四类最基础的现象：

- `@Qualifier` 会影响单值选择。
- prototype 和 provider 取到的对象不是一回事。
- `@PostConstruct` 在容器初始化阶段执行。
- 缺失 Bean 会直接失败，不会静默返回空对象。

这一步的目的不是记住答案，而是给后面的主线调用链准备问题清单。

## 10-20 分钟：把主线串起来

再跑 `SpringCoreBeansMainlineCallChainLabTest`：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansMainlineCallChainLabTest test
```

这组 Lab 负责把 refresh、创建、注入、初始化和暴露放到同一条链里。你不需要一次看懂全部细节，只要先确认：

1. refresh 先把 BeanDefinition、后处理器和基础设施准备好。
2. 非懒 singleton 的单 Bean 创建会在 refresh 内部的预实例化阶段被触发。
3. 属性注入和初始化不是构造器本身。
4. 最终 `getBean()` 拿到的是对外暴露对象，不一定等于 raw instance。

后续显式 `getBean()`、lazy Bean、prototype 或运行期依赖解析，也可能再次按需进入同一条单 Bean 创建主线。不要把 `refresh()` 误解成“创建完全结束”，也不要把 `getBean()` 误解成唯一创建入口。

如果你在这一步已经能把输出和源码方法名对上，说明主线已经能走通。

## 20-30 分钟：按症状选下一步

接下来不要继续硬啃主线，直接按现象选分支：

| 现象 | 下一步看哪里 |
| --- | --- |
| 找不到 BeanDefinition | `bean-definition-registration.md` |
| 单值注入报不唯一 | `dependency-injection-resolution.md` 和 `autowire-candidate-selection.md` |
| 构造器和最终对象不一样 | `bean-creation-mainline.md` 和 `proxying-phase.md` |
| 默认 Bean 没有出现 | `boot-auto-configuration-beans.md` |
| JVM 能跑，Native 不能跑 | `aot-native-overview.md` |

如果你不确定从哪一层开始，就先回到 [知识地图：Spring Bean 文档归属](appendix-knowledge-map.md)。那张表的作用不是解释机制，而是帮你少走回头路。
