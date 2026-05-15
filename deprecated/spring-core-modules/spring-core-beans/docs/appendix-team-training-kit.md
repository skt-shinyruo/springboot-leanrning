    # Appendix：团队内训脚本
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这页只做支持：把 owner 文档、Lab 和自检题编排成课时。
        - 需要机制解释时，跳回主文档。
        - 最短契约：`SpringCoreBeansDocumentationContractTest`。

        观察对象：路线、索引、断点或 Lab 入口。
        主线位置：支持文档，不拥有新的 Bean 知识点。
        对照入口：`SpringCoreBeansDocumentationContractTest`。
    <!-- CHAPTER-CARD:END -->

    ## 职责

    把 owner 文档、Lab 和自检题编排成课时。

    ## 路由表

    | 入口 | 用途 |
    | --- | --- |
    | [guide-quickstart-30min.md](guide-quickstart-30min.md) | 主文档或支持入口 |
| [appendix-knowledge-map.md](appendix-knowledge-map.md) | 主文档或支持入口 |
| [appendix-self-check.md](appendix-self-check.md) | 主文档或支持入口 |
    | [appendix-knowledge-map.md](appendix-knowledge-map.md) | 全量 owner 归属表 |

    ## 维护规则

    - 只保留路线、索引、断点、Lab 或 checklist。
    - 发现需要解释 Bean 行为时，新增或修改对应主文档，而不是扩写本页。
    - 修改后运行 `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test`。
