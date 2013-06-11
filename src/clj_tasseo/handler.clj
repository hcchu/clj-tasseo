(ns clj-tasseo.handler
  (:use compojure.core)
  (:use hiccup.core)
  (:use hiccup.element)
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

(def index-header
   [:head
    [:title "Tasseo"]
    [:meta {:content "text/html;charset=utf-8" :http-equiv "Content-Type"}]
    [:link {:href "c/style.css" :rel "stylesheet" :type "text/css"} ]
    [:script {:src "https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"
              :type "text/javascript"}]
    [:script {:src "j/d3.v2.min.js" :type "text/javascript"}]
    [:script {:src "j/rickshaw.min.js" :type "text/javascript"}]
    [:script {:src "j/crypto-min.js" :type "text/javascript"}]])

(defn index-page [dashboards]
  (do (html
        [:html {:xmlns "http://www.w3.org/1999/xhtml"}
         index-header
         [:body
          [:div {:class "title"}
           [:span "Tasseo"]]]
         (if-not (empty? dashboards)
           [:div {:class "nav"}
            [:ul
             (map #(html [:li [:a {:href %} (str %)]]) dashboards)]]
           (html [:p "No dashboard files found."]))])))

(defn index-json [dashboards]
  (if-not (empty? dashboards)
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (generate-string {:dashboards dashboards})}
    {:status 204
     :headers {"Content-Type" "application/json"}}))

(defn show-dashboard [path]
  (do (html
        [:html {:xmlns "http://www.w3.org/1999/xhtml"}
         index-header
         [:body
          [:div.title
           [:span path]
           [:div.toolbar
            [:ul.timepanel
             [:li.timepanel.live.selected
              [:a.play {:href "#"} "live" ]]
             [:li.timepanel
              [:a.range {:href "#" :title "60"} "1h" ]]
             [:li.timepanel
              [:a.range {:href "#" :title "180"} "3h" ]]
             [:li.timepanel
              [:a.range {:href "#" :title "1440"} "1d" ]]
             [:li.timepanel
              [:a.range {:href "#" :title "10080"} "1w" ]]]
            [:ul.toggle
             [:li.toggle-nonum
              [:a {:href "#"} [:img {:src "i/toggle-number.png"}]]]
             [:li.toggle-night
              [:a {:href "#"} [:img {:src "i/toggle-night.png"}]]]]]]
          [:div.main
           (javascript-tag
             "var url = \"http://admin.upverter.com\"; var auth = \"\";")
           [:script {:type "text/javascript" :src (str "dashboards/" path ".js")}]
           [:script {:type "text/javascript" :src "j/tasseo.js"}]
           [:script {:type "text/javascript" :src "j/clj-tasseo.js"}]]]])))

(defn no-dashboard []
  (do
    (html
      [:html {:xmlns "http://www.w3.org/1999/xhtml"}
       index-header
       [:body
        [:div {:class "title"}
         [:span "Tasseo"]]
        [:p "That dashboard does not exist."]]])))

(defroutes app-routes
  (GET "/" {{accept "accept"} :headers}
       (if (re-find #"application\/json" accept)
         (index-json dashboards)
         (index-page dashboards)))
  (GET "/health" [] {:headers {"Content-Type" "json" }
                     :body (generate-string {"status" "ok"})})
  (route/resources "/")
  (route/resources "/dashboards" {:root "dashboards"})
  (GET ["/:path", :path #"\S+$"] [path]
       (if (some (partial = path) dashboards)
         (show-dashboard path)
         (no-dashboard)))
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
