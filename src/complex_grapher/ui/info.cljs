(ns complex-grapher.ui.info)

(defn- about [back]
  [:div#about.info
   [:div.info-wrapper
    [:h2 "About"]
    [:p "This is a tool for graphing functions of " [:a {:href "https://en.wikipedia.org/wiki/Complex_number" :target "_blank"} "complex numbers"] ". "
        [:a {:href "https://en.wikipedia.org/wiki/Domain_coloring" :target "_blank"} "Domain coloring"] " is used to graph functions over the complex plane."]
    [:p "The complex grapher was created by " [:a {:href "https://talbrenev.com/" :target "_blank"} "Tal Brenev"] ". "
        "This software is open source, and licensed under the " [:a {:href "https://github.com/TalBrenev/complex-grapher/blob/master/LICENSE" :target "_blank"} "GNU General Public License"] ". "
        "The source code can be found on " [:a {:href "https://github.com/TalBrenev/complex-grapher" :target "_blank"} "GitHub"] "."]]
   [:button {:onClick back} "Close About"]])

(defn- help [back]
  [:div#help.info
   [:div.info-wrapper
    [:h2 "Help"]
    [:div.section
     [:h3 "Entering a Function"]
     [:p "Enter a function by typing in the textbox at the top. The function must be expressed in terms of \"z\"."]]
    [:div.section
     [:h3 "Navigation"]
     [:p "Move around the complex plane by clicking & dragging with your mouse. Zoom in and out by using the scroll wheel of your mouse."]
     [:p "On mobile devices, tap and drag to move around the complex plane, and pinch with two fingers to zoom in and out."]]
    [:div.section
     [:h3 "Supported Symbols"]
     [:p "The below table describes operations, functions, and constants which can be used when entering a function."]
     [:table
      [:thead
       [:tr
        [:th "Symbol"]
        [:th "Description"]]]
      [:tbody
       [:tr
        [:td "+"]
        [:td "addition"]]
       [:tr
        [:td "-"]
        [:td "subtraction, additive inverse"]]
       [:tr
        [:td "*"]
        [:td "multiplication"]]
       [:tr
        [:td "/"]
        [:td "division"]]
       [:tr
        [:td "^"]
        [:td "exponentiation"]]
       [:tr
        [:td "re"]
        [:td "real part of complex number"]]
       [:tr
        [:td "im"]
        [:td "imaginary part of complex number"]]
       [:tr
        [:td "arg"]
        [:td "argument of complex number"]]
       [:tr
        [:td "mag"]
        [:td "magnitude of complex number"]]
       [:tr
        [:td "sin"]
        [:td "complex sine"]]
       [:tr
        [:td "cos"]
        [:td "complex cosine"]]
       [:tr
        [:td "tan"]
        [:td "complex tangent"]]
       [:tr
        [:td "log, ln"]
        [:td "complex logarithm"]]
       [:tr
        [:td "e"]
        [:td "Euler's number"]]
       [:tr
        [:td "pi"]
        [:td "3.14159..."]]
       [:tr
        [:td "i"]
        [:td "the imaginary unit"]]]]]]
   [:button {:onClick back} "Close Help"]])

(defn info [show-about show-help]
  (when (or @show-about @show-help)
    [:div#info
     (when @show-about
       [about #(reset! show-about false)])
     (when @show-help
       [help #(reset! show-help false)])]))
