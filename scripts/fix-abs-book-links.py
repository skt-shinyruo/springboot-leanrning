#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
将 docs/**/*.md 中以 /book/ 开头的绝对链接，批量修复为相对链接。

目标：
- GitHub Pages 子路径部署下仍可正确跳转
- 与 scripts/check-abs-book-links.py 配套使用

注意：
- 仅修改“Markdown 链接/引用定义/HTML href”中的 /book/...；不会替换代码块中的示例文本
- 对于复杂写法（带 title 的 link 等）如果未命中，会保留原样并在统计里体现“未处理”
"""

from __future__ import annotations

import argparse
import os
import re
import sys
from dataclasses import dataclass
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]
DOCS_DIR = REPO_ROOT / "docs"


MD_LINK_RE = re.compile(r"\]\(\s*(/book(?:/[^)\s]*)?)\s*\)")
MD_REF_RE = re.compile(r"^(\s*\[[^\]]+\]:\s*)(/book(?:/\S*)?)(\s*)$")
HTML_HREF_RE = re.compile(r'(href=")(/book(?:/[^"]*)?)(")')


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        return path.read_text(encoding="utf-8", errors="ignore")


def write_text(path: Path, content: str) -> None:
    path.write_text(content, encoding="utf-8")


def iter_md_files(root: Path) -> list[Path]:
    files = [p for p in root.rglob("*.md") if p.is_file()]
    files.sort(key=lambda p: p.as_posix())
    return files


def to_relative_book_link(current_file: Path, abs_url: str) -> str:
    """
    将 /book/... 转为相对路径（相对 current_file 所在目录）。
    """
    url = abs_url.strip()
    if not url.startswith("/book"):
        return abs_url

    anchor = ""
    if "#" in url:
        url, anchor = url.split("#", 1)
        anchor = "#" + anchor

    # normalize path
    if url in {"/book", "/book/"}:
        target = DOCS_DIR / "book" / "index.md"
    else:
        rest = url.removeprefix("/book").lstrip("/").rstrip("/")
        if not rest:
            target = DOCS_DIR / "book" / "index.md"
        else:
            if rest.endswith(".md"):
                target = DOCS_DIR / "book" / rest
            else:
                target = DOCS_DIR / "book" / f"{rest}.md"

    rel = os.path.relpath(target, start=current_file.parent)
    rel = rel.replace(os.sep, "/")
    return rel + anchor


@dataclass
class FixStats:
    changed_files: int = 0
    replaced_links: int = 0


def fix_file(path: Path, stats: FixStats) -> bool:
    """
    返回是否发生变更。
    """
    in_fence = False
    fence_marker = ""

    out_lines: list[str] = []
    changed = False

    for raw in read_text(path).splitlines(keepends=False):
        line = raw
        stripped = line.strip()
        if stripped.startswith("```") or stripped.startswith("~~~"):
            marker = stripped[:3]
            if not in_fence:
                in_fence = True
                fence_marker = marker
            elif fence_marker == marker:
                in_fence = False
                fence_marker = ""
            out_lines.append(line)
            continue

        if in_fence:
            out_lines.append(line)
            continue

        # Inline markdown links: ](/book/...)
        def md_link_repl(m: re.Match[str]) -> str:
            nonlocal changed
            stats.replaced_links += 1
            changed = True
            new_url = to_relative_book_link(path, m.group(1))
            return f"]({new_url})"

        new_line = MD_LINK_RE.sub(md_link_repl, line)

        # Reference style: [ref]: /book/...
        mref = MD_REF_RE.match(new_line)
        if mref:
            prefix, url, suffix = mref.group(1), mref.group(2), mref.group(3)
            new_url = to_relative_book_link(path, url)
            if new_url != url:
                stats.replaced_links += 1
                changed = True
            new_line = f"{prefix}{new_url}{suffix}"

        # HTML href="/book/..."
        def html_repl(m: re.Match[str]) -> str:
            nonlocal changed
            stats.replaced_links += 1
            changed = True
            new_url = to_relative_book_link(path, m.group(2))
            return f'{m.group(1)}{new_url}{m.group(3)}'

        new_line = HTML_HREF_RE.sub(html_repl, new_line)

        out_lines.append(new_line)

    new_text = "\n".join(out_lines) + "\n"
    if changed and read_text(path) != new_text:
        write_text(path, new_text)
        stats.changed_files += 1
        return True
    return False


def parse_args(argv: list[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="批量修复 docs 中 /book/ 绝对链接为相对链接。")
    parser.add_argument("--root", default=str(DOCS_DIR), help="扫描根目录（默认 docs/）")
    parser.add_argument("--dry-run", action="store_true", help="只统计不写入")
    return parser.parse_args(argv)


def main(argv: list[str]) -> int:
    args = parse_args(argv[1:])
    root = Path(args.root)
    if not root.is_dir():
        print(f"[ERROR] root 不是目录：{root}", file=sys.stderr)
        return 2

    stats = FixStats()
    touched: list[Path] = []

    for md in iter_md_files(root):
        before = read_text(md)
        tmp_stats = FixStats()
        # Dry-run: we still need to detect changes without writing.
        if args.dry_run:
            in_fence = False
            fence_marker = ""
            changed = False
            for raw in before.splitlines():
                stripped = raw.strip()
                if stripped.startswith("```") or stripped.startswith("~~~"):
                    marker = stripped[:3]
                    if not in_fence:
                        in_fence = True
                        fence_marker = marker
                    elif fence_marker == marker:
                        in_fence = False
                        fence_marker = ""
                    continue
                if in_fence:
                    continue
                if MD_LINK_RE.search(raw) or MD_REF_RE.match(raw) or HTML_HREF_RE.search(raw):
                    changed = True
                    break
            if changed:
                touched.append(md)
            continue

        if fix_file(md, stats):
            touched.append(md)

    if args.dry_run:
        print(f"[OK] dry-run: would touch {len(touched)} files")
        return 0

    print(f"[OK] fixed: files={stats.changed_files} links={stats.replaced_links}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
