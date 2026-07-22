#!/usr/bin/env bash
# Produce the release jars into build-out/.
#
#   vpt-<version>.jar          compiled library
#   vpt-<version>-sources.jar  repaired sources (the substantive artifact —
#                              this tree is a repaired decompile, so the source
#                              is what a consumer actually needs to read)
#
# VERSION comes from the git tag when present, otherwise a dev string.
set -euo pipefail
cd "$(dirname "$0")/.."

VERSION="${VERSION:-$(git describe --tags --exact-match 2>/dev/null || echo "0.0.0-dev+$(git rev-parse --short HEAD 2>/dev/null || echo local)")}"
VERSION="${VERSION#v}"

OUT="build-out"
rm -rf "$OUT" && mkdir -p "$OUT"

[ -d classes ] || { echo "!! no classes/ — run ./build.sh first"; exit 1; }

COMMIT="$(git rev-parse HEAD 2>/dev/null || echo unknown)"
BUILT="$(date -u +%Y-%m-%dT%H:%M:%SZ)"

cat > "$OUT/MANIFEST.MF" <<MANIFEST
Manifest-Version: 1.0
Implementation-Title: Visual Processing Library (repaired decompile)
Implementation-Version: ${VERSION}
Implementation-Vendor: vpt-java-oracle
Build-Jdk: $(javac -version 2>&1 | awk '{print $2}')
Build-Commit: ${COMMIT}
Build-Date: ${BUILT}
MANIFEST

echo ">> vpt-${VERSION}.jar"
jar --create --file "$OUT/vpt-${VERSION}.jar" \
    --manifest "$OUT/MANIFEST.MF" \
    -C classes .

echo ">> vpt-${VERSION}-sources.jar"
jar --create --file "$OUT/vpt-${VERSION}-sources.jar" \
    --manifest "$OUT/MANIFEST.MF" \
    -C src .

rm -f "$OUT/MANIFEST.MF"

( cd "$OUT" && sha256sum ./*.jar > SHA256SUMS )

echo ""
ls -lh "$OUT"
echo ""
cat "$OUT/SHA256SUMS"
