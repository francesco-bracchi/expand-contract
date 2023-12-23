(ns exco.handler-check.core
  (:require [exco.workspace-io.interface :as io]
            [exco.project.interface :as project]
            [exco.defaults.interface :as defaults]))

(def default-cmd
  {:directory defaults/directory
   :workspace-file defaults/workspace-file})

(defn check!
  [{:workspace/keys [default-project projects]} {:keys [project]}]
   (let [project (or project default-project)
         {:keys [errors] :as res} (project/migrations-explain (projects project))]
     (when (seq errors) (throw (ex-info "check-failed" res)))))

(defn handle*
  [{:keys [directory workspace-file] :as cmd}]
  (io/with-base directory
    (check! (io/read-file workspace-file) cmd)))

(defn handle
  [cmd]
  (handle* (merge default-cmd cmd)))
