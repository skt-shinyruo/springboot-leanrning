    # FactoryBean：产品对象与工厂对象
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：FactoryBean 的产品对象和工厂对象如何区分？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansFactoryBeanDeepDiveLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：getBean(name) 返回 product、getBean(&name) 返回 factory 和产品缓存语义。
        主线位置：生命周期、Scope 与代理边界。
        对照入口：`SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：FactoryBean 的产品对象和工厂对象如何区分？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansFactoryBeanDeepDiveLabTest`
- `SpringCoreBeansServiceLoaderFactoryBeansLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | FactoryBean 的产品对象和工厂对象如何区分？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | getBean(name) 返回 product、getBean(&name) 返回 factory 和产品缓存语义。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [factorybean-type-matching.md](factorybean-type-matching.md)
- [bean-name-and-alias.md](bean-name-and-alias.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    factorybean.md 的完成标准是：读者能用上面的 Lab 证明“FactoryBean 的产品对象和工厂对象如何区分？”，并知道哪些相邻问题应该跳到其他 owner 文档。
