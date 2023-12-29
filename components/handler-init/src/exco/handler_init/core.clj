(ns exco.handler-init.core
  (:require [exco.defaults.interface :as defaults]
            [exco.fs.interface :as fs]
            [exco.project.interface :as project]
            [exco.migration.interface :as mg]
            [exco.workspace-io.interface :as io]
            [exco.workspace.interface :as ws]))

(def basic-workspace
  #:workspace{:revision "0.0.1"
              :projects {}
              :migrators {:local {:migrator/type defaults/migrator-type
                                  :migrator/db-url defaults/migrator-db-url}}
              :default-migrator :local})

(defn set-migrations-dir
  [ws {:keys [migrations-dir]}]
  (assoc ws :workspace/migrations-dir migrations-dir))

(defn create-project
  [ws {:keys [migrations-dir default-project project-description]}]
  (let [project-des (or project-description "main application project")
        project-dir (fs/dir migrations-dir (name default-project))
        project-data (project/empty project-des)]
    (fs/mkdir project-dir)
    (-> ws
        (update :workspace/projects assoc (keyword default-project) project-data)
        (assoc :workspace/default-project (keyword default-project)))))

(defn create-migration
  [ws {:keys [migrations-dir default-project migration migration-description]}]
  (let [mg-des (or migration-description (str "<FIXME> first migration '" (name migration) "'"))
        mg-file (fs/dir migrations-dir (name default-project) (str (name migration) ".edn"))
        mg-data (mg/empty mg-des)
        mg-ref (io/ref mg-file mg-data)]
    (update-in ws [:workspace/projects default-project :project/migrations] conj mg-ref)))

(def default-args
  {:directory defaults/directory
   :workspace-file defaults/workspace-file
   :migrations-dir defaults/migrations-dir
   :default-project defaults/project
   :migration (symbol defaults/migration)})

(defn build-ws
  [cmd]
  (-> basic-workspace
      (set-migrations-dir cmd)
      (create-project cmd)
      (create-migration cmd)
      (ws/validate!)))

(defn handle
  [cmd]
  (let [{:keys [directory workspace-file] :as cmd} (merge default-args cmd)]
    (fs/with-base directory
      (io/print-file (build-ws cmd) workspace-file))))
