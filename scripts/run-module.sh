#!/usr/bin/env bash
set -euo pipefail

module="${1:-}"
if [[ -z "${module}" ]]; then
  echo "Usage: scripts/run-module.sh <module>"
  echo "Example: scripts/run-module.sh spring-boot-basics"
  echo "Example: scripts/run-module.sh :spring-boot-basics"
  exit 2
fi

selector="${module}"
if [[ "${module}" != *":"* && "${module}" != */* && ! -d "${module}" ]]; then
  selector=":${module}"
fi

mvn -pl "${selector}" spring-boot:run
