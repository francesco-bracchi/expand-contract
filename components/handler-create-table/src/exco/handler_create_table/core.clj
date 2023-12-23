(ns exco.handler-create-table.core
  (:require [clojure.core :as clj]
            [exco.defaults.interface  :as defaults]
            [exco.patch.interface :as patch]
            [exco.project.interface :as project]
            [exco.patch-apply.interface  :as pa]
            [exco.workspace-io.interface :as io]))

(def default-args
  {:directory defaults/directory
   :workspace-file defaults/workspace-file})

(defn patch
  [name]
  [#:hunk{:action :action/create-table
          :table-name (keyword name)
          :columns {}}])

(defn update-last
  [coll fun & args]
  (conj (pop coll) (apply fun (peek coll) args)))

(defn create-table
  [{:workspace/keys [default-project projects] :as ws} {:keys [project name]}]
  (let [project (or (keyword project) default-project)
        pc (patch (clj/name name))
        sc (project/schema (projects project))]
    (when-let [errors (seq (pa/check sc pc))]
      (throw (ex-info "cannot create table" {:errors errors})))
    (update-in ws
               [:workspace/projects project :project/migrations]
               update-last
               update :migration/patch patch/compose pc)))

(defn handle*
  [{:keys [directory workspace-file] :as cmd}]
  (io/with-base directory
    (io/print-file
     (create-table (io/read-file workspace-file) cmd)
     workspace-file)))

(defn handle
  [cmd]
  (handle* (merge default-args cmd)))
