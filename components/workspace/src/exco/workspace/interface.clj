(ns exco.workspace.interface
  (:refer-clojure :exclude [swap!])
  (:require [exco.workspace.core :as core]))

(defn valid?
  [ws]
  (core/valid? ws))

(defn validate!
  [ws]
  (core/validate! ws))
