    # 方法注入：lookup-method 与 replaced-method
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：lookup-method / replaced-method 解决的是什么动态取对象问题？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansReplacedMethodLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：lookup-method/replaced-method 的动态方法替换语义和 Bean 创建时增强边界。
        主线位置：值解析、转换与外部输入。
        对照入口：`SpringCoreBeansReplacedMethodLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：lookup-method / replaced-method 解决的是什么动态取对象问题？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansReplacedMethodLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | lookup-method / replaced-method 解决的是什么动态取对象问题？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansReplacedMethodLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | lookup-method/replaced-method 的动态方法替换语义和 Bean 创建时增强边界。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [configuration-and-bean-method.md](configuration-and-bean-method.md)
- [aot-method-injection.md](aot-method-injection.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    method-injection.md 的完成标准是：读者能用上面的 Lab 证明“lookup-method / replaced-method 解决的是什么动态取对象问题？”，并知道哪些相邻问题应该跳到其他 owner 文档。
