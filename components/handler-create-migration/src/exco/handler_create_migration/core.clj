(ns exco.handler-create-migration.core
  (:require [clojure.core :as clj]
            [exco.defaults.interface :as defaults]
            [exco.workspace.interface]
            [exco.migration.interface :as migration]
            [exco.workspace-io.interface :as io]
            [exco.fs.interface :as fs]))

(def default-args
  {:directory defaults/directory
   :workspace-file defaults/workspace-file})

(defn default-description
  [name project]
  (str "<FIXME> migration '" (clj/name name) "' on " (clj/name project)))

(defn create-migration
  [{:workspace/keys [migrations-dir default-project] :as ws}
   {:keys [name project description]}]
  (let [project (keyword (or project default-project))
        mg-desc (or description (default-description name project))
        mg-file (fs/dir migrations-dir (clj/name project) (str (clj/name name) ".edn"))
        mg-data (migration/empty mg-desc)
        mg-ref (io/ref mg-file mg-data)]
    (update-in ws [:workspace/projects project :project/migrations] conj mg-ref)))

(defn handle*
  [{:keys [workspace-file directory] :as cmd}]
  (io/with-base directory
    (io/print-file
     (create-migration (io/read-file workspace-file) cmd)
     workspace-file)))

(defn handle
  [cmd]
  (handle* (merge default-args cmd)))
