    # dependsOn：初始化顺序而非注入规则
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：`dependsOn` 如何强制初始化顺序，为什么它不是依赖注入规则？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDependsOnLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：dependsOn 初始化边、销毁反序和与依赖解析规则的区别。
        主线位置：生命周期、Scope 与代理边界。
        对照入口：`SpringCoreBeansDependsOnLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：`dependsOn` 如何强制初始化顺序，为什么它不是依赖注入规则？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansDependsOnLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | `dependsOn` 如何强制初始化顺序，为什么它不是依赖注入规则？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansDependsOnLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | dependsOn 初始化边、销毁反序和与依赖解析规则的区别。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [bean-creation-mainline.md](bean-creation-mainline.md)
- [smart-lifecycle.md](smart-lifecycle.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    depends-on.md 的完成标准是：读者能用上面的 Lab 证明“`dependsOn` 如何强制初始化顺序，为什么它不是依赖注入规则？”，并知道哪些相邻问题应该跳到其他 owner 文档。
