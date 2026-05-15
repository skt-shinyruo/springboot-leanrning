    # BeanDefinition 元数据与来源排查
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：BeanDefinition 的 primary/autowireCandidate/source/factoryMethod 等元数据如何支撑候选选择和来源排查？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanDefinitionMetadataFlagsLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：候选标记、来源字段、工厂方法字段和排障时的 origin 观察口径。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansBeanDefinitionMetadataFlagsLabTest` / `SpringCoreBeansBeanDefinitionOriginLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：BeanDefinition 的 primary/autowireCandidate/source/factoryMethod 等元数据如何支撑候选选择和来源排查？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest`
- `SpringCoreBeansBeanDefinitionOriginLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | BeanDefinition 的 primary/autowireCandidate/source/factoryMethod 等元数据如何支撑候选选择和来源排查？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest` / `SpringCoreBeansBeanDefinitionOriginLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | 候选标记、来源字段、工厂方法字段和排障时的 origin 观察口径。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [autowire-candidate-selection.md](autowire-candidate-selection.md)
- [qualifier-primary-priority-order.md](qualifier-primary-priority-order.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    bean-definition-metadata-and-origin.md 的完成标准是：读者能用上面的 Lab 证明“BeanDefinition 的 primary/autowireCandidate/source/factoryMethod 等元数据如何支撑候选选择和来源排查？”，并知道哪些相邻问题应该跳到其他 owner 文档。
