(ns complex-grapher.utils)

(defn find-first [pred coll]
  "Returns the first item in `coll` for which `pred` returns logical true."
  (first (filter pred coll)))

(defn in? [item coll]
  "Returns `true` if `item` is in `coll`, otherwise returns `nil`."
  (some #(= item %) coll))

(defn get-element [id]
  "Gets the element with the given id in the DOM."
  (.getElementById js/document id))

(defn get-attr [id attr]
  "Gets an attribute of the element with the given id."
  (aget (get-element id) attr))

(defn set-attr [id attr value]
  "Sets an attribute of the element with the given id."
  (aset (get-element id) attr value))

(defn width [id]
  "Gets the width of an element."
  (get-attr id "scrollWidth"))

(defn height [id]
  "Gets the height of an element."
  (get-attr id "scrollHeight"))
