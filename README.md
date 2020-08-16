# The Complex Grapher

An online tool for creating graphs of functions on the [complex
plane](https://en.wikipedia.org/wiki/Complex_plane).

Written in [ClojureScript](https://github.com/clojure/clojurescript) with
[Reagent](https://github.com/reagent-project/reagent), using
[WebGL](https://developer.mozilla.org/en-US/docs/Web/API/WebGL_API) to do the
heavy lifting.

## Usage

The Complex Grapher lives [here](https://talbrenev.com/complexgrapher).

## Development

You'll need [Leiningen](https://github.com/technomancy/leiningen) version 2.9.0 or later.

In the project root, `lein repl` will run the dev environment at
`localhost:9500` and give you a ClojureScript repl. It will also run tests
automatically; you can see the results at
`localhost:9500/figwheel-extra-main/auto-testing`. If you want to run tests
from the command line, you can use `lein test`.
