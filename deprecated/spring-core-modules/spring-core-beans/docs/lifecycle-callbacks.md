    # 生命周期回调：Aware、init、destroy 顺序
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：Aware、init、destroy、`@PostConstruct` 的顺序如何理解？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLifecycleCallbackOrderLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：Aware、BeanPostProcessor before/after init、InitializingBean、init-method、destroy 的调用顺序。
        主线位置：生命周期、Scope 与代理边界。
        对照入口：`SpringCoreBeansLifecycleCallbackOrderLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：Aware、init、destroy、`@PostConstruct` 的顺序如何理解？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansLifecycleCallbackOrderLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | Aware、init、destroy、`@PostConstruct` 的顺序如何理解？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansLifecycleCallbackOrderLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | Aware、BeanPostProcessor before/after init、InitializingBean、init-method、destroy 的调用顺序。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [bean-creation-mainline.md](bean-creation-mainline.md)
- [scope-and-prototype.md](scope-and-prototype.md)
- [beanpost-processors.md](beanpost-processors.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    lifecycle-callbacks.md 的完成标准是：读者能用上面的 Lab 证明“Aware、init、destroy、`@PostConstruct` 的顺序如何理解？”，并知道哪些相邻问题应该跳到其他 owner 文档。
