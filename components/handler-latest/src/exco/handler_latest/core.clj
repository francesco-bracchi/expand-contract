(ns exco.handler-latest.core
  (:require [exco.workspace-io.interface :as io]
            [exco.fs.interface :as fs]
            [exco.format.interface :as format]
            [exco.defaults.interface :as defaults]))

(def default-args
  {:directory defaults/directory
   :workspace-file defaults/workspace-file})

(defn print-migration
  [mig]
  (if (= :io/ref (-> mig meta :type))
    (format/print (-> mig meta :file fs/file (.getAbsolutePath)))
    (format/print-color mig)))

(defn latest
  [ws project]
  (let [project (or project (:workspace/default-project ws))]
    (-> ws
        :workspace/projects
        (get project)
        :project/migrations
        (last)
        (print-migration))))

(defn handle*
  [{:keys [directory workspace-file project]}]
  (io/with-base directory
    (latest (io/read-file workspace-file) project)))

(defn handle
  [cmd]
  (handle* (merge default-args cmd)))
