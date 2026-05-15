    # 候选 Bean 选择：收集、筛选、收敛
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：候选 bean 是如何被收集、筛选、收敛的？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansAutowireCandidateSelectionLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：按类型收集候选、autowireCandidate 筛选、primary/priority/name 等收敛步骤。
        主线位置：依赖解析与注入。
        对照入口：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansAutowireCandidateSelectionExerciseTest` / `SpringCoreBeansAutowireCandidateSelectionExerciseSolutionTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：候选 bean 是如何被收集、筛选、收敛的？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansAutowireCandidateSelectionLabTest`
- `SpringCoreBeansAutowireCandidateSelectionExerciseTest`
- `SpringCoreBeansAutowireCandidateSelectionExerciseSolutionTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | 候选 bean 是如何被收集、筛选、收敛的？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansAutowireCandidateSelectionExerciseTest` / `SpringCoreBeansAutowireCandidateSelectionExerciseSolutionTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | 按类型收集候选、autowireCandidate 筛选、primary/priority/name 等收敛步骤。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [qualifier-primary-priority-order.md](qualifier-primary-priority-order.md)
- [bean-definition-metadata-and-origin.md](bean-definition-metadata-and-origin.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    autowire-candidate-selection.md 的完成标准是：读者能用上面的 Lab 证明“候选 bean 是如何被收集、筛选、收敛的？”，并知道哪些相邻问题应该跳到其他 owner 文档。
