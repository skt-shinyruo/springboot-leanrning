#!/usr/bin/env bash
set -euo pipefail

module="${1:-}"
if [[ -z "${module}" ]]; then
  echo "Usage: scripts/test-module.sh <module>"
  echo "Example: scripts/test-module.sh spring-boot-web-mvc"
  echo "Example: scripts/test-module.sh :spring-boot-web-mvc"
  exit 2
fi

selector="${module}"
if [[ "${module}" != *":"* && "${module}" != */* && ! -d "${module}" ]]; then
  selector=":${module}"
fi

mvn -q -pl "${selector}" test
