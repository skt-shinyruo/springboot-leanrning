    # refresh 主线：上下文刷新顺序
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：`refresh()` 这条主线到底先做什么、后做什么？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansMainlineCallChainLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：ApplicationContext#refresh 从准备上下文到预实例化单例的阶段顺序。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansMainlineCallChainLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：`refresh()` 这条主线到底先做什么、后做什么？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansMainlineCallChainLabTest`
- `SpringCoreBeansBootstrapInternalsLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | `refresh()` 这条主线到底先做什么、后做什么？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansMainlineCallChainLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | ApplicationContext#refresh 从准备上下文到预实例化单例的阶段顺序。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [container-bootstrap-and-infrastructure.md](container-bootstrap-and-infrastructure.md)
- [bean-creation-mainline.md](bean-creation-mainline.md)
- [post-processors-overview.md](post-processors-overview.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    refresh-mainline.md 的完成标准是：读者能用上面的 Lab 证明“`refresh()` 这条主线到底先做什么、后做什么？”，并知道哪些相邻问题应该跳到其他 owner 文档。
