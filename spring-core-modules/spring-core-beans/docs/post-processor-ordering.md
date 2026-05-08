    # Post-Processor Ordering：处理器排序规则
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：PriorityOrdered、Ordered、无序处理器的排序规则如何影响行为？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPostProcessorOrderingLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：PriorityOrdered、Ordered、普通处理器分组排序以及注册窗口对行为的影响。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansPostProcessorOrderingLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：PriorityOrdered、Ordered、无序处理器的排序规则如何影响行为？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansPostProcessorOrderingLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | PriorityOrdered、Ordered、无序处理器的排序规则如何影响行为？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansPostProcessorOrderingLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | PriorityOrdered、Ordered、普通处理器分组排序以及注册窗口对行为的影响。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [post-processors-overview.md](post-processors-overview.md)
- [programmatic-bpp-registration.md](programmatic-bpp-registration.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    post-processor-ordering.md 的完成标准是：读者能用上面的 Lab 证明“PriorityOrdered、Ordered、无序处理器的排序规则如何影响行为？”，并知道哪些相邻问题应该跳到其他 owner 文档。
