(ns exco.patch.interface
  (:require [exco.patch.core :as core]))

(def zero
  core/zero)

(defn compose
  [p & ps]
  (apply core/compose p ps))

(defn inverse
  [p]
  (core/inverse p))
