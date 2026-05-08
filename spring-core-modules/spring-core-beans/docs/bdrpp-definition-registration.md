    # BDRPP：后处理器阶段新增定义
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：BDRPP 为什么能在普通 BFPP 之前新增或改写 BeanDefinition？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansRegistryPostProcessorLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：BeanDefinitionRegistryPostProcessor 的 registry 回调、重复发现窗口和与普通 BFPP 的顺序差异。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansRegistryPostProcessorLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：BDRPP 为什么能在普通 BFPP 之前新增或改写 BeanDefinition？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansRegistryPostProcessorLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | BDRPP 为什么能在普通 BFPP 之前新增或改写 BeanDefinition？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansRegistryPostProcessorLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | BeanDefinitionRegistryPostProcessor 的 registry 回调、重复发现窗口和与普通 BFPP 的顺序差异。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [post-processors-overview.md](post-processors-overview.md)
- [beanfactory-post-processors.md](beanfactory-post-processors.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    bdrpp-definition-registration.md 的完成标准是：读者能用上面的 Lab 证明“BDRPP 为什么能在普通 BFPP 之前新增或改写 BeanDefinition？”，并知道哪些相邻问题应该跳到其他 owner 文档。
