(ns clj-tasseo.tasseo
  (:use-macros [dommy.macros :only [sel sel1 node]])
  (:require [dommy.core :as dommy]
            [dommy.attrs :as attrs]
            [dommy.template :as template]))

(extend-protocol template/PElement
  js/SVGSVGElement
  (-elem [this] this))

(defn enableNightMode []
  (do (dommy/add-class! (sel1 :body) "night")
      (mapv #(dommy/add-class! % "night") (sel [:div.title :h1]))
      (mapv #(attrs/set-style! % :opacity 0.8) (sel [:div.graph :svg]))
      (mapv #(dommy/add-class! % "night") (sel [:span.description]))
      (mapv #(dommy/add-class! % "night") (sel [:div.overlay-name]))
      (mapv #(dommy/add-class! % "night") (sel [:div.overlay-number]))
      (mapv #(dommy/add-class! % "night") (sel [:div.toolbar :ul :li.timepanel]))))

(defn disableNightMode []
  (do (dommy/remove-class! (sel1 :body) "night")
      (mapv #(dommy/remove-class! % "night") (sel [:div.title :h1]))
      (mapv #(attrs/set-style! % :opacity 1.0) (sel [:div.graph :svg]))
      (mapv #(dommy/remove-class! % "night") (sel [:span.description]))
      (mapv #(dommy/remove-class! % "night") (sel [:div.overlay-name]))
      (mapv #(dommy/remove-class! % "night") (sel [:div.overlay-number]))
      (mapv #(dommy/remove-class! % "night") (sel [:div.toolbar :ul :li.timepanel]))))

(-> (sel1 :li.toggle-night)
    (dommy/listen! :click (fn [] (if (attrs/has-class? (sel1 :body) "night")
                                (disableNightMode)
                                (enableNightMode)))))

(-> (sel1 :li.toggle-nonum)
    (sel1 :a)
    (dommy/listen! :click (fn [] (mapv #(dommy/toggle-class! % "nonum")
                                       (sel [:div.overlay-number])))))


