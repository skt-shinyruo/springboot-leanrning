    # Guide：深入阅读顺序
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这页只做支持：给进阶阅读顺序和回跳路径。
        - 需要机制解释时，跳回主文档。
        - 最短契约：`SpringCoreBeansDocumentationContractTest`。

        观察对象：路线、索引、断点或 Lab 入口。
        主线位置：支持文档，不拥有新的 Bean 知识点。
        对照入口：`SpringCoreBeansDocumentationContractTest`。
    <!-- CHAPTER-CARD:END -->

    ## 职责

    给进阶阅读顺序和回跳路径。

    ## 路由表

    | 入口 | 用途 |
    | --- | --- |
    | [appendix-knowledge-map.md](appendix-knowledge-map.md) | 主文档或支持入口 |
| [post-processors-overview.md](post-processors-overview.md) | 主文档或支持入口 |
| [early-reference-and-three-level-cache.md](early-reference-and-three-level-cache.md) | 主文档或支持入口 |
    | [appendix-knowledge-map.md](appendix-knowledge-map.md) | 全量 owner 归属表 |

    ## 维护规则

    - 只保留路线、索引、断点、Lab 或 checklist。
    - 发现需要解释 Bean 行为时，新增或修改对应主文档，而不是扩写本页。
    - 修改后运行 `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test`。
