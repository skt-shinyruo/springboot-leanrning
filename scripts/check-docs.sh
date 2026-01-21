#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${repo_root}"

python3 scripts/check-abs-book-links.py --root docs

tmp="$(mktemp)"
trap 'rm -f "${tmp}"' EXIT

python3 scripts/generate-book-labs-index.py --out "${tmp}" >/dev/null

if ! diff -q "${tmp}" "docs/book/labs-index.md" >/dev/null; then
  echo "[ERROR] docs/book/labs-index.md 已过期，请重新生成："
  echo "  python3 scripts/generate-book-labs-index.py"
  exit 2
fi

python3 scripts/check-md-relative-links.py --root docs

echo "[OK] docs gate passed."
