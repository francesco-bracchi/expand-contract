(ns exco.table.core
  (:refer-clojure :exclude [empty])
  (:require [clojure.spec.alpha :as s]
            [exco.table.spec]))

(defn valid?
  [descr]
  (s/valid? :table/t descr))

(defn validate!
  [descr]
  (when-not (valid? descr)
    (throw (ex-info "invalid table definition" {:reason (s/explain-data :table/t descr)}))))

(defn make
  [cols]
  #:table{:columns cols})

(def empty (make []))

(defn add-column
  [descr name col]
  (update descr :table/columns assoc name col))

(defn drop-column
  [descr name]
  (update descr :table/columns dissoc name))

(defn add-constraint
  [descr name const]
  (update descr :table/constraints assoc name const))

(defn drop-constraint
  [descr name]
  (update descr :table/constraints dissoc name))
