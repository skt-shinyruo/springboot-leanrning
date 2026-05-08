    # XML Namespace 扩展：自定义标签到定义
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：XML namespace 扩展如何把自定义标签变成定义？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansXmlNamespaceExtensionLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：NamespaceHandler、BeanDefinitionParser、spring.handlers/spring.schemas 如何扩展 XML 定义解析。
        主线位置：值解析、转换与外部输入。
        对照入口：`SpringCoreBeansXmlNamespaceExtensionLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：XML namespace 扩展如何把自定义标签变成定义？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansXmlNamespaceExtensionLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | XML namespace 扩展如何把自定义标签变成定义？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansXmlNamespaceExtensionLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | NamespaceHandler、BeanDefinitionParser、spring.handlers/spring.schemas 如何扩展 XML 定义解析。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [xml-bean-definition-reader.md](xml-bean-definition-reader.md)
- [aot-xml-namespace-extension.md](aot-xml-namespace-extension.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    xml-namespace-extension.md 的完成标准是：读者能用上面的 Lab 证明“XML namespace 扩展如何把自定义标签变成定义？”，并知道哪些相邻问题应该跳到其他 owner 文档。
