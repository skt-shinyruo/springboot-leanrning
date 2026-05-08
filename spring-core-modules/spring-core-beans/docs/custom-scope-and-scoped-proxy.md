    # 自定义 Scope 与 scoped proxy
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：自定义 Scope 与 scoped proxy 如何改变注入对象和目标对象的关系？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansCustomScopeLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：自定义 Scope 的对象获取、scoped proxy 注入形态和目标对象访问边界。
        主线位置：生命周期、Scope 与代理边界。
        对照入口：`SpringCoreBeansCustomScopeLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：自定义 Scope 与 scoped proxy 如何改变注入对象和目标对象的关系？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansCustomScopeLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | 自定义 Scope 与 scoped proxy 如何改变注入对象和目标对象的关系？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansCustomScopeLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | 自定义 Scope 的对象获取、scoped proxy 注入形态和目标对象访问边界。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [scope-and-prototype.md](scope-and-prototype.md)
- [proxying-phase.md](proxying-phase.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    custom-scope-and-scoped-proxy.md 的完成标准是：读者能用上面的 Lab 证明“自定义 Scope 与 scoped proxy 如何改变注入对象和目标对象的关系？”，并知道哪些相邻问题应该跳到其他 owner 文档。
