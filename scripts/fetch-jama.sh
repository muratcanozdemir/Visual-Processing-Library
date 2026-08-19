#!/usr/bin/env bash
# Fetch Jama 1.0.3 and verify its checksum.
#
# Jama is public domain, last published 2013, and is the only third-party
# runtime dependency (PCA, ReversePCA, NonFlatSE). The checksum is pinned
# because Maven Central artifacts are not immutable by policy alone.
#
# If you would rather not depend on the network at build time, commit
# lib/jama.jar to the repository — it is 37 KB — and this script becomes a
# no-op.
set -euo pipefail
cd "$(dirname "$0")/.."

JAMA_URL="https://repo1.maven.org/maven2/gov/nist/math/jama/1.0.3/jama-1.0.3.jar"
JAMA_SHA256="c7325ee29be1aac1287ddac690f73671f1cd911ca9eca7c619990e86310556fd"
JAMA="lib/jama.jar"

verify() {
  echo "${JAMA_SHA256}  ${JAMA}" | sha256sum -c - >/dev/null 2>&1
}

if [ -f "$JAMA" ] && verify; then
  echo ">> Jama present and verified"
  exit 0
fi

echo ">> fetching Jama 1.0.3"
mkdir -p lib
curl -sSLf --retry 3 --retry-delay 2 -o "$JAMA" "$JAMA_URL"

if ! verify; then
  echo "!! Jama checksum mismatch"
  echo "   expected ${JAMA_SHA256}"
  echo "   actual   $(sha256sum "$JAMA" | cut -d' ' -f1)"
  rm -f "$JAMA"
  exit 1
fi
echo ">> Jama verified"
