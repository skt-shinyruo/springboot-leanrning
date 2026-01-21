#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
生成 spring-beans（Spring Framework）Public API 索引（可检索/可审计）。

设计目标：
- 输入：本地 Maven 仓库的 spring-beans-*-sources.jar（默认 Spring 6.2.15）
- 输出：
  1) docs/beans/spring-core-beans/appendix/95-spring-beans-public-api-index.md
  2) docs/beans/spring-core-beans/appendix/96-spring-beans-public-api-gap.md

说明：
- 本脚本优先保证“可维护 + 可重复生成”，不追求实现 Java 语法的完美解析。
- 以 public 顶层类型为主（Java 规则：public 顶层类型与文件名一一对应）。
"""

from __future__ import annotations

import argparse
import os
import re
import sys
import textwrap
import zipfile
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path
from typing import Iterable

from repo_paths import find_module_root


@dataclass(frozen=True)
class MappingRule:
    package_prefix: str
    domain: str
    primary_chapter: str | None
    primary_labs: tuple[str, ...]
    coverage: str
    note: str | None = None


@dataclass(frozen=True)
class PublicType:
    fqcn: str
    package: str
    simple_name: str
    kind: str
    source_entry: str
    mapping: MappingRule | None


def _default_sources_jar(version: str) -> Path:
    return (
        Path.home()
        / ".m2"
        / "repository"
        / "org"
        / "springframework"
        / "spring-beans"
        / version
        / f"spring-beans-{version}-sources.jar"
    )


def _read_text(zf: zipfile.ZipFile, entry: str) -> str:
    data = zf.read(entry)
    return data.decode("utf-8", errors="replace")


def _infer_kind(java_text: str, simple_name: str) -> str:
    # 兼容 public abstract/final/sealed/non-sealed 等修饰符，non-sealed 有 '-'。
    pattern = (
        r"^\s*public\s+(?:[\w-]+\s+)*"
        r"(?P<kind>@interface|class|interface|enum|record)\s+"
        + re.escape(simple_name)
        + r"\b"
    )
    match = re.search(pattern, java_text, flags=re.MULTILINE)
    if not match:
        return "unknown"
    kind = match.group("kind")
    if kind == "@interface":
        return "annotation"
    return kind


def _iter_public_types(sources_jar: Path) -> list[PublicType]:
    if not sources_jar.exists():
        raise FileNotFoundError(f"找不到 sources.jar：{sources_jar}")

    rules = _mapping_rules()

    public_types: list[PublicType] = []
    with zipfile.ZipFile(sources_jar) as zf:
        for entry in sorted(zf.namelist()):
            if not entry.endswith(".java"):
                continue
            if not entry.startswith("org/springframework/beans/"):
                continue
            if entry.endswith("package-info.java") or entry.endswith("module-info.java"):
                continue

            package = ".".join(entry.split("/")[:-1])
            simple_name = entry.split("/")[-1].removesuffix(".java")
            fqcn = f"{package}.{simple_name}"

            java_text = _read_text(zf, entry)
            kind = _infer_kind(java_text, simple_name)

            mapping = _pick_mapping_rule(rules, package)
            public_types.append(
                PublicType(
                    fqcn=fqcn,
                    package=package,
                    simple_name=simple_name,
                    kind=kind,
                    source_entry=entry,
                    mapping=mapping,
                )
            )

    return public_types


def _mapping_rules() -> tuple[MappingRule, ...]:
    # chapter/lab 路径：
    # - chapter：相对 docs/beans/spring-core-beans/ 的路径
    # - lab：相对 spring-core-beans/ 的路径（会在 Markdown 中换算成 ../../src/...）
    return (
        MappingRule(
            package_prefix="org.springframework.beans.factory.aot",
            domain="AOT（spring-beans）",
            primary_chapter="part-05-aot-and-real-world/024-40-aot-and-native-overview.md",
            primary_labs=(
                "src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotFactoriesLabTest.java",
                "src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java",
            ),
            coverage="core",
            note="AOT 包的 API 面很大：本项目以“可断点理解主线”为目标，建议先从 aot.factories/AotServices 入手，再逐步深入代码生成链路。",
        ),
        MappingRule(
            package_prefix="org.springframework.beans.factory.groovy",
            domain="BeanDefinitionReader（Groovy）",
            primary_chapter="part-05-aot-and-real-world/47-beandefinitionreader-other-inputs-properties-groovy.md",
            primary_labs=("src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansGroovyBeanDefinitionReaderLabTest.java",),
            coverage="core",
        ),
        MappingRule(
            package_prefix="org.springframework.beans.factory.xml",
            domain="XML → BeanDefinitionReader",
            primary_chapter="part-05-aot-and-real-world/42-xml-bean-definition-reader.md",
            primary_labs=("src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlBeanDefinitionReaderLabTest.java",),
            coverage="core",
        ),
        MappingRule(
            package_prefix="org.springframework.beans.factory.parsing",
            domain="XML parsing / namespace 扩展",
            primary_chapter="part-05-aot-and-real-world/46-xml-namespace-extension.md",
            primary_labs=("src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlNamespaceExtensionLabTest.java",),
            coverage="core",
        ),
        MappingRule(
            package_prefix="org.springframework.beans.factory.serviceloader",
            domain="内置 FactoryBean（ServiceLoader*）",
            primary_chapter="part-05-aot-and-real-world/49-built-in-factorybeans-gallery.md",
            primary_labs=(
                "src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansServiceLoaderFactoryBeansLabTest.java",
                "src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBuiltInFactoryBeansLabTest.java",
            ),
            coverage="core",
        ),
        MappingRule(
            package_prefix="org.springframework.beans.factory.wiring",
            domain="容器外对象装配（BeanConfigurerSupport）",
            primary_chapter="part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md",
            primary_labs=("src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAutowireCapableBeanFactoryLabTest.java",),
            coverage="core",
        ),
        MappingRule(
            package_prefix="org.springframework.beans.factory.annotation",
            domain="注解注入（@Autowired/@Qualifier/@Value 等）",
            primary_chapter="part-01-ioc-container/014-03-dependency-injection-resolution.md",
            primary_labs=("src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java",),
            coverage="core",
        ),
        MappingRule(
            package_prefix="org.springframework.beans.factory.config",
            domain="配置模型与扩展点（BFPP/BPP/Scope/FactoryBean 等）",
            primary_chapter="part-01-ioc-container/017-06-post-processors.md",
            primary_labs=("src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java",),
            coverage="core",
        ),
        MappingRule(
            package_prefix="org.springframework.beans.factory.support",
            domain="容器内部实现（support）",
            primary_chapter="part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md",
            primary_labs=("src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java",),
            coverage="core",
        ),
        MappingRule(
            package_prefix="org.springframework.beans.factory",
            domain="BeanFactory API（最小容器入口）",
            primary_chapter="part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md",
            primary_labs=("src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanFactoryApiLabTest.java",),
            coverage="core",
        ),
        MappingRule(
            package_prefix="org.springframework.beans.propertyeditors",
            domain="PropertyEditor（legacy 但重要）",
            primary_chapter="part-05-aot-and-real-world/50-property-editor-and-value-resolution.md",
            primary_labs=("src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansPropertyEditorLabTest.java",),
            coverage="core",
        ),
        MappingRule(
            package_prefix="org.springframework.beans.support",
            domain="Beans 支撑（偏低层）",
            primary_chapter="part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md",
            primary_labs=(
                "src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeansSupportUtilitiesLabTest.java",
                "src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansTypeConversionLabTest.java",
            ),
            coverage="core",
        ),
        MappingRule(
            package_prefix="org.springframework.beans",
            domain="Beans 核心（BeanWrapper/PropertyValues/异常模型等）",
            primary_chapter="part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md",
            primary_labs=("src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansTypeConversionLabTest.java",),
            coverage="core",
        ),
    )


def _pick_mapping_rule(rules: Iterable[MappingRule], package: str) -> MappingRule | None:
    # 最长前缀匹配，越具体优先级越高（如 beans.factory.xml 覆盖 beans.factory）
    best: MappingRule | None = None
    for rule in rules:
        if not package.startswith(rule.package_prefix):
            continue
        if best is None or len(rule.package_prefix) > len(best.package_prefix):
            best = rule
    return best


def _rel_link_from_appendix_to_chapter(chapter_from_docs_root: str) -> str:
    # appendix/*.md → ../<chapter>
    return f"../{chapter_from_docs_root}"


def _rel_link_from_appendix_to_lab(*, out_path: Path, module_root: Path, lab_from_module_root: str) -> str:
    """
    生成从 appendix 文件到 Lab 源码文件的相对链接。

    注意：
    - docs 已迁移到仓库根 `docs/`，而 Lab 源码在 module 目录（已按分组聚合）。
    - 因此不能再用固定的 `../../src/...` 假设，需要按文件实际位置计算 relpath。
    """
    lab_fs = (module_root / lab_from_module_root).resolve()
    rel = os.path.relpath(lab_fs, start=out_path.parent)
    return Path(rel).as_posix()


def _render_index_md(
    *,
    public_types: list[PublicType],
    version: str,
    sources_jar: Path,
    out_path: Path,
) -> str:
    now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    repo_root = Path(__file__).resolve().parent.parent
    module_root = find_module_root(repo_root, "spring-core-beans")
    if module_root is None:
        raise FileNotFoundError("找不到模块目录：spring-core-beans（预期在 spring-core-modules/ 下）。")

    packages: dict[str, list[PublicType]] = {}
    for t in public_types:
        packages.setdefault(t.package, []).append(t)
    for pkg in packages:
        packages[pkg] = sorted(packages[pkg], key=lambda x: x.simple_name.lower())

    header = textwrap.dedent(
        f"""\
        <!--
        ⚠️ GENERATED FILE - 请勿手工编辑。
        - Generator: scripts/generate-spring-beans-public-api-index.py
        - Source: {sources_jar}
        - Generated at: {now}
        -->

        # 95. spring-beans Public API 索引（Spring Framework {version}）

        本索引用于把 `spring-beans` 的 public 类型做成“可检索/可审计”的入口，并为每个类型给出：
        - 机制域（Domain）
        - 主入口章节（Chapter）
        - 主入口 Lab（Lab）

        重要说明：
        - 这里的 **Chapter/Lab 是“主入口”**：并不意味着该类型只有一个知识点；它只是把你带到“主线/边界/断点观察点”的起点。
        - 如果你想再生本文件：运行 `python3 scripts/generate-spring-beans-public-api-index.py`。

        ---
        """
    )

    lines: list[str] = [header]

    lines.append("## 包索引（按 package 分组）\n")
    for pkg in sorted(packages.keys()):
        anchor = pkg.replace(".", "").lower()
        lines.append(f"- `{pkg}`（{len(packages[pkg])}） → [跳转](#{anchor})\n")
    lines.append("\n---\n")

    for pkg in sorted(packages.keys()):
        anchor = pkg.replace(".", "").lower()
        lines.append(f"\n## {pkg}\n")
        lines.append(f"<a id=\"{anchor}\"></a>\n\n")
        lines.append("| Type | Kind | Domain | Chapter | Lab | Coverage |\n")
        lines.append("| --- | --- | --- | --- | --- | --- |\n")
        for t in packages[pkg]:
            if t.mapping is None:
                lines.append(f"| `{t.fqcn}` | `{t.kind}` | - | - | - | ⚠️ unmapped |\n")
                continue

            chapter_link = (
                f"[{Path(t.mapping.primary_chapter).name}]({_rel_link_from_appendix_to_chapter(t.mapping.primary_chapter)})"
                if t.mapping.primary_chapter
                else "-"
            )
            lab_links: list[str] = []
            for lab in t.mapping.primary_labs:
                lab_path = Path(lab)
                lab_links.append(
                    f"[`{lab_path.name}`]({_rel_link_from_appendix_to_lab(out_path=out_path, module_root=module_root, lab_from_module_root=lab)})"
                )
            lab_cell = "<br/>".join(lab_links) if lab_links else "-"

            coverage = "✅ core" if t.mapping.coverage == "core" else "🟡 partial"
            lines.append(
                "| "
                + " | ".join(
                    [
                        f"`{t.fqcn}`",
                        f"`{t.kind}`",
                        t.mapping.domain,
                        chapter_link,
                        lab_cell,
                        coverage,
                    ]
                )
                + " |\n"
            )

        # 包级备注（如果规则里有 note）
        # 同包所有类型的 mapping 相同（按 package_prefix 归类），取第一个即可。
        first = packages[pkg][0]
        if first.mapping and first.mapping.note:
            lines.append(f"\n> 备注：{first.mapping.note}\n")

    lines.append(
        textwrap.dedent(
            """\

            ---

            ## 如何用它（建议）

            - 你遇到某个类/接口名时：先在本索引里搜 `FQCN`，找到“主入口章节”。
            - 进入章节后：按章节的“断点入口/观察点”跑一遍对应 Lab，让概念落到可证明的主线上。
            - 想做源码深挖：从 Lab 的断点入口顺着调用链往下走（比从 IDE 全局搜索更快）。
            """
        )
    )

    return "".join(lines)


def _render_gap_md(
    *,
    public_types: list[PublicType],
    version: str,
    sources_jar: Path,
    out_index: Path,
) -> str:
    now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    unmapped = [t for t in public_types if t.mapping is None]
    partial = [t for t in public_types if t.mapping is not None and t.mapping.coverage != "core"]

    # 以 “chapter/lab 路径存在性”做一层静态校验（用于发现索引指向漂移）
    missing_chapter: list[tuple[str, str]] = []
    missing_lab: list[tuple[str, str]] = []

    repo_root = Path(__file__).resolve().parent.parent
    module_root = find_module_root(repo_root, "spring-core-beans")
    if module_root is None:
        raise FileNotFoundError("找不到模块目录：spring-core-beans（预期在 spring-core-modules/ 下）。")

    docs_module_candidates = sorted((repo_root / "docs").glob("*/spring-core-beans"))
    if not docs_module_candidates:
        raise FileNotFoundError("找不到 docs 目录：docs/*/spring-core-beans。")
    docs_module_root = docs_module_candidates[0]

    for t in public_types:
        if not t.mapping:
            continue
        if t.mapping.primary_chapter:
            chapter_fs = docs_module_root / t.mapping.primary_chapter
            if not chapter_fs.exists():
                missing_chapter.append((t.fqcn, str(chapter_fs)))
        for lab in t.mapping.primary_labs:
            lab_fs = module_root / lab
            if not lab_fs.exists():
                missing_lab.append((t.fqcn, str(lab_fs)))

    header = textwrap.dedent(
        f"""\
        <!--
        ⚠️ GENERATED FILE - 请勿手工编辑。
        - Generator: scripts/generate-spring-beans-public-api-index.py
        - Source: {sources_jar}
        - Generated at: {now}
        -->

        # 96. spring-beans Public API 覆盖差距（Gap）清单（Spring Framework {version}）

        本文件用于把“还缺什么”变成显式清单，配合：
        - 索引：`{out_index}`
        - 分批补齐策略：HelloAGENTS 方案包 task.md

        ---
        """
    )

    lines: list[str] = [header]

    lines.append("## 可运行入口（建议先跑再看 Gap）\n\n")
    lines.append(
        "本章是“缺口清单”，推荐先跑一个能让你进入 Spring Beans 主线的 Lab，再回来按需查 Gap：\n\n"
    )
    lines.append("- `SpringCoreBeansContainerLabTest`\n")
    lines.append("- `SpringCoreBeansBeanCreationTraceLabTest`\n")
    lines.append("- `SpringCoreBeansRegistryPostProcessorLabTest`\n")
    lines.append("- `SpringCoreBeansTypeConversionLabTest`\n")
    lines.append("\n---\n")

    lines.append("## 概览\n\n")
    lines.append(f"- 总 public 顶层类型（按 sources.jar 统计）：**{len(public_types)}**\n")
    lines.append(f"- 未映射（unmapped）：**{len(unmapped)}**\n")
    lines.append(f"- partial 覆盖（需要后续补齐/深化）：**{len(partial)}**\n")
    lines.append(f"- 索引指向缺失的 chapter：**{len(missing_chapter)}**\n")
    lines.append(f"- 索引指向缺失的 lab：**{len(missing_lab)}**\n")

    if unmapped:
        lines.append("\n## 未映射类型（需要补规则或新增章节入口）\n\n")
        for t in sorted(unmapped, key=lambda x: x.fqcn):
            lines.append(f"- `{t.fqcn}`\n")

    # 将 partial 按 package 归类，便于按机制域分批推进
    if partial:
        lines.append("\n## partial 覆盖类型（建议按 package/机制域分批深化）\n\n")
        by_pkg: dict[str, list[PublicType]] = {}
        for t in partial:
            by_pkg.setdefault(t.package, []).append(t)
        for pkg in sorted(by_pkg.keys()):
            lines.append(f"### `{pkg}`（{len(by_pkg[pkg])}）\n\n")
            note = by_pkg[pkg][0].mapping.note if by_pkg[pkg][0].mapping else None
            if note:
                lines.append(f"> 备注：{note}\n\n")
            for t in sorted(by_pkg[pkg], key=lambda x: x.simple_name.lower()):
                lines.append(f"- `{t.fqcn}`\n")

    if missing_chapter:
        lines.append("\n## 索引指向缺失的章节文件（需要修复链接）\n\n")
        for fqcn, path in sorted(missing_chapter, key=lambda x: x[0]):
            lines.append(f"- `{fqcn}` → `{path}`\n")

    if missing_lab:
        lines.append("\n## 索引指向缺失的 Lab 文件（需要修复链接）\n\n")
        for fqcn, path in sorted(missing_lab, key=lambda x: x[0]):
            lines.append(f"- `{fqcn}` → `{path}`\n")

    if not (unmapped or partial or missing_chapter or missing_lab):
        lines.append("\n## 结论\n\n- 当前索引规则无缺口（0 unmapped），且索引指向的 chapter/lab 均存在。\n")

    return "".join(lines)


def _write_text(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")


_PITFALL_PLACEHOLDER = "- （本章坑点待补齐：建议先跑一次 E，再回看断言失败场景与边界条件。）"


def _patch_generated_appendix(markdown: str, *, kind: str) -> str:
    """
    对生成的 Markdown 做轻量修补，使 Appendix 真正“可用”。

    说明：Appendix 95/96 声明为生成文件；这里通过生成器侧修补，避免出现“待补齐/空标题”等占位内容，
    同时保持“生成器为唯一事实来源（SSOT）”。
    """

    pitfalls = """## 坑点与排障（把索引变成“可用工具”）

- **索引不是学习路线**：Index/GAP 的价值是“定位”，不是“背诵清单”。推荐先按 `docs/README.md` 的 Start Here 跑最小 Lab，再回索引做反查定位。
- **BeanFactory vs ApplicationContext 差异**：很多“注解不生效/生命周期不触发”的现象，根因是没有安装 `AnnotationConfigProcessors`（仅 `BeanFactory` 不会自动做这件事）。
- **FactoryBean 的双重身份**：`getBean("foo")` 拿到的是“产品对象”，`getBean("&foo")` 才是 `FactoryBean` 本身；排查类型不匹配/注入歧义时先确认你拿到的到底是谁。
- **代理导致的类型错觉**：JDK Proxy 只实现接口，无法赋值给具体类；当 BPP 提前暴露早期引用/创建代理时，“按具体类注入”可能失败，优先按接口注入或切换到 class-based proxy。
- **版本差异与定位方式**：不要依赖行号；用“入口测试方法 + 关键接口名 + `rg` 关键词”定位更稳（Spring 小版本内部实现经常移动）。

"""

    if _PITFALL_PLACEHOLDER in markdown:
        markdown = markdown.replace(_PITFALL_PLACEHOLDER, pitfalls.rstrip())
        return markdown

    # If the file doesn't contain the placeholder, still ensure there is a pitfalls section.
    if "## 坑点与排障" not in markdown:
        markdown = markdown.rstrip() + "\n\n" + pitfalls.rstrip() + "\n"
        return markdown

    return markdown


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(
        description="生成 spring-beans Public API 索引与 gap 清单（写入 docs/beans/spring-core-beans/appendix）。"
    )
    parser.add_argument("--version", default="6.2.15", help="Spring Framework spring-beans 版本（默认：6.2.15）")
    parser.add_argument(
        "--sources-jar",
        default="",
        help="spring-beans-*-sources.jar 的路径（默认：从 ~/.m2 推断）",
    )
    parser.add_argument(
        "--out-index",
        default="docs/beans/spring-core-beans/appendix/95-spring-beans-public-api-index.md",
        help="索引输出路径（相对 repo root）",
    )
    parser.add_argument(
        "--out-gap",
        default="docs/beans/spring-core-beans/appendix/96-spring-beans-public-api-gap.md",
        help="gap 输出路径（相对 repo root）",
    )
    args = parser.parse_args(argv)

    sources_jar = Path(args.sources_jar).expanduser() if args.sources_jar else _default_sources_jar(args.version)
    repo_root = Path(__file__).resolve().parent.parent

    public_types = _iter_public_types(sources_jar)

    out_index = (repo_root / args.out_index).resolve()
    out_gap = (repo_root / args.out_gap).resolve()

    index_md = _render_index_md(public_types=public_types, version=args.version, sources_jar=sources_jar, out_path=out_index)
    gap_md = _render_gap_md(public_types=public_types, version=args.version, sources_jar=sources_jar, out_index=out_index)

    index_md = _patch_generated_appendix(index_md, kind="index")
    gap_md = _patch_generated_appendix(gap_md, kind="gap")
    _write_text(out_index, index_md)
    _write_text(out_gap, gap_md)

    print(f"[OK] wrote: {out_index}")
    print(f"[OK] wrote: {out_gap}")
    print(f"[OK] public types: {len(public_types)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
