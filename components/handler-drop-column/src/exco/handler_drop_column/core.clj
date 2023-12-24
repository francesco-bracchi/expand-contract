(ns exco.handler-drop-column.core
  (:require [clojure.core :as clj]
            [clojure.spec.alpha :s s]
            [exco.defaults.interface  :as defaults]
            [exco.column.interface :as column]
            [exco.patch.interface :as patch]
            [exco.project.interface :as project]
            [exco.patch-apply.interface  :as pa]
            [exco.format.interface :as format]
            [exco.workspace-io.interface :as io]))

(defn update-last
  [coll fun & args]
  (conj (pop coll) (apply fun (peek coll) args)))

(defn patch
  [table name col]
  [#:hunk{:action :action/drop-column
          :table-name (clj/keyword table)
          :column-name (clj/keyword name)
          :column (column/validate! col)}])

(defn drop-column
  [{:workspace/keys [default-project projects] :as ws}
   {:keys [project table name]}]
  (let [project (or (keyword project) default-project)
        tn (clj/keyword table)
        cn (clj/keyword name)
        sc (project/schema (projects project))
        cl (-> sc :schema/tables (get tn) :table/columns (get cn))
        pc (patch tn cn cl)]
    (when-let [errors (seq (pa/check sc pc))]
      (throw (ex-info "cannot drop column" {:errors errors})))
     (update-in ws
                [:workspace/projects project :project/migrations]
                update-last
                update :migration/patch patch/compose pc)))

(def default-args
  {:directory defaults/directory
   :workspace-file defaults/workspace-file})

(defn handle*
  [{:keys [directory workspace-file] :as cmd}]
  (io/with-base directory
    (io/print-file
     (drop-column (io/read-file workspace-file) cmd)
     workspace-file)
    (format/print :ok)))

(defn handle
  [cmd]
  (handle* (merge default-args cmd)))
