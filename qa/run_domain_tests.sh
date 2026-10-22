#!/usr/bin/env sh
# Runs pure-Kotlin domain assertions on Linux/macOS; cannot replace Android SDK build or instrumentation.
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
SRC="$ROOT/app/src/main/java/br/com/evolufisico/nativo"
OUTPUT="${TMPDIR:-/tmp}/ef-native-domain-$$.jar"
trap 'rm -f "$OUTPUT"' EXIT
command -v kotlinc >/dev/null 2>&1 || { echo 'Kotlin compiler not found'; exit 1; }
kotlinc \
  "$ROOT/qa/stubs/JsonStub.kt" "$ROOT/qa/stubs/AndroidBase64Stub.kt" \
  "$SRC/data/Entities.kt" "$SRC/data/WorkspaceEntities.kt" "$SRC/data/ExerciseDefinition.kt" \
  "$SRC/data/LegacyImporter.kt" "$SRC/data/ReadLimits.kt" \
  "$SRC"/domain/*.kt \
  "$ROOT/qa/AgendaTimelineSelfTest.kt" "$ROOT/qa/DomainSelfTest.kt" -include-runtime -d "$OUTPUT"
java -jar "$OUTPUT"
python3 "$ROOT/qa/check_source.py"
