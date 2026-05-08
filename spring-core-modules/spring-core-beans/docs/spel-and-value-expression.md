    # SpEL 与 @Value：表达式和占位符顺序
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：`#{...}` SpEL 与 `${...}` 占位符的解析顺序是什么？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansSpelValueLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：@Value 中 placeholder、SpEL、类型转换的先后关系。
        主线位置：值解析、转换与外部输入。
        对照入口：`SpringCoreBeansSpelValueLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：`#{...}` SpEL 与 `${...}` 占位符的解析顺序是什么？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansSpelValueLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | `#{...}` SpEL 与 `${...}` 占位符的解析顺序是什么？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansSpelValueLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | @Value 中 placeholder、SpEL、类型转换的先后关系。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [value-placeholder-resolution.md](value-placeholder-resolution.md)
- [type-conversion-and-beanwrapper.md](type-conversion-and-beanwrapper.md)
- [aot-spel-and-value-expression.md](aot-spel-and-value-expression.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    spel-and-value-expression.md 的完成标准是：读者能用上面的 Lab 证明“`#{...}` SpEL 与 `${...}` 占位符的解析顺序是什么？”，并知道哪些相邻问题应该跳到其他 owner 文档。
