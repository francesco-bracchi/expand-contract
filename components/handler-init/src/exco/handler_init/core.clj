(ns exco.handler-init.core
  (:require [exco.defaults.interface :as defaults]
            [exco.fs.interface :as fs]
            [exco.db.interface :as db]
            [exco.migration.interface :as mg]
            [exco.workspace-io.interface :as io]
            [exco.workspace.interface :as ws]))

(def basic-workspace
  #:workspace{:revision "0.0.1"
              :databases {}})

(defn set-migrations-dir
  [ws {:keys [migrations-dir]}]
  (assoc ws :workspace/migrations-dir migrations-dir))

(defn create-database
  [ws {:keys [migrations-dir default-db db-description]}]
  (let [db-des (or db-description "main application database")
        db-dir (fs/dir migrations-dir (name default-db))
        db-data (db/empty db-des)]
    (fs/mkdir db-dir)
    (-> ws
        (update :workspace/databases assoc (keyword default-db) db-data)
        (assoc :workspace/default-db (keyword default-db)))))

(defn create-migration
  [ws {:keys [migrations-dir default-db migration migration-description]}]
  (let [mg-des (or migration-description (str "<FIXME> first migration '" (name migration) "'"))
        mg-file (fs/dir migrations-dir (name default-db) (str (name migration) ".edn"))
        mg-data (mg/empty mg-des)
        mg-ref (io/ref mg-file mg-data)]
    (update-in ws [:workspace/databases default-db :db/migrations] conj mg-ref)))

(def default-args
  {:directory defaults/directory
   :workspace-file defaults/workspace-file
   :migrations-dir defaults/migrations-dir
   :default-db defaults/db
   :migration (symbol defaults/migration)})

(defn build-ws
  [cmd]
  (-> basic-workspace
      (set-migrations-dir cmd)
      (create-database cmd)
      (create-migration cmd)
      (ws/validate!)))

(defn handle
  [cmd]
  (let [{:keys [directory workspace-file] :as cmd} (merge default-args cmd)]
    (fs/with-base directory
      (io/print-file (build-ws cmd) workspace-file))))
