# Scope 与 prototype：谁复用、谁创建、谁销毁

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 singleton、prototype 以及常见 Web scope 的差异，不从注解写法出发，而从对象复用、创建、缓存和销毁责任出发。
    - 核心结论：singleton 只表示容器内复用同一个实例，不等于线程安全；prototype 每次请求都创建，但容器通常不负责完整销毁。
    - 另一个关键边界是：singleton 直接注入 prototype 时，默认只会固定住一次解析得到的实例，除非改成 provider、ObjectFactory 或 scoped proxy。

    观察对象：scope 决定对象是否复用、何时创建、缓存归属和销毁归属。
    主线位置：BeanDefinition 已存在，容器决定怎样保存和返回对象时。
    对照入口：`SpringCoreBeansLabTest`、`SpringCoreBeansPrototypeDestroySemanticsLabTest`。
<!-- CHAPTER-CARD:END -->

scope 讲的是“这个名字背后的对象按什么边界复用”。它不是一个单独的生命周期开关，而是容器对对象身份的管理方式。

## 先看四个责任

| 维度 | singleton | prototype | 常见 Web scope |
| --- | --- | --- | --- |
| 是否复用 | 容器内复用同一个实例 | 每次请求都创建新实例 | 复用边界由请求、会话或应用上下文决定 |
| 谁创建 | 容器创建一次 | 容器每次解析时创建 | 容器在对应上下文内创建 |
| 谁缓存 | 容器的 singleton 缓存 | 通常不进入 singleton 缓存 | 各自的 scope 上下文缓存 |
| 谁销毁 | 容器关闭时统一处理 | 默认不由容器完整销毁 | 由 scope 生命周期负责 |

这里最容易误解的点是 singleton。它只说明“同一个容器实例里返回同一个对象”，不说明这个对象内部状态可以并发共享，也不说明它天然线程安全。线程安全仍然取决于对象设计。

## singleton 不是线程安全保证

singleton 的含义是缓存复用，不是并发保护。容器只保证同一 bean name 在 singleton 语义下复用同一个对象，并不会替业务对象加锁。

`SpringCoreBeansLabTest` 中可以看到 singleton bean 在容器内只构造一次，但这只证明身份唯一，不证明方法调用可并发安全。只要对象内部有可变状态，多个线程仍然会共享同一份状态。

## prototype 的创建和销毁边界

prototype 的重点是“每次拿到的都是新实例”。容器会创建它，但默认不会像 singleton 那样在 `close()` 时统一调用完整销毁链路。

`SpringCoreBeansPrototypeDestroySemanticsLabTest` 直接固定了两个事实：

- `context.close()` 不会自动触发 prototype 的销毁回调
- 如果确实需要销毁，可以显式调用 `BeanFactory#destroyBean(beanName, instance)`

这意味着 prototype 的资源释放责任往往要由调用方自己承担。若 prototype 持有文件句柄、连接或其他外部资源，就不能假设容器会像管理 singleton 一样替你收尾。

## singleton 注入 prototype 时，默认只会抓住一个实例

这是 scope 里最常见的坑。singleton 在构造或注入阶段解析到 prototype 时，默认拿到的是那一次解析结果。之后再次调用 singleton 的方法，看到的仍然是同一个 prototype 引用。

所以“我把依赖声明成 prototype”并不自动意味着“每次使用都会得到新对象”。如果需要按调用动态获取，通常要改成：

- `ObjectProvider` 或 `ObjectFactory`
- scoped proxy
- 显式 lookup 方法

`SpringCoreBeansLabTest` 的 scope 相关实验和 `SpringCoreBeansPrototypeDestroySemanticsLabTest` 共同说明了这条边界：容器负责创建 prototype，但不会替你把 prototype 的所有生命周期都接管到底。

## 其他 scope 的本质

request、session、application 这类 scope 的差异，不在“是不是 Bean”，而在“对象缓存属于谁”。

- request scope 绑定当前请求上下文
- session scope 绑定会话上下文
- application scope 绑定应用级上下文

因此它们和 singleton 的关系不是“是否能被容器管理”，而是“复用边界不同”。一旦超出当前上下文，容器就不能像 singleton 一样直接返回固定实例。

## 看到 scope 时，先问三个问题

- 这个对象是每次创建，还是只创建一次后复用？
- 缓存放在容器 singleton 缓存里，还是放在某个 scope 上下文里？
- 销毁是容器统一负责，还是调用方 / scope 负责？

把这三个问题答清楚，scope 的语义就不会被“单例”“多例”这种粗标签带偏。
