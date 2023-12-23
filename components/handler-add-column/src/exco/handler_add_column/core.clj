(ns exco.handler-add-column.core
  (:require [clojure.core :as clj]
            [clojure.spec.alpha :s s]
            [exco.defaults.interface  :as defaults]
            [exco.project.interface :as project]
            [exco.column.interface :as column]
            [exco.patch.interface :as patch]
            [exco.patch-apply.interface  :as pa]
            [exco.format.interface :as format]
            [exco.workspace-io.interface :as io]))

(defn update-last
  [coll fun & args]
  (conj (pop coll) (apply fun (peek coll) args)))

(defn column
  [{:keys [type nullable default unique reference primary-key]}]
  (cond-> #:column{:type type
                   :nullable? (boolean nullable)
                   :unique? (boolean unique)
                   :primary-key? (boolean primary-key)}
    (some? default) (assoc :column/default default)
    (some? reference) (assoc :column/reference #:reference{:table (first reference) :column (second reference)})))

(defn patch
  [{:keys [table name] :as cmd}]
  [#:hunk{:action :action/add-column
          :table-name (clj/keyword table)
          :column-name (clj/keyword name)
          :column (column/validate! (column cmd))}])

(defn add-column
  [{:workspace/keys [default-project projects] :as ws}
   {:keys [project] :as cmd}]
  (let [project (or (keyword project) default-project)
        pc (patch cmd)
        sc (project/schema! (projects project))]
    (when-let [errors (seq (pa/check sc pc))]
      (throw (ex-info "cannot add column" {:errors errors})))
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
     (add-column (io/read-file workspace-file) cmd)
     workspace-file)
    (format/print :ok)))

(defn handle
  [cmd]
  (handle* (merge default-args cmd)))
