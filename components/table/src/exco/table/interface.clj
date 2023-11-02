(ns exco.table.interface
  (:refer-clojure :exclude [empty])
  (:require [exco.table.core :as core]))

(def empty
  core/empty)

(defn valid?
  [descr]
  (core/valid? descr))

(defn validate!
  [descr]
  (core/validate! descr))

(defn make
  [cols]
  (core/make cols))

(defn add-column
  [descr name col]
  (core/add-column descr name col))

(defn drop-column
  [descr name]
  (core/drop-column descr name))

(defn add-constraint
  [descr name const]
  (core/add-constraint descr name const))

(defn drop-constraint
  [descr name]
  (core/drop-constraint descr name))
