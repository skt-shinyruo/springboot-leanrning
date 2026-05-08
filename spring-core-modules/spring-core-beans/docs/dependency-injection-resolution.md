    # 依赖注入解析：注入点提出的需求
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：注入点到底向容器提出了什么需求？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansInjectionAmbiguityLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：注入点类型、名称、required 标记和容器解析依赖的入口问题。
        主线位置：依赖解析与注入。
        对照入口：`SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：注入点到底向容器提出了什么需求？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansInjectionAmbiguityLabTest`
- `SpringCoreBeansAutowireCandidateSelectionLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | 注入点到底向容器提出了什么需求？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | 注入点类型、名称、required 标记和容器解析依赖的入口问题。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [dependency-descriptor-and-injection-point.md](dependency-descriptor-and-injection-point.md)
- [autowire-candidate-selection.md](autowire-candidate-selection.md)
- [optional-and-provider-injection.md](optional-and-provider-injection.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    dependency-injection-resolution.md 的完成标准是：读者能用上面的 Lab 证明“注入点到底向容器提出了什么需求？”，并知道哪些相邻问题应该跳到其他 owner 文档。
