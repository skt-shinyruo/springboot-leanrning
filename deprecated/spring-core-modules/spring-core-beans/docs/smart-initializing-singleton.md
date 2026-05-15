    # SmartInitializingSingleton：所有单例之后
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：`SmartInitializingSingleton` 为什么要等所有单例都创建完？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansSmartInitializingSingletonLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：preInstantiateSingletons 结束后的 afterSingletonsInstantiated 回调窗口。
        主线位置：生命周期、Scope 与代理边界。
        对照入口：`SpringCoreBeansSmartInitializingSingletonLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：`SmartInitializingSingleton` 为什么要等所有单例都创建完？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansSmartInitializingSingletonLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | `SmartInitializingSingleton` 为什么要等所有单例都创建完？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansSmartInitializingSingletonLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | preInstantiateSingletons 结束后的 afterSingletonsInstantiated 回调窗口。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [bean-creation-mainline.md](bean-creation-mainline.md)
- [smart-lifecycle.md](smart-lifecycle.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    smart-initializing-singleton.md 的完成标准是：读者能用上面的 Lab 证明“`SmartInitializingSingleton` 为什么要等所有单例都创建完？”，并知道哪些相邻问题应该跳到其他 owner 文档。
