    # BeanFactory vs ApplicationContext：能力边界
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：BeanFactory 与 ApplicationContext 的能力差异是什么？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanFactoryVsApplicationContextLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：容器基础能力、上下文增强能力、事件/资源/环境等 ApplicationContext 叠加职责。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansBeanFactoryVsApplicationContextLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：BeanFactory 与 ApplicationContext 的能力差异是什么？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | BeanFactory 与 ApplicationContext 的能力差异是什么？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | 容器基础能力、上下文增强能力、事件/资源/环境等 ApplicationContext 叠加职责。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [beanfactory-api-and-autowirecapablebeanfactory.md](beanfactory-api-and-autowirecapablebeanfactory.md)
- [refresh-mainline.md](refresh-mainline.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    beanfactory-vs-applicationcontext.md 的完成标准是：读者能用上面的 Lab 证明“BeanFactory 与 ApplicationContext 的能力差异是什么？”，并知道哪些相邻问题应该跳到其他 owner 文档。
