#!/usr/bin/env bash
# Build the VPT Java oracle.
#
#   ./build.sh          compile everything into ./classes
#   ./build.sh test     compile, then run the three verification harnesses
#
# Requires: JDK 17+ (developed against javac 21) and curl for the Jama fetch.

set -euo pipefail
cd "$(dirname "$0")"

JAMA_URL="https://repo1.maven.org/maven2/gov/nist/math/jama/1.0.3/jama-1.0.3.jar"
JAMA="lib/jama.jar"

if [ ! -f "$JAMA" ]; then
  echo ">> fetching Jama 1.0.3"
  mkdir -p lib
  curl -sSL -o "$JAMA" "$JAMA_URL"
  file "$JAMA" | grep -q 'Java archive' || { echo "!! Jama download is not a JAR"; exit 1; }
fi

echo ">> compiling"
rm -rf classes && mkdir -p classes

# Build every source except the entries listed in EXCLUDED.txt (see README).
find src -name '*.java' | sed 's|^src/||' | sort > /tmp/vpt_all.txt
sort EXCLUDED.txt > /tmp/vpt_excl.txt
comm -23 /tmp/vpt_all.txt /tmp/vpt_excl.txt | sed 's|^|src/|' > /tmp/vpt_build.txt

echo "   $(wc -l < /tmp/vpt_build.txt) source files"
javac -Xmaxerrs 3000 -nowarn -d classes -cp "$JAMA" @/tmp/vpt_build.txt

echo ">> $(find classes -name '*.class' | wc -l) class files"

if [ "${1:-}" = "test" ]; then
  echo ">> compiling harnesses"
  javac -nowarn -cp "classes:$JAMA" -d oracle oracle/*.java
  CP="classes:oracle:$JAMA"
  for t in Smoke Recovered Sweep; do
    echo ""
    echo "== $t =="
    java -cp "$CP" "$t"
  done
fi
