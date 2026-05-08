    # 类型转换与 BeanWrapper
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：BeanWrapper、ConversionService、PropertyEditor 各负责哪一段？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansTypeConversionLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：BeanWrapper 写属性、ConversionService 转换、PropertyEditor fallback 和解析边界。
        主线位置：值解析、转换与外部输入。
        对照入口：`SpringCoreBeansTypeConversionLabTest` / `SpringCoreBeansPropertyEditorLabTest` / `SpringCoreBeansPropertyEditorResolutionLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：BeanWrapper、ConversionService、PropertyEditor 各负责哪一段？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansTypeConversionLabTest`
- `SpringCoreBeansPropertyEditorLabTest`
- `SpringCoreBeansPropertyEditorResolutionLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | BeanWrapper、ConversionService、PropertyEditor 各负责哪一段？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansTypeConversionLabTest` / `SpringCoreBeansPropertyEditorLabTest` / `SpringCoreBeansPropertyEditorResolutionLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | BeanWrapper 写属性、ConversionService 转换、PropertyEditor fallback 和解析边界。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [value-placeholder-resolution.md](value-placeholder-resolution.md)
- [aot-property-editor-and-value-resolution.md](aot-property-editor-and-value-resolution.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    type-conversion-and-beanwrapper.md 的完成标准是：读者能用上面的 Lab 证明“BeanWrapper、ConversionService、PropertyEditor 各负责哪一段？”，并知道哪些相邻问题应该跳到其他 owner 文档。
