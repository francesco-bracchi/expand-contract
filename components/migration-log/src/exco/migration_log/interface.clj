(ns exco.migration-log.interface
  (:refer-clojure :exclude [update sort])
  (:require [exco.migration-log.core :as core]))

(defn ddl
  [d]
  (core/ddl d))

(defn query
  []
  (core/query))

(defn insert
  []
  (core/insert))

(defn delete
  []
  (core/delete))

(defn update
  []
  (core/update))

(defn bound
  ([query prj] (core/bound query prj))
  ([query prj env] (core/bound query prj env)))

(defn sort
  [q]
  (core/sort q))

(defn with-states
  [q ss]
  (core/with-states q ss))

(defn with-state
  [q s]
  (core/with-state q s))

(defn fields
  [q]
  (core/fields q))
