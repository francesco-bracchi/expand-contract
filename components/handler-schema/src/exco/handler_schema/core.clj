(ns exco.handler-schema.core
  (:require [exco.defaults.interface :as defaults]
            [exco.workspace.interface]
            [exco.format.interface :as format]
            [exco.workspace-io.interface :as io]
            [exco.db.interface :as db]))

(defn schema
  [{:workspace/keys [default-db databases]} {:keys [database color]}]
  (let [db (or (keyword database) default-db)
        schema (db/schema (databases db))]
    (if color
      (format/print-color schema)
      (format/print schema))))

(defn handle*
  [{:keys [workspace-file directory] :as cmd}]
  (io/with-base directory
    (schema (io/read-file workspace-file) cmd)))

(def default-args
  {:directory defaults/directory
   :workspace-file defaults/workspace-file
   :color defaults/color})

(defn handle
  [cmd]
  (handle* (merge default-args cmd)))
