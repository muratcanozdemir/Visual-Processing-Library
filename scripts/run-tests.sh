#!/usr/bin/env bash
# Compile and run every verification harness. Any non-zero exit fails the run.
#
# This step is the point of the pipeline. javac alone cannot catch decompiler
# damage that is type-correct but wrong at runtime; the harnesses can.
set -euo pipefail
cd "$(dirname "$0")/.."

CP="classes:oracle:lib/jama.jar"

[ -d classes ] || { echo "!! no classes/ — run ./build.sh first"; exit 1; }

echo ">> compiling harnesses"
javac -nowarn -cp "classes:lib/jama.jar" -d oracle oracle/*.java

status=0
for t in Smoke Recovered Sweep; do
  echo ""
  echo "== $t =="
  if java -cp "$CP" "$t"; then
    :
  else
    echo "!! $t failed"
    status=1
  fi
done

echo ""
if [ "$status" -eq 0 ]; then
  echo ">> all harnesses passed"
else
  echo ">> FAILURES — see above"
fi
exit "$status"
