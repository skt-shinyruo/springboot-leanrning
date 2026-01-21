#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
全模块 docs 书本化（Bookify）：为每个章节 upsert “对应 Lab/Test”入口块与“上一章｜目录｜下一章”导航。

设计目标：
1) 以 `docs/<topic>/<module>/README.md` 的章节链接清单为 SSOT；
2) 不改写正文知识点，只在文末插入/更新一个稳定的尾部区块；
3) 可重复执行（idempotent）：多次运行不会重复叠加；
4) 尽量不破坏既有“可跑入口”引用：不会主动删除正文中的 Lab/Test 引用。

用法：
  python3 scripts/bookify-docs.py
  python3 scripts/bookify-docs.py --dry-run
  python3 scripts/bookify-docs.py --module spring-core-beans --module springboot-web-mvc
"""

from __future__ import annotations

import argparse
import os
import re
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

from repo_paths import find_module_root


MD_LINK_WITH_TEXT_RE = re.compile(r"(!?)\[([^\]]*)\]\(([^)]+)\)")
SCHEME_RE = re.compile(r"^[a-zA-Z][a-zA-Z0-9+.-]*:")

# 用于生成“对应 Lab/Test”列表（从正文中提取可能的入口类名/测试文件路径）
TEST_CLASS_RE = re.compile(
    r"\b([A-Za-z_][A-Za-z0-9_]*(?:LabTest|ExerciseTest|ExerciseSolutionTest))\b"
)
TEST_FILE_PATH_RE = re.compile(
    r"(?P<path>(?:(?:spring-boot-modules|spring-core-modules)/)?(?:(?:spring-core|springboot)-[a-z0-9-]+/)?src/test/java/[A-Za-z0-9_./-]+\.java)",
    re.IGNORECASE,
)

BOOKIFY_START = "<!-- BOOKIFY:START -->"
BOOKIFY_END = "<!-- BOOKIFY:END -->"

# 导航行识别：兼容本仓库已有的 `｜` 分隔符与可能出现的 ASCII `|`
NAV_SEP = r"(?:\||｜)"
NAV_LINE_RE = re.compile(
    rf"^上一章：\[[^\]]+\]\([^)]+\)\s*{NAV_SEP}\s*目录：\[Docs TOC\]\([^)]+\)\s*{NAV_SEP}\s*下一章：\[[^\]]+\]\([^)]+\)\s*$"
)


@dataclass(frozen=True)
class ChapterRef:
    chapter: Path
    title: str
    last_index: int


def is_external_link(dest: str) -> bool:
    if dest.startswith("#"):
        return True
    if dest.startswith("//"):
        return True
    return bool(SCHEME_RE.match(dest))


def normalize_md_link_target(raw: str) -> str | None:
    dest = raw.strip()
    if not dest:
        return None

    # Strip angle brackets: (<path>) form
    if dest.startswith("<") and dest.endswith(">"):
        dest = dest[1:-1].strip()

    # Remove title part: (path "title")
    if " " in dest or "\t" in dest:
        dest = re.split(r"\s+", dest, maxsplit=1)[0]

    # Strip anchor
    if "#" in dest:
        dest = dest.split("#", 1)[0]

    return dest or None


def discover_modules(repo_root: Path) -> list[tuple[str, Path]]:
    """
    发现“有模块目录页”的模块：以 docs/<topic>/<module>/README.md 为准。
    返回 (module_name, docs_readme_path)。
    """
    docs_root = repo_root / "docs"
    if not docs_root.is_dir():
        return []

    modules: list[tuple[str, Path]] = []
    for readme in sorted(docs_root.glob("*/*/README.md")):
        module = readme.parent.name
        if find_module_root(repo_root, module) is None:
            continue
        modules.append((module, readme))
    return modules


def resolve_docs_readme(repo_root: Path, module: str) -> Path | None:
    candidates = sorted((repo_root / "docs").glob(f"*/{module}/README.md"))
    return candidates[0] if candidates else None


def iter_links_from_docs_readme(readme: Path) -> Iterable[tuple[str, str]]:
    """
    Yield (title, target_raw) for markdown links in docs/README.md (images excluded).
    """
    content = readme.read_text(encoding="utf-8", errors="replace")
    for m in MD_LINK_WITH_TEXT_RE.finditer(content):
        is_image = m.group(1) == "!"
        if is_image:
            continue
        title = (m.group(2) or "").strip()
        target_raw = m.group(3)
        yield title, target_raw


def iter_chapters_ordered_by_last_occurrence(repo_root: Path, docs_readme: Path) -> list[ChapterRef]:
    if not docs_readme.is_file():
        return []

    index = 0
    last: dict[Path, ChapterRef] = {}
    for title, target_raw in iter_links_from_docs_readme(docs_readme):
        index += 1
        target = normalize_md_link_target(target_raw)
        if target is None:
            continue
        if is_external_link(target):
            continue
        if not target.endswith(".md"):
            continue

        chapter = (docs_readme.parent / target).resolve()
        try:
            chapter.relative_to(repo_root)
        except ValueError:
            continue
        if "/docs/" not in chapter.as_posix():
            continue
        if chapter == docs_readme:
            continue

        # 使用“最后一次出现”的标题与顺序，避免 docs/README.md 中“快速定位”类重复链接影响主线顺序
        last[chapter] = ChapterRef(
            chapter=chapter,
            title=title or chapter.name,
            last_index=index,
        )

    ordered = sorted(last.values(), key=lambda c: c.last_index)
    return ordered


def build_test_class_index(module_root: Path) -> set[str]:
    test_root = module_root / "src" / "test" / "java"
    if not test_root.is_dir():
        return set()
    names: set[str] = set()
    for p in test_root.rglob("*.java"):
        if p.is_file():
            names.add(p.stem)
    return names


def relpath_posix(target: Path, base_dir: Path) -> str:
    rel = os.path.relpath(target, start=base_dir)
    return Path(rel).as_posix()


def extract_runnable_entries(
    repo_root: Path, module_root: Path, content: str, module_test_class_names: set[str]
) -> tuple[list[str], list[str], list[str], list[str]]:
    """
    Returns (lab_classes, exercise_classes, solution_classes, test_file_paths)
    - classes are simple names, filtered by existence in module test sources
    - file paths are repo-relative posix paths (validated to exist)
    """
    # 防止脚本重复运行时把自己生成的尾部区块也当成“正文引用”
    if BOOKIFY_START in content and BOOKIFY_END in content:
        content = remove_existing_bookify_block(content)[0]

    labs: list[str] = []
    exercises: list[str] = []
    solutions: list[str] = []
    seen_cls: set[str] = set()

    for m in TEST_CLASS_RE.finditer(content):
        cls = m.group(1)
        if cls in seen_cls:
            continue
        seen_cls.add(cls)
        if cls not in module_test_class_names:
            continue
        if cls.endswith("LabTest"):
            labs.append(cls)
        elif cls.endswith("ExerciseSolutionTest"):
            solutions.append(cls)
        elif cls.endswith("ExerciseTest"):
            exercises.append(cls)

    paths: list[str] = []
    seen_path: set[str] = set()
    for m in TEST_FILE_PATH_RE.finditer(content):
        raw = (m.group("path") or "").strip()
        if not raw:
            continue
        if "..." in raw or "…" in raw:
            continue

        if raw.startswith("spring-core-") or raw.startswith("springboot-"):
            resolved = (repo_root / raw).resolve()
        else:
            resolved = (module_root / raw).resolve()

        if not resolved.exists():
            continue
        try:
            rel = resolved.relative_to(repo_root).as_posix()
        except ValueError:
            continue
        if rel in seen_path:
            continue
        seen_path.add(rel)
        paths.append(rel)

    return labs, exercises, solutions, paths


def remove_existing_bookify_block(content: str) -> tuple[str, bool]:
    lines = content.splitlines()
    start_idx = None
    end_idx = None
    for i, line in enumerate(lines):
        if BOOKIFY_START in line:
            start_idx = i
            break
    if start_idx is None:
        return content, False
    for j in range(start_idx + 1, len(lines)):
        if BOOKIFY_END in lines[j]:
            end_idx = j
            break
    if end_idx is None:
        # block corrupted; do not attempt partial rewrite
        return content, False

    new_lines = lines[:start_idx] + lines[end_idx + 1 :]
    return "\n".join(new_lines) + ("\n" if content.endswith("\n") else ""), True


def remove_trailing_nav_line(content: str) -> tuple[str, bool]:
    lines = content.splitlines()
    changed = False

    # trim trailing blank lines (but keep internal trailing spaces untouched)
    while lines and lines[-1].strip() == "":
        lines.pop()
        changed = True

    if lines and NAV_LINE_RE.match(lines[-1].strip()):
        lines.pop()
        changed = True
        while lines and lines[-1].strip() == "":
            lines.pop()
            changed = True

    if not changed:
        return content, False

    return "\n".join(lines) + "\n", True


def build_bookify_footer(
    repo_root: Path,
    module_root: Path,
    docs_readme: Path,
    chapter: ChapterRef,
    prev_ref: ChapterRef | None,
    next_ref: ChapterRef | None,
    module_test_class_names: set[str],
) -> list[str]:
    chapter_dir = chapter.chapter.parent

    toc_rel = relpath_posix(docs_readme, chapter_dir)
    toc_link = f"[Docs TOC]({toc_rel})"

    if prev_ref is None:
        prev_link = toc_link
    else:
        prev_rel = relpath_posix(prev_ref.chapter, chapter_dir)
        prev_link = f"[{prev_ref.title}]({prev_rel})"

    if next_ref is None:
        next_link = toc_link
    else:
        next_rel = relpath_posix(next_ref.chapter, chapter_dir)
        next_link = f"[{next_ref.title}]({next_rel})"

    nav_line = f"上一章：{prev_link} ｜ 目录：{toc_link} ｜ 下一章：{next_link}"

    content = chapter.chapter.read_text(encoding="utf-8", errors="replace")
    labs, exercises, solutions, paths = extract_runnable_entries(
        repo_root=repo_root,
        module_root=module_root,
        content=content,
        module_test_class_names=module_test_class_names,
    )

    bullets: list[str] = []
    if labs:
        bullets.append("- Lab：" + " / ".join(f"`{c}`" for c in labs))
    if exercises:
        bullets.append("- Exercise：" + " / ".join(f"`{c}`" for c in exercises))
    if solutions:
        bullets.append("- Solution：" + " / ".join(f"`{c}`" for c in solutions))
    if paths:
        # 路径通常更长，单独列；控制行数，避免某些章节过长
        max_paths = 6
        shown = paths[:max_paths]
        bullets.append("- Test file：" + " / ".join(f"`{p}`" for p in shown))
        if len(paths) > max_paths:
            bullets.append(f"- （另有 {len(paths) - max_paths} 个 test file 路径引用，略）")

    if not bullets:
        bullets.append("- （本章入口请参考正文中的 Lab/Test 引用）")

    footer: list[str] = []
    footer.append(BOOKIFY_START)
    footer.append("")
    footer.append("### 对应 Lab/Test")
    footer.append("")
    footer.extend(bullets)
    footer.append("")
    footer.append(nav_line)
    footer.append("")
    footer.append(BOOKIFY_END)
    return footer


def upsert_bookify_footer(
    repo_root: Path,
    module_root: Path,
    docs_readme: Path,
    chapters: list[ChapterRef],
    dry_run: bool,
) -> tuple[int, int, list[str]]:
    """
    Returns (changed_files, total_files, warnings)
    """
    changed = 0
    total = 0
    warnings: list[str] = []

    test_class_names = build_test_class_index(module_root)

    for i, chapter in enumerate(chapters):
        if not chapter.chapter.exists():
            try:
                rel = chapter.chapter.relative_to(repo_root)
            except ValueError:
                rel = chapter.chapter
            warnings.append(f"- missing chapter: {rel}")
            continue

        total += 1
        prev_ref = None if i == 0 else chapters[i - 1]
        next_ref = None if i == len(chapters) - 1 else chapters[i + 1]

        original = chapter.chapter.read_text(encoding="utf-8", errors="replace")

        updated = original
        updated, _ = remove_existing_bookify_block(updated)
        updated, _ = remove_trailing_nav_line(updated)

        # remove trailing blank lines before append
        lines = updated.splitlines()
        while lines and lines[-1].strip() == "":
            lines.pop()

        footer_lines = build_bookify_footer(
            repo_root=repo_root,
            module_root=module_root,
            docs_readme=docs_readme,
            chapter=chapter,
            prev_ref=prev_ref,
            next_ref=next_ref,
            module_test_class_names=test_class_names,
        )

        if lines:
            lines.append("")
        lines.extend(footer_lines)
        new_text = "\n".join(lines) + "\n"

        if new_text != original:
            changed += 1
            if not dry_run:
                chapter.chapter.write_text(new_text, encoding="utf-8")

    return changed, total, warnings


def parse_args(argv: list[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        prog="bookify-docs.py",
        description="为模块文档章节统一尾部入口块与导航（以 docs/<topic>/<module>/README.md 为 SSOT）。",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="只输出统计信息，不写入文件。",
    )
    parser.add_argument(
        "--module",
        action="append",
        default=[],
        help="仅处理指定模块（可重复）。例如：--module spring-core-beans",
    )
    return parser.parse_args(argv)


def main(argv: list[str]) -> int:
    repo_root = Path(__file__).resolve().parents[1]
    args = parse_args(argv[1:])

    if args.module:
        module_items: list[tuple[str, Path]] = []
        for module in args.module:
            docs_readme = resolve_docs_readme(repo_root, module)
            if docs_readme is None:
                module_items.append((module, Path()))
                continue
            module_items.append((module, docs_readme))
    else:
        module_items = discover_modules(repo_root)

    if not module_items:
        print("[ERROR] 未发现任何模块目录页（docs/<topic>/<module>/README.md）。", file=sys.stderr)
        return 2

    total_changed = 0
    total_files = 0
    all_warnings: list[str] = []

    for module, docs_readme in module_items:
        module_root = find_module_root(repo_root, module)
        if module_root is None:
            all_warnings.append(f"- {module}: missing module directory")
            continue
        if not docs_readme or not docs_readme.is_file():
            all_warnings.append(f"- {module}: missing docs/<topic>/{module}/README.md")
            continue

        chapters = iter_chapters_ordered_by_last_occurrence(repo_root, docs_readme)
        if not chapters:
            all_warnings.append(f"- {module}: no chapters parsed from docs README")
            continue

        changed, total, warnings = upsert_bookify_footer(
            repo_root=repo_root,
            module_root=module_root,
            docs_readme=docs_readme,
            chapters=chapters,
            dry_run=args.dry_run,
        )
        total_changed += changed
        total_files += total
        all_warnings.extend(warnings)

        mode = "DRY-RUN" if args.dry_run else "APPLIED"
        print(f"[{mode}] {module}: chapters={total}, changed={changed}")

    if all_warnings:
        print("[WARN] Issues detected:")
        for w in all_warnings[:50]:
            print(w)
        if len(all_warnings) > 50:
            print(f"- ... and {len(all_warnings) - 50} more")

    print(f"[DONE] modules={len(module_items)} total_chapters={total_files} changed={total_changed} dry_run={args.dry_run}")

    # missing chapters are treated as error, because navigation requires an actual file
    missing = [w for w in all_warnings if w.startswith("- missing chapter:")]
    if missing:
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
