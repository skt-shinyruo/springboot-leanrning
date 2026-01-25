#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
全站“章节正文二次书籍化”批处理脚本（V2）。

目标（第二层，尽量自动化、可重复执行）：
1) 以“章节学习卡片（五问闭环）”作为事实来源（SSOT），为章节补齐更稳定的叙事骨架；
2) 清理常见空块与重复入口（例如：空的“一句话总结”、重复的实验入口清单）；
3) 对不同页面类型分流处理：普通章节 / 工具页 / redirect 页（避免一刀切）。

重要约束：
- 不凭空编造机制细节：生成内容以卡片字段为依据，语气以“建议/推荐/可验证”为主；
- 幂等：支持多次重复执行；脚本生成片段带有 BOOKLIKE-V2 marker，便于更新与避免重复插入；
- 保留并不破坏现有 marker：CHAPTER-CARD / GLOBAL-BOOK-NAV / BOOKIFY。

用法：
  # 自动发现（模块 docs + Book）
  python3 scripts/rewrite-chapters-booklike-v2.py

  # 指定模块（可重复）
  python3 scripts/rewrite-chapters-booklike-v2.py --module springboot-basics --module spring-core-beans

  # 只处理 Book
  python3 scripts/rewrite-chapters-booklike-v2.py --exclude-modules --include-book

  # dry-run + 输出报告（JSON / Markdown）
  python3 scripts/rewrite-chapters-booklike-v2.py --dry-run --report helloagents/plan/.../booklike-v2-report.json
  python3 scripts/rewrite-chapters-booklike-v2.py --dry-run --report helloagents/plan/.../booklike-v2-report.md

  # 使用外部章节清单（建议配合 generate-docs-chapter-list.py）
  python3 scripts/rewrite-chapters-booklike-v2.py --chapter-list /path/to/chapters.json
"""

from __future__ import annotations

import argparse
import json
import os
import re
import sys
from dataclasses import asdict, dataclass, field
from pathlib import Path
from typing import Iterable

from repo_paths import find_module_root, to_maven_artifact_id


REPO_ROOT = Path(__file__).resolve().parents[1]

MD_LINK_WITH_TEXT_RE = re.compile(r"(!?)\[([^\]]*)\]\(([^)]+)\)")
SCHEME_RE = re.compile(r"^[a-zA-Z][a-zA-Z0-9+.-]*:")

CHAPTER_CARD_START = "<!-- CHAPTER-CARD:START -->"
CHAPTER_CARD_END = "<!-- CHAPTER-CARD:END -->"

BOOKIFY_BLOCK_RE = re.compile(r"(?ms)<!--\s*BOOKIFY:START\s*-->.*?<!--\s*BOOKIFY:END\s*-->")

BOOKLIKE_V2_INTRO_START = "<!-- BOOKLIKE-V2:INTRO:START -->"
BOOKLIKE_V2_INTRO_END = "<!-- BOOKLIKE-V2:INTRO:END -->"
BOOKLIKE_V2_EVIDENCE_START = "<!-- BOOKLIKE-V2:EVIDENCE:START -->"
BOOKLIKE_V2_EVIDENCE_END = "<!-- BOOKLIKE-V2:EVIDENCE:END -->"
BOOKLIKE_V2_SUMMARY_START = "<!-- BOOKLIKE-V2:SUMMARY:START -->"
BOOKLIKE_V2_SUMMARY_END = "<!-- BOOKLIKE-V2:SUMMARY:END -->"

CHAPTER_LAB_CALLOUT_LINE = '!!! example "本章配套实验（先跑再读）"'

# 这些标题如果出现为空，几乎总是“模板残留/空块”
EMPTY_BLOCK_WATCHLIST = [
    "导读",
    "证据链",
    "小结与下一章",
    "一句话总结",
]


@dataclass(frozen=True)
class ChapterRef:
    kind: str  # module|book
    module: str
    path: str  # repo-relative posix
    title: str


@dataclass(frozen=True)
class ChapterCard:
    knowledge_point: str
    how_to_use: str
    principle: str
    source_entry: str
    recommended_lab: str


@dataclass
class RewriteWarning:
    kind: str
    message: str


@dataclass
class RewriteItem:
    kind: str
    module: str
    path: str
    page_type: str
    status: str  # changed|skipped|failed
    warnings: list[RewriteWarning] = field(default_factory=list)
    error: str | None = None


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


def read_text_utf8(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        return path.read_text(encoding="utf-8", errors="replace")


def write_text_utf8(path: Path, content: str) -> None:
    path.write_text(content, encoding="utf-8")


def normalize_blank_lines(text: str) -> str:
    # 统一多余空行，避免批处理反复累积
    text = re.sub(r"\n{4,}", "\n\n\n", text)
    if not text.endswith("\n"):
        text += "\n"
    return text


def discover_modules(repo_root: Path) -> list[str]:
    """
    发现“有模块目录页”的模块：以 docs/<topic>/<module>/README.md 为准，并要求代码模块目录存在。
    """
    docs_root = repo_root / "docs"
    if not docs_root.is_dir():
        return []

    modules: list[str] = []
    for readme in sorted(docs_root.glob("*/*/README.md")):
        module = readme.parent.name
        if find_module_root(repo_root, module) is None:
            continue
        modules.append(module)
    return modules


def resolve_docs_readme(repo_root: Path, module: str) -> Path | None:
    candidates = sorted((repo_root / "docs").glob(f"*/{module}/README.md"))
    return candidates[0] if candidates else None


def iter_links_from_docs_readme(readme: Path) -> Iterable[tuple[str, str]]:
    content = readme.read_text(encoding="utf-8", errors="replace")
    for m in MD_LINK_WITH_TEXT_RE.finditer(content):
        is_image = m.group(1) == "!"
        if is_image:
            continue
        title = (m.group(2) or "").strip()
        target_raw = m.group(3)
        yield title, target_raw


def extract_title(md: Path) -> str | None:
    try:
        content = md.read_text(encoding="utf-8", errors="replace")
    except OSError:
        return None
    for line in content.splitlines():
        if line.startswith("# "):
            return line[2:].strip()
    return None


def iter_module_chapters(repo_root: Path, docs_readme: Path) -> list[ChapterRef]:
    if not docs_readme.is_file():
        return []

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
    out: list[ChapterRef] = []
    for chapter_path, (_, title_from_readme) in ordered:
        title = extract_title(chapter_path) or title_from_readme
        out.append(
            ChapterRef(
                kind="module",
                module=docs_readme.parent.name,
                path=chapter_path.relative_to(repo_root).as_posix(),
                title=title,
            )
        )
    return out


def iter_book_chapters(repo_root: Path) -> list[ChapterRef]:
    root = repo_root / "docs" / "book"
    if not root.is_dir():
        return []
    out: list[ChapterRef] = []
    for md in sorted(root.rglob("*.md")):
        title = extract_title(md) or md.name
        out.append(
            ChapterRef(kind="book", module="", path=md.relative_to(repo_root).as_posix(), title=title)
        )
    return out


def load_chapters(
    *,
    repo_root: Path,
    chapter_list: Path | None,
    include_modules: bool,
    include_book: bool,
    modules: list[str] | None,
) -> list[ChapterRef]:
    if chapter_list:
        raw = json.loads(chapter_list.read_text(encoding="utf-8"))
        chapters = [ChapterRef(**c) for c in raw]
    else:
        chapters: list[ChapterRef] = []
        if include_modules:
            for module_name in discover_modules(repo_root):
                if modules and module_name not in modules:
                    continue
                docs_readme = resolve_docs_readme(repo_root, module_name)
                if docs_readme is None:
                    continue
                chapters.extend(iter_module_chapters(repo_root, docs_readme))
        if include_book:
            chapters.extend(iter_book_chapters(repo_root))

    if modules:
        # 章节清单来自外部时，也允许按 module 过滤（仅对 module kind 生效）
        filtered: list[ChapterRef] = []
        for c in chapters:
            if c.kind == "module" and c.module not in modules:
                continue
            filtered.append(c)
        chapters = filtered

    if not include_modules:
        chapters = [c for c in chapters if c.kind != "module"]
    if not include_book:
        chapters = [c for c in chapters if c.kind != "book"]

    return chapters


def extract_chapter_card(text: str) -> tuple[ChapterCard | None, list[RewriteWarning]]:
    warnings: list[RewriteWarning] = []
    start = text.find(CHAPTER_CARD_START)
    if start < 0:
        warnings.append(RewriteWarning(kind="missing_card", message="缺少章节学习卡片 marker"))
        return None, warnings
    end = text.find(CHAPTER_CARD_END, start)
    if end < 0:
        warnings.append(RewriteWarning(kind="broken_card", message="章节学习卡片 marker 不完整"))
        return None, warnings
    end += len(CHAPTER_CARD_END)
    block = text[start:end]

    def get_field(name: str) -> str | None:
        m = re.search(rf"(?m)^\s*-\s*{re.escape(name)}：\s*(.+?)\s*$", block)
        return m.group(1).strip() if m else None

    knowledge_point = get_field("知识点")
    how_to_use = get_field("怎么使用")
    principle = get_field("原理")
    source_entry = get_field("源码入口")
    recommended_lab = get_field("推荐 Lab")

    missing: list[str] = []
    for k, v in [
        ("知识点", knowledge_point),
        ("怎么使用", how_to_use),
        ("原理", principle),
        ("源码入口", source_entry),
        ("推荐 Lab", recommended_lab),
    ]:
        if not v:
            missing.append(k)
    if missing:
        warnings.append(RewriteWarning(kind="missing_card_fields", message=f"卡片缺字段：{', '.join(missing)}"))

    return (
        ChapterCard(
            knowledge_point=knowledge_point or "（未填写）",
            how_to_use=how_to_use or "（未填写）",
            principle=principle or "（未填写）",
            source_entry=source_entry or "N/A",
            recommended_lab=recommended_lab or "N/A",
        ),
        warnings,
    )


def classify_page(*, path: Path, title: str, card: ChapterCard | None) -> str:
    if "（Redirect）" in title or "已迁移" in title:
        return "redirect"
    if card and "Redirect" in card.knowledge_point:
        return "redirect"

    tool_names = {
        "index.md",
        "labs-index.md",
        "debugger-pack.md",
        "exercises-and-solutions.md",
        "migration-rules.md",
    }
    if path.name in tool_names:
        return "tool"
    if "断点地图" in title or "Breakpoint Map" in title:
        return "tool"
    if card and any(k in card.knowledge_point for k in ["索引", "工具", "地图", "导航"]):
        return "tool"
    return "normal"


def strip_bookify_block(text: str) -> tuple[str, str | None]:
    m = BOOKIFY_BLOCK_RE.search(text)
    if not m:
        return text, None
    block = m.group(0).strip("\n") + "\n"
    new = text[: m.start()] + text[m.end() :]
    new = normalize_blank_lines(new)
    return new, block


@dataclass
class H2Section:
    title: str
    heading_line: str
    body: str


def split_h2_sections(text: str) -> tuple[str, list[H2Section]]:
    # 只按 H2 拆分，保留每个 section 内的 H3/H4 等
    matches = list(re.finditer(r"(?m)^##\s+(.+?)\s*$", text))
    if not matches:
        return text, []

    preamble = text[: matches[0].start()]
    sections: list[H2Section] = []
    for i, m in enumerate(matches):
        start = m.start()
        end = matches[i + 1].start() if i + 1 < len(matches) else len(text)
        chunk = text[start:end]
        lines = chunk.splitlines(keepends=True)
        heading_line = lines[0].rstrip("\n")
        title = m.group(1).strip()
        body = "".join(lines[1:])
        sections.append(H2Section(title=title, heading_line=heading_line, body=body))
    return preamble, sections


def normalize_section_title(title: str) -> str:
    # 去掉常见括号注释，便于匹配
    t = title.strip()
    t = re.sub(r"[（(].*?[）)]", "", t).strip()
    return t


def is_effectively_empty(body: str) -> bool:
    # 去掉注释与空白后判断
    cleaned = re.sub(r"(?s)<!--.*?-->", "", body)
    cleaned = "\n".join(line.rstrip() for line in cleaned.splitlines())
    cleaned = cleaned.strip()
    if not cleaned:
        return True
    if re.fullmatch(r"(?is)(todo|tbd|pending|n/a|none|待补|未补齐|略)", cleaned):
        return True
    # 仅有极短文本也视为“基本空”
    return len(cleaned) < 8


def replace_or_insert_marker_block(
    *,
    body: str,
    start_marker: str,
    end_marker: str,
    new_block: str,
) -> str:
    if start_marker in body and end_marker in body:
        start = body.find(start_marker)
        end = body.find(end_marker, start)
        end = end + len(end_marker)
        return body[:start] + new_block + body[end:]
    # 插入到 section body 顶部
    return new_block + ("\n" if not new_block.endswith("\n") else "") + body.lstrip("\n")


def build_intro_block(*, card: ChapterCard, run_command: str | None = None) -> str:
    lab = card.recommended_lab
    lab_hint = "推荐先跑一遍本章 Lab，再带着问题回到正文。" if lab and lab != "N/A" else "建议先带着问题顺读一遍正文，再按证据链回到源码/断点验证。"
    run_hint = ""
    if run_command:
        run_hint = (
            "\n"
            "验证入口（可直接跑）：\n"
            "```bash\n"
            f"{run_command}\n"
            "```\n"
        )
    return (
        f"{BOOKLIKE_V2_INTRO_START}\n"
        f"这一章围绕「{card.knowledge_point}」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。\n"
        f"\n"
        f"阅读建议：\n"
        f"- 先看章首的“章节学习卡片/本章要点”，建立预期；\n"
        f"- {lab_hint}\n"
        f"{run_hint}"
        f"{BOOKLIKE_V2_INTRO_END}\n"
    )


def split_source_entries(raw: str) -> list[str]:
    s = (raw or "").strip()
    if not s:
        return []
    if s == "N/A":
        return []
    parts = [p.strip() for p in s.split("/") if p.strip()]
    return parts or [s]


def build_evidence_block(*, card: ChapterCard, min_items: int = 2, max_items: int = 4) -> str:
    entries = split_source_entries(card.source_entry)
    # 尽量用不同入口撑起 2–4 条；不足时使用同一入口但不同观察点
    target_n = 3 if len(entries) >= 3 else 2
    target_n = max(min_items, min(max_items, target_n))
    if not entries:
        entries = ["（源码入口见章节学习卡片）"]

    bullets: list[str] = []
    for i in range(target_n):
        entry = entries[i] if i < len(entries) else entries[-1]
        bullets.append(
            f"- 观察点 {i+1}：运行本章推荐入口后，聚焦「{card.knowledge_point}」的生效时机/顺序/边界；断点/入口：{entry}；断言：你能解释“为什么此处生效/为什么此处不生效”。"
        )
    if card.recommended_lab and card.recommended_lab != "N/A":
        bullets.append(f"- 建议：跑完 `{card.recommended_lab}` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。")

    body = "\n".join(bullets)
    return f"{BOOKLIKE_V2_EVIDENCE_START}\n{body}\n{BOOKLIKE_V2_EVIDENCE_END}\n"


def build_summary_block(*, card: ChapterCard, has_footer_nav: bool) -> str:
    next_hint = "下一章：见页尾导航（顺读不迷路）。" if has_footer_nav else "下一章：建议按模块目录/全书目录继续顺读。"
    return (
        f"{BOOKLIKE_V2_SUMMARY_START}\n"
        f"- 一句话总结：{card.knowledge_point} —— {card.how_to_use}\n"
        f"- 回到主线：{card.principle}\n"
        f"- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。\n"
        f"- {next_hint}\n"
        f"{BOOKLIKE_V2_SUMMARY_END}\n"
    )


def find_dest_link_for_redirect(text: str) -> str | None:
    candidates: list[str] = []
    for m in re.finditer(r"\[[^\]]+\]\(([^)]+)\)", text):
        dest = m.group(1).strip()
        if not dest or is_external_link(dest):
            continue
        candidates.append(dest)
    if not candidates:
        return None

    # redirect 页自身通常也会包含“返回全书目录”的 `/book/` 链接；
    # 为保证幂等与准确性，需要优先选择“更具体的新位置”，而不是目录页。
    def is_book_index_link(d: str) -> bool:
        return d.rstrip("/") in {"/book", "/book/index"}

    # 1) 优先选择更具体的 /book/...（排除 /book/ 目录）
    for dest in candidates:
        if "/book/" in dest and not is_book_index_link(dest):
            return dest

    # 2) 优先选择 markdown 文件（排除 README/index）
    for dest in candidates:
        lower = dest.lower()
        if not lower.endswith(".md"):
            continue
        if "readme" in lower:
            continue
        if lower.endswith("index.md"):
            continue
        return dest

    # 3) 退化：允许 /book/ 作为兜底
    for dest in candidates:
        if is_book_index_link(dest):
            return dest

    return candidates[0]


def rewrite_redirect_page(*, original: str, path: Path, card: ChapterCard | None) -> tuple[str, list[RewriteWarning]]:
    warnings: list[RewriteWarning] = []
    title = extract_title(path) or path.name
    dest = find_dest_link_for_redirect(original)
    if not dest:
        warnings.append(RewriteWarning(kind="redirect_missing_dest", message="redirect 页未找到新位置链接"))
        dest = "/book/"

    # 保留可跑入口（如果存在），用于教学回归
    preamble, sections = split_h2_sections(original)
    kept: list[H2Section] = []
    for s in sections:
        if "可跑入口" in s.title or "实验入口" in s.title:
            kept.append(s)

    bookify_stripped, bookify_block = strip_bookify_block(original)
    # 重新 split（去掉 bookify 后更干净）
    preamble, _ = split_h2_sections(bookify_stripped)

    card_block = ""
    if card:
        start = original.find(CHAPTER_CARD_START)
        end = original.find(CHAPTER_CARD_END, start) if start >= 0 else -1
        if start >= 0 and end >= 0:
            end += len(CHAPTER_CARD_END)
            card_block = original[start:end].strip("\n") + "\n\n"
        else:
            warnings.append(RewriteWarning(kind="redirect_missing_card_block", message="redirect 页未找到卡片块文本"))

    # 尽量保留全书/模块导航（若 preamble 已包含则不重复）
    header = ""
    for line in original.splitlines():
        if line.startswith("# "):
            header = line
            break
    if not header:
        header = f"# {title}"

    out = [header, ""]
    if card_block:
        out.append(card_block.rstrip("\n"))
        out.append("")

    out.append("## 已迁移")
    out.append(f"本页为旧入口兼容页，正文已迁移到：[新位置]({dest})。")
    out.append("")
    if kept:
        for s in kept:
            out.append(s.heading_line)
            out.append(s.body.rstrip("\n"))
            out.append("")
    out.append("## 返回")
    # module redirect 通常有 README；book redirect 也可回 index
    if path.as_posix().startswith("docs/book/"):
        out.append("- [全书目录](/book/)")
    else:
        # 尽量复用已有相对 README 链接
        if (path.parent / "../README.md").resolve().is_file():
            out.append("- [模块目录](../README.md)")
        out.append("- [全书目录](/book/)")
    out.append("")

    rebuilt = "\n".join(out).rstrip() + "\n"
    if bookify_block:
        rebuilt = rebuilt.rstrip() + "\n\n" + bookify_block.strip("\n") + "\n"
    rebuilt = normalize_blank_lines(rebuilt)
    return rebuilt, warnings


def ensure_section(
    *,
    sections: list[H2Section],
    title: str,
    matcher: re.Pattern[str],
    insert_before_titles: list[re.Pattern[str]] | None,
    new_body: str,
    marker_start: str,
    marker_end: str,
) -> tuple[list[H2Section], bool]:
    changed = False

    for s in sections:
        if matcher.search(normalize_section_title(s.title)):
            if is_effectively_empty(s.body) or (marker_start in s.body and marker_end in s.body):
                s.body = replace_or_insert_marker_block(
                    body=s.body, start_marker=marker_start, end_marker=marker_end, new_block=new_body
                )
                changed = True
            return sections, changed

    new_section = H2Section(title=title, heading_line=f"## {title}", body="\n" + new_body + "\n")

    if not insert_before_titles:
        sections.insert(0, new_section)
        return sections, True

    insert_at = None
    for i, s in enumerate(sections):
        norm = normalize_section_title(s.title)
        if any(p.search(norm) for p in insert_before_titles):
            insert_at = i
            break
    if insert_at is None:
        sections.append(new_section)
    else:
        sections.insert(insert_at, new_section)
    return sections, True


def clean_duplicate_experiment_section(*, sections: list[H2Section], has_lab_callout: bool) -> tuple[list[H2Section], bool]:
    if not has_lab_callout:
        return sections, False
    changed = False
    exp_re = re.compile(r"^实验入口")
    for s in sections:
        if exp_re.search(normalize_section_title(s.title)):
            # 内容很短且像“清单”→ 改为指向章首提示框
            raw_lines = [ln.strip() for ln in s.body.splitlines() if ln.strip()]
            if len(raw_lines) <= 8 and any("Lab" in ln or "Test" in ln for ln in raw_lines):
                s.body = (
                    "\n"
                    + BOOKLIKE_V2_EVIDENCE_START
                    + "\n"
                    + "实验入口已在章首提示框给出（先跑再读）。建议跑完后回到本章“证据链”逐条验证关键结论。\n"
                    + BOOKLIKE_V2_EVIDENCE_END
                    + "\n"
                )
                changed = True
    return sections, changed


def fix_empty_watchlist_blocks(
    *, sections: list[H2Section], card: ChapterCard, has_footer_nav: bool, run_command: str | None
) -> tuple[list[H2Section], bool]:
    changed = False
    for s in sections:
        norm = normalize_section_title(s.title)
        if norm not in EMPTY_BLOCK_WATCHLIST:
            continue
        if not is_effectively_empty(s.body):
            continue
        if norm == "导读":
            s.body = "\n" + build_intro_block(card=card, run_command=run_command) + "\n"
            changed = True
        elif norm == "证据链":
            s.body = "\n" + build_evidence_block(card=card) + "\n"
            changed = True
        elif norm == "小结与下一章":
            s.body = "\n" + build_summary_block(card=card, has_footer_nav=has_footer_nav) + "\n"
            changed = True
        elif norm == "一句话总结":
            s.body = "\n" + f"{BOOKLIKE_V2_SUMMARY_START}\n{card.knowledge_point} —— {card.how_to_use}\n{BOOKLIKE_V2_SUMMARY_END}\n"
            changed = True
    return sections, changed


def rewrite_normal_or_tool_page(
    *,
    original: str,
    path: Path,
    page_type: str,
    card: ChapterCard,
    run_command: str | None,
) -> tuple[str, list[RewriteWarning]]:
    warnings: list[RewriteWarning] = []
    stripped, bookify_block = strip_bookify_block(original)
    preamble, sections = split_h2_sections(stripped)
    has_lab_callout = CHAPTER_LAB_CALLOUT_LINE in preamble or CHAPTER_LAB_CALLOUT_LINE in stripped
    has_footer_nav = bool(bookify_block)

    # tool 页：只补齐导读与小结（更像“怎么用这页”），不强制证据链
    if page_type == "tool":
        intro_re = re.compile(r"^(导读|怎么用这页|使用指南|如何使用)$")
        sections, c1 = ensure_section(
            sections=sections,
            title="怎么用这页",
            matcher=intro_re,
            insert_before_titles=None,
            new_body=build_intro_block(card=card, run_command=run_command),
            marker_start=BOOKLIKE_V2_INTRO_START,
            marker_end=BOOKLIKE_V2_INTRO_END,
        )
        sections, c2 = ensure_section(
            sections=sections,
            title="小结与下一章",
            matcher=re.compile(r"^(小结与下一章|小结|总结)$"),
            insert_before_titles=None,
            new_body=build_summary_block(card=card, has_footer_nav=has_footer_nav),
            marker_start=BOOKLIKE_V2_SUMMARY_START,
            marker_end=BOOKLIKE_V2_SUMMARY_END,
        )
        sections, c3 = fix_empty_watchlist_blocks(
            sections=sections, card=card, has_footer_nav=has_footer_nav, run_command=run_command
        )
        changed = c1 or c2 or c3
    else:
        sections, c_intro = ensure_section(
            sections=sections,
            title="导读",
            matcher=re.compile(r"^(导读|本章定位|前言)$"),
            insert_before_titles=None,
            new_body=build_intro_block(card=card, run_command=run_command),
            marker_start=BOOKLIKE_V2_INTRO_START,
            marker_end=BOOKLIKE_V2_INTRO_END,
        )

        # 证据链：优先插到 Lab/实验入口/Debug 之前
        insert_before = [
            re.compile(r"^(最小可运行实验|实验入口|可跑入口|Debug|源码与断点)$"),
        ]
        sections, c_evidence = ensure_section(
            sections=sections,
            title="证据链（如何验证你真的理解了）",
            matcher=re.compile(r"^(证据链|你应该观察到什么|What to observe|源码与断点)$", re.IGNORECASE),
            insert_before_titles=insert_before,
            new_body=build_evidence_block(card=card),
            marker_start=BOOKLIKE_V2_EVIDENCE_START,
            marker_end=BOOKLIKE_V2_EVIDENCE_END,
        )

        sections, c_summary = ensure_section(
            sections=sections,
            title="小结与下一章",
            matcher=re.compile(r"^(小结与下一章|小结|总结)$"),
            insert_before_titles=None,
            new_body=build_summary_block(card=card, has_footer_nav=has_footer_nav),
            marker_start=BOOKLIKE_V2_SUMMARY_START,
            marker_end=BOOKLIKE_V2_SUMMARY_END,
        )

        sections, c_empty = fix_empty_watchlist_blocks(
            sections=sections, card=card, has_footer_nav=has_footer_nav, run_command=run_command
        )
        sections, c_dup = clean_duplicate_experiment_section(sections=sections, has_lab_callout=has_lab_callout)
        changed = c_intro or c_evidence or c_summary or c_empty or c_dup

    rebuilt = preamble
    if sections:
        rebuilt += "".join(f"{s.heading_line}\n{s.body.rstrip()}\n\n" for s in sections).rstrip() + "\n"
    rebuilt = normalize_blank_lines(rebuilt)
    if bookify_block:
        rebuilt = rebuilt.rstrip() + "\n\n" + bookify_block.strip("\n") + "\n"
        rebuilt = normalize_blank_lines(rebuilt)
    return rebuilt, warnings


def to_test_selector(recommended_lab: str) -> str | None:
    s = (recommended_lab or "").strip()
    if not s or s == "N/A":
        return None
    # 典型形式：`SomeLabTest` / `SomeLabTest#method`
    if s.startswith("`") and s.endswith("`"):
        s = s[1:-1].strip()
    if not s:
        return None
    return s


def build_run_command(*, kind: str, module: str, recommended_lab: str) -> str | None:
    if kind != "module" or not module:
        return None
    selector = to_test_selector(recommended_lab)
    if not selector:
        return None
    artifact = to_maven_artifact_id(module)
    return f"mvn -q -pl :{artifact} -Dtest={selector} test"


def rewrite_page(*, path: Path, kind: str, module: str) -> tuple[str, str, list[RewriteWarning]]:
    original = read_text_utf8(path)
    card, card_warnings = extract_chapter_card(original)
    title = extract_title(path) or path.name
    page_type = classify_page(path=path, title=title, card=card)

    if page_type == "redirect":
        new_text, warnings = rewrite_redirect_page(original=original, path=path, card=card)
        return new_text, page_type, card_warnings + warnings
    if not card:
        # 没有卡片时保守：不改写正文，只返回原文 + warning
        return original, page_type, card_warnings
    run_command = build_run_command(kind=kind, module=module, recommended_lab=card.recommended_lab)
    new_text, warnings = rewrite_normal_or_tool_page(
        original=original, path=path, page_type=page_type, card=card, run_command=run_command
    )
    return new_text, page_type, card_warnings + warnings


def write_report(path: Path, items: list[RewriteItem]) -> None:
    if path.suffix.lower() == ".md":
        lines: list[str] = []
        lines.append("# Booklike V2 Rewrite Report\n")
        lines.append(f"- Items: {len(items)}\n")
        lines.append("\n")
        lines.append("| Path | Type | Status | Warnings |\n")
        lines.append("|---|---:|---:|---|\n")
        for it in items:
            ws = ", ".join(w.kind for w in it.warnings) if it.warnings else ""
            lines.append(f"| `{it.path}` | {it.page_type} | {it.status} | {ws} |\n")
        path.write_text("".join(lines), encoding="utf-8")
        return

    payload = [asdict(it) for it in items]
    path.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(
        prog="rewrite-chapters-booklike-v2.py",
        description="以章节学习卡片为 SSOT，对全站章节执行“正文二次书籍化”批处理改写。",
    )
    parser.add_argument("--dry-run", action="store_true", help="仅统计将改动的文件数量与报告，不写入文件。")
    parser.add_argument("--report", help="输出报告路径（.json 或 .md）。")
    parser.add_argument("--chapter-list", help="章节清单 JSON（建议由 generate-docs-chapter-list.py 输出）。")
    parser.add_argument("--include-modules", action="store_true", help="包含模块 docs 章节（默认开启）。")
    parser.add_argument("--exclude-modules", action="store_true", help="不包含模块 docs 章节。")
    parser.add_argument("--include-book", action="store_true", help="包含 Book-only 页面（默认开启）。")
    parser.add_argument("--exclude-book", action="store_true", help="不包含 Book-only 页面。")
    parser.add_argument("--module", action="append", default=[], help="仅处理指定模块（可重复）。")

    args = parser.parse_args(argv)

    include_modules = True
    include_book = True
    if args.include_modules or args.exclude_modules:
        include_modules = args.include_modules and not args.exclude_modules
    if args.include_book or args.exclude_book:
        include_book = args.include_book and not args.exclude_book

    chapter_list = Path(args.chapter_list).resolve() if args.chapter_list else None
    report_path = Path(args.report).resolve() if args.report else None
    modules = args.module or None

    chapters = load_chapters(
        repo_root=REPO_ROOT,
        chapter_list=chapter_list,
        include_modules=include_modules,
        include_book=include_book,
        modules=modules,
    )
    if not chapters:
        print("[FAIL] No chapters to process (empty scope).", file=sys.stderr)
        return 2

    items: list[RewriteItem] = []
    scanned = 0
    changed = 0
    skipped = 0
    failed = 0
    warn_count = 0

    for c in chapters:
        scanned += 1
        p = (REPO_ROOT / c.path).resolve()
        if not p.is_file():
            failed += 1
            items.append(
                RewriteItem(
                    kind=c.kind,
                    module=c.module,
                    path=c.path,
                    page_type="unknown",
                    status="failed",
                    warnings=[],
                    error="file_missing",
                )
            )
            continue
        try:
            new_text, page_type, warnings = rewrite_page(path=p, kind=c.kind, module=c.module)
            original = read_text_utf8(p)
            ws = warnings
            warn_count += len(ws)
            if new_text != original:
                if not args.dry_run:
                    write_text_utf8(p, new_text)
                changed += 1
                status = "changed"
            else:
                skipped += 1
                status = "skipped"
            items.append(
                RewriteItem(
                    kind=c.kind,
                    module=c.module,
                    path=c.path,
                    page_type=page_type,
                    status=status,
                    warnings=ws,
                )
            )
        except Exception as e:  # noqa: BLE001 - batch tool should continue
            failed += 1
            items.append(
                RewriteItem(
                    kind=c.kind,
                    module=c.module,
                    path=c.path,
                    page_type="unknown",
                    status="failed",
                    warnings=[],
                    error=str(e),
                )
            )

    if report_path:
        write_report(report_path, items)

    if failed:
        print(f"[FAIL] scanned={scanned} changed={changed} skipped={skipped} failed={failed} warnings={warn_count}")
        return 1

    mode = "DRY-RUN" if args.dry_run else "RUN"
    print(f"[OK] {mode} scanned={scanned} changed={changed} skipped={skipped} warnings={warn_count}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
