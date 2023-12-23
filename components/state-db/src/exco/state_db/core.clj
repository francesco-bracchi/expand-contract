(ns exco.state-db.core
  (:refer-clojure :exclude [name])
  (:require [clojure.core :as clj]))

(def ^:dynamic *namespace*)

(defn create-table
  [name table]
  (ddl/create-table {:namespace *namespace*
                     :table-name name
                     :table table}))

(defn create-namespace
  []
  (ddl/create-namespace {:namespace *namespace*}))

(defn init
  [namespace]
  (binding [*namespace* namespace]
    (create-namespace)
    (create-table :databases database-table)))
