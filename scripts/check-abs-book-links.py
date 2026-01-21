#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
检查 docs/**/*.md 中是否存在以 /book/ 开头的绝对链接。

原因：
- GitHub Pages 往往部署在子路径（/repo/），/book/ 绝对路径会跳到错误位置
- 文档内部链接建议使用相对路径（指向 docs/book/*.md）

本脚本用于门禁（CI/gate）：发现即失败。
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]
DOCS_DIR = REPO_ROOT / "docs"


MD_LINK_RE = re.compile(r"\]\(\s*(/book(?:/[^)\s]*)?)\s*\)")
MD_REF_RE = re.compile(r"^\s*\[[^\]]+\]:\s*(/book(?:/\S*)?)\s*$")
HTML_HREF_RE = re.compile(r'href="(/book(?:/[^"]*)?)"')


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        return path.read_text(encoding="utf-8", errors="ignore")


def iter_md_files(root: Path) -> list[Path]:
    files = [p for p in root.rglob("*.md") if p.is_file()]
    files.sort(key=lambda p: p.as_posix())
    return files


def scan_file(path: Path) -> list[tuple[int, str]]:
    """
    返回命中列表：(line_no, matched_url)
    仅扫描非代码块区域，避免误报（例如代码片段展示 URL）。
    """
    hits: list[tuple[int, str]] = []
    in_fence = False
    fence_marker = ""

    for idx, raw in enumerate(read_text(path).splitlines(), start=1):
        line = raw.rstrip("\n")

        stripped = line.strip()
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

        m = MD_LINK_RE.search(line)
        if m:
            hits.append((idx, m.group(1)))
            continue

        m = MD_REF_RE.match(line)
        if m:
            hits.append((idx, m.group(1)))
            continue

        m = HTML_HREF_RE.search(line)
        if m:
            hits.append((idx, m.group(1)))
            continue

    return hits


def parse_args(argv: list[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="检查 docs 中 /book/ 绝对链接（门禁）。")
    parser.add_argument("--root", default=str(DOCS_DIR), help="扫描根目录（默认 docs/）")
    parser.add_argument("--max", type=int, default=20, help="最多输出多少条命中样本（默认 20）")
    return parser.parse_args(argv)


def main(argv: list[str]) -> int:
    args = parse_args(argv[1:])
    root = Path(args.root)

    if not root.is_dir():
        print(f"[ERROR] root 不是目录：{root}", file=sys.stderr)
        return 2

    total_hits = 0
    samples: list[str] = []

    for md in iter_md_files(root):
        hits = scan_file(md)
        if not hits:
            continue
        total_hits += len(hits)
        for line_no, url in hits:
            if len(samples) < args.max:
                rel = md.relative_to(REPO_ROOT).as_posix()
                samples.append(f"- {rel}:{line_no} -> {url}")

    if total_hits == 0:
        print("[OK] no absolute /book/ links found")
        return 0

    print(f"[ERROR] 检测到 /book/ 绝对链接：{total_hits} 处（示例最多 {args.max} 条）", file=sys.stderr)
    for s in samples:
        print(s, file=sys.stderr)
    print("[HINT] 请运行：python3 scripts/fix-abs-book-links.py", file=sys.stderr)
    return 2


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))

