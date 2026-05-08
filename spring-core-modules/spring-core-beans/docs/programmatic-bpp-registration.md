    # 手工注册 BPP：绕过排序的边界
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：手工添加 BeanPostProcessor 为什么会绕过容器排序？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansProgrammaticBeanPostProcessorLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：addBeanPostProcessor 的直接注册语义和与容器自动排序注册的差异。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansProgrammaticBeanPostProcessorLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：手工添加 BeanPostProcessor 为什么会绕过容器排序？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | 手工添加 BeanPostProcessor 为什么会绕过容器排序？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansProgrammaticBeanPostProcessorLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | addBeanPostProcessor 的直接注册语义和与容器自动排序注册的差异。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [post-processor-ordering.md](post-processor-ordering.md)
- [beanpost-processors.md](beanpost-processors.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    programmatic-bpp-registration.md 的完成标准是：读者能用上面的 Lab 证明“手工添加 BeanPostProcessor 为什么会绕过容器排序？”，并知道哪些相邻问题应该跳到其他 owner 文档。
