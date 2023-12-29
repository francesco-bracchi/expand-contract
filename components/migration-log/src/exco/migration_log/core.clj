(ns exco.migration-log.core
  (:refer-clojure :exclude [update sort])
  (:require [honey.sql.helpers :as sqh]
            [exco.binding.interface :as b]))

(def ^:const table
  :migration_logs)

;; todo: add index over binding_id
(defn ddl
  [d]
  (-> (sqh/create-table table)
      (sqh/with-columns
        [:id :integer :primary-key [:not nil]]
        [:binding_id :integer [:references [:bindings :id]]]
        [:progressive :integer [:not nil]]
        [:hash :text [:not nil]]
        [:status :text])))

(defn query
  []
  (sqh/from table))

(defn insert
  []
  (sqh/insert-into table))

(defn delete
  []
  (sqh/delete-from table))

(defn update
  []
  (sqh/update table))

(defn binding-ids
  ([query prj]
   (-> query
       (sqh/select :id)
       (b/with-project prj)))
  ([query prj env]
   (-> query
       (sqh/select :id)
       (b/with-project prj)
       (b/with-env env))))

(defn bound
  ([query prj]
   (let [ids (-> (b/query) (binding-ids prj))]
     (sqh/where query [:in :binding_id ids])))
  ([query prj env]
   (let [ids (-> (b/query) (binding-ids prj env))]
     (sqh/where query [:in :binding_id ids]))))

(defn sort
  [query]
  (sqh/order-by query :progressive))

(defn with-states
  [query states]
  (sqh/where query [:in :state states]))

(defn with-state
  [query state]
  (with-states query [state]))

(defn fields
  [query]
  (-> query (sqh/select :id :progressive :hash :status)))
