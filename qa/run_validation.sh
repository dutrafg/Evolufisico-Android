#!/usr/bin/env sh
# Full local gate. Any missing toolchain or failed check stops with a nonzero exit.
set -eu
project_dir=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$project_dir"
python3 qa/check_source.py
python3 qa/check_window_contract.py
python3 qa/check_palette.py
python3 -c 'from pathlib import Path; import xml.etree.ElementTree as ET; files=list(Path("app/src/main").rglob("*.xml")); [ET.parse(p) for p in files]; print("XML válidos:", len(files))'
bash gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --console=plain
