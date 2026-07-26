#!/usr/bin/env bash
# Regenerate the golden files from the Java oracle.
#
# The goldens are committed so that the Python test suite runs without a JDK.
# Run this only when the oracle's behaviour legitimately changes, or when a
# new Dump*.java driver is added -- never to make a failing test pass. A
# golden that changes without a corresponding change in src/ means the Java
# tree moved, and that needs explaining, not blessing.
#
# Requires the Java tree to be built first (../build.sh).
set -euo pipefail
cd "$(dirname "$0")/../.."

[ -d classes ] || { echo "!! no classes/ — run ./build.sh first"; exit 1; }

CP="classes:oracle:lib/jama.jar"
OUT="python/tests/goldens"
mkdir -p "$OUT"

echo ">> compiling dump drivers"
javac -nowarn -cp "classes:lib/jama.jar" -d oracle oracle/Dump*.java

for driver in oracle/Dump*.java; do
  name="$(basename "$driver" .java)"
  # DumpCore -> core.txt, DumpJavaCompat -> javacompat.txt
  target="$(echo "${name#Dump}" | tr '[:upper:]' '[:lower:]').txt"
  echo ">> $name -> $OUT/$target"
  # stderr is dropped: IntegerImage.fill(double) legitimately prints
  # "Not yet implemented!!!", which is behaviour under test, not an error.
  java -cp "$CP" "$name" 2>/dev/null > "$OUT/$target"
  echo "   $(wc -l < "$OUT/$target") rows"
done

echo ""
echo ">> goldens regenerated; review the diff before committing"
git -C . diff --stat -- "$OUT" || true
