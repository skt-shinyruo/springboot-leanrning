    # AOT / Native 总览：JVM 可运行不等于 Native 可运行
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：为什么 JVM 可运行不等于 Native 可运行？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansAotRuntimeHintsLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：JVM 动态能力、native closed world 和 RuntimeHints 之间的总体边界。
        主线位置：AOT / Native。
        对照入口：`SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansRuntimeHintsBoundaryLabTest` / `SpringCoreBeansAotFactoriesLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：为什么 JVM 可运行不等于 Native 可运行？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansAotRuntimeHintsLabTest`
- `SpringCoreBeansRuntimeHintsBoundaryLabTest`
- `SpringCoreBeansAotFactoriesLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | 为什么 JVM 可运行不等于 Native 可运行？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansRuntimeHintsBoundaryLabTest` / `SpringCoreBeansAotFactoriesLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | JVM 动态能力、native closed world 和 RuntimeHints 之间的总体边界。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [aot-runtimehints.md](aot-runtimehints.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    aot-native-overview.md 的完成标准是：读者能用上面的 Lab 证明“为什么 JVM 可运行不等于 Native 可运行？”，并知道哪些相邻问题应该跳到其他 owner 文档。
