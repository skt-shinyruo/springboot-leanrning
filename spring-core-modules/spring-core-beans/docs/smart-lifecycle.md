    # SmartLifecycle：start/stop 与 phase
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：`SmartLifecycle` 的 start/stop 与 phase 顺序如何工作？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansSmartLifecycleLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：LifecycleProcessor、autoStartup、phase 顺序和 stop 回调。
        主线位置：生命周期、Scope 与代理边界。
        对照入口：`SpringCoreBeansSmartLifecycleLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：`SmartLifecycle` 的 start/stop 与 phase 顺序如何工作？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansSmartLifecycleLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | `SmartLifecycle` 的 start/stop 与 phase 顺序如何工作？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansSmartLifecycleLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | LifecycleProcessor、autoStartup、phase 顺序和 stop 回调。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [depends-on.md](depends-on.md)
- [lifecycle-callbacks.md](lifecycle-callbacks.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    smart-lifecycle.md 的完成标准是：读者能用上面的 Lab 证明“`SmartLifecycle` 的 start/stop 与 phase 顺序如何工作？”，并知道哪些相邻问题应该跳到其他 owner 文档。
