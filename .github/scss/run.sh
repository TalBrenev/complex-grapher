#!/bin/sh

set -e

sass --no-source-map resources/scss/style.scss resources/public/style.min.css
