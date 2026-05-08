    # Bean 创建主线：doGetBean 到 doCreateBean
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：`doGetBean()` / `doCreateBean()` 的主线是什么？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanCreationTraceLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：依赖解析、实例化、属性填充、初始化、最终暴露对象的单 Bean 创建路径。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansBeanCreationTraceLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：`doGetBean()` / `doCreateBean()` 的主线是什么？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansBeanCreationTraceLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | `doGetBean()` / `doCreateBean()` 的主线是什么？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansBeanCreationTraceLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | 依赖解析、实例化、属性填充、初始化、最终暴露对象的单 Bean 创建路径。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [refresh-mainline.md](refresh-mainline.md)
- [dependency-injection-resolution.md](dependency-injection-resolution.md)
- [proxying-phase.md](proxying-phase.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    bean-creation-mainline.md 的完成标准是：读者能用上面的 Lab 证明“`doGetBean()` / `doCreateBean()` 的主线是什么？”，并知道哪些相邻问题应该跳到其他 owner 文档。
