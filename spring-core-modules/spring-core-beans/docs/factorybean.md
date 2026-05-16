# FactoryBean：工厂对象与产品对象的双重身份

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 `FactoryBean` 的双重身份：同一个名字背后既可能是工厂对象，也可能是产品对象。
    - 重点讲 `getBean("x")`、`getBean("&x")`、产品缓存、singleton/prototype 产品语义和生命周期分离。
    - 读完后应该能分清自己取到的是工厂还是产品。

    观察对象：FactoryBean 本身、产品对象和容器缓存。
    主线位置：bean 定义已注册之后、按名字或按类型取值时。
    对照入口：`SpringCoreBeansFactoryBeanDeepDiveLabTest`、`SpringCoreBeansFactoryBeanEdgeCasesLabTest`。
<!-- CHAPTER-CARD:END -->

`FactoryBean` 是 Spring 里最容易让“Bean 就是一个对象实例”这个直觉失效的地方。它让同一个 bean name 同时承担两层身份：

- 容器里有一个工厂 bean。
- 这个工厂 bean 又生产另一个产品对象。

所以，名字相同不等于对象相同。你要先判断自己是在问工厂，还是在问工厂产出的东西。

## `getBean("x")` 和 `getBean("&x")`

默认情况下，`getBean("x")` 取到的是产品对象，不是工厂本身。只有在名字前加 `&` 时，容器才把这个名字解释为“我要工厂对象本身”。

`SpringCoreBeansFactoryBeanDeepDiveLabTest` 直接验证了这一点：

- `context.getBean("valueFactory")` 返回 `Value` 产品。
- `context.getBean("&valueFactory")` 返回 `ValueFactoryBean` 工厂。
- `context.getType("valueFactory")` 看的是产品类型。
- `context.getType("&valueFactory")` 看的是工厂类型。

这也是为什么排障时不能只看 bean name，要先看取值语义。

## 产品缓存与 singleton/prototype 语义

`FactoryBean` 有两套生命周期语义：

1. 工厂对象本身，作为普通 bean 参与容器管理。
2. 产品对象，由 `getObject()` 决定是否缓存、何时创建。

`FactoryBean#isSingleton()` 决定的是**产品**是否按单例缓存，不是工厂 bean 本身是不是单例。`SpringCoreBeansFactoryBeanDeepDiveLabTest` 里，工厂本身默认仍是 singleton；但如果 `isSingleton()` 返回 `false`，容器每次取产品都可能重新调用 `getObject()`，返回新对象。

这说明：

- factory bean 的 scope 和 product 的缓存策略不是一回事。
- `getBean("&x")` 拿到的是普通 bean 生命周期里的工厂对象。
- `getBean("x")` 拿到的是 `getObject()` 产物，可能被缓存，也可能不被缓存。

## 生命周期是分开的

工厂 bean 作为容器里的普通 bean，会经历构造、依赖注入、初始化和销毁。

产品对象则不一定有完整的 bean 生命周期。它的创建逻辑在 `getObject()` 里，是否缓存由 `isSingleton()` 决定。换句话说，容器认识工厂 bean，但产品对象的生命周期主要由工厂实现决定。

`SpringCoreBeansFactoryBeanDeepDiveLabTest` 和 `SpringCoreBeansFactoryBeanEdgeCasesLabTest` 合在一起说明了这件事：

- 工厂对象可以稳定地被 `&` 前缀取到。
- 产品对象可以按类型注入、按名字取值，也可以通过 `ObjectProvider` 延迟获取。
- 当产品不是缓存单例时，直接注入拿到的是一个固定引用，而 provider 每次可能拿到新产品。

## 本模块的观察入口

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansFactoryBeanDeepDiveLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansFactoryBeanEdgeCasesLabTest test
```

- `SpringCoreBeansFactoryBeanDeepDiveLabTest`：验证 `getBean("x")`、`getBean("&x")`、`getType()` 和产品缓存语义。
- `SpringCoreBeansFactoryBeanEdgeCasesLabTest`：验证产品和工厂的分离、provider 的延迟获取，以及产品缓存缺席时的行为。

