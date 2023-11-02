(ns exco.db.interface
  (:refer-clojure :exclude [empty])
  (:require [exco.db.core :as core]))

(defn empty
  ([] (core/empty))
  ([descr] (core/empty descr)))

(defn schema
  [db]
  (core/schema db))

(defn schema!
  [db]
  (core/schema! db))

(defn valid?
  [db]
  (core/valid? db))

(defn validate!
  [db]
  (core/validate! db))

(defn migrations-explain
  [db]
  (core/migrations-explain db))
