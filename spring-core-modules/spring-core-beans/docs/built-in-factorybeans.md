    # 内置 FactoryBean：常见工厂形态
    <!-- CHAPTER-CARD:START -->
    !!! summary "章节入口"
        - 这一页只回答：Spring 内置 FactoryBean 的常见形态有哪些？
        - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBuiltInFactoryBeansLabTest test`
        - 相邻主题只做跳转，不在本页重复展开。

        观察对象：ServiceLoaderFactoryBean 等内置 FactoryBean 形态和 product 暴露边界。
        主线位置：值解析、转换与外部输入。
        对照入口：`SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest`。
    <!-- CHAPTER-CARD:END -->

    ## 归属边界

    这一页只回答一个问题：Spring 内置 FactoryBean 的常见形态有哪些？

    本页负责把这个问题收束到一个可运行证据入口。相邻主题只在“相邻跳转”中出现，避免同一个知识点散落到多个文件。

    ## 最短证据入口

    - `SpringCoreBeansBuiltInFactoryBeansLabTest`
- `SpringCoreBeansServiceLoaderFactoryBeansLabTest`

    ## 观察口径

    | 观察点 | 看什么 | 不在这里展开 |
    | --- | --- | --- |
    | 问题定位 | Spring 内置 FactoryBean 的常见形态有哪些？ | 支持页只负责导航 |
    | 运行证据 | `SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest` | 不用未验证的口头结论替代 Lab |
    | 边界判断 | ServiceLoaderFactoryBean 等内置 FactoryBean 形态和 product 暴露边界。 | 相邻 owner 文档另行负责 |

    ## 相邻跳转

    - [factorybean.md](factorybean.md)
- [aot-built-in-factorybeans.md](aot-built-in-factorybeans.md)
- [appendix-knowledge-map.md](appendix-knowledge-map.md)

    ## 小结

    built-in-factorybeans.md 的完成标准是：读者能用上面的 Lab 证明“Spring 内置 FactoryBean 的常见形态有哪些？”，并知道哪些相邻问题应该跳到其他 owner 文档。
