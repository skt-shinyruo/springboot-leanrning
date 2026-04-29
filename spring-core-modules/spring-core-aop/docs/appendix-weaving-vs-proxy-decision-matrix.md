# 02. Weaving vs Proxy：能力边界决策表（跳转 aop-weaving）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕Weaving vs Proxy：能力边界决策表（跳转 aop-weaving）展开，主线可以概括为：Spring AOP 默认是 proxy-based，只能拦截“通过 proxy 的方法执行”；当需求超出这个模型（构造器/字段访问/static/final/self-invocation 等）时，应当切换到 weaving（编译期/加载期织入）或改造调用方式。

    本章不追求完整教材，而是一张可复用的“选型/排障分流表”：看到现象就能判断它属于 proxy 边界还是 weaving 领域，并给出下一跳。

    对照入口：`spring-core-modules/spring-core-aop-weaving`（本仓库对应模块）。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 真实项目叠加 Debug Playbook：AOP/Tx/Cache/Security 如何叠、如何断点验证](proxy-stacking-real-world-stacking-playbook.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 常见坑清单（排查时对照）](appendix-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

真实工程里，很多“表面上像 AOP 不生效”的问题，根因是：

> 在要求一个 proxy-based AOP 做 weaving 才能做到的事。

本章给一张分流表，目的不是“默认选择 weaving”，而是：

- 快速识别：问题是不是超出了 proxy 的能力边界
- 给出替代路径：改调用方式 / 改设计 / 或跳转到 weaving

## 1) 一张决策表（够用版）

| 需求/现象 | proxy-based（Spring AOP）能否做到 | 说明 | 下一步 |
| --- | --- | --- | --- |
| 拦截普通方法调用（外部通过 Spring bean 调用） | ✅ | 典型主线 | 本模块即可 |
| 自调用 `this.inner()` 也要被拦截 | ❌ | call path 绕过 proxy | 改为从容器拿 proxy / `exposeProxy`，或 weaving |
| 拦截 `final` 方法 | ❌（proxy） | CGLIB 不能覆盖 final method；JDK proxy 也只代理接口方法 | 去掉 final / 调整设计 / weaving |
| 拦截 `static` 方法 | ❌ | proxy 没有实例调用入口 | weaving 或改设计 |
| 拦截构造器执行 / 初始化阶段内部调用 | ❌ | proxy 还没产生（Bean 创建阶段边界） | 改生命周期钩子/延后调用，或 weaving |
| 拦截字段读写（field get/set） | ❌ | Spring AOP 仅支持 method execution join point | weaving |
| 拦截非 Spring 管理对象（`new` 出来的） | ❌ | 不在容器里就没有 proxy | 把对象交给容器 / 手工 ProxyFactory / weaving |

## 2) “换到 weaving”之前的三步自检

很多问题并不需要 weaving，先做三件事：

1. **确认调用是否走 proxy**（见 `proxy-fundamentals-self-invocation.md`）
2. **确认 proxy 上有没有 advisors**（`((Advised) bean).getAdvisors()`）
3. **确认是不是踩了 proxy 限制**（final/private/static/构造期等）

如果三步都自证无误，仍然需要拦截“proxy 做不到的 join point”，再考虑 weaving。

## 3) 下一跳（本仓库模块）

本仓库将 weaving 相关内容放在独立模块（避免把 proxy 主线讲乱）：

- `spring-core-modules/spring-core-aop-weaving`

## 小结

- proxy 与 weaving 的边界，不是“哪个好”，而是“能不能做到”。
- 当需求天然属于 weaving，请尽早切换心智模型，避免在 proxy 世界里消耗时间。

<!-- BOOKIFY:START -->

上一章：[10-real-world-stacking-playbook](proxy-stacking-real-world-stacking-playbook.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[90-common-pitfalls](appendix-common-pitfalls.md)

<!-- BOOKIFY:END -->

