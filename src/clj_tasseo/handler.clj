(ns clj-tasseo.handler
  (:use compojure.core)
  (:use hiccup.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [cheshire.core :refer :all]
            [me.raynes.fs :as fs]))

(def dashboards_dir (fs/normalized-path "resources/dashboards"))

(defn find-dashboards [dashboards_dir]
  (into []
        (map #(first (clojure.string/split % #"\."))
             (filter #(re-matches #".+\.js$" %) (fs/list-dir dashboards_dir)))))

(def dashboards (find-dashboards dashboards_dir))

(defn index-page [dashboards]
  (html [:html {:xmlns "http://www.w3.org/1999/xhtml"}
         [:head
          [:title "Tasseo"]
          [:meta {:content "text/html;charset=utf-8" :http-equiv "Content-Type"}]
          [:link {:href "c/style.css" :rel "stylesheet" :type "text/css"} ]
          [:script {:src "https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"
                    :type "text/javascript"}]
          [:script {:src "j/d3.v2.min.js" :type "text/javascript"}]
          [:script {:src "j/rickshaw.min.js" :type "text/javascript"}]
          [:script {:src "j/crypto-min.js" :type "text/javascript"}]]
         [:body
          [:div {:class "title"}
           [:span "Tasseo"]
           ]]]
        (if-not (empty? dashboards)
          [:div {:class "nav"}
           [:ul
            (map #(html [:li [:a {:href %} (str %)]]) dashboards)]]
          (html [:p "No dashboard files found."]))))

(defn index-json [dashboards]
  (if-not (empty? dashboards)
    ({:status 200
     :headers {"Content-Type" "application/json"}
     :body (generate-string dashboards)})
    {:status 204
     :headers {"Content-Type" "application/json"}}))

(defroutes app-routes
  (GET "/" {{accept "accept"} :headers}
       (if (re-find #"application\/json" accept)
         (index-json dashboards)
         (index-page dashboards)))
  (GET "/health" [] {:headers {"Content-Type" "json" }
                     :body (generate-string {"status" "ok"})})
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
