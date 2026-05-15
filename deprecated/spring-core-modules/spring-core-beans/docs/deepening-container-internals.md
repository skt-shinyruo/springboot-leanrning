    # Deepening：容器内部主线维护边界

    ## 维护问题

    refresh、创建、后处理器和循环依赖文档的归属维护。

    ## 控制面

    | 对象 | 维护动作 |
    | --- | --- |
    | [refresh-mainline.md](refresh-mainline.md) | 需要同步检查的文档 |
| [bean-creation-mainline.md](bean-creation-mainline.md) | 需要同步检查的文档 |
| [post-processors-overview.md](post-processors-overview.md) | 需要同步检查的文档 |
    | [appendix-knowledge-map.md](appendix-knowledge-map.md) | owner 归属变更时同步更新 |

    ## 风险链接

    - 指向旧 `ioc-*`、`wiring-*`、`internals-*` 主文档名的链接。
    - 在支持页中扩写主文档已经负责的问题。
    - 引用不存在的 `SpringCoreBeans*Test`。

    ## 验证

    - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test`
    - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansModuleContractLabTest test`
