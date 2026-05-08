    # AOT SpEL 与 Value 约束
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：SpEL / Value 在 AOT 下会遇到什么约束？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansSpelValueLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：SpEL、占位符和值转换在 native 场景中暴露的反射/表达式限制。
        主线位置：AOT / Native。
        对照入口：`SpringCoreBeansSpelValueLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：SpEL / Value 在 AOT 下会遇到什么约束？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansSpelValueLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | SpEL / Value 在 AOT 下会遇到什么约束？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansSpelValueLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | SpEL、占位符和值转换在 native 场景中暴露的反射/表达式限制。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [spel-and-value-expression.md](spel-and-value-expression.md)
- [aot-runtimehints.md](aot-runtimehints.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    aot-spel-and-value-expression.md 的完成标准是：读者能用上面的 Lab 证明“SpEL / Value 在 AOT 下会遇到什么约束？”，并知道哪些相邻问题应该跳到其他 owner 文档。
