    # Deepening：Wiring 与边界维护说明

    ## 维护问题

    依赖解析、注入、Scope、FactoryBean 和代理边界如何防止重复。

    ## 控制面

    | 对象 | 维护动作 |
    | --- | --- |
    | [dependency-injection-resolution.md](dependency-injection-resolution.md) | 需要同步检查的文档 |
| [autowire-candidate-selection.md](autowire-candidate-selection.md) | 需要同步检查的文档 |
| [factorybean-type-matching.md](factorybean-type-matching.md) | 需要同步检查的文档 |
    | [appendix-knowledge-map.md](appendix-knowledge-map.md) | owner 归属变更时同步更新 |

    ## 风险链接

    - 指向旧 `ioc-*`、`wiring-*`、`internals-*` 主文档名的链接。
    - 在支持页中扩写主文档已经负责的问题。
    - 引用不存在的 `SpringCoreBeans*Test`。

    ## 验证

    - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test`
    - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansModuleContractLabTest test`
