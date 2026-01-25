#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
全模块“书籍化重写”第一层：去 A–G 契约化排版（自动化批处理）。

目标（第一层，自动化可重复执行）：
1) 移除章节中的 `AG-CONTRACT` 标记；
2) 去掉 `## A.`～`## G.` 的字母前缀；
3) 将 “B. 核心结论” 转换为 MkDocs 的 summary 提示框（更像书的“本章要点”）；
4) 将 BOOKIFY 尾部块中的 Lab/Test 信息提炼为章首 example 提示框（先跑再读）。

注意：
- 本脚本不会尝试“合并/拆章/重排主线时间线”（那是第二层，人工为主）。
- 变更较大，建议在执行后自行做一次站点构建预览（可选）：
  - mkdocs build -f docs-site/mkdocs.yml

用法：
  python3 scripts/rewrite-docs-book-style.py
  python3 scripts/rewrite-docs-book-style.py --dry-run
  python3 scripts/rewrite-docs-book-style.py --module spring-core-beans --module springboot-web-mvc
"""

from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass
from pathlib import Path

from repo_paths import find_module_root


REPO_ROOT = Path(__file__).resolve().parents[1]


AG_CONTRACT_START_RE = re.compile(r"<!--\s*AG-CONTRACT:START\s*-->\s*\n?", re.IGNORECASE)
AG_CONTRACT_END_RE = re.compile(r"<!--\s*AG-CONTRACT:END\s*-->\s*\n?", re.IGNORECASE)

# 捕获 B 段（核心结论）：直到下一段 A–G 或文件结束
B_SECTION_RE = re.compile(
    r"(?ms)^##\s*B\.\s*(?P<title>.+?)\s*\n(?P<body>.*?)(?=^##\s*[A-G]\.\s|\Z)"
)

# A–G heading：只去掉字母前缀
AG_HEADING_PREFIX_RE = re.compile(r"(?m)^##\s*[A-G]\.\s+")

# A 段标题变体：统一改为“导读”
A_HEADING_TO_FOREWORD_RE = re.compile(r"(?m)^##\s*本章定位\b.*$")

# 典型“契约式阅读建议”文本：去字母引用
READING_TIP_REPLACEMENTS: list[tuple[str, str]] = [
    (
        "- 阅读方式建议：先看 B 的结论，再按 C→D 跟主线，最后用 E 跑通闭环。",
        "- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。",
    ),
    (
        "- 如果只看一眼：请先跑一次 E 的最小实验，再回到 C 对照主线。",
        "- 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。",
    ),
]

BOOKIFY_START = "<!-- BOOKIFY:START -->"
BOOKIFY_END = "<!-- BOOKIFY:END -->"

BOOKIFY_BLOCK_RE = re.compile(r"(?ms)<!--\s*BOOKIFY:START\s*-->.*?<!--\s*BOOKIFY:END\s*-->")

# 从 BOOKIFY block 里提取的最小信息（如果不存在也不阻塞）
BOOKIFY_LAB_LINE_RE = re.compile(r"(?m)^\s*-\s*Lab：\s*(?P<lab>.+?)\s*$")
BOOKIFY_TEST_FILE_LINE_RE = re.compile(r"(?m)^\s*-\s*Test file：\s*(?P<path>.+?)\s*$")

CHAPTER_LAB_CALLOUT_TITLE = '!!! example "本章配套实验（先跑再读）"'


@dataclass(frozen=True)
class RewriteStats:
    changed_files: int
    scanned_files: int


def discover_modules(repo_root: Path) -> list[str]:
    docs_root = repo_root / "docs"
    if not docs_root.is_dir():
        return []

    modules: list[str] = []
    for readme in sorted(docs_root.glob("*/*/README.md")):
        module = readme.parent.name
        if find_module_root(repo_root, module) is not None:
            modules.append(module)
    return modules


def resolve_module_docs_root(module: str) -> Path | None:
    candidates = sorted((REPO_ROOT / "docs").glob(f"*/{module}"))
    return candidates[0] if candidates else None


def read_text_utf8(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        return path.read_text(encoding="utf-8", errors="ignore")


def write_text_utf8(path: Path, content: str) -> None:
    path.write_text(content, encoding="utf-8")


def indent_for_admonition(block: str) -> str:
    lines = block.splitlines()
    out: list[str] = []
    for line in lines:
        if line.strip() == "":
            out.append("")
        else:
            out.append("    " + line)
    return "\n".join(out)


def convert_b_section_to_summary(text: str) -> str:
    def repl(m: re.Match[str]) -> str:
        body = (m.group("body") or "").strip("\n")
        if not body.strip():
            # 空内容：退化为一个空 summary（仍保持格式）
            return f'{CHAPTER_LAB_CALLOUT_TITLE}\n\n    - （未提取到实验入口）\n\n'
        return f'!!! summary "本章要点"\n\n{indent_for_admonition(body)}\n\n'

    return B_SECTION_RE.sub(repl, text)


def extract_lab_callout_from_bookify(text: str) -> str | None:
    if BOOKIFY_START not in text or BOOKIFY_END not in text:
        return None

    m = BOOKIFY_BLOCK_RE.search(text)
    if not m:
        return None

    block = m.group(0)
    labs = [m.group("lab").strip() for m in BOOKIFY_LAB_LINE_RE.finditer(block)]
    test_files = [m.group("path").strip() for m in BOOKIFY_TEST_FILE_LINE_RE.finditer(block)]

    # 只要有一个信息就生成 callout
    items: list[str] = []
    if labs:
        items.append(f"- Lab：{labs[0]}")
    if test_files:
        items.append(f"- Test file：{test_files[0]}")
    if not items:
        return None

    body = "\n".join("    " + it for it in items)
    return f"{CHAPTER_LAB_CALLOUT_TITLE}\n\n{body}\n\n"


def insert_callout_after_summary(text: str, callout: str) -> str:
    if CHAPTER_LAB_CALLOUT_TITLE in text:
        return text

    lines = text.splitlines()
    for i, line in enumerate(lines):
        if line.startswith('!!! summary "本章要点"'):
            # 向下找到 summary block 的结束位置：
            # summary 内容行通常以 4 空格缩进；遇到非缩进且非空行即结束
            j = i + 1
            while j < len(lines):
                cur = lines[j]
                if cur.strip() == "":
                    j += 1
                    continue
                if cur.startswith("    "):
                    j += 1
                    continue
                break

            # j 是下一块内容的开始，插入 callout
            out = lines[:j] + [""] + callout.rstrip("\n").splitlines() + [""] + lines[j:]
            return "\n".join(out) + ("\n" if text.endswith("\n") else "")

    # 没有 summary：插在标题之后
    for i, line in enumerate(lines):
        if line.startswith("# "):
            # 找到标题后的第一个空行
            j = i + 1
            while j < len(lines) and lines[j].strip() != "":
                j += 1
            out = lines[: j + 1] + callout.rstrip("\n").splitlines() + [""] + lines[j + 1 :]
            return "\n".join(out) + ("\n" if text.endswith("\n") else "")

    return text


def normalize_newlines(text: str) -> str:
    # 避免批处理后出现过多空行
    text = text.replace("\r\n", "\n").replace("\r", "\n")
    text = re.sub(r"\n{4,}", "\n\n\n", text)
    return text


def rewrite_markdown(text: str) -> str:
    original = text

    # 1) 去掉 AG-CONTRACT 注释
    text = AG_CONTRACT_START_RE.sub("", text)
    text = AG_CONTRACT_END_RE.sub("", text)

    # 2) 典型“契约阅读建议”去字母引用（尽量在转换前做，避免误伤）
    for src, dst in READING_TIP_REPLACEMENTS:
        text = text.replace(src, dst)

    # 3) 将 B 段改为 summary
    text = convert_b_section_to_summary(text)

    # 4) 去掉 A–G heading 的字母前缀
    text = AG_HEADING_PREFIX_RE.sub("## ", text)

    # 5) A 段标题改为更书籍化的“导读”
    text = A_HEADING_TO_FOREWORD_RE.sub("## 导读", text)

    # 6) 章首插入“先跑再读”的实验入口（来自 BOOKIFY）
    callout = extract_lab_callout_from_bookify(text)
    if callout:
        text = insert_callout_after_summary(text, callout)

    text = normalize_newlines(text)

    return text if text != original else original


def iter_module_docs_markdown(module: str) -> list[Path]:
    docs_root = resolve_module_docs_root(module)
    if docs_root is None or not docs_root.is_dir():
        return []

    paths = [p for p in docs_root.rglob("*.md") if p.is_file()]
    # 排除目录页（通常无 A–G，不需要批处理）
    paths = [p for p in paths if p.name != "README.md"]
    return sorted(paths)


def run(modules: list[str], dry_run: bool) -> RewriteStats:
    changed_files = 0
    scanned_files = 0

    for module in modules:
        for md in iter_module_docs_markdown(module):
            scanned_files += 1
            before = read_text_utf8(md)
            after = rewrite_markdown(before)
            if after == before:
                continue
            changed_files += 1
            if not dry_run:
                write_text_utf8(md, after)

    return RewriteStats(changed_files=changed_files, scanned_files=scanned_files)


def parse_args(argv: list[str]) -> argparse.Namespace:
    p = argparse.ArgumentParser(prog="rewrite-docs-book-style.py")
    p.add_argument("--dry-run", action="store_true", help="仅统计将改动的文件数量，不写入文件。")
    p.add_argument(
        "--module",
        action="append",
        default=[],
        help="仅处理指定模块（可重复）。例如：--module spring-core-beans",
    )
    return p.parse_args(argv)


def main(argv: list[str]) -> int:
    args = parse_args(argv[1:])
    modules = args.module or discover_modules(REPO_ROOT)
    if not modules:
        print("[ERROR] 未发现任何模块目录页（docs/<topic>/<module>/README.md）。", file=sys.stderr)
        return 2

    stats = run(modules, dry_run=args.dry_run)
    mode = "DRY-RUN" if args.dry_run else "WRITE"
    print(f"[OK] {mode} scanned={stats.scanned_files} changed={stats.changed_files}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
