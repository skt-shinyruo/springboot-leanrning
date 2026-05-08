    # @Import、ImportSelector 与 Registrar
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：`@Import`、ImportSelector、ImportBeanDefinitionRegistrar 的边界在哪里？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansImportLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：@Import 普通类、ImportSelector 返回类名、ImportBeanDefinitionRegistrar 直接操作 registry 的差异。
        主线位置：容器与注册。
        对照入口：`SpringCoreBeansImportLabTest` / `SpringCoreBeansImportExerciseTest` / `SpringCoreBeansImportExerciseSolutionTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：`@Import`、ImportSelector、ImportBeanDefinitionRegistrar 的边界在哪里？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansImportLabTest`
- `SpringCoreBeansImportExerciseTest`
- `SpringCoreBeansImportExerciseSolutionTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | `@Import`、ImportSelector、ImportBeanDefinitionRegistrar 的边界在哪里？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansImportLabTest` / `SpringCoreBeansImportExerciseTest` / `SpringCoreBeansImportExerciseSolutionTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | @Import 普通类、ImportSelector 返回类名、ImportBeanDefinitionRegistrar 直接操作 registry 的差异。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [bean-definition-registration.md](bean-definition-registration.md)
- [programmatic-registration.md](programmatic-registration.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    import-selector-and-registrar.md 的完成标准是：读者能用上面的 Lab 证明“`@Import`、ImportSelector、ImportBeanDefinitionRegistrar 的边界在哪里？”，并知道哪些相邻问题应该跳到其他 owner 文档。
