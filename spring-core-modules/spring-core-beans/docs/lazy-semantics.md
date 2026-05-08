    # Lazy 语义：定义延迟与注入点代理
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：lazy-init 与注入点 `@Lazy` 分别延迟了什么？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLazyLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：BeanDefinition lazy-init 与注入点 @Lazy 代理的触发时机差异。
        主线位置：生命周期、Scope 与代理边界。
        对照入口：`SpringCoreBeansLazyLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：lazy-init 与注入点 `@Lazy` 分别延迟了什么？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansLazyLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | lazy-init 与注入点 `@Lazy` 分别延迟了什么？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansLazyLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | BeanDefinition lazy-init 与注入点 @Lazy 代理的触发时机差异。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [optional-and-provider-injection.md](optional-and-provider-injection.md)
- [bean-creation-mainline.md](bean-creation-mainline.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    lazy-semantics.md 的完成标准是：读者能用上面的 Lab 证明“lazy-init 与注入点 `@Lazy` 分别延迟了什么？”，并知道哪些相邻问题应该跳到其他 owner 文档。
