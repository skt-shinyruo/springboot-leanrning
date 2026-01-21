#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
生成“章节集合 SSOT”清单（用于全量文档改写/进度追踪）。

约定：
- 模块章节：以 `docs/<topic>/<module>/README.md` 内的 Markdown 链接清单为 SSOT（排除 README 本身）
- Book-only：`docs/book/**/*.md`

输出：
- 默认输出到 stdout（TSV）
- 可通过 --output 写入文件

用法：
  python3 scripts/generate-docs-chapter-list.py
  python3 scripts/generate-docs-chapter-list.py --format md --output helloagents/plan/.../chapters.md
"""

from __future__ import annotations

import argparse
import json
import os
import re
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

from repo_paths import find_module_root


MD_LINK_WITH_TEXT_RE = re.compile(r"(!?)\[([^\]]*)\]\(([^)]+)\)")
SCHEME_RE = re.compile(r"^[a-zA-Z][a-zA-Z0-9+.-]*:")


@dataclass(frozen=True)
class Chapter:
    kind: str  # module|book
    module: str  # module name or ""
    path: str  # repo-relative posix path
    title: str  # best-effort extracted title


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
    if dest.startswith("<") and dest.endswith(">"):
        dest = dest[1:-1].strip()
    if " " in dest or "\t" in dest:
        dest = re.split(r"\s+", dest, maxsplit=1)[0]
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
        # 仅收录确实存在代码模块目录的条目（避免把非模块目录误判为模块）
        if find_module_root(repo_root, module) is None:
            continue
        modules.append((module, readme))

    return modules


def iter_links_from_docs_readme(readme: Path) -> Iterable[tuple[str, str]]:
    content = readme.read_text(encoding="utf-8", errors="replace")
    for m in MD_LINK_WITH_TEXT_RE.finditer(content):
        is_image = m.group(1) == "!"
        if is_image:
            continue
        title = (m.group(2) or "").strip()
        target_raw = m.group(3)
        yield title, target_raw


def iter_module_chapters(repo_root: Path, module: str, docs_readme: Path) -> list[Chapter]:
    if not docs_readme.is_file():
        return []

    # 使用“最后一次出现”作为章节顺序（避免目录页里“快速定位”类重复链接干扰主线顺序）
    index = 0
    last: dict[Path, tuple[int, str]] = {}
    for title, target_raw in iter_links_from_docs_readme(docs_readme):
        index += 1
        target = normalize_md_link_target(target_raw)
        if target is None or is_external_link(target):
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

        last[chapter] = (index, title or chapter.name)

    ordered = sorted(last.items(), key=lambda it: it[1][0])
    chapters: list[Chapter] = []
    for chapter_path, (_, title_from_readme) in ordered:
        title = extract_title(chapter_path) or title_from_readme
        chapters.append(
            Chapter(
                kind="module",
                module=module,
                path=chapter_path.relative_to(repo_root).as_posix(),
                title=title,
            )
        )
    return chapters


def iter_book_pages(repo_root: Path) -> list[Chapter]:
    book_root = repo_root / "docs" / "book"
    if not book_root.is_dir():
        return []
    pages: list[Chapter] = []
    for p in sorted(book_root.rglob("*.md")):
        if not p.is_file():
            continue
        title = extract_title(p) or p.name
        pages.append(
            Chapter(
                kind="book",
                module="",
                path=p.relative_to(repo_root).as_posix(),
                title=title,
            )
        )
    return pages


def extract_title(md_file: Path) -> str | None:
    try:
        text = md_file.read_text(encoding="utf-8", errors="replace")
    except OSError:
        return None
    for line in text.splitlines():
        if line.startswith("# "):
            return line.removeprefix("# ").strip() or None
    return None


def parse_args(argv: list[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        prog="generate-docs-chapter-list.py",
        description="生成模块 docs 与 Book-only 的章节清单（SSOT）。",
    )
    parser.add_argument("--include-modules", action="store_true", default=True, help="包含模块 docs 章节（默认开启）")
    parser.add_argument("--exclude-modules", action="store_true", help="不包含模块 docs 章节")
    parser.add_argument("--include-book", action="store_true", default=True, help="包含 Book-only 页面（默认开启）")
    parser.add_argument("--exclude-book", action="store_true", help="不包含 Book-only 页面")
    parser.add_argument(
        "--format",
        choices=["tsv", "md", "json"],
        default="tsv",
        help="输出格式：tsv（默认）/ md（Markdown checklist）/ json",
    )
    parser.add_argument("--output", help="输出文件路径（默认 stdout）")
    return parser.parse_args(argv)


def render_tsv(chapters: list[Chapter]) -> str:
    lines = ["kind\tmodule\tpath\ttitle"]
    for c in chapters:
        lines.append(f"{c.kind}\t{c.module}\t{c.path}\t{c.title}")
    return "\n".join(lines) + "\n"


def render_md_checklist(chapters: list[Chapter]) -> str:
    lines: list[str] = []
    lines.append("# 章节清单（SSOT）\n")
    lines.append("> 本文件用于追踪“章节学习卡片（五问闭环）”覆盖进度。\n")
    lines.append("")
    for c in chapters:
        label = f"{c.kind}:{c.module}" if c.kind == "module" else "book"
        lines.append(f"- [ ] ({label}) `{c.path}` - {c.title}")
    lines.append("")
    return "\n".join(lines)


def main(argv: list[str]) -> int:
    repo_root = Path(__file__).resolve().parents[1]
    args = parse_args(argv[1:])

    include_modules = args.include_modules and not args.exclude_modules
    include_book = args.include_book and not args.exclude_book

    chapters: list[Chapter] = []

    if include_modules:
        for module_name, docs_readme in discover_modules(repo_root):
            chapters.extend(iter_module_chapters(repo_root, module_name, docs_readme))

    if include_book:
        chapters.extend(iter_book_pages(repo_root))

    if args.format == "json":
        out = json.dumps([c.__dict__ for c in chapters], ensure_ascii=False, indent=2) + "\n"
    elif args.format == "md":
        out = render_md_checklist(chapters)
    else:
        out = render_tsv(chapters)

    if args.output:
        out_path = Path(args.output)
        abs_path = (repo_root / out_path).resolve() if not out_path.is_absolute() else out_path.resolve()
        abs_path.parent.mkdir(parents=True, exist_ok=True)
        abs_path.write_text(out, encoding="utf-8")
    else:
        print(out, end="")

    return 0


if __name__ == "__main__":
    raise SystemExit(main(os.sys.argv))
