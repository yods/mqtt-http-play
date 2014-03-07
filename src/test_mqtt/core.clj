(ns test-mqtt.core
  (:require [clojurewerkz.machine-head.client :as mh]
            [clojure.data.json :as json]
            [org.httpkit.server :refer :all]))



(defn handler [request]
  (with-channel request channel
    (on-close channel (fn [status] (println "channel closed, " status)))
    (let [test-topics ["yods/pollution/test1" "yods/foo/test2" "yods/pollution/test11"]
          id (mh/generate-id)
          conn  (mh/connect "tcp://127.0.0.1:1883" id)]

            (mh/subscribe conn test-topics (fn [^String topic _ ^bytes payload]
                                       (send! channel "gotit";;{:topic topic :payload (String. payload "UTF-8")} false
                                              )))
      (dotimes [_ 100]
        (mh/publish conn (rand-nth test-topics) (json/write-str {:value (rand-int 100)}))
        (Thread/sleep 10))
      (close channel)))) ;; close in 10s.

;;; open you browser http://127.0.0.1:9090, 
#_(run-server handler {:port 4000})
