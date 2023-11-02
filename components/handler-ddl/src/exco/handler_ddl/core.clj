(ns exco.handler-ddl.core
  (:require [exco.defaults.interface :as defaults]
            [exco.schema-ddl.interface :as ddl]
            [exco.workspace-io.interface :as io]
            [exco.db.interface :as db]))

(def default-args
  {:directory defaults/directory
   :workspace-file defaults/workspace-file})

(defn ddl
  [{:workspace/keys [default-db databases]} {:keys [database]}]
  (let [db (or (keyword database) default-db)
        schema (db/schema (databases db))]
    (ddl/ddl schema)))

(defn handle*
  [{:keys [workspace-file directory] :as cmd}]
  (io/with-base directory
    (ddl (io/read-file workspace-file) cmd)))

(defn handle
  [cmd]
  (handle* (merge default-args cmd)))
