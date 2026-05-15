    # FactoryBean 类型匹配：getObjectType 的边界
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：FactoryBean 的类型匹配边界在哪里，`getObjectType()` 为什么关键？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansFactoryBeanDeepDiveLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：getObjectType、allowEagerInit、isSingleton 和按类型发现 FactoryBean product 的边界。
        主线位置：生命周期、Scope 与代理边界。
        对照入口：`SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：FactoryBean 的类型匹配边界在哪里，`getObjectType()` 为什么关键？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansFactoryBeanDeepDiveLabTest`
- `SpringCoreBeansFactoryBeanEdgeCasesLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | FactoryBean 的类型匹配边界在哪里，`getObjectType()` 为什么关键？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | getObjectType、allowEagerInit、isSingleton 和按类型发现 FactoryBean product 的边界。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [factorybean.md](factorybean.md)
- [autowire-candidate-selection.md](autowire-candidate-selection.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    factorybean-type-matching.md 的完成标准是：读者能用上面的 Lab 证明“FactoryBean 的类型匹配边界在哪里，`getObjectType()` 为什么关键？”，并知道哪些相邻问题应该跳到其他 owner 文档。
