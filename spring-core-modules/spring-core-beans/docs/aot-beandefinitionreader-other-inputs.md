    # AOT 其他 BeanDefinitionReader 输入
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：Properties / Groovy 等输入在 AOT 下有哪些边界？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPropertiesBeanDefinitionReaderLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：Properties/Groovy Reader 在构建期资源、脚本和动态特性上的约束。
        主线位置：AOT / Native。
        对照入口：`SpringCoreBeansPropertiesBeanDefinitionReaderLabTest` / `SpringCoreBeansGroovyBeanDefinitionReaderLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：Properties / Groovy 等输入在 AOT 下有哪些边界？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`
- `SpringCoreBeansGroovyBeanDefinitionReaderLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | Properties / Groovy 等输入在 AOT 下有哪些边界？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest` / `SpringCoreBeansGroovyBeanDefinitionReaderLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | Properties/Groovy Reader 在构建期资源、脚本和动态特性上的约束。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [properties-and-groovy-reader.md](properties-and-groovy-reader.md)
- [aot-runtimehints.md](aot-runtimehints.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    aot-beandefinitionreader-other-inputs.md 的完成标准是：读者能用上面的 Lab 证明“Properties / Groovy 等输入在 AOT 下有哪些边界？”，并知道哪些相邻问题应该跳到其他 owner 文档。
