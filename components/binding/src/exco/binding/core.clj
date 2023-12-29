(ns exco.binding.core
  (:refer-clojure :exclude [get])
  (:require [honey.sql.helpers :as sqh]))

(def ^:const table
  :bindings)

(defn ddl
  [_dialect]
  (-> (sqh/create-table table)
      (sqh/with-columns
        [:id :integer :primary_key]
        [:project :text [:not nil]]
        [:env :text [:not nil]]
        [:conn :text [:not nil]]
        [[:constraint :unique_env_per_project] :unique [:composite :project :env]])))

(defn insert
  []
  (sqh/insert-into table))

(defn delete
  []
  (sqh/delete-from table))

(defn query
  []
  (sqh/from table))

(defn fields
  [query]
  (sqh/select query :id :project :env :conn))

(defn with-project
  [query prj]
  (sqh/where query := :project prj))

(defn with-env
  [query env]
  (sqh/where query  := :env env))
