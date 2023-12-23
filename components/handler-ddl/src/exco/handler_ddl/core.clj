(ns exco.handler-ddl.core
  (:require [exco.defaults.interface :as defaults]
            [exco.schema-ddl.interface :as ddl]
            [exco.workspace-io.interface :as io]
            [exco.project.interface :as project]))

(def default-args
  {:directory defaults/directory
   :workspace-file defaults/workspace-file})

(defn ddl
  [{:workspace/keys [default-project projects]} {:keys [project]}]
  (let [project (or (keyword project) default-project)
        schema (project/schema (projects project))]
    (ddl/ddl schema)))

(defn handle*
  [{:keys [workspace-file directory] :as cmd}]
  (io/with-base directory
    (ddl (io/read-file workspace-file) cmd)))

(defn handle
  [cmd]
  (handle* (merge default-args cmd)))
