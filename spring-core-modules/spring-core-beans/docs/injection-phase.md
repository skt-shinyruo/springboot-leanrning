    # 注入阶段：字段、构造器与属性填充窗口
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：field injection 与 constructor injection 处在什么阶段，观察点有什么不同？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansInjectionPhaseLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：构造器参数解析、populateBean 属性填充、字段/方法注入 BPP 的不同窗口。
        主线位置：依赖解析与注入。
        对照入口：`SpringCoreBeansInjectionPhaseLabTest` / `SpringCoreBeansInjectionPhaseMatrixLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：field injection 与 constructor injection 处在什么阶段，观察点有什么不同？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansInjectionPhaseLabTest`
- `SpringCoreBeansInjectionPhaseMatrixLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | field injection 与 constructor injection 处在什么阶段，观察点有什么不同？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansInjectionPhaseLabTest` / `SpringCoreBeansInjectionPhaseMatrixLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | 构造器参数解析、populateBean 属性填充、字段/方法注入 BPP 的不同窗口。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [dependency-injection-resolution.md](dependency-injection-resolution.md)
- [bean-creation-mainline.md](bean-creation-mainline.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    injection-phase.md 的完成标准是：读者能用上面的 Lab 证明“field injection 与 constructor injection 处在什么阶段，观察点有什么不同？”，并知道哪些相邻问题应该跳到其他 owner 文档。
