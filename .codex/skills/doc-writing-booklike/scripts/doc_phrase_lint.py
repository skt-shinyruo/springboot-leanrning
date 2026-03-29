#!/usr/bin/env python3
"""
doc_phrase_lint.py

Lightweight linter for book-like, knowledge-point-driven Markdown chapters.

It checks:
- Generic template headings (e.g. "## 导读" / "## 机制") => ERROR
- Placeholders (TODO / <TBD>) => ERROR
- Evidence/density suggestions (runnable entry, comparables, anchors) => WARN by default

Usage:
  python3 skills/doc-writing-booklike/scripts/doc_phrase_lint.py <file...>
  python3 skills/doc-writing-booklike/scripts/doc_phrase_lint.py --strict <file...>
  python3 skills/doc-writing-booklike/scripts/doc_phrase_lint.py --gate <file...>
"""

from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable


@dataclass(frozen=True)
class PatternRule:
    level: str  # "ERROR" | "WARN"
    label: str
    pattern: re.Pattern[str]


def _compile(level: str, label: str, regex: str) -> PatternRule:
    return PatternRule(level=level, label=label, pattern=re.compile(regex))


ERROR_RULES: list[PatternRule] = [
    _compile(
        "ERROR",
        "template-heading:generic",
        r"^#{2,6}\s+(导读|背景|概念|原理|机制|实验|验证|观察|观察点|问题|解释|边界|结论|常见误区|误区|练习|小结|总结|延伸阅读|参考|术语|定义)(?:\s*[（(][^）)]*[）)]\s*)?\s*(?:[:：\-—]\s*)?$",
    ),
    _compile("ERROR", "placeholder:TODO", r"\bTODO\b"),
    _compile("ERROR", "placeholder:<TBD>", r"<TBD>"),
]

WARN_RULES: list[PatternRule] = [
    _compile("WARN", "meta:本文将", r"本文将"),
    _compile("WARN", "meta:接下来我们", r"接下来我们"),
    _compile("WARN", "meta:下面我们", r"下面我们"),
    _compile("WARN", "meta:注意这不是", r"注意.{0,10}不是"),
    _compile("WARN", "meta:理想状态", r"理想状态"),
    _compile("WARN", "placeholder:……", r"……"),
    _compile("WARN", "tone:强烈建议", r"强烈建议"),
    _compile("WARN", "tone:强烈推荐", r"强烈推荐"),
    _compile("WARN", "tone:显然", r"显然"),
    _compile("WARN", "tone:众所周知", r"众所周知"),
    _compile("WARN", "tone:很重要", r"很重要"),
    _compile("WARN", "tone:非常关键", r"非常关键"),
    _compile("WARN", "voice:你可以", r"你可以"),
    _compile("WARN", "voice:我们先", r"我们先"),
]


FENCE_RE = re.compile(r"^\s*(```+|~~~+)")


def iter_text_files(paths: Iterable[str]) -> list[Path]:
    files: list[Path] = []
    for raw in paths:
        path = Path(raw)
        if not path.exists():
            raise FileNotFoundError(str(path))
        if path.is_dir():
            raise IsADirectoryError(str(path))
        files.append(path)
    return files


def iter_inline_code_ranges(line: str) -> list[tuple[int, int]]:
    """
    Return ranges [start, end) that are inside Markdown inline code spans.

    This is a best-effort parser that handles the common case:
    - single-backtick spans: `code`
    - multi-backtick spans: ``code with ` inside``

    If an opening backtick run is not closed on the same line, treat the rest
    of the line as code to avoid false positives.
    """
    ranges: list[tuple[int, int]] = []
    i = 0
    while i < len(line):
        if line[i] != "`":
            i += 1
            continue
        j = i
        while j < len(line) and line[j] == "`":
            j += 1
        tick_len = j - i
        close_token = "`" * tick_len
        k = line.find(close_token, j)
        if k == -1:
            ranges.append((i, len(line)))
            break
        ranges.append((i, k + tick_len))
        i = k + tick_len
    return ranges


def iter_quote_ranges(line: str, open_quote: str, close_quote: str) -> list[tuple[int, int]]:
    """
    Return ranges [start, end) inside quote pairs on the same line.
    """
    ranges: list[tuple[int, int]] = []
    i = 0
    while i < len(line):
        start = line.find(open_quote, i)
        if start == -1:
            break
        end = line.find(close_quote, start + len(open_quote))
        if end == -1:
            # Unclosed quote: ignore to EOL to avoid false positives.
            ranges.append((start, len(line)))
            break
        ranges.append((start, end + len(close_quote)))
        i = end + len(close_quote)
    return ranges


def merge_ranges(ranges: list[tuple[int, int]]) -> list[tuple[int, int]]:
    if not ranges:
        return []
    ranges_sorted = sorted(ranges, key=lambda x: (x[0], x[1]))
    merged: list[tuple[int, int]] = [ranges_sorted[0]]
    for start, end in ranges_sorted[1:]:
        prev_start, prev_end = merged[-1]
        if start <= prev_end:
            merged[-1] = (prev_start, max(prev_end, end))
        else:
            merged.append((start, end))
    return merged


def is_in_ignored_ranges(start: int, end: int, ignored: list[tuple[int, int]]) -> bool:
    for i_start, i_end in ignored:
        if end <= i_start or start >= i_end:
            continue
        return True
    return False


TABLE_SEP_RE = re.compile(r"^\s*\|?\s*:?-{3,}:?\s*(\|\s*:?-{3,}:?\s*)+\|?\s*$")
TABLE_ROW_RE = re.compile(r"^\s*\|.+\|\s*$")
CALL_CHAIN_RE = re.compile(r"[A-Za-z0-9_`#.]+\s*(->|→|=>|⇒)\s*[A-Za-z0-9_`#.]+")
METHOD_ANCHOR_RE = re.compile(r"\b[A-Z][A-Za-z0-9_]+#[A-Za-z0-9_]+\b")
CAMELCASE_RE = re.compile(r"\b[A-Z][A-Za-z0-9]{2,}\b")
EXCEPTION_RE = re.compile(r"\b[A-Z][A-Za-z0-9_]*(Exception|Error)\b")
HEADING_RE = re.compile(r"^(#{2,6})\s+(.+?)\s*$")
TEMPLATE_LABEL_RE = re.compile(
    r"^(导读|背景|概念|原理|机制|实验|验证|观察|观察点|问题|解释|边界|结论|常见误区|误区|练习|小结|总结|延伸阅读|参考|术语|定义)\s*[:：]"
)


def has_markdown_table(text: str) -> bool:
    lines = text.splitlines()
    for i in range(len(lines) - 1):
        if TABLE_ROW_RE.match(lines[i]) and TABLE_SEP_RE.match(lines[i + 1]):
            return True
    return False


def has_diagram(text: str) -> bool:
    if "```mermaid" in text or "```dot" in text or "```plantuml" in text:
        return True
    return bool(re.search(r"!\[[^\]]*\]\([^)]+\)", text))


def extract_inline_code_spans(text: str) -> list[str]:
    spans: list[str] = []
    for line in text.splitlines():
        i = 0
        while i < len(line):
            if line[i] != "`":
                i += 1
                continue
            j = i
            while j < len(line) and line[j] == "`":
                j += 1
            tick_len = j - i
            token = "`" * tick_len
            k = line.find(token, j)
            if k == -1:
                break
            spans.append(line[j:k])
            i = k + tick_len
    return spans


def count_code_anchors(text: str) -> tuple[int, int, int]:
    """
    Returns:
      inline_code_anchors: count of inline code spans containing latin letters/digits
      method_anchors: count of method-like anchors (Class#method)
      camelcase_anchors: count of CamelCase tokens in raw text
    """
    spans = extract_inline_code_spans(text)
    inline_code_anchors = sum(1 for s in spans if re.search(r"[A-Za-z0-9]", s))
    method_anchors = sum(1 for s in spans if METHOD_ANCHOR_RE.search(s))
    camelcase_anchors = len(CAMELCASE_RE.findall(text))
    return inline_code_anchors, method_anchors, camelcase_anchors


def add_file_error(
    hits: list[tuple[int, int, PatternRule, str]],
    rule_label: str,
    message: str,
) -> None:
    hits.append((1, 1, PatternRule(level="ERROR", label=rule_label, pattern=re.compile(r"$^")), message))


def add_file_warn(
    hits: list[tuple[int, int, PatternRule, str]],
    rule_label: str,
    message: str,
) -> None:
    hits.append((1, 1, PatternRule(level="WARN", label=rule_label, pattern=re.compile(r"$^")), message))


def format_snippet(line: str, start: int, end: int, max_len: int = 160) -> str:
    stripped = line.rstrip("\n")
    prefix = stripped[:start]
    mid = stripped[start:end]
    suffix = stripped[end:]
    snippet = f"{prefix}⟦{mid}⟧{suffix}"
    if len(snippet) <= max_len:
        return snippet
    # Keep match centered when possible
    left_budget = max_len // 2
    right_budget = max_len - left_budget
    left = max(0, start - left_budget)
    right = min(len(stripped), end + right_budget)
    cropped = stripped[left:right]
    # Recompute indices within cropped
    rel_start = start - left
    rel_end = end - left
    cropped = f"{cropped[:rel_start]}⟦{cropped[rel_start:rel_end]}⟧{cropped[rel_end:]}"
    if left > 0:
        cropped = "…" + cropped
    if right < len(stripped):
        cropped = cropped + "…"
    return cropped


def lint_file(path: Path, rules: list[PatternRule]) -> list[tuple[int, int, PatternRule, str]]:
    hits: list[tuple[int, int, PatternRule, str]] = []
    text = path.read_text(encoding="utf-8", errors="replace")
    in_fence = False
    fence_char = ""
    fence_len = 0
    headings: list[str] = []
    for line_no, line in enumerate(text.splitlines(keepends=True), start=1):
        fence_match = FENCE_RE.match(line)
        if fence_match:
            marker = fence_match.group(1)
            if not in_fence:
                in_fence = True
                fence_char = marker[0]
                fence_len = len(marker)
            else:
                if marker[0] == fence_char and len(marker) >= fence_len:
                    in_fence = False
                    fence_char = ""
                    fence_len = 0
            continue
        if in_fence:
            continue
        heading_match = HEADING_RE.match(line.rstrip("\n"))
        if heading_match:
            headings.append(heading_match.group(2).strip())
        ignored_ranges = merge_ranges(
            iter_inline_code_ranges(line)
            + iter_quote_ranges(line, open_quote="“", close_quote="”")
            + iter_quote_ranges(line, open_quote="「", close_quote="」")
        )
        for rule in rules:
            for m in rule.pattern.finditer(line):
                if is_in_ignored_ranges(m.start(), m.end(), ignored_ranges):
                    continue
                col = m.start() + 1
                snippet = format_snippet(line, m.start(), m.end())
                hits.append((line_no, col, rule, snippet))

    # File-level checks (density/depth). These operate on the raw text (including code fences).
    # IMPORTANT: These are advisory by default (WARN). This tool is meant to help writers,
    # not to force a fixed template.
    if not re.search(r"\bmvn\b", text):
        add_file_warn(
            hits,
            "missing:runnable-entry",
            "No obvious runnable entry found (recommended: `mvn ... -Dtest=... test`). If this chapter explains a phenomenon, consider giving one shortest reproducible command + 2–3 observation points.",
        )

    has_table = has_markdown_table(text)
    has_diagram_flag = has_diagram(text)
    has_call_chain = bool(CALL_CHAIN_RE.search(text))
    comparable_count = sum(1 for v in (has_table, has_diagram_flag, has_call_chain) if v)
    if comparable_count < 2:
        found = ", ".join(
            name
            for name, ok in (
                ("table", has_table),
                ("diagram", has_diagram_flag),
                ("call-chain", has_call_chain),
            )
            if ok
        )
        found = found or "none"
        add_file_warn(
            hits,
            "missing:comparables",
            "Comparables are thin: consider adding at least 2 of (table / diagram / call-chain) to carry the explanation. "
            f"Found: {found}.",
        )
    if not has_table:
        add_file_warn(
            hits,
            "missing:table",
            "No Markdown table found. Consider adding a small A vs B table (timing/semantics/failure point/verification hook).",
        )
    if not has_diagram_flag:
        add_file_warn(
            hits,
            "missing:diagram",
            "No diagram found. Consider adding an image or ` ```mermaid` / ` ```dot` diagram for object relations or flow.",
        )
    if not has_call_chain:
        add_file_warn(
            hits,
            "missing:call-chain",
            "No call-chain found. Consider adding a chain like `A#x -> B#y -> C#z` (or `=>`) to anchor the flow.",
        )
    inline_code_anchors, method_anchors, camelcase_anchors = count_code_anchors(text)
    if inline_code_anchors < 8:
        add_file_warn(
            hits,
            "missing:code-anchors",
            f"Not enough code anchors: inline code spans with anchors < 8 (got {inline_code_anchors}). Add more class/method/config anchors in backticks.",
        )
    if method_anchors < 2:
        add_file_warn(
            hits,
            "missing:method-anchors",
            f"Not enough method-level anchors: need >= 2 `Class#method` (got {method_anchors}).",
        )
    if camelcase_anchors < 6:
        add_file_warn(
            hits,
            "low:object-density",
            f"Low object density: CamelCase tokens < 6 (got {camelcase_anchors}). Consider adding more concrete class/object anchors.",
        )
    if headings:
        labeled = sum(1 for h in headings if TEMPLATE_LABEL_RE.match(h))
        total = len(headings)
        if total >= 6 and labeled / total >= 0.5:
            add_file_warn(
                hits,
                "template-heading:overused-labels",
                f"Many headings look like '栏目：知识点' labels ({labeled}/{total}). Consider letting headings be pure knowledge points instead of repeating栏目名.",
            )
    if not re.search(r"[？?]", text):
        add_file_warn(hits, "missing:question", "No question mark found. Consider adding at least one rhetorical question to match author-voice style.")
    if not re.search(r"(就像|好比|类似于|像是|像)", text):
        add_file_warn(hits, "missing:analogy", "No obvious analogy marker found (e.g., 就像/好比/类似于). Consider adding one short analogy tied to an anchor.")
    return hits


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Lint Markdown docs for book-like, knowledge-point-driven writing (anti-template headings + evidence density).",
    )
    parser.add_argument(
        "--gate",
        action="store_true",
        help="Exit non-zero on issues (default: always exit 0; this tool is advisory).",
    )
    parser.add_argument(
        "--strict",
        action="store_true",
        help="With --gate: also fail on WARN (default: only ERROR fails).",
    )
    parser.add_argument("files", nargs="+", help="One or more Markdown files to lint.")
    args = parser.parse_args()

    try:
        files = iter_text_files(args.files)
    except (FileNotFoundError, IsADirectoryError) as e:
        print(f"[ERROR] Invalid path: {e}", file=sys.stderr)
        return 2

    rules = ERROR_RULES + WARN_RULES
    total_error = 0
    total_warn = 0

    for file_path in files:
        hits = lint_file(file_path, rules)
        if not hits:
            continue
        for line_no, col, rule, snippet in hits:
            print(f"{file_path}:{line_no}:{col} [{rule.level}] {rule.label} :: {snippet}")
            if rule.level == "ERROR":
                total_error += 1
            else:
                total_warn += 1

    if total_error == 0 and total_warn == 0:
        print("[OK] No lint issues found.")
        return 0

    print(f"[SUMMARY] ERROR={total_error} WARN={total_warn}")
    if not args.gate:
        return 0
    if total_error > 0:
        return 1
    if args.strict and total_warn > 0:
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
