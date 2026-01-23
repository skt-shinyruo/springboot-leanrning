#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
仓库路径辅助工具（用于适配目录重排）。

历史结构（legacy）：
  <repo_root>/<module>/

当前结构（tutorials 风格分组）：
  <repo_root>/spring-boot-modules/<module>/
  <repo_root>/spring-core-modules/<module>/

目标：让 scripts/* 在“迁移前/迁移后”都能工作，避免脚本逻辑与目录结构强耦合。
"""

from __future__ import annotations

from pathlib import Path


MODULE_GROUP_DIRS: tuple[str, ...] = ("spring-boot-modules", "spring-core-modules")


def to_maven_artifact_id(module: str) -> str:
    """
    将“文档模块名 / 旧命名”映射为 Maven artifactId。

    背景：仓库的 docs 模块目录沿用 `springboot-*` 命名，但代码模块已迁移为 `spring-boot-*`。
    为了让 scripts/* 在迁移前/迁移后都能工作，这里做一个兼容映射。
    """
    if module.startswith("springboot-"):
        return module.replace("springboot-", "spring-boot-", 1)
    return module


def find_module_root(repo_root: Path, module: str) -> Path | None:
    """
    返回 module 的目录（Path），找不到返回 None。

    兼容：
    - legacy: <repo_root>/<module>
    - grouped: <repo_root>/<group>/<module>
    """
    normalized = to_maven_artifact_id(module)

    candidates = [repo_root / module]
    candidates.extend(repo_root / group / module for group in MODULE_GROUP_DIRS)
    if normalized != module:
        candidates.extend(repo_root / group / normalized for group in MODULE_GROUP_DIRS)
    for p in candidates:
        if p.is_dir():
            return p
    return None
