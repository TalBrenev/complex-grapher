#!/bin/sh

# Fail fast on error
set -e

lein deps
lein build
mkdir -p build/complexgrapher
cp -r resources/public/* build/complexgrapher
rm -r build/complexgrapher/cljs-out
