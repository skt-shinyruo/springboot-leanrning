#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
检查 Markdown 相对链接的目标是否存在（断链=0）。

约定：
- 仅检查仓库内的相对链接（相对路径 / 目录 / .md 文件）。
- 忽略外部链接：scheme（http/https/mailto/...）、以 # 开头的锚点、以 / 开头的站内路由。
- 会自动忽略图片链接（![](...)）。

用法：
  python3 scripts/check-md-relative-links.py
  python3 scripts/check-md-relative-links.py --root docs
  python3 scripts/check-md-relative-links.py --root docs --max-report 80
"""

from __future__ import annotations

import argparse
import os
import re
import sys
from dataclasses import dataclass
from pathlib import Path
from urllib.parse import unquote


REPO_ROOT = Path(__file__).resolve().parents[1]

MD_LINK_WITH_TEXT_RE = re.compile(r"(!?)\[([^\]]*)\]\(([^)]+)\)")
SCHEME_RE = re.compile(r"^[a-zA-Z][a-zA-Z0-9+.-]*:")


@dataclass(frozen=True)
class MissingLink:
    source: str
    target: str
    resolved: str


def is_external_or_ignored(dest: str) -> bool:
    if not dest:
        return True
    if dest.startswith("#"):
        return True
    if dest.startswith("//"):
        return True
    if dest.startswith("/"):
        # 站内路由（MkDocs）：不在本脚本范围内
        return True
    return bool(SCHEME_RE.match(dest))


def normalize_destination(raw: str) -> str | None:
    dest = raw.strip()
    if not dest:
        return None
    if dest.startswith("<") and dest.endswith(">"):
        dest = dest[1:-1].strip()
    if " " in dest or "\t" in dest:
        dest = re.split(r"\s+", dest, maxsplit=1)[0]
    dest = unquote(dest)
    if "#" in dest:
        dest = dest.split("#", 1)[0]
    return dest or None


def read_text(md: Path) -> str:
    try:
        return md.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        return md.read_text(encoding="utf-8", errors="replace")


def iter_relative_link_targets(md_file: Path) -> list[str]:
    content = read_text(md_file)
    targets: list[str] = []
    for m in MD_LINK_WITH_TEXT_RE.finditer(content):
        is_image = m.group(1) == "!"
        if is_image:
            continue
        target_raw = m.group(3)
        dest = normalize_destination(target_raw)
        if dest is None:
            continue
        if is_external_or_ignored(dest):
            continue
        targets.append(dest)
    return targets


def resolve_target(md_file: Path, target: str) -> Path:
    return (md_file.parent / target).resolve()


def is_under_repo_root(path: Path) -> bool:
    try:
        path.relative_to(REPO_ROOT)
        return True
    except ValueError:
        return False


def check_links(root: Path) -> list[MissingLink]:
    missing: list[MissingLink] = []
    for md in sorted(root.rglob("*.md")):
        if not md.is_file():
            continue
        for target in iter_relative_link_targets(md):
            resolved = resolve_target(md, target)
            if not is_under_repo_root(resolved):
                continue
            if resolved.exists():
                continue
            missing.append(
                MissingLink(
                    source=md.relative_to(REPO_ROOT).as_posix(),
                    target=target,
                    resolved=resolved.relative_to(REPO_ROOT).as_posix(),
                )
            )
    return missing


def parse_args(argv: list[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="检查 docs 下 Markdown 相对链接是否断链。")
    parser.add_argument(
        "--root",
        default="docs",
        help="要检查的根目录（默认：docs）。",
    )
    parser.add_argument(
        "--max-report",
        type=int,
        default=50,
        help="最多输出多少条断链明细（默认 50）。",
    )
    return parser.parse_args(argv)


def main(argv: list[str]) -> int:
    args = parse_args(argv[1:])
    root = Path(args.root)
    if not root.is_absolute():
        root = (REPO_ROOT / root).resolve()

    if not root.is_dir():
        print(f"[ERROR] 目录不存在：{root}", file=sys.stderr)
        return 2

    missing = check_links(root)

    print(f"[CHECK] root={root.relative_to(REPO_ROOT)} files={len(list(root.rglob('*.md')))} missing={len(missing)}")

    if not missing:
        return 0

    for item in missing[: max(0, args.max_report)]:
        print(f"[MISSING] {item.source} -> ({item.target}) resolved={item.resolved}", file=sys.stderr)

    print(
        "[ERROR] 检测到 Markdown 相对链接断链，请修复后重试。",
        file=sys.stderr,
    )
    return 1


if __name__ == "__main__":
    raise SystemExit(main(os.sys.argv))
