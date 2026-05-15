    # XML BeanDefinitionReader：XML 变成定义
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：XML 如何变成 BeanDefinition？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansXmlBeanDefinitionReaderLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：XmlBeanDefinitionReader 读取资源、解析 <bean> 并注册 BeanDefinition 的非 AOT 基线。
        主线位置：值解析、转换与外部输入。
        对照入口：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：XML 如何变成 BeanDefinition？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansXmlBeanDefinitionReaderLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | XML 如何变成 BeanDefinition？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansXmlBeanDefinitionReaderLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | XmlBeanDefinitionReader 读取资源、解析 <bean> 并注册 BeanDefinition 的非 AOT 基线。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [xml-namespace-extension.md](xml-namespace-extension.md)
- [aot-xml-bean-definition-reader.md](aot-xml-bean-definition-reader.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    xml-bean-definition-reader.md 的完成标准是：读者能用上面的 Lab 证明“XML 如何变成 BeanDefinition？”，并知道哪些相邻问题应该跳到其他 owner 文档。
