#!/bin/sh
set -e

BUILD_DIR="/tmp/rentacar_test_classes"

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

javac -d "$BUILD_DIR" $(find src test -type f -name '*.java')
java -cp "$BUILD_DIR" test.ServiceTests
