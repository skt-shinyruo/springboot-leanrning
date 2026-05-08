    # Post-Processor 总览：定义阶段与实例阶段
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：BFPP / BDRPP / BPP 的职责边界是什么，分别属于定义阶段还是实例阶段？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansStaticBeanFactoryPostProcessorLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：BFPP、BDRPP、BPP 的阶段边界、可修改对象和相邻详细页路由。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：BFPP / BDRPP / BPP 的职责边界是什么，分别属于定义阶段还是实例阶段？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`
- `SpringCoreBeansRegistryPostProcessorLabTest`
- `SpringCoreBeansPostProcessorOrderingLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | BFPP / BDRPP / BPP 的职责边界是什么，分别属于定义阶段还是实例阶段？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | BFPP、BDRPP、BPP 的阶段边界、可修改对象和相邻详细页路由。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [beanfactory-post-processors.md](beanfactory-post-processors.md)
- [bdrpp-definition-registration.md](bdrpp-definition-registration.md)
- [beanpost-processors.md](beanpost-processors.md)
- [post-processor-ordering.md](post-processor-ordering.md)
- [programmatic-bpp-registration.md](programmatic-bpp-registration.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    post-processors-overview.md 的完成标准是：读者能用上面的 Lab 证明“BFPP / BDRPP / BPP 的职责边界是什么，分别属于定义阶段还是实例阶段？”，并知道哪些相邻问题应该跳到其他 owner 文档。
