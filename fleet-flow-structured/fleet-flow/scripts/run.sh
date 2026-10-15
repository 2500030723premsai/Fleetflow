#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
BUILD="$ROOT/build"
rm -rf "$BUILD"
mkdir -p "$BUILD"
find "$ROOT/src" -name '*.java' -print0 | xargs -0 javac -encoding UTF-8 -d "$BUILD"
java -cp "$BUILD" fleetflow.main.Main
