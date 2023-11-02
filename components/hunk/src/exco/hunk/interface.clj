(ns exco.hunk.interface
  (:refer-clojure :exclude [commute])
  (:require [exco.hunk.core :as core]))

(defn inverse
  [p]
  (core/inverse p))

(defn coalesce
  [p q]
  (core/coalesce p q))

(defn commute
  [p q]
  (core/commute p q))

(defn valid?
  [p]
  (core/valid? p))

(defn validate!
  [p]
  (core/validate! p))
