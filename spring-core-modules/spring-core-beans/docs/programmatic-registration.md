    # 编程式注册：定义层 API 与实例层 API
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：`registerBeanDefinition`、`registerBean`、`registerSingleton` 的根本差异是什么？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansProgrammaticRegistrationLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：registerBeanDefinition/registerBean 的定义层契约与 registerSingleton 的实例层缓存契约。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansProgrammaticRegistrationLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：`registerBeanDefinition`、`registerBean`、`registerSingleton` 的根本差异是什么？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansProgrammaticRegistrationLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | `registerBeanDefinition`、`registerBean`、`registerSingleton` 的根本差异是什么？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansProgrammaticRegistrationLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | registerBeanDefinition/registerBean 的定义层契约与 registerSingleton 的实例层缓存契约。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [bean-definition-registration.md](bean-definition-registration.md)
- [bean-creation-mainline.md](bean-creation-mainline.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    programmatic-registration.md 的完成标准是：读者能用上面的 Lab 证明“`registerBeanDefinition`、`registerBean`、`registerSingleton` 的根本差异是什么？”，并知道哪些相邻问题应该跳到其他 owner 文档。
