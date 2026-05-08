    # Bean 心智模型：定义、实例、缓存与最终暴露对象
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：Bean、BeanDefinition、单例缓存、最终暴露对象分别是什么关系？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：定义元数据、运行时实例、singleton 缓存、暴露给调用方的对象之间的边界。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansBeanGraphDebugLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：Bean、BeanDefinition、单例缓存、最终暴露对象分别是什么关系？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansContainerLabTest`
- `SpringCoreBeansBeanGraphDebugLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | Bean、BeanDefinition、单例缓存、最终暴露对象分别是什么关系？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansContainerLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | 定义元数据、运行时实例、singleton 缓存、暴露给调用方的对象之间的边界。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [bean-definition-registration.md](bean-definition-registration.md)
- [bean-creation-mainline.md](bean-creation-mainline.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    bean-mental-model.md 的完成标准是：读者能用上面的 Lab 证明“Bean、BeanDefinition、单例缓存、最终暴露对象分别是什么关系？”，并知道哪些相邻问题应该跳到其他 owner 文档。
