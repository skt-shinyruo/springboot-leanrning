    # Properties 与 Groovy Reader：其他定义输入
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：Properties / Groovy 这类输入如何变成 BeanDefinition？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPropertiesBeanDefinitionReaderLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：PropertiesBeanDefinitionReader 与 GroovyBeanDefinitionReader 的输入格式和定义注册边界。
        主线位置：值解析、转换与外部输入。
        对照入口：`SpringCoreBeansPropertiesBeanDefinitionReaderLabTest` / `SpringCoreBeansGroovyBeanDefinitionReaderLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：Properties / Groovy 这类输入如何变成 BeanDefinition？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`
- `SpringCoreBeansGroovyBeanDefinitionReaderLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | Properties / Groovy 这类输入如何变成 BeanDefinition？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest` / `SpringCoreBeansGroovyBeanDefinitionReaderLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | PropertiesBeanDefinitionReader 与 GroovyBeanDefinitionReader 的输入格式和定义注册边界。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [bean-definition-registration.md](bean-definition-registration.md)
- [aot-beandefinitionreader-other-inputs.md](aot-beandefinitionreader-other-inputs.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    properties-and-groovy-reader.md 的完成标准是：读者能用上面的 Lab 证明“Properties / Groovy 这类输入如何变成 BeanDefinition？”，并知道哪些相邻问题应该跳到其他 owner 文档。
