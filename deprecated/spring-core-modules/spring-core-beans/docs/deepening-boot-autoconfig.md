    # Deepening：Boot Auto-Config 维护边界

    ## 维护问题

    Boot owner 页与 Boot 支持页的分工。

    ## 控制面

    | 对象 | 维护动作 |
    | --- | --- |
    | [boot-auto-configuration-ordering.md](boot-auto-configuration-ordering.md) | 需要同步检查的文档 |
| [boot-auto-configuration-beans.md](boot-auto-configuration-beans.md) | 需要同步检查的文档 |
| [boot-debugging-and-observability.md](boot-debugging-and-observability.md) | 需要同步检查的文档 |
    | [appendix-knowledge-map.md](appendix-knowledge-map.md) | owner 归属变更时同步更新 |

    ## 风险链接

    - 指向旧 `ioc-*`、`wiring-*`、`internals-*` 主文档名的链接。
    - 在支持页中扩写主文档已经负责的问题。
    - 引用不存在的 `SpringCoreBeans*Test`。

    ## 验证

    - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test`
    - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansModuleContractLabTest test`
