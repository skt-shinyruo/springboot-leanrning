    # @Resource 与 @Autowired：name-first vs by-type
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：`@Resource` 的 name-first 与 `@Autowired` 的 by-type 有何本质差异？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansResourceInjectionLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：@Resource 的名称优先解析与 @Autowired 类型优先解析的排障分流。
        主线位置：依赖解析与注入。
        对照入口：`SpringCoreBeansResourceInjectionLabTest` / `SpringCoreBeansResourceResolutionLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：`@Resource` 的 name-first 与 `@Autowired` 的 by-type 有何本质差异？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansResourceInjectionLabTest`
- `SpringCoreBeansResourceResolutionLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | `@Resource` 的 name-first 与 `@Autowired` 的 by-type 有何本质差异？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansResourceInjectionLabTest` / `SpringCoreBeansResourceResolutionLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | @Resource 的名称优先解析与 @Autowired 类型优先解析的排障分流。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [bean-name-and-alias.md](bean-name-and-alias.md)
- [dependency-injection-resolution.md](dependency-injection-resolution.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    resource-vs-autowired.md 的完成标准是：读者能用上面的 Lab 证明“`@Resource` 的 name-first 与 `@Autowired` 的 by-type 有何本质差异？”，并知道哪些相邻问题应该跳到其他 owner 文档。
