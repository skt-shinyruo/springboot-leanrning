#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
检查 docs 章节契约（Chapter Contract）是否满足最低闭环要求。

本脚本的目标是“快速发现缺口 + 给出修复建议”，而不是对正文内容做价值判断。

默认检查项（对 SSOT 章节集合生效）：
- 章节学习卡片（CHAPTER-CARD）存在且字段完整
- 全书导航（GLOBAL-BOOK-NAV）存在且 marker 完整
- 章节尾部入口块（BOOKIFY）存在且 marker 完整

用法：
  python3 scripts/check-chapter-contract.py
  python3 scripts/check-chapter-contract.py --root docs
  python3 scripts/check-chapter-contract.py --root docs --root spring-core-modules
  python3 scripts/check-chapter-contract.py --max-report 80
  python3 scripts/check-chapter-contract.py --mode all
"""

from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]

MD_LINK_WITH_TEXT_RE = re.compile(r"(!?)\[([^\]]*)\]\(([^)]+)\)")
SCHEME_RE = re.compile(r"^[a-zA-Z][a-zA-Z0-9+.-]*:")

CHAPTER_CARD_START = "<!-- CHAPTER-CARD:START -->"
CHAPTER_CARD_END = "<!-- CHAPTER-CARD:END -->"
BOOKIFY_START = "<!-- BOOKIFY:START -->"
BOOKIFY_END = "<!-- BOOKIFY:END -->"
GLOBAL_NAV_START = "<!-- GLOBAL-BOOK-NAV:START -->"
GLOBAL_NAV_END = "<!-- GLOBAL-BOOK-NAV:END -->"

CARD_REQUIRED_FIELDS = ("知识点", "怎么使用", "原理", "源码入口", "推荐 Lab")


@dataclass(frozen=True)
class Issue:
    path: str
    level: str  # error|warn
    kind: str
    detail: str
    hint: str


def read_text_utf8(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        return path.read_text(encoding="utf-8", errors="replace")


def is_skip_file(md: Path) -> bool:
    # 目录页/全局目录页不强制契约（它们是索引而不是“章节”）
    if md.name in {"README.md", "SUMMARY.md"}:
        return True
    return False


def extract_card_block(text: str) -> str | None:
    start = text.find(CHAPTER_CARD_START)
    if start < 0:
        return None
    end = text.find(CHAPTER_CARD_END, start)
    if end < 0:
        return None
    end += len(CHAPTER_CARD_END)
    return text[start:end]


def card_has_field(block: str, field_name: str) -> bool:
    return bool(re.search(rf"(?m)^\s*-\s*{re.escape(field_name)}：\s*.+$", block))


def resolve_root(raw: str) -> Path:
    p = Path(raw)
    if not p.is_absolute():
        p = (REPO_ROOT / p).resolve()
    return p


def is_external_or_ignored_link(dest: str) -> bool:
    if not dest:
        return True
    if dest.startswith("#"):
        return True
    if dest.startswith("//"):
        return True
    if dest.startswith("/"):
        # MkDocs 站内路由：不在本脚本范围内
        return True
    return bool(SCHEME_RE.match(dest))


def normalize_link_destination(raw: str) -> str | None:
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


def iter_chapter_links_from_readme(readme: Path) -> list[Path]:
    """
    以 docs/<topic>/<module>/README.md 作为 SSOT，解析其中的 .md 链接并返回章节 Path。
    重要约束：仅把 README 所在目录下的页面当作“本模块章节”，避免跨模块公共页（docs/book）导致重复改写。
    """
    try:
        content = readme.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        content = readme.read_text(encoding="utf-8", errors="replace")

    chapters: list[Path] = []
    for m in MD_LINK_WITH_TEXT_RE.finditer(content):
        if m.group(1) == "!":
            continue
        target_raw = m.group(3)
        dest = normalize_link_destination(target_raw)
        if dest is None or is_external_or_ignored_link(dest):
            continue
        if not dest.endswith(".md"):
            continue
        chapter = (readme.parent / dest).resolve()
        if chapter == readme.resolve():
            continue
        # 只接受模块目录页所在目录下的章节
        try:
            chapter.relative_to(readme.parent)
        except ValueError:
            continue
        chapters.append(chapter)

    # 去重并保持顺序（README 中可能出现重复链接）
    seen: set[Path] = set()
    out: list[Path] = []
    for p in chapters:
        if p in seen:
            continue
        seen.add(p)
        out.append(p)
    return out


def iter_ssot_targets_for_docs_root(docs_root: Path) -> list[Path]:
    """
    SSOT 模式下：
    - 模块章节：来自 docs/*/*/README.md 的链接清单（仅模块目录下）
    - Book-only：docs/book/**/*.md（包含工具页/索引页）
    """
    targets: list[Path] = []

    for readme in sorted(docs_root.glob("*/*/README.md")):
        targets.extend(iter_chapter_links_from_readme(readme))

    book_root = docs_root / "book"
    if book_root.is_dir():
        for md in sorted(book_root.rglob("*.md")):
            if md.is_file():
                targets.append(md.resolve())

    # 去重
    return sorted(set(targets))


def iter_markdown_files(root: Path) -> list[Path]:
    if root.is_file() and root.suffix == ".md":
        return [root]
    if not root.is_dir():
        return []

    md_files: list[Path] = []
    for md in sorted(root.rglob("*.md")):
        if not md.is_file():
            continue
        # 排除 docs-site 的生成物与 helloagents/history/plan 归档
        rel = md.relative_to(REPO_ROOT).as_posix()
        if rel.startswith("docs-site/.generated/") or rel.startswith("docs-site/.site/"):
            continue
        if rel.startswith("helloagents/history/") or rel.startswith("helloagents/plan/"):
            continue
        md_files.append(md)
    return md_files


def check_one_file(md: Path) -> list[Issue]:
    issues: list[Issue] = []
    text = read_text_utf8(md)
    rel = md.relative_to(REPO_ROOT).as_posix()

    if CHAPTER_CARD_START not in text or CHAPTER_CARD_END not in text:
        issues.append(
            Issue(
                path=rel,
                level="error",
                kind="missing_chapter_card",
                detail="缺少 CHAPTER-CARD marker（或 marker 不完整）",
                hint="建议运行：python3 scripts/upsert-chapter-cards.py",
            )
        )
    else:
        block = extract_card_block(text)
        if not block:
            issues.append(
                Issue(
                    path=rel,
                    level="error",
                    kind="broken_chapter_card",
                    detail="CHAPTER-CARD marker 不可解析（START/END 异常）",
                    hint="建议运行：python3 scripts/upsert-chapter-cards.py（会重建卡片区块）",
                )
            )
        else:
            missing_fields = [f for f in CARD_REQUIRED_FIELDS if not card_has_field(block, f)]
            if missing_fields:
                issues.append(
                    Issue(
                        path=rel,
                        level="error",
                        kind="missing_card_fields",
                        detail=f"卡片缺字段：{', '.join(missing_fields)}",
                        hint="建议运行：python3 scripts/upsert-chapter-cards.py（会补齐字段）",
                    )
                )

    if GLOBAL_NAV_START not in text or GLOBAL_NAV_END not in text:
        issues.append(
            Issue(
                path=rel,
                level="warn",
                kind="missing_global_nav",
                detail="缺少 GLOBAL-BOOK-NAV marker（或 marker 不完整）",
                hint="建议运行：python3 scripts/bookify-global-chapters.py（如果你需要全书级页首导航）",
            )
        )

    # BOOKIFY 对模块章节更重要；Book-only 页面可先容忍缺失（通常用全书导航串联）
    bookify_level = "warn" if rel.startswith("docs/book/") else "error"
    if BOOKIFY_START not in text or BOOKIFY_END not in text:
        issues.append(
            Issue(
                path=rel,
                level=bookify_level,
                kind="missing_bookify",
                detail="缺少 BOOKIFY marker（或 marker 不完整）",
                hint="建议运行：python3 scripts/bookify-docs.py",
            )
        )

    return issues


def parse_args(argv: list[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="检查 Markdown 章节契约（Chapter Contract）完整性。")
    parser.add_argument(
        "--mode",
        choices=["ssot", "all"],
        default="ssot",
        help="检查模式：ssot=仅检查目录页（README）引用的章节 + docs/book；all=扫描 root 下所有 .md（默认：ssot）。",
    )
    parser.add_argument(
        "--root",
        action="append",
        default=[],
        help="要检查的根目录或文件（可重复，默认：docs）。",
    )
    parser.add_argument(
        "--max-report",
        type=int,
        default=50,
        help="最多输出多少条问题明细（默认 50）。",
    )
    return parser.parse_args(argv)


def main(argv: list[str]) -> int:
    args = parse_args(argv[1:])
    roots = args.root or ["docs"]

    md_files: list[Path] = []
    invalid_roots: list[str] = []
    for raw in roots:
        root = resolve_root(raw)
        if not root.exists():
            invalid_roots.append(raw)
            continue
        if args.mode == "ssot" and root.is_dir() and root.samefile(REPO_ROOT / "docs"):
            md_files.extend(iter_ssot_targets_for_docs_root(root))
        else:
            md_files.extend(iter_markdown_files(root))

    if invalid_roots:
        print(f"[ERROR] root 不存在：{', '.join(invalid_roots)}", file=sys.stderr)
        return 2

    md_files = sorted(set(md_files))
    checked = 0
    skipped = 0
    issues: list[Issue] = []

    for md in md_files:
        if is_skip_file(md):
            skipped += 1
            continue
        checked += 1
        issues.extend(check_one_file(md))

    roots_display = ", ".join(roots)
    err_count = sum(1 for it in issues if it.level == "error")
    warn_count = sum(1 for it in issues if it.level == "warn")
    print(
        f"[CHECK] roots=[{roots_display}] files={len(md_files)} checked={checked} skipped={skipped} "
        f"errors={err_count} warnings={warn_count}"
    )

    if not issues:
        return 0

    for item in issues[: max(0, args.max_report)]:
        tag = "[ERROR]" if item.level == "error" else "[WARN]"
        print(f"{tag} {item.path} :: {item.kind} :: {item.detail} :: {item.hint}", file=sys.stderr)

    if err_count:
        print("[ERROR] 检测到章节契约缺口（error），请按提示修复后重试。", file=sys.stderr)
        return 1
    print("[WARN] 检测到章节契约警告（warn），建议按提示逐步补齐。", file=sys.stderr)
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
