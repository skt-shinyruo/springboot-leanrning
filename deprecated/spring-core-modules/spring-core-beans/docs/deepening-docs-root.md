    # Deepening：docs 根目录维护说明

    ## 维护问题

    docs/ 扁平目录、README 顺序来源和链接检查的维护规则。

    ## 控制面

    | 对象 | 维护动作 |
    | --- | --- |
    | [appendix-knowledge-map.md](appendix-knowledge-map.md) | 需要同步检查的文档 |
| [guide-breakpoint-map.md](guide-breakpoint-map.md) | 需要同步检查的文档 |
| [appendix-self-check.md](appendix-self-check.md) | 需要同步检查的文档 |
    | [appendix-knowledge-map.md](appendix-knowledge-map.md) | owner 归属变更时同步更新 |

    ## 风险链接

    - 指向旧 `ioc-*`、`wiring-*`、`internals-*` 主文档名的链接。
    - 在支持页中扩写主文档已经负责的问题。
    - 引用不存在的 `SpringCoreBeans*Test`。

    ## 验证

    - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test`
    - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansModuleContractLabTest test`
