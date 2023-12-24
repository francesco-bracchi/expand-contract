(ns exco.cli.core
  (:require [exco.command.interface :as command]
            [exco.handler.interface :as handler]
            [exco.format.interface  :as format])
  (:gen-class))

;; todo: separate in specific functions handling errors
;; in parsing commands and handle
;; try to give nice and clear error messages (and hints on what's wrong)
(defn -main
  [& args]
  (try
    (-> args
        (command/parse)
        (handler/handle))
    (catch clojure.lang.ExceptionInfo ex
      (println (ex-message ex))
      (format/print-color (ex-data ex))
      (System/exit 1))
    (catch Exception ex
      (println "unknown exception")
      (format/print-color ex)
      (System/exit 2))))
