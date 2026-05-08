    # 代理发生阶段：BPP 包装最终对象
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：BPP 在哪个窗口把 bean 包装成 proxy，自调用为什么绕过它？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansProxyingPhaseLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：BPP after-initialization 代理替换、自调用绕过代理和 early reference 交叉窗口。
        主线位置：生命周期、Scope 与代理边界。
        对照入口：`SpringCoreBeansProxyingPhaseLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：BPP 在哪个窗口把 bean 包装成 proxy，自调用为什么绕过它？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansProxyingPhaseLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | BPP 在哪个窗口把 bean 包装成 proxy，自调用为什么绕过它？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansProxyingPhaseLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | BPP after-initialization 代理替换、自调用绕过代理和 early reference 交叉窗口。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [beanpost-processors.md](beanpost-processors.md)
- [early-reference-and-three-level-cache.md](early-reference-and-three-level-cache.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    proxying-phase.md 的完成标准是：读者能用上面的 Lab 证明“BPP 在哪个窗口把 bean 包装成 proxy，自调用为什么绕过它？”，并知道哪些相邻问题应该跳到其他 owner 文档。
