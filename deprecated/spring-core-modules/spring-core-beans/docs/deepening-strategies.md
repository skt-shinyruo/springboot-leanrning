    # Deepening：维护策略总览

    ## 维护问题

    长期维护 owner、support 和 Lab 证据链的策略。

    ## 控制面

    | 对象 | 维护动作 |
    | --- | --- |
    | [appendix-knowledge-map.md](appendix-knowledge-map.md) | 需要同步检查的文档 |
| `SpringCoreBeansModuleContractLabTest` | 需要运行或保留的契约 |
| [guide-breakpoint-map.md](guide-breakpoint-map.md) | 需要同步检查的文档 |
    | [appendix-knowledge-map.md](appendix-knowledge-map.md) | owner 归属变更时同步更新 |

    ## 风险链接

    - 指向旧 `ioc-*`、`wiring-*`、`internals-*` 主文档名的链接。
    - 在支持页中扩写主文档已经负责的问题。
    - 引用不存在的 `SpringCoreBeans*Test`。

    ## 验证

    - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test`
    - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansModuleContractLabTest test`
