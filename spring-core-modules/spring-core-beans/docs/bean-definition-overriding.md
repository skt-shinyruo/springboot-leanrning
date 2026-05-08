    # BeanDefinition 覆盖：同名定义谁生效
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：同名 BeanDefinition 冲突时，谁生效、谁失败、什么时候失败？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanDefinitionOverridingLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：allowBeanDefinitionOverriding 开关、last-wins 与 fail-fast 的定义层行为。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansBeanDefinitionOverridingLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：同名 BeanDefinition 冲突时，谁生效、谁失败、什么时候失败？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansBeanDefinitionOverridingLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | 同名 BeanDefinition 冲突时，谁生效、谁失败、什么时候失败？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansBeanDefinitionOverridingLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | allowBeanDefinitionOverriding 开关、last-wins 与 fail-fast 的定义层行为。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [bean-definition-registration.md](bean-definition-registration.md)
- [bean-definition-metadata-and-origin.md](bean-definition-metadata-and-origin.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    bean-definition-overriding.md 的完成标准是：读者能用上面的 Lab 证明“同名 BeanDefinition 冲突时，谁生效、谁失败、什么时候失败？”，并知道哪些相邻问题应该跳到其他 owner 文档。
