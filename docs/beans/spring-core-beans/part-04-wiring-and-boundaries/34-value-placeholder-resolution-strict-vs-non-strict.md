# 34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast

## 导读

- 本章主题：**34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansValuePlaceholderResolutionLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`

## 机制主线

这一章回答一个很折磨人的问题：

> 为什么我写了 `@Value("${demo.missing}")`，应用居然没启动失败？  
> 注入进来的值甚至变成了字符串 `"${demo.missing}"`？

答案是：**占位符解析不是“固定严格”的；它取决于容器里安装的 value resolver 是 non-strict 还是 strict。**

建议直接跑：

## 1. 先拆开机制：`@Value` 不是“直接读 Environment”

`@Value` 的解析链路大致可以这样理解：

1) `@Value` 注解被一个基础设施处理器识别（通常是 `AutowiredAnnotationBeanPostProcessor`）
2) 它把注解里的字符串（例如 `"${demo.present}"`）交给 `BeanFactory` 做 embedded value 解析
3) 解析出来的字符串再做类型转换，注入到字段/参数里

也就是说：`@Value` 是否“严格”，取决于 **BeanFactory 里安装的 embedded value resolver**。

对照阅读（为什么注解能生效）：  
- [12. 容器启动与基础设施处理器：为什么注解能工作？](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)

## 2. 默认行为：Environment resolver（non-strict）可能让缺失占位符“悄悄通过”

在纯 Spring 容器里（不额外注册 placeholder configurer），`AbstractApplicationContext` 在 refresh 的后期会确保 BeanFactory 至少有一个 resolver：

- 如果还没有 resolver，就把一个 “Environment placeholder resolver” 装进去
- 它本质上是：`Environment.resolvePlaceholders(...)`

关键点：`resolvePlaceholders` 默认是 **non-strict** 的：

- 能解析到值就替换
- 解析不到的占位符可能保留为原样字符串 `"${...}"`

- `demo.present` 存在 ⇒ `@Value("${demo.present}")` 注入为 `"hello"`
- `demo.missing` 缺失 ⇒ `@Value("${demo.missing}")` 注入为 `"${demo.missing}"`（没有 fail-fast）

## 3. 变成 strict：注册 `PropertySourcesPlaceholderConfigurer`（一个 BFPP）

如果你希望 “占位符缺失就立刻失败”，常见做法是注册：

- `PropertySourcesPlaceholderConfigurer`

- `ignoreUnresolvablePlaceholders = false`

因此当 `demo.missing` 缺失时：

- refresh 直接失败（root cause 通常是 `IllegalArgumentException: Could not resolve placeholder ...`）

对照阅读（BFPP vs BPP，谁更早）：  
- [06. 容器扩展点：BFPP vs BPP](../part-01-ioc-container/017-06-post-processors.md)

当你遇到 `@Value` 解析异常或“没报错但值不对”时，建议按这个顺序：

1) 看注入进来的值到底是什么（是不是 `"${...}"` 原样）
2) 确认容器里是否注册了 placeholder configurer（strict/non-strict）
3) 确认 property source 是否真的包含该 key（以及它的优先级/覆盖关系）

延伸阅读：

- `AutowiredAnnotationBeanPostProcessor#postProcessProperties`：`@Value` 注入发生的入口之一（它会把字符串交给 BeanFactory 解析）
- `AbstractBeanFactory#resolveEmbeddedValue`：embedded value resolver 的执行点（non-strict/strict 的差异最终会体现在这里）
- `AbstractApplicationContext#prepareBeanFactory`：容器在 refresh 过程中安装默认 embedded value resolver 的常见位置
- `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`：strict 行为的典型来源（作为 BFPP 提前介入并配置解析器）
- `Environment#resolvePlaceholders`：默认 non-strict 解析入口（缺失时可能保留 `"${...}"`）

入口：

1) `AbstractApplicationContext#prepareBeanFactory`：观察默认 embedded resolver 是何时被装入 BeanFactory 的
2) `AbstractBeanFactory#resolveEmbeddedValue`：对照 `demo.present` 与 `demo.missing`，观察 non-strict 下为什么会返回原样 `"${...}"`
3) `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`：对照 strict 场景，观察它如何改变解析行为（并导致缺失占位符 fail-fast）
4) `AutowiredAnnotationBeanPostProcessor#postProcessProperties`：观察最终注入点拿到的是“解析后的字符串”还是“原样占位符”

## 排障分流：这是定义层问题还是实例层问题？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansValuePlaceholderResolutionLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansValuePlaceholderResolutionLabTest test`

对应实验（可运行 + 可断言）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`

```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansValuePlaceholderResolutionLabTest test
```

这就解释了 Lab 里的现象：

它是一个 **BeanFactoryPostProcessor（BFPP）**，会在 bean 实例化之前运行，并把更严格的 placeholder 解析能力装进 BeanFactory。

Lab 里我们显式设置：

## 4. Debug / 观察建议

- 不要把 `@Value("${missing}")` 的“默认行为”想成永远 fail-fast：它取决于 resolver
- 如果你希望配置缺失能尽早暴露：考虑启用 strict placeholder（Lab 已演示）
- 如果你在 Boot 应用里看到行为不同：优先把场景缩小到“纯容器”来验证机制，再回到 Boot 叠加条件（参考本模块的调试章节）

- 调试与自检：如何“看见”容器正在做什么：[11. Debugging and Observability](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)

## 源码锚点（建议从这里下断点）

- `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`：占位符解析的 BFPP 入口（定义层改写）
- `PlaceholderConfigurerSupport#doProcessProperties`：遍历并替换 `${...}` 的主流程
- `AbstractBeanFactory#resolveEmbeddedValue`：运行期解析 embedded value 的入口（与定义层改写对照）
- `AbstractBeanFactory#resolveBeanClass` / `BeanDefinitionVisitor`：占位符替换发生在“定义层”的典型路径

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`

建议断点：

- “`@Value("${missing}")` 没失败，值变成原样字符串” → **优先定义层（resolver 语义）**：当前容器可能只装了 non-strict resolver（本章第 2 节）
- “启用 strict 后启动直接失败” → **定义层（BFPP 提前失败）**：`PropertySourcesPlaceholderConfigurer` 会在实例化前就 fail-fast（本章第 3 节）
- “`@Value` 完全不生效/字段没被注入” → **优先定义层/基础设施问题**：是否具备注解处理能力？（回看 [12](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)）
- “值不对但不报错” → **先拆分路径**：确认 property source 是否包含 key，再确认 strict/non-strict（本章第 4 节 + `resolveEmbeddedValue`）
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`
推荐断点：`PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`、`AbstractBeanFactory#resolveEmbeddedValue`、`AutowiredAnnotationBeanPostProcessor#postProcessProperties`

## 常见坑与边界

## 5. 常见坑与实践建议

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansValuePlaceholderResolutionLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`

上一章：[33. 候选选择与优先级：@Primary/@Priority/@Order 的边界](33-autowire-candidate-selection-primary-priority-order.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[35. MergedBeanDefinition：合并后的 RootBeanDefinition](35-merged-bean-definition.md)

<!-- BOOKIFY:END -->
