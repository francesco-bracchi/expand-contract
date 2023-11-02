(ns exco.workspace.core
  (:refer-clojure :exclude [swap!])
  (:require [clojure.spec.alpha :as s]
            [exco.workspace.spec]))

(defn valid?
  [ws]
  (s/valid? :workspace/t ws))

(defn validate!
  [ws]
  (when-not (valid? ws)
    (throw (ex-info "invalid workspace" {:reason (s/explain-data :workspace/t ws)})))
  ws)
