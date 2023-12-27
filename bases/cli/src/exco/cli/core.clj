(ns exco.cli.core
  (:require [exco.command.interface :as command]
            [exco.handler.interface :as handler]
            [exco.format.interface  :as format])
  (:gen-class))

(defn main
  [& args]
   (-> args
       (command/parse)
       (handler/handle)))

(defn -main
  [& args]
  (try (apply main args)
       (catch clojure.lang.ExceptionInfo ex
         (binding [*out* *err*]
           (format/print-error (:reason (ex-data ex)))
           (println)
           (System/exit 1)))
       (catch Exception ex
         (binding [*out* *err*]
           (println "unknown exception")
           (format/print-color ex)
           (println)
           (System/exit 2)))))
