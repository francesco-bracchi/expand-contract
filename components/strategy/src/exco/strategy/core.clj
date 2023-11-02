(ns exco.strategy.core
  (:require [exco.ddl.interface :as ddl]))

(def ^:dynamic *main-ns*)

(def ^:dynamic *next-ns*)

(def ^:dynamic *curr-ns*)

(defn expand-create-table
  [id name table]
  (let [temp-name (gensym "exco_table")]
    (exec
     (ddl/create-table *main-ns* temp-name table)
     (ddl/create-proxy *next-ns* name temp-name))))

(defn contract-create-table
  [id name table]
  (let [temp-name (gensym "exco_table")]
    (exec
     (ddl/rename-table *main-ns*)
     (ddl/create-table *main-ns* temp-name table)
     (ddl/create-proxy *next-ns* name temp-name))))
