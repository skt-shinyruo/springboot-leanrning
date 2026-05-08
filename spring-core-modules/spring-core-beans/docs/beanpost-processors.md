    # BeanPostProcessor：实例创建中的介入窗口
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：BPP 如何介入实例创建，什么时候会把 bean 换成 proxy？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLifecycleRawVsProxyLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：BPP before/after initialization、InstantiationAwareBPP 和替换 exposedObject 的窗口。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansLifecycleRawVsProxyLabTest` / `SpringCoreBeansProxyingPhaseLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：BPP 如何介入实例创建，什么时候会把 bean 换成 proxy？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansLifecycleRawVsProxyLabTest`
- `SpringCoreBeansProxyingPhaseLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | BPP 如何介入实例创建，什么时候会把 bean 换成 proxy？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansLifecycleRawVsProxyLabTest` / `SpringCoreBeansProxyingPhaseLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | BPP before/after initialization、InstantiationAwareBPP 和替换 exposedObject 的窗口。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [post-processors-overview.md](post-processors-overview.md)
- [proxying-phase.md](proxying-phase.md)
- [bean-creation-mainline.md](bean-creation-mainline.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    beanpost-processors.md 的完成标准是：读者能用上面的 Lab 证明“BPP 如何介入实例创建，什么时候会把 bean 换成 proxy？”，并知道哪些相邻问题应该跳到其他 owner 文档。
