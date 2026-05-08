    # Qualifier、Primary、Priority、Order 的边界
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：`@Qualifier`、`@Primary`、`@Priority`、`@Order` 各自管哪一步？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansAutowireCandidateSelectionLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：限定符、primary、priority 与集合排序的不同作用点。
        主线位置：依赖解析与注入。
        对照入口：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：`@Qualifier`、`@Primary`、`@Priority`、`@Order` 各自管哪一步？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansAutowireCandidateSelectionLabTest`
- `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | `@Qualifier`、`@Primary`、`@Priority`、`@Order` 各自管哪一步？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | 限定符、primary、priority 与集合排序的不同作用点。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [autowire-candidate-selection.md](autowire-candidate-selection.md)
- [bean-definition-metadata-and-origin.md](bean-definition-metadata-and-origin.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    qualifier-primary-priority-order.md 的完成标准是：读者能用上面的 Lab 证明“`@Qualifier`、`@Primary`、`@Priority`、`@Order` 各自管哪一步？”，并知道哪些相邻问题应该跳到其他 owner 文档。
