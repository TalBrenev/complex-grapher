(ns complex-grapher.utils)

(defn find-first [pred coll]
  "Returns the first item in `coll` for which `pred` returns logical true."
  (first (filter pred coll)))

(defn in? [item coll]
  "Returns `true` if `item` is in `coll`, otherwise returns `nil`."
  (some #(= item %) coll))
