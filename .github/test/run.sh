#!/bin/sh

# Fail fast on error
set -e

lein deps
lein test
