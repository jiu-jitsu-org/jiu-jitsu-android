#!/usr/bin/env bash

# Fast, dependency-free guardrail for the module boundaries documented in docs/architecture.
# This is intentionally a source scan rather than a replacement for compilation or unit tests.
set -euo pipefail

readonly UPPER_LAYER_PATHS=(
  "app/src"
  "core/domain/src"
  "core/ui/src"
  "feature"
)

readonly FORBIDDEN_IMPORT_PATTERN='^import com\.kyu\.jiu_jitsu\.data\.(api\.|model\.dto\.|datastore\.|module\.|session\.|repository\.impl\.|model\.singleton\.)'

failed=0

if rg -n --glob '*.kt' "$FORBIDDEN_IMPORT_PATTERN" "${UPPER_LAYER_PATHS[@]}"; then
  echo "Architecture violation: an upper layer imports a data implementation type." >&2
  failed=1
fi

if rg -n 'projects\.core\.data' core/ui/build.gradle.kts; then
  echo "Architecture violation: core:ui depends on core:data." >&2
  failed=1
fi

if rg -n 'projects\.core\.data|jjs\.android|androidx\.datastore|jjs\.hilt' core/domain/build.gradle.kts; then
  echo "Architecture violation: core:domain is no longer framework independent." >&2
  failed=1
fi

if (( failed != 0 )); then
  exit 1
fi

echo "Architecture boundary check passed."
