    # Context Hierarchy：父子容器可见性
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：父子 ApplicationContext 的可见性和覆盖边界是什么？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContextHierarchyLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：child 查 parent、parent 不查 child、同名覆盖只影响当前层级的查找规则。
        主线位置：生命周期、Scope 与代理边界。
        对照入口：`SpringCoreBeansContextHierarchyLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：父子 ApplicationContext 的可见性和覆盖边界是什么？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansContextHierarchyLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | 父子 ApplicationContext 的可见性和覆盖边界是什么？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansContextHierarchyLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | child 查 parent、parent 不查 child、同名覆盖只影响当前层级的查找规则。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [beanfactory-vs-applicationcontext.md](beanfactory-vs-applicationcontext.md)
- [bean-definition-overriding.md](bean-definition-overriding.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    context-hierarchy.md 的完成标准是：读者能用上面的 Lab 证明“父子 ApplicationContext 的可见性和覆盖边界是什么？”，并知道哪些相邻问题应该跳到其他 owner 文档。
