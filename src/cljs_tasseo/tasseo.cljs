(ns clj-tasseo.tasseo
  (:use-macros [dommy.macros :only [sel sel1 node]])
  (:require [dommy.core :as dommy]
            [dommy.attrs :as attrs]
            [dommy.template :as template]))

;; allow dommy to work on svg elements
(extend-protocol template/PElement
  js/SVGSVGElement
  (-elem [this] this)
  js/SVGPathElement
  (-elem [this] this))

;; display description
(->> (sel [:div.graph])
     (mapv #(-> % (dommy/listen! 
                   :mouseenter 
                   (fn [] (let [elem (-> % (sel1 :span.description))
                                textlength (.-length (.-textContent elem))]
                            (if (> textlength 0)
                              (attrs/set-style! elem :visibility "visible"))))))))

;; hide description
(->> (sel [:div.graph])
     (mapv #(-> % (dommy/listen! 
                   :mouseleave
                   (fn [] (let [elem (-> % (sel1 :span.description))]
                              (attrs/set-style! elem :visibility "hidden")))))))

;; clear navigation list on focusout
(-> (sel1 :div.title)
    (dommy/listen! :focusout (fn []
                               (let [title (clojure.string/replace 
                                             js/window.location.pathname 
                                             #"^/" "")]
                                 (dommy/set-html! (sel1 :span) title)))))

;; navigation to selection
(-> (sel1 :div.title)
    (sel1 :span) 
    (dommy/listen! :change (fn []
                             (let [pathstring (str "/" (.-value (sel1 :select)))]
                               (set! js/window.location.pathname pathstring))))) 

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

;; night mode toggle button
(-> (sel1 :li.toggle-night)
    (dommy/listen! :click (fn [] (if (attrs/has-class? (sel1 :body) "night")
                                (disableNightMode)
                                (enableNightMode)))))

;; numbers toggle button
(-> (sel1 :li.toggle-nonum)
    (sel1 :a)
    (dommy/listen! :click (fn [] (mapv #(dommy/toggle-class! % "nonum")
                                       (sel [:div.overlay-number])))))

