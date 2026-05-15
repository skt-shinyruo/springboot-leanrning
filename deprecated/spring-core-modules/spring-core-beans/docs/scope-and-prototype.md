    # Scope 与 prototype：对象复用和销毁边界
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：singleton、prototype、其他 scope 的行为边界是什么？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：singleton/prototype 的创建、缓存、销毁托管和自定义 scope 的相邻边界。
        主线位置：生命周期、Scope 与代理边界。
        对照入口：`SpringCoreBeansLabTest` / `SpringCoreBeansPrototypeDestroySemanticsLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：singleton、prototype、其他 scope 的行为边界是什么？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansLabTest`
- `SpringCoreBeansPrototypeDestroySemanticsLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | singleton、prototype、其他 scope 的行为边界是什么？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansLabTest` / `SpringCoreBeansPrototypeDestroySemanticsLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | singleton/prototype 的创建、缓存、销毁托管和自定义 scope 的相邻边界。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [custom-scope-and-scoped-proxy.md](custom-scope-and-scoped-proxy.md)
- [lifecycle-callbacks.md](lifecycle-callbacks.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    scope-and-prototype.md 的完成标准是：读者能用上面的 Lab 证明“singleton、prototype、其他 scope 的行为边界是什么？”，并知道哪些相邻问题应该跳到其他 owner 文档。
