# Exercises & Solutions（练习与答案）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Exercises & Solutions（练习与答案）
    - 怎么使用：本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
    - 原理：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
    - 源码入口：N/A（本页为索引/工具页）
    - 推荐 Lab：N/A
<!-- CHAPTER-CARD:END -->


本页目标：解释本仓库“练习题”的启用方式与运行方式，并保证默认情况下不会破坏 CI（默认 `mvn -q test` 全绿）。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Exercises & Solutions（练习与答案） —— 本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
- 回到主线：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 怎么用这页

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Exercises & Solutions（练习与答案）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 建议先带着问题顺读一遍正文，再按证据链回到源码/断点验证。
<!-- BOOKLIKE-V2:INTRO:END -->

## 练习题是什么样的

本仓库约定：

- Labs：`*LabTest.java`（默认启用，必须全绿）
- Exercises：`*ExerciseTest.java`（默认 `@Disabled`，学习者手动开启）
- Solutions：`*ExerciseSolutionTest.java`（可选：用于提供参考实现的可回归验证）

---

## 如何启用练习题（推荐流程）

1. 找到你要做的练习：从对应模块的 `docs/README.md` 或书里的 [Labs 索引](labs-index.md) / 章节入口进入
2. 打开对应 `*ExerciseTest.java`
3. 移除/注释 `@Disabled`
4. 按测试提示完成实现（或改造现有实现）
5. 只跑当前模块测试验证：

```bash
mvn -q -pl :<artifactId> test
```

---

## 为什么 Exercises 默认禁用

原因只有一个：**保证仓库在默认模式下可回归**。

- CI / GitHub Actions 默认跑 `mvn -q test`
- Exercises 的目标是“让学习者改坏再改好”，不应该影响默认回归

---

## Solutions 的使用建议

如果某个练习提供了 `*ExerciseSolutionTest.java`：

- 它是“答案的回归验证”，不是让你直接复制粘贴答案
- 推荐学习方式：先自己完成 Exercise，再用 Solution 对照差异

---

## 常见坑

- 只改了业务实现但没理解代理/事务边界：建议回到书的 [Debugger Pack](debugger-pack.md) 把断点装在关键入口上
- 多模块联动导致你以为“改对了”：建议缩小范围，先用 `-pl :<artifactId>` 只跑单模块
