    # AOT 外部对象注入
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：容器外对象注入在 AOT 下怎样成立？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansAutowireCapableBeanFactoryLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：AutowireCapableBeanFactory 对容器外对象进行注入/初始化时的 AOT 可达性约束。
        主线位置：AOT / Native。
        对照入口：`SpringCoreBeansAutowireCapableBeanFactoryLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：容器外对象注入在 AOT 下怎样成立？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansAutowireCapableBeanFactoryLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | 容器外对象注入在 AOT 下怎样成立？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansAutowireCapableBeanFactoryLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | AutowireCapableBeanFactory 对容器外对象进行注入/初始化时的 AOT 可达性约束。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [beanfactory-api-and-autowirecapablebeanfactory.md](beanfactory-api-and-autowirecapablebeanfactory.md)
- [aot-runtimehints.md](aot-runtimehints.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    aot-autowirecapablebeanfactory-external-objects.md 的完成标准是：读者能用上面的 Lab 证明“容器外对象注入在 AOT 下怎样成立？”，并知道哪些相邻问题应该跳到其他 owner 文档。
