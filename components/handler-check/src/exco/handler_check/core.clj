(ns exco.handler-check.core
  (:require [exco.workspace-io.interface :as io]
            [exco.db.interface :as db]
            [exco.defaults.interface :as defaults]))

(def default-cmd
  {:directory defaults/directory
   :workspace-file defaults/workspace-file})

(defn check!
  [{:workspace/keys [default-db databases]} {:keys [database]}]
   (let [db (or database default-db)
         {:keys [errors] :as res} (db/migrations-explain (databases db))]
     (when (seq errors) (throw (ex-info "check-failed" res)))))

(defn handle*
  [{:keys [directory workspace-file] :as cmd}]
  (io/with-base directory
    (check! (io/read-file workspace-file) cmd)))

(defn handle
  [cmd]
  (handle* (merge default-cmd cmd)))
