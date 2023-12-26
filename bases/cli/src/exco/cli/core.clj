(ns exco.cli.core
  (:require [exco.command.interface :as command]
            [exco.handler.interface :as handler]
            [exco.format.interface  :as format])
  (:gen-class))


(defn print-error
  [err]
  (binding [*out* *err*]
    (err)))

(defn main
  [args]
  (try (-> args
           (command/parse)
           (handler/handle))
       0
       (catch clojure.lang.ExceptionInfo ex
         (binding [*out* *err*]
           (format/print-error (:reason (ex-data ex)))
           (println)
           1))

       (catch Exception ex
         (binding [*out* *err*]
           (println "unknown exception")
           (format/print-color ex)
           (println))
         2)))

(main [])

(defn -main
  [& args]
  (-> args (main) (System/exit)))
