(ns exco.migration-log.interface
  (:require [exco.migration-log.core :as core]))

(def ^:const table
  core/table)

(defn bound-to
  [query prj env]
  (core/bound-to query prj env))

(defn sorted
  [query]
  (core/sorted query))

(defn with-states
  [query states]
  (core/with-states query states))

(defn with-state
  [query state]
  (core/with-state query state))
