    # MergedBeanDefinition：创建前的最终定义视图
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：MergedBeanDefinition / RootBeanDefinition 在什么阶段形成，解决什么问题？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansMergedBeanDefinitionLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：原始定义、父子定义、RootBeanDefinition 合并缓存和创建阶段读取的最终视图。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansMergedBeanDefinitionLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：MergedBeanDefinition / RootBeanDefinition 在什么阶段形成，解决什么问题？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansMergedBeanDefinitionLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | MergedBeanDefinition / RootBeanDefinition 在什么阶段形成，解决什么问题？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansMergedBeanDefinitionLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | 原始定义、父子定义、RootBeanDefinition 合并缓存和创建阶段读取的最终视图。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [bean-definition-metadata-and-origin.md](bean-definition-metadata-and-origin.md)
- [bean-creation-mainline.md](bean-creation-mainline.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    merged-bean-definition.md 的完成标准是：读者能用上面的 Lab 证明“MergedBeanDefinition / RootBeanDefinition 在什么阶段形成，解决什么问题？”，并知道哪些相邻问题应该跳到其他 owner 文档。
