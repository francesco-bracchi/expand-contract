(ns exco.binding.core
  (:refer-clojure :exclude [get])
  (:require [clojure.java.io :as io]
            [honey.sql.helpers :as sqh]))

(defn ddl
  [_dialect]
  [(-> "binding/create.ddl"
      io/resource
      slurp)])

(defn bind!
  [& bindings]
  (-> (sqh/insert-into :bindings)
      (sqh/columns :project :env :conn)
      (sqh/values bindings)))

(def ^:const table
  (sqh/from :bindings))

(defn with-project
  [query prj]
  (sqh/where query := :project prj))

(defn with-env
  [query env]
  (sqh/where query  := :env env))

(defn get
  [query prj env]
  (-> query
      (with-project prj)
      (with-env env)))
