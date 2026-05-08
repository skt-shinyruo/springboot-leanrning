    # Deepening：AOT 与真实项目维护面

    ## 维护问题

    AOT owner 页与真实项目约束如何保持一致。

    ## 控制面

    | 对象 | 维护动作 |
    | --- | --- |
    | [aot-native-overview.md](aot-native-overview.md) | 需要同步检查的文档 |
| [aot-runtimehints.md](aot-runtimehints.md) | 需要同步检查的文档 |
| [aot-built-in-factorybeans.md](aot-built-in-factorybeans.md) | 需要同步检查的文档 |
    | [appendix-knowledge-map.md](appendix-knowledge-map.md) | owner 归属变更时同步更新 |

    ## 风险链接

    - 指向旧 `ioc-*`、`wiring-*`、`internals-*` 主文档名的链接。
    - 在支持页中扩写主文档已经负责的问题。
    - 引用不存在的 `SpringCoreBeans*Test`。

    ## 验证

    - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test`
    - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansModuleContractLabTest test`
