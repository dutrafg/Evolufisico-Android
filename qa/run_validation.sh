#!/usr/bin/env sh
# Full local gate. Any missing toolchain or failed check stops with a nonzero exit.
set -eu
project_dir=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$project_dir"
python3 qa/check_source.py
python3 qa/check_window_contract.py
python3 qa/check_html_ui.py
python3 qa/check_palette.py
python3 qa/check_visual_effects.py
python3 qa/check_dashboard_grid.py
python3 -c 'from pathlib import Path; import xml.etree.ElementTree as ET; files=list(Path("app/src/main").rglob("*.xml")); [ET.parse(p) for p in files]; print("XML válidos:", len(files))'
case "${1:-}" in
  "") bash gradlew :app:testDebugUnitTest :app:lintDebug --console=plain ;;
  --apk) bash gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --console=plain ;;
  *) echo "Uso: bash qa/run_validation.sh [--apk]" >&2; exit 2 ;;
esac
