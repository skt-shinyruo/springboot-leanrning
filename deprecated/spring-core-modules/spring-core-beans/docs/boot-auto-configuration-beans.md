    # Boot Auto-Configuration Bean：出现与 backoff
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：Boot 自动装配如何决定一个 Bean 出现还是退回 backoff？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansAutoConfigurationLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：@ConditionalOnMissingBean、backoff、用户定义 Bean 和 ConditionEvaluationReport 的证据链。
        主线位置：Boot 叠加后的变化。
        对照入口：`SpringCoreBeansAutoConfigurationLabTest` / `SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationOverrideMatrixLabTest` / `SpringCoreBeansConditionEvaluationReportLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：Boot 自动装配如何决定一个 Bean 出现还是退回 backoff？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansAutoConfigurationLabTest`
- `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`
- `SpringCoreBeansAutoConfigurationOverrideMatrixLabTest`
- `SpringCoreBeansConditionEvaluationReportLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | Boot 自动装配如何决定一个 Bean 出现还是退回 backoff？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansAutoConfigurationLabTest` / `SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationOverrideMatrixLabTest` / `SpringCoreBeansConditionEvaluationReportLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | @ConditionalOnMissingBean、backoff、用户定义 Bean 和 ConditionEvaluationReport 的证据链。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [boot-auto-configuration-ordering.md](boot-auto-configuration-ordering.md)
- [boot-debugging-and-observability.md](boot-debugging-and-observability.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    boot-auto-configuration-beans.md 的完成标准是：读者能用上面的 Lab 证明“Boot 自动装配如何决定一个 Bean 出现还是退回 backoff？”，并知道哪些相邻问题应该跳到其他 owner 文档。
