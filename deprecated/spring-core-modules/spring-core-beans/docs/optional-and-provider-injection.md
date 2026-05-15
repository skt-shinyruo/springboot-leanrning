    # Optional 与 Provider：可选依赖和延迟获取
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：Optional、`required=false`、`ObjectProvider`、`Provider` 怎么表达可选与延迟？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansOptionalInjectionLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：Optional、required=false、ObjectProvider、JSR-330 Provider 的缺失依赖与延迟获取语义。
        主线位置：依赖解析与注入。
        对照入口：`SpringCoreBeansOptionalInjectionLabTest` / `SpringCoreBeansJsr330InjectionLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：Optional、`required=false`、`ObjectProvider`、`Provider` 怎么表达可选与延迟？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansOptionalInjectionLabTest`
- `SpringCoreBeansJsr330InjectionLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | Optional、`required=false`、`ObjectProvider`、`Provider` 怎么表达可选与延迟？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansOptionalInjectionLabTest` / `SpringCoreBeansJsr330InjectionLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | Optional、required=false、ObjectProvider、JSR-330 Provider 的缺失依赖与延迟获取语义。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [dependency-injection-resolution.md](dependency-injection-resolution.md)
- [lazy-semantics.md](lazy-semantics.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    optional-and-provider-injection.md 的完成标准是：读者能用上面的 Lab 证明“Optional、`required=false`、`ObjectProvider`、`Provider` 怎么表达可选与延迟？”，并知道哪些相邻问题应该跳到其他 owner 文档。
