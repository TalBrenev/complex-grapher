#!/bin/sh

# Fail fast on error
set -e

lein deps
lein fig:min
mkdir -p build/complex-grapher
cp -r resources/public/* build/complex-grapher
rm -r build/complex-grapher/cljs-out
