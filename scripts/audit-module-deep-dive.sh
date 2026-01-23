#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${repo_root}"

task_file=""
format="text"
out=""

usage() {
  cat <<'EOF'
Usage: scripts/audit-module-deep-dive.sh [--task <task.md>] [--format text|md] [--out <file>]

默认行为：
  - 从 task.md（3.1/3.2 章节）解析每模块的 docs/tests/perf 入口清单
  - 检查这些入口文件是否存在
  - 输出缺失项与建议运行命令（不输出文件内容）

Examples:
  scripts/audit-module-deep-dive.sh
  scripts/audit-module-deep-dive.sh --format md --out helloagents/plan/YYYYMMDDHHMM_<feature>/module-deep-dive-audit.md
  scripts/audit-module-deep-dive.sh --task helloagents/history/YYYY-MM/YYYYMMDDHHMM_<feature>/task.md --format text
EOF
}

while [[ $# -gt 0 ]]; do
  case "${1}" in
    --task)
      task_file="${2:-}"
      shift 2
      ;;
    --format)
      format="${2:-}"
      shift 2
      ;;
    --out)
      out="${2:-}"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "[ERROR] Unknown arg: ${1}"
      echo
      usage
      exit 2
      ;;
  esac
done

if [[ -n "${task_file}" ]]; then
  if [[ ! -f "${task_file}" ]]; then
    echo "[ERROR] task file not found: ${task_file}"
    exit 2
  fi
else
  shopt -s nullglob
  plan_candidates=(helloagents/plan/*/task.md)
  if [[ ${#plan_candidates[@]} -eq 1 ]]; then
    task_file="${plan_candidates[0]}"
  else
    history_candidates=(helloagents/history/*/*/task.md)
    if [[ ${#history_candidates[@]} -ge 1 ]]; then
      task_file="$(printf '%s\n' "${history_candidates[@]}" | sort | tail -n 1)"
    else
      echo "[ERROR] no task.md found under helloagents/plan/*/task.md or helloagents/history/*/*/task.md"
      echo "        please specify explicitly: --task <path>"
      exit 2
    fi
  fi
fi

if [[ "${format}" != "text" && "${format}" != "md" ]]; then
  echo "[ERROR] Unsupported --format: ${format} (expected: text|md)"
  exit 2
fi

run_audit() {
python3 - "${repo_root}" "${task_file}" "${format}" <<'PY'
from __future__ import annotations

import re
import sys
from collections import OrderedDict
from dataclasses import dataclass, field
from pathlib import Path


repo_root = Path(sys.argv[1]).resolve()
task_file = Path(sys.argv[2]).resolve()
fmt = sys.argv[3]

sys.path.insert(0, str(repo_root / "scripts"))
import repo_paths  # type: ignore  # noqa: E402


@dataclass
class ModuleAudit:
    module: str
    artifact_id: str
    docs: OrderedDict[str, str | None] = field(default_factory=OrderedDict)
    tests: OrderedDict[str, str | None] = field(default_factory=OrderedDict)

    def all_items(self) -> list[tuple[str, str, str | None]]:
        items: list[tuple[str, str, str | None]] = []
        for k, v in self.docs.items():
            items.append(("docs", k, v))
        for k, v in self.tests.items():
            items.append(("tests", k, v))
        return items


def file_exists(path: str | None) -> bool:
    if not path:
        return False
    return (repo_root / path).is_file()


def parse_task_md(text: str) -> "OrderedDict[str, ModuleAudit]":
    modules: "OrderedDict[str, ModuleAudit]" = OrderedDict()

    section: str | None = None
    current: ModuleAudit | None = None

    header_re = re.compile(r"^- \[[^\]]+\]\s+3\.(?P<section>[12])\.\d+\s+`(?P<module>[^`]+)`\s+(?P<kind>docs|tests)\b")
    entry_with_path_re = re.compile(r"^\s+-\s*(?P<key>[^：]+)：`(?P<path>[^`]+)`\s*$")
    entry_missing_re = re.compile(r"^\s+-\s*(?P<key>[^：]+)：\s*缺失\b")

    for raw in text.splitlines():
        line = raw.rstrip("\n")

        if line.startswith("### 3.1 "):
            section = "docs"
            current = None
            continue
        if line.startswith("### 3.2 "):
            section = "tests"
            current = None
            continue
        if line.startswith("### 3.3 "):
            section = None
            current = None
            continue

        m = header_re.match(line)
        if m:
            # 对 task.md 的结构做一个保险：章节号与 kind 必须一致
            if section == "docs" and m.group("kind") != "docs":
                continue
            if section == "tests" and m.group("kind") != "tests":
                continue

            module = m.group("module")
            artifact_id = repo_paths.to_maven_artifact_id(module)
            current = modules.setdefault(module, ModuleAudit(module=module, artifact_id=artifact_id))
            continue

        if not section or current is None:
            continue

        m = entry_with_path_re.match(line)
        if m:
            key = m.group("key").strip()
            path = m.group("path").strip()
            if section == "docs":
                current.docs[key] = path
            else:
                current.tests[key] = path
            continue

        m = entry_missing_re.match(line)
        if m:
            key = m.group("key").strip()
            if section == "docs":
                current.docs[key] = None
            else:
                current.tests[key] = None
            continue

    return modules


def test_class_name(path: str | None) -> str | None:
    if not path:
        return None
    p = Path(path)
    if p.suffix != ".java":
        return None
    return p.stem


def module_root(module: str) -> str:
    root = repo_paths.find_module_root(repo_root, module)
    return str(root.relative_to(repo_root)) if root else "(not-found)"


def render_text(modules: "OrderedDict[str, ModuleAudit]") -> str:
    lines: list[str] = []
    total_missing = 0

    lines.append("[AUDIT] module deep-dive entrypoints (docs/tests/perf)")
    lines.append(f"- task file: {task_file.relative_to(repo_root)}")
    lines.append("")

    for mod in modules.values():
        missing_items: list[str] = []
        for kind, key, path in mod.all_items():
            ok = file_exists(path)
            if not ok:
                missing_items.append(f"{kind}:{key} -> {path or '(missing)'}")
        total_missing += len(missing_items)

        lines.append(f"== {mod.module}  (artifactId={mod.artifact_id}) ==")
        lines.append(f"- moduleRoot: {module_root(mod.module)}")
        if mod.docs:
            lines.append("- docs:")
            for k, p in mod.docs.items():
                mark = "OK" if file_exists(p) else "MISSING"
                lines.append(f"  - {k}: {p or '(missing)'} [{mark}]")
        if mod.tests:
            lines.append("- tests:")
            for k, p in mod.tests.items():
                mark = "OK" if file_exists(p) else "MISSING"
                cls = test_class_name(p)
                cmd = ""
                if cls:
                    cmd = f" | mvn -q -pl :{mod.artifact_id} -Dtest={cls} test"
                lines.append(f"  - {k}: {p or '(missing)'} [{mark}]{cmd}")
        if missing_items:
            lines.append("- missing:")
            for item in missing_items:
                lines.append(f"  - {item}")
        lines.append("")

    lines.append(f"[SUMMARY] modules={len(modules)} missing_items={total_missing}")
    return "\n".join(lines).rstrip() + "\n"


def render_md(modules: "OrderedDict[str, ModuleAudit]") -> str:
    lines: list[str] = []
    total_missing = 0

    lines.append("# Module Deep-Dive Audit（docs/tests/perf 入口基线）")
    lines.append("")
    lines.append(f"- 生成来源：`{task_file.relative_to(repo_root)}`（解析 3.1/3.2 章节）")
    lines.append("- 生成方式：`scripts/audit-module-deep-dive.sh --format md --out <file>`")
    lines.append("")

    for mod in modules.values():
        missing_items: list[str] = []
        for kind, key, path in mod.all_items():
            ok = file_exists(path)
            if not ok:
                missing_items.append(f"- {kind}:{key} -> `{path or '(missing)'}`")
        total_missing += len(missing_items)

        lines.append(f"## {mod.module}")
        lines.append("")
        lines.append(f"- Maven artifactId：`{mod.artifact_id}`")
        lines.append(f"- Code module root：`{module_root(mod.module)}`")
        lines.append("")

        if mod.docs:
            lines.append("### Docs 入口")
            for k, p in mod.docs.items():
                mark = "✅" if file_exists(p) else "❌"
                lines.append(f"- {mark} {k}：`{p or '(missing)'}`")
            lines.append("")

        if mod.tests:
            lines.append("### Tests 入口（可跑命令）")
            for k, p in mod.tests.items():
                mark = "✅" if file_exists(p) else "❌"
                cls = test_class_name(p)
                if cls:
                    lines.append(f"- {mark} {k}：`{p or '(missing)'}`")
                    lines.append(f"  - `mvn -q -pl :{mod.artifact_id} -Dtest={cls} test`")
                else:
                    lines.append(f"- {mark} {k}：`{p or '(missing)'}`")
            lines.append("")

        if missing_items:
            lines.append("### 缺失项")
            lines.extend(missing_items)
            lines.append("")

    lines.append("---")
    lines.append("")
    lines.append(f"- 总模块数：{len(modules)}")
    lines.append(f"- 缺失项总数：{total_missing}")
    lines.append("")
    return "\n".join(lines).rstrip() + "\n"


text = task_file.read_text(encoding="utf-8")
modules = parse_task_md(text)
if not modules:
    raise SystemExit("[ERROR] No modules parsed from task.md (3.1/3.2). Please check task file format.")

out = render_md(modules) if fmt == "md" else render_text(modules)
sys.stdout.write(out)
PY
}

if [[ -n "${out}" ]]; then
  mkdir -p "$(dirname "${out}")"
  run_audit > "${out}"
  echo "[OK] wrote: ${out}"
else
  run_audit
fi
