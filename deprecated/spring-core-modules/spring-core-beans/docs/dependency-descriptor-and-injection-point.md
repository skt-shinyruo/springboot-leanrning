    # DependencyDescriptor 与 InjectionPoint 元数据
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：DependencyDescriptor / InjectionPoint 里有哪些元数据可用于排障？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDependencyDescriptorMetadataLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：DependencyDescriptor 暴露的字段、方法参数/字段来源和程序化 resolveDependency 的观察入口。
        主线位置：依赖解析与注入。
        对照入口：`SpringCoreBeansDependencyDescriptorMetadataLabTest` / `SpringCoreBeansProgrammaticResolveDependencyLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：DependencyDescriptor / InjectionPoint 里有哪些元数据可用于排障？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansDependencyDescriptorMetadataLabTest`
- `SpringCoreBeansProgrammaticResolveDependencyLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | DependencyDescriptor / InjectionPoint 里有哪些元数据可用于排障？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansDependencyDescriptorMetadataLabTest` / `SpringCoreBeansProgrammaticResolveDependencyLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | DependencyDescriptor 暴露的字段、方法参数/字段来源和程序化 resolveDependency 的观察入口。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [dependency-injection-resolution.md](dependency-injection-resolution.md)
- [resolvable-dependency.md](resolvable-dependency.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    dependency-descriptor-and-injection-point.md 的完成标准是：读者能用上面的 Lab 证明“DependencyDescriptor / InjectionPoint 里有哪些元数据可用于排障？”，并知道哪些相邻问题应该跳到其他 owner 文档。
