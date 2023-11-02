(ns exco.handler-rename-table.core
  (:require [clojure.core :as clj]
            [clojure.spec.alpha :s s]
            [exco.defaults.interface  :as defaults]
            [exco.patch.interface :as patch]
            [exco.db.interface :as db]
            [exco.patch-apply.interface  :as pa]
            [exco.format.interface :as format]
            [exco.workspace-io.interface :as io]))

(defn update-last
  [coll fun & args]
  (conj (pop coll) (apply fun (peek coll) args)))

(defn patch
  [name-from name-to]
  [#:hunk{:action :action/rename-table
          :name-from name-from
          :name-to name-to}])

(defn rename-table
  [{:workspace/keys [default-db databases] :as ws} {:keys [database from to]}]
  (let [db (or (keyword database) default-db)
        fr (clj/keyword from)
        to (clj/keyword to)
        sc (db/schema (databases db))
        pc (patch fr to)]
    (when-let [errors (seq (pa/check sc pc))]
      (throw (ex-info "cannot rename table" {:errors errors})))
     (update-in ws
                [:workspace/databases db :db/migrations]
                update-last
                update :migration/patch patch/compose pc)))

(def default-args
  {:directory defaults/directory
   :workspace-file defaults/workspace-file})

(defn handle*
  [{:keys [directory workspace-file] :as cmd}]
  (io/with-base directory
    (io/print-file
     (rename-table (io/read-file workspace-file) cmd)
     workspace-file)
    (format/print :ok)))

(defn handle
  [cmd]
  (handle* (merge default-args cmd)))
