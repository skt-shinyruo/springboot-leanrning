    # AOT 自定义 Qualifier
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：自定义 Qualifier 在 AOT 下要补什么契约？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansCustomQualifierLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：自定义 qualifier meta-annotation 在候选选择和 native 可达性中的约束。
        主线位置：AOT / Native。
        对照入口：`SpringCoreBeansCustomQualifierLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：自定义 Qualifier 在 AOT 下要补什么契约？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansCustomQualifierLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | 自定义 Qualifier 在 AOT 下要补什么契约？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansCustomQualifierLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | 自定义 qualifier meta-annotation 在候选选择和 native 可达性中的约束。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [qualifier-primary-priority-order.md](qualifier-primary-priority-order.md)
- [aot-runtimehints.md](aot-runtimehints.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    aot-custom-qualifier.md 的完成标准是：读者能用上面的 Lab 证明“自定义 Qualifier 在 AOT 下要补什么契约？”，并知道哪些相邻问题应该跳到其他 owner 文档。
