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

(defn add-event-listener [element-id event handler]
  "Adds an event listener to an element in the DOM."
  (.addEventListener (get-element element-id) event handler))

(defn width [id]
  "Gets the width of an element."
  (.-scrollWidth (get-element id)))

(defn height [id]
  "Gets the height of an element."
  (.-scrollHeight (get-element id)))
