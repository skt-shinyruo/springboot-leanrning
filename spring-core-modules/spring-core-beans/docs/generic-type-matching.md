    # 泛型类型匹配：ResolvableType 与代理失真
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：泛型信息如何参与注入匹配，代理为什么会让它失真？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansGenericTypeMatchingPitfallsLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：ResolvableType 参与候选匹配、泛型擦除和代理类型导致的观察偏差。
        主线位置：依赖解析与注入。
        对照入口：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：泛型信息如何参与注入匹配，代理为什么会让它失真？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansGenericTypeMatchingPitfallsLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | 泛型信息如何参与注入匹配，代理为什么会让它失真？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansGenericTypeMatchingPitfallsLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | ResolvableType 参与候选匹配、泛型擦除和代理类型导致的观察偏差。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [autowire-candidate-selection.md](autowire-candidate-selection.md)
- [proxying-phase.md](proxying-phase.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    generic-type-matching.md 的完成标准是：读者能用上面的 Lab 证明“泛型信息如何参与注入匹配，代理为什么会让它失真？”，并知道哪些相邻问题应该跳到其他 owner 文档。
