(ns exco.project.interface
  (:refer-clojure :exclude [empty])
  (:require [exco.project.core :as core]))

(defn empty
  ([] (core/empty))
  ([descr] (core/empty descr)))

(defn schema
  [project]
  (core/schema project))

(defn schema!
  [project]
  (core/schema! project))

(defn valid?
  [project]
  (core/valid? project))

(defn validate!
  [project]
  (core/validate! project))

(defn migrations-explain
  [project]
  (core/migrations-explain project))
