    # Bean 名称与 alias：定位和注入的名字边界
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：beanName 和 alias 如何影响定位、注入和排障？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanNameAliasLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：canonical name、alias 注册、按名定位和注入点名称回退之间的关系。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansBeanNameAliasLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：beanName 和 alias 如何影响定位、注入和排障？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansBeanNameAliasLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | beanName 和 alias 如何影响定位、注入和排障？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansBeanNameAliasLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | canonical name、alias 注册、按名定位和注入点名称回退之间的关系。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [resource-vs-autowired.md](resource-vs-autowired.md)
- [factorybean.md](factorybean.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    bean-name-and-alias.md 的完成标准是：读者能用上面的 Lab 证明“beanName 和 alias 如何影响定位、注入和排障？”，并知道哪些相邻问题应该跳到其他 owner 文档。
