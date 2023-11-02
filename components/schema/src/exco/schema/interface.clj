(ns exco.schema.interface
  (:refer-clojure :exclude [empty])
  (:require [exco.schema.core :as core]))

(def empty
  core/empty)

(defn valid?
  [s]
  (core/valid? s))

(defn validate!
  [s]
  (core/validate! s))
