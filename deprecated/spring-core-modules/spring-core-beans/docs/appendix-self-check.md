    # Appendix：自检清单
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这页只做支持：用问题检查读者是否能定位 owner、Lab 和断点。
        - 需要机制解释时，跳回主文档。
        - 最短契约：`SpringCoreBeansDocumentationContractTest`。

        观察对象：路线、索引、断点或 Lab 入口。
        主线位置：支持文档，不拥有新的 Bean 知识点。
        对照入口：`SpringCoreBeansDocumentationContractTest`。
    <!-- CHAPTER-CARD:END -->

    ## 职责

    用问题检查读者是否能定位 owner、Lab 和断点。

    ## 路由表

    | 入口 | 用途 |
    | --- | --- |
    | [appendix-knowledge-map.md](appendix-knowledge-map.md) | 主文档或支持入口 |
| [guide-breakpoint-map.md](guide-breakpoint-map.md) | 主文档或支持入口 |
| [appendix-interview-playbook.md](appendix-interview-playbook.md) | 主文档或支持入口 |
    | [appendix-knowledge-map.md](appendix-knowledge-map.md) | 全量 owner 归属表 |

    ## 维护规则

    - 只保留路线、索引、断点、Lab 或 checklist。
    - 发现需要解释 Bean 行为时，新增或修改对应主文档，而不是扩写本页。
    - 修改后运行 `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test`。
